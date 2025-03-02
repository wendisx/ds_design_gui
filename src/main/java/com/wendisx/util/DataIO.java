package com.wendisx.util;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wendisx.model.Point;
import com.wendisx.model.Road;
import com.wendisx.model.Village;

/**
 * 数据读取工具类
 */
public class DataIO {
    // 村庄数据文件路径
    private  String VillageInfoPath = "/assets/villages.xlsx";
    // 道路数据文件路径
    private  String RoadInfoPath = "/assets/roads.xlsx";
    
    // 村庄列表
    private List<Village> villages = readVillageInfo(VillageInfoPath);
    // 道路列表
    private  List<Road> roads = readRoadInfo(RoadInfoPath);

    // 设置暴露村庄列表引用
    public void setVillages(List<Village> villages){
        this.villages = villages;
    }
    public List<Village> getVillages(){
        return villages;
    }

    // 设置暴露道路列表引用
    public void setRoads(List<Road> roads){
        this.roads = roads;
    }
    public List<Road> getRoads(){
        return roads;
    }

     // 从资源下读取项目介绍
     public  String readFromResources(String fileName,int preTabSize,int preSpaceSize){
        StringBuilder content = new StringBuilder();
        // 插入前置空tab数
        for(int i=0;i<preTabSize;i++){
            content.append("\t");
        }
        // 插入前置空格数
        for(int i=0;i<preSpaceSize;i++){
            content.append(" ");
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(fileName)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // 按照行划分内容
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content.toString();
     }

     // 读取村庄数据
     public  List<Village> readVillageInfo(String filePath){
        // 初始化村庄信息线性表
        List<Village> villages = new ArrayList<>();
        
        // 尝试读取xlsx文件信息
        try(XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream(filePath))){
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);

            // 跳过表头
            for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
                Row row = sheet.getRow(i);

                if(row!=null){
                    // 编号
                    String number = row.getCell(0).getStringCellValue();
                    // 名称
                    String name = row.getCell(1).getStringCellValue();
                    // 横坐标
                    long x = (long)row.getCell(2).getNumericCellValue();
                    // 纵坐标
                    long y = (long)row.getCell(3).getNumericCellValue();
                    // 简介
                    String description = row.getCell(4).getStringCellValue();
                    
                    // 添加到线性表中
                    villages.add(new Village(number, name, new Point(x, y), description));
                }
            }
        }catch (IOException e){
            // 打印异常信息
            e.printStackTrace();
        }
        return villages;
     }

     // 根据村庄编号找到坐标
     private Point getVillagePosition(String Number){
        for(Village village : villages){
            if(village.getNumber().equals(Number)){
                return village.getPositionObject();
            }
        }
        return null;
     }

     // 读取道路数据
     public  List<Road> readRoadInfo(String filePath){
        // 初始化道路信息线性表
        List<Road> roads = new ArrayList<>();

        // 尝试读取xlsx文件信息
        try(XSSFWorkbook workbook = new XSSFWorkbook(getClass().getResourceAsStream(filePath))){
            // 获取第一个工作表
            Sheet sheet = workbook.getSheetAt(0);
            
            // 跳过属性字段表头读取数据
            for(int i=1;i<sheet.getPhysicalNumberOfRows();i++){
                Row row = sheet.getRow(i);

                if(row!=null){
                    // 起始编号
                    String beginNumber = row.getCell(0).getStringCellValue();
                    // 终点编号
                    String endNumber = row.getCell(1).getStringCellValue();
                    Road road = new Road(i, beginNumber, endNumber);
                    // 直接计算道路长度
                    Point start = getVillagePosition(beginNumber);
                    Point stop = getVillagePosition(endNumber);
                    road.setRoadLength(start, stop);
                    roads.add(road);
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        }
        return roads;
     }

     // 写入村庄数据
     public void writeVilageInfo(){
        try (XSSFWorkbook workbook = new XSSFWorkbook()){
            // 表名
            Sheet sheet = workbook.createSheet("test");
            
            // 创建标题行
            Row row = sheet.createRow(0);
            // 编号
            Cell numberCell = row.createCell(0);
            numberCell.setCellValue("编号");
            // 名称
            Cell namCell = row.createCell(1);
            namCell.setCellValue("名称");
            // 横坐标
            Cell xCell = row.createCell(2);
            xCell.setCellValue("横坐标");
            // 纵坐标
            Cell yCell = row.createCell(3);
            yCell.setCellValue("纵坐标");
            // 简介
            Cell descriptionCell = row.createCell(4);
            descriptionCell.setCellValue("简介");

            // 开始填入数据的行
            int curRow = 1;
            for(Village curVillage: villages){
                row = sheet.createRow(curRow++);
                // 编号
                Cell number = row.createCell(0);
                number.setCellValue(curVillage.getNumber());
                // 名称
                Cell name = row.createCell(1);
                name.setCellValue(curVillage.getName());
                // 横坐标
                Cell x = row.createCell(2);
                x.setCellValue(curVillage.getPositionObject().getX());
                // 纵坐标
                Cell y = row.createCell(3);
                y.setCellValue(curVillage.getPositionObject().getY());
                // 简介
                Cell description = row.createCell(4);
                description.setCellValue(curVillage.getDescription());
            }
            
            // 写入文件
            String filePath = Paths.get("src", "main","resources","assets","villages.xlsx").toString(); 
            try(FileOutputStream fos = new FileOutputStream(filePath)){
                workbook.write(fos);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
     }

     // 写入道路数据
     public void writeRoadInfo(){
        try (XSSFWorkbook workbook = new XSSFWorkbook()){
            // 表名
            Sheet sheet = workbook.createSheet("test");

            // 创建标题行
            Row row = sheet.createRow(0);
            // 起点编号
            Cell beginNumberCell = row.createCell(0);
            beginNumberCell.setCellValue("起点编号");
            // 终点编号 
            Cell endNumberCell = row.createCell(1);
            endNumberCell.setCellValue("终点编号");
            
            // 开始填入数据
            int curRow = 1;
            for(Road curRoad:roads){
                row = sheet.createRow(curRow++);
                // 起点编号
                Cell beginNumber = row.createCell(0);
                beginNumber.setCellValue(curRoad.getBeginNumber());
                // 终点编号
                Cell endNumber = row.createCell(1);
                endNumber.setCellValue(curRoad.getEndNumber());
            }

            // 写入文件
            String filePath = Paths.get("src", "main","resources","assets","roads.xlsx").toString(); 
            try(FileOutputStream fos = new FileOutputStream(filePath)){
                workbook.write(fos);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
     }
     
}
