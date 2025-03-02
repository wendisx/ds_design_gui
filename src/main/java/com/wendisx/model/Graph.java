package com.wendisx.model;
/**
 * 图
 */
import java.util.*;

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

    // 构建内部并查集
    private static class UnionFind {
        // 代表节点
        private Map<String,String> parent;
        // 构造函数
        public UnionFind(Collection<String> nodes){
            parent = new HashMap<>();
            for(String node:nodes){
                // 初始时添加自身为代表节点
                parent.put(node, node);
            }
        }
        // 路径压缩
        public String find(String node){
            if(!parent.get(node).equals(node)){
                parent.put(node, find(parent.get(node)));
            }
            return parent.get(node);
        }
        // 合并集合
        public void union(String u,String v){
            String uroot = find(u);
            String vroot = find(v);
            if(!uroot.equals(vroot)){
                // 不在一个集合下，合并集合
                parent.put(uroot, vroot);
            }
        }
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
    public static List<List<String>> findConnectedColletion(){
        Set<String> visited = new HashSet<>();
        List<List<String>> components = new ArrayList<>();

        for(String node:graph.keySet()){
            if(!visited.contains(node)){
                List<String> component = new ArrayList<>();
                DFS(node,visited,component);
                components.add(component);
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
        List<List<String>> components = findConnectedColletion();
        for(int i=0;i<components.size();i++){
            for(int j=i+1;j<components.size();j++){
                List<String> comp1 = components.get(i);
                List<String> comp2 = components.get(j);

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
        //System.out.println("addCount:"+addRoadCount);
        IsConnected = true;
        return result;
    }

    // kruskal算法计算MST
    public static List<Edge> kruskal(){
        List<Edge> result = new ArrayList<>();
        List<Edge> allEdges = new ArrayList<>();

        // 所有边放入列表等待操作
        for(Map.Entry<String,List<Edge>> entry:graph.entrySet()){
            allEdges.addAll(entry.getValue());
        }

        // 排序所有边
        Collections.sort(allEdges);

        // 初始化并查集
        UnionFind uf = new UnionFind(graph.keySet());
        // 遍历所有边，按权值升序加入MST
        for(Edge edge: allEdges){
            String u = edge.begin;
            String v = edge.end;

            // 检查是否成环
            if(uf.find(u).equals(uf.find(v))){
                continue;
            }
            // 合并加边将其加入结果
            uf.union(u, v);
            result.add(edge);
        }
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
     *  遍历全图最短路径
     */
    
    // 计算所有点度数
    private static Map<String,Integer> getDegrees(){
        Map<String,Integer> degree = new HashMap<>();

        for(String node:graph.keySet()){
            degree.put(node, graph.get(node).size());
        }
        return degree;
    }

    // Dijkstra计算所有对点最短路径
    private static Map<String,Integer> Dijkstra(String start){
        Map<String,Integer> dist = new HashMap<>();
        PriorityQueue<Edge> pq = new PriorityQueue<>(Comparator.comparingInt(e -> (int)e.weight));
        pq.add(new Edge(start, start, 0));
        
        while (!pq.isEmpty()) {
            Edge cur = pq.poll();
            if(dist.containsKey(cur.end))continue;
            dist.put(cur.end, (int)cur.weight);

            for(Edge next:graph.getOrDefault(cur.end, new ArrayList<>())){
                if(!dist.containsKey(next.end)){
                    pq.add(new Edge(next.end, next.end, cur.weight+next.weight));
                }
            }
        }
        return dist;
    }

    // 获取所有点对之间的最短路径
    private static Map<String,Map<String,Integer>> getAllPairsShortPath(){
        Map<String,Map<String,Integer>> shortPath = new HashMap<>();
        for(String node:graph.keySet()){
            shortPath.put(node, Dijkstra(node));
        }
        return shortPath;
    }

    // 找到度数为奇数的点
    private static List<String> getOddDegreesNodes(){
        List<String> oddNodes = new ArrayList<>();
        Map<String,Integer> degree = getDegrees();
        for(String node:degree.keySet()){
            if(degree.get(node)%2!=0){
                oddNodes.add(node);
            }
        }
        return oddNodes;
    }

    // 最小权匹配，变为欧拉图
    private static void makeEulerGraph(Map<String,Map<String,Integer>> shortPath){
        List<String> oddNodes = getOddDegreesNodes();
        while (!oddNodes.isEmpty()) {
            String node1 = oddNodes.remove(0);
            String bestMatch = null;
            int minDist = Integer.MAX_VALUE;

            for(String node2:oddNodes){
                int dist = shortPath.get(node1).get(node2);
                if(dist<minDist){
                    minDist = dist;
                    bestMatch = node2;
                }
            }
             // 连接最佳匹配点
        graph.get(node1).add(new Edge(node1, bestMatch, minDist));
        graph.get(bestMatch).add(new Edge(bestMatch, node1, minDist));
        oddNodes.remove(bestMatch);
        }
    }

    // Hierholzer 算法求欧拉路径
    private static List<Edge> findEulerPath(String start){
        Stack<String> stack = new Stack<>();
        List<Edge> path = new ArrayList<>();
        Map<String,List<Edge>> tmpGraph = new HashMap<>();

        for(String key:graph.keySet()){
            tmpGraph.put(key, new ArrayList<>(graph.get(key)));
        }

        stack.push(start);
        while (!stack.isEmpty()) {
            String u = stack.peek();
            if(!tmpGraph.get(u).isEmpty()){
                Edge e = tmpGraph.get(u).remove(0);
                tmpGraph.get(e.end).removeIf(edge -> edge.end.equals(u));
                stack.push(e.end);
            }
            else{
                stack.pop();
                if(!stack.isEmpty()){
                    path.add(new Edge(stack.peek(), u, 0));
                }
            }
        }
        return path;
    }

    // 遍历所有点的最短路径
    public static List<Edge> findShortestTravelPath(String start){
        Map<String,Map<String,Integer>> shortPath = getAllPairsShortPath();
        makeEulerGraph(shortPath);
        return findEulerPath(start);
    }

}
