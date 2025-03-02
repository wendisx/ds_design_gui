package com.wendisx.model;
/**
 * 坐标模型
 */

public class Point {
    // 横坐标
    private long x;
    // 纵坐标
    private long y;
    // 构造方法
    public Point(long tx,long ty){
        this.x = tx;
        this.y = ty;
    }
    // 设置获取横坐标
    public void setX(long tx){
        this.x = tx;
    }
    public long getX(){
        return this.x;
    }
    // 设置获取纵坐标
    public void setY(long ty){
        this.y = ty;
    }
    public long getY(){
        return this.y;
    }
    // toString方法
    @Override
    public String toString(){
        return "("+this.x+","+this.y+")";
    }
}
