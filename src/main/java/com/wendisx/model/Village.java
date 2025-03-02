package com.wendisx.model;
/**
 * 村庄模型
 */
public class Village {
    // 村庄编号
    private String number;
    // 村庄名称
    private String name;
    // 村庄坐标
    private Point position;
    // 村庄简介
    private String description;

    // 构造函数
    public Village(String tnumber,String tname,Point tposition,String tdescription){
        this.number = tnumber;
        this.name = tname;
        this.position = tposition;
        this.description = tdescription;
    }
    // 设置获取编号
    public void setNumber(String tnumber){
        this.number = tnumber;
    }
    public String getNumber(){
        return this.number;
    }
    // 设置获取名称
    public void setName(String tname){
        this.name = tname;
    }
    public String getName(){
        return this.name;
    }
    // 设置获取坐标
    public void setPosition(Point tposition){
        this.position = tposition;
    }
    public String getPosition(){
        String pos = "("+this.position.getX()+","+this.position.getY()+")";
        return pos;
    }
    public Point getPositionObject(){
        return this.position;
    }
    // 设置获取简介
    public void setDescription(String tdescription){
        this.description = tdescription;
    }
    public String getDescription(){
        return this.description;
    }

}
