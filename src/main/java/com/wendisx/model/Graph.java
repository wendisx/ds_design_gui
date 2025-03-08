package com.wendisx.model;
/**
 * 图
 */
import java.util.*;

import org.apache.poi.hpsf.Array;

import com.wendisx.util.DataIO;

public class Graph {
    // 图存储
    private static Map<String,List<Edge>> graph;
    // 村庄列表
    private static List<Village> villagesList;
    // 道路列表
    private static List<Road> roadsList;
    // dijkstra单源最短路径长度
    private static int shortestPathLength;
    public static int getShortestPathLength(){
        return shortestPathLength;
    }

    // 新增道路构建连通图条数
    private static int addRoadCount = 0;
    public static void setAddRoadCount(int count){
        addRoadCount = count;
    }

    // 图是否连通
    private static boolean IsConnected = false;
    public static void setConnectedStatus(boolean status){
        IsConnected = status;
    }

    // 构建内部静态边类
    public static class  Edge implements Comparable<Edge> {
        String begin; //起点编号
        String end; // 终点编号
        long weight; // 边权重
        // 构造函数
        public Edge(String beginNumber,String endNumber,long weight){
            this.begin = beginNumber;
            this.end = endNumber;
            this.weight = weight;
        }

        // 暴露起点和终点
        public String getBegin(){
            return begin;
        }
        public String getEnd(){
            return end;
        }

        // 实现比较用于MST算法按照权重排序
        @Override
        public int compareTo(Edge other){
            return Integer.compare((int)this.weight,(int)other.weight);
        }
    }

    // 添加边
    private static void addEdge(String beginNumber,String endNumber,long weight){
        graph.computeIfAbsent(beginNumber, k -> new ArrayList<>());
        graph.get(beginNumber).add(new Edge(beginNumber,endNumber, weight));
    }

    // 使用村庄和道路数据构建图
    public static void createGraph(DataIO dataIO){
        graph = new HashMap<>();

        // 存储村庄和道路列表
        villagesList = dataIO.getVillages();
        roadsList = dataIO.getRoads();
        // 不是连通图表示当前需要回溯
        if(!IsConnected){
            //System.out.println("删除构建最小连通方案道路数量："+addRoadCount);
            while (addRoadCount>0) {
                int index = roadsList.size()-1;
                roadsList.remove(index);
                addRoadCount--;
            }
        }
        
        // 遍历村庄数据，构建邻接空表
        for(Village village:villagesList){
            // 每个村庄初始化一个空列表，将编号作为键
            graph.computeIfAbsent(village.getNumber(), k -> new ArrayList<>());
        }

        // 遍历道路，构建邻接表
        for(Road road:roadsList){
            String beginVillage = road.getBeginNumber();
            String endVillage = road.getEndNumber();
            // 添加道路到指定编号村庄下
            addEdge(beginVillage, endVillage, road.getRoadLength());
            addEdge(endVillage, beginVillage, road.getRoadLength());
        }
        //System.out.println("图构建成功！");
    }

    // 暴露图
    public static Map<String,List<Edge>> getGraph(){
        return graph;
    }

    // BFS判断图是否连通
    private static int BFS(){
        // 构建访问列表和待访问队列
        Set<String> visited = new HashSet<>();
        LinkedList<String> queue = new LinkedList<>();
        // 起始节点入队
        String start = villagesList.get(0).getNumber();
        visited.add(start);
        queue.add(start);
        // 遍历图
        while (!queue.isEmpty()) {
            String node = queue.poll();
            // 遍历所有邻接节点
            for(Edge neighbor:graph.getOrDefault(node, new ArrayList<>())){
                if(!visited.contains(neighbor.end)){
                    // 未访问过，标记访问并加入队列
                    visited.add(neighbor.end);
                    queue.add(neighbor.end);
                }
            }
        }
        return visited.size();
    }

    // 判断是否连通
    public static boolean isConnected(){
        int visitedSize = BFS();
        return visitedSize==villagesList.size();
    }

    // 根据编号获取村庄坐标
    private static Point getPositionByVillageNumber(String villageNumber){
        for(Village village:villagesList){
            if(village.getNumber().equals(villageNumber)){
                return village.getPositionObject();
            }
        }
        return null;
    }

    // 根据编号计算边权值
    private static long getWeight(String start,String stop){
        Point starPoint = getPositionByVillageNumber(start);
        Point stopPoint = getPositionByVillageNumber(stop);
        if(starPoint==null || stopPoint==null){
            return -1;
        }
        long xDiff = stopPoint.getX()-starPoint.getX();
        long yDiff = stopPoint.getY()-starPoint.getY();
        return Math.round(Math.sqrt(xDiff*xDiff+yDiff*yDiff));
    }

    // 识别所有连通分量
    public static List<List<String>> findConnectedCollection(){
        Set<String> visited = new HashSet<>();
        List<List<String>> components = new ArrayList<>();

        for(String node:graph.keySet()){
            if(!visited.contains(node)){
                List<String> component = new ArrayList<>();
                DFS(node,visited,component);
                components.add(component);
            }
        }

        // 处理孤立节点
        for(String node:graph.keySet()){
            if(!visited.contains(node)){
                components.add(Collections.singletonList(node));
                visited.add(node);
            }
        }

        return components;
    }

    // DFS遍历连通分量
    private static void DFS(String node,Set<String> visited,List<String> component){
        visited.add(node);
        component.add(node);

        for(Edge edge:graph.getOrDefault(node, new ArrayList<>())){
            if(!visited.contains(edge.end)){
                DFS(edge.end,visited,component);
            }
        }
    }

    // 获取最小连通方案
    public static List<Edge> getMiniConnectedEdges(){
        List<Edge> result = new ArrayList<>();
        List<List<String>> components = findConnectedCollection();

        // 处理孤立节点和普通连通分量
        List<String> isolatedNodes = new ArrayList<>();
        List<List<String>> normalNodes = new ArrayList<>();

        // 记录孤立节点
        for(List<String> node:components){
            if(node.size()==1 && graph.get(node.get(0)).isEmpty()){
                // 孤立节点
                isolatedNodes.add(node.get(0));
            }else{
                // 普通节点
                normalNodes.add(node);
            }

        }
        // 处理普通节点
        for(int i=0;i<normalNodes.size();i++){
            for(int j=i+1;j<normalNodes.size();j++){
                List<String> comp1 = normalNodes.get(i);
                List<String> comp2 = normalNodes.get(j);

                long minWeight = Long.MAX_VALUE;
                Edge minEdge = null;

                for(String node1:comp1){
                    for(String node2:comp2){
                        long weight = getWeight(node1, node2);
                        if(weight<minWeight){
                            minWeight=weight;
                            minEdge = new Edge(node1, node2, weight);
                        }
                    }
                }

                if(minEdge!=null){
                    result.add(minEdge);
                    // 当前图不连通时新增道路
                    if(!IsConnected){
                        // 新增道路
                        roadsList.add(new Road((long)roadsList.size(),minEdge.begin,minEdge.end));
                        // 记录条数
                        addRoadCount++;
                    }
                }
            }
        }
        // 处理孤立节点
        for(String isolated:isolatedNodes){
            long minWeight = Long.MAX_VALUE;
            Edge bestEdge = null;

            for(List<String> norList:normalNodes){
                for(String norNode:norList){
                    long weight = getWeight(isolated, norNode);
                    if(weight<minWeight){
                        minWeight=weight;
                        bestEdge = new Edge(isolated, norNode, weight);
                    }
                }
            }
            if(bestEdge!=null){
                result.add(bestEdge);
                    // 当前图不连通时新增道路
                    if(!IsConnected){
                        // 新增道路
                        roadsList.add(new Road((long)roadsList.size(),bestEdge.begin,bestEdge.end));
                        // 记录条数
                        addRoadCount++;
                    }
            }
        }

        //System.out.println("addCount:"+addRoadCount);
        IsConnected = true;
        return result;
    }

    // Dijkstra计算单源最短路径
    public static List<Edge> dijkstra(String start,String end){
        Map<String,Integer> dist = new HashMap<>();
        Map<String,Edge> prevEdge = new HashMap<>(); // 记录前驱边
        Set<String> visited = new HashSet<>();

        // 初始化所有节点距离为无穷大
        for(String node:graph.keySet()){
            dist.put(node, Integer.MAX_VALUE);
        }

        // 初始节点距离为0
        dist.put(start, 0);

        PriorityQueue<Map.Entry<String,Integer>> pq = new PriorityQueue<>(Comparator.comparingInt(Map.Entry::getValue));
        pq.offer(new AbstractMap.SimpleEntry<>(start, 0));

        while (!pq.isEmpty()) {
            String node = pq.poll().getKey();
            if(visited.contains(node))continue;
            visited.add(node);

            // 遍历邻接节点
            for(Edge edge:graph.get(node)){
                String neighbor = edge.end;
                int newDist = (int)(dist.get(node) + edge.weight);
                if(newDist<dist.get(neighbor)){
                    dist.put(neighbor, newDist);
                    prevEdge.put(neighbor, edge);
                    pq.offer(new AbstractMap.SimpleEntry<>(neighbor, newDist));
                }
            }
        }

        shortestPathLength = dist.get(end);
        List<Edge> result = new ArrayList<>();
        for(String at = end;at!=null;at=prevEdge.containsKey(at)?prevEdge.get(at).begin : null){
            if(prevEdge.containsKey(at)){
                result.add(prevEdge.get(at));
            }
        }
        Collections.reverse(result);

        return result.isEmpty()?Collections.emptyList():result;

    }

    /**
     * 遍历全图最短路径
     * 使用MST+DFS近似最优求解
     */

     // 并查集查方法
     private static String find(String node,Map<String,String> parent){
        if(!parent.get(node).equals(node)){
            parent.put(node,find(parent.get(node), parent));
        }
        return parent.get(node);
     }

     // 并查集合并
     private static boolean union(String begin,String end,Map<String,String> parent){
        String beginp = find(begin, parent);
        String endp = find(end, parent);

        if(!beginp.equals(endp)){
            parent.put(beginp,endp);
            return true;
        }
        return false;
     }
    // 构建MST使用kruskal算法

    private static List<Edge> buildMST(){
        List<Edge> edges = new ArrayList<>();
        for(List<Edge> edgeList:graph.values()){
            edges.addAll(edgeList);
        }
        Collections.sort(edges);
        Map<String,String> parent = new HashMap<>();
        for(String node:graph.keySet()){
            parent.put(node,node);
        }
        List<Edge> mstEdges = new ArrayList<>();
        for(Edge edge:edges){
            if(union(edge.getBegin(), edge.getEnd(), parent)){
                mstEdges.add(edge);
            }
        }
        return mstEdges;
    }

    // DFS遍历MST找到最短路径
    private static List<Edge> dfsMSTpath(List<Edge> mstEdges,String start){
        Map<String,List<Edge>> mstGraph = new HashMap<>();
        
        for(Edge edge:mstEdges){
            mstGraph.computeIfAbsent(edge.getBegin(), k-> new ArrayList<>()).add(edge);
            mstGraph.computeIfAbsent(edge.end, k-> new ArrayList<>()).add(new Edge(edge.getEnd(), edge.getBegin(),edge.weight));
        }
        
        List<Edge> pathEdges = new ArrayList<>();
        Set<String> visited = new HashSet<>();

        dfs(start, visited, mstGraph, pathEdges);
        return pathEdges;

    }

    // dfs遍历
    private static void dfs(String start, Set<String> visited, Map<String, List<Edge>> mstGraph, List<Edge> pathEdges) {
        visited.add(start);
        for (Edge edge : mstGraph.getOrDefault(start, Collections.emptyList())) {
            if (!visited.contains(edge.end)) {
                pathEdges.add(edge);
                dfs(edge.end, visited, mstGraph, pathEdges);
            }
        }
    }

    // 最短遍历全图路径
    public static List<Edge> findMiniVisiteAllNodePath(String start){
        List<Edge> mstEdges = buildMST();
        return dfsMSTpath(mstEdges, start);
    }

    // 最短回溯路径
    public static List<Edge> findMiniBackSourcePath(String start){
        List<Edge> tspPath = findMiniVisiteAllNodePath(start);
        if(tspPath.isEmpty()){
            return tspPath;
        }

        // 找到回归路径
        String last = tspPath.get(tspPath.size()-1).getEnd();
        List<Edge> returnPath  = dijkstra(last,start);
        // 合并回归路径
        tspPath.addAll(returnPath);
        return tspPath;
    }

}
