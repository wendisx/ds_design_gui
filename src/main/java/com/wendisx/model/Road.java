package com.wendisx.model;
/**
 * 道路模型
 */
public class Road {
    // 道路序号
    private long roadNumber;

    // 起始村庄编号
    private String beginNumber;

    // 终点村庄编号
    private String endNumber;
    
    // 道路长度
    private long roadLength;

    // 构造函数
    public Road(long trNumber,String tbNumber,String teNumber){
        this.roadNumber = trNumber;
        this.beginNumber = tbNumber;
        this.endNumber = teNumber;
    }

    // 计算道路长度
    public static long calculateRoadLength(Point p1,Point p2){
        long x_diff = p2.getX()-p1.getX();
        long y_diff = p2.getY()-p1.getY();
        return Math.round(Math.sqrt(x_diff*x_diff+y_diff*y_diff));
    }
    
    // 设置获取道路序号
    public void setRoadNumber(long rnumber){
        this.roadNumber = rnumber;
    }
    public long getRoadNumber(){
        return this.roadNumber;
    }

    // 设置获取起始编号
    public void setBeginNumber(String bnumber){
        this.beginNumber = bnumber;
    }
    public String getBeginNumber(){
        return this.beginNumber;
    }
    
    // 设置获取终点编号
    public void setEndNumber(String enumber){
        this.endNumber = enumber;
    }
    public String getEndNumber(){
        return this.endNumber;
    }

    // 设置获取道路长度
    public void setRoadLength(Point p1,Point p2){
        this.roadLength = calculateRoadLength(p1, p2);
    }
    public long getRoadLength(){
        return this.roadLength;
    }
}
