package com.wendisx.ui;

import java.util.ArrayList;

import com.wendisx.model.Graph;
import com.wendisx.util.DataIO;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;


/**
 * 主页面显示组件类
 */
public class MainUI {
    private String pathToIntroduction = "/docs/introduction.txt";

    // 当前选中的按钮
    private Button selectedButton = null;

    // 彩蛋点击次数
    private int eggShitCount = 0;

    // 通用组件类实例
    private CommonUI commonUI = new CommonUI();

    // io工具类实例
    private DataIO dataIO = new DataIO();

    // 加载village ui
    VillageUI villageUI = new VillageUI(dataIO);
     
    // 加载road ui
    RoadUI roadUI = new RoadUI(dataIO);

    // 加载map ui
    MapUI mapUI = new MapUI(dataIO);

    // 创建保存按钮
    Button saveButton = new Button("Save");
    // 创建刷新按钮
    Button reloadButton = new Button("Reload");

    // 获取主页介绍内容
    String introductionContent = dataIO.readFromResources(pathToIntroduction,7,3);

    // 导航按钮控件
    private Button createNavButton(String text) {
        Button button = new Button(text);
        // 按钮宽度最大化
        button.setMaxWidth(Double.MAX_VALUE);
        if(text == "Home"){
            button.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
        }else{
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
        }
        button.setOnMouseEntered(event -> {
            if(button != selectedButton){
                button.setStyle("-fx-background-color:rgba(52, 152, 219, 0.7); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
            }
        });
        button.setOnMouseExited(event -> {
            if(button!=selectedButton){
                button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
            }
        });
        return button;
    }

    // 点击更新按钮样式
    private void updateButtonStyle(Button clickedButton) {
        if (selectedButton != null) {
            // 恢复上一个选择按钮的颜色为蓝色
            selectedButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
        }
        // 设置选中按钮为绿色
        clickedButton.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 14px;");
        // 记录选中的按钮
        selectedButton = clickedButton;  
    }

    // 导航栏按钮列表(数据操作)
    private ArrayList<Button> createNavButtons(){
        ArrayList<Button> buttonsList = new ArrayList<>();
        // 创建导航按钮
        Button HomeButton = createNavButton("Home");
        // 初始选择主页按钮
        selectedButton = HomeButton;
        Button VillageButton = createNavButton("Village");
        Button RoadButton = createNavButton("Road");
        Button MapButton = createNavButton("Map");
        // 加入列表
        buttonsList.add(HomeButton);
        buttonsList.add(VillageButton);
        buttonsList.add(RoadButton);
        buttonsList.add(MapButton);
        return buttonsList;
    }

    // TODO: 刷新内容区域
    private void reloadContentArea(VBox contentArea){
        reloadButton.setOnAction(event -> {
            if(selectedButton==null){
                commonUI.createWarningAlert("提示", null, "刷新失败！").showAndWait();
            }else{
                System.out.println("[Reload] 刷新内容区");
                String curContentArea = selectedButton.getText();
                System.out.println(curContentArea);
                contentArea.getChildren().remove(1,contentArea.getChildren().size());
                if(curContentArea.equals("Village")){
                    villageUI.setCurrentPage(1);
                    contentArea.getChildren().add(villageUI.createUI());
                }else if(curContentArea.equals("Road")){
                    roadUI.setCurrentPage(1);
                    contentArea.getChildren().add(roadUI.createUI());
                }else if(curContentArea.equals("Map")){
                    contentArea.getChildren().add(mapUI.createUI());
                }else if(curContentArea.equals("Home")){
                    TextArea introduction = creatTextArea(introductionContent);
                    contentArea.getChildren().add(introduction);
                    HBox eggBox = createEggShitBox();
                    contentArea.getChildren().add(eggBox);
                }
                commonUI.createInformationAlert("Reload", null, "刷新成功！").showAndWait();
            }
        });
    }

    // 导航栏操作按钮列表(页面文件操作)
    private ArrayList<Button> createNavOperation(){
        ArrayList<Button> operationButtons = new ArrayList<>();
        // 设置保存操作按钮
        
        saveButton.setStyle("-fx-background-color:rgba(79, 245, 33, 0.85); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 10px;");
        saveButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setOnMouseEntered(event -> {
            saveButton.setStyle("-fx-background-color:rgba(79, 245, 33, 0.85); -fx-text-fill: red; -fx-padding: 10px; -fx-font-size: 10px;");
        });
        saveButton.setOnMouseExited(event -> {
            saveButton.setStyle("-fx-background-color:rgba(79, 245, 33, 0.85); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 10px;");
        });
        saveButton.setOnAction(event -> {
            Alert confirmationAlert = commonUI.createConfirmationAlert("Save", null, "保存后更改的数据无法恢复，你真的需要保存吗？");
            confirmationAlert.showAndWait().ifPresent(response -> {
                if(response == commonUI.yesButton){
                    System.out.println("[Save] 点击了 '是' 按钮");
                    // TODO: 保存触发执行操作(api)
                    if(MapUI.getAlterCount()>0){
                        dataIO.writeVilageInfo();
                        dataIO.writeRoadInfo();
                        MapUI.resetAlterCount();
                    }
                    commonUI.createInformationAlert("Save", null, "保存成功！").showAndWait();
                }
            });
        });
        // 设置刷新操作按钮
        
        reloadButton.setStyle("-fx-background-color:rgb(237, 240, 55); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 10px;");
        reloadButton.setMaxWidth(Double.MAX_VALUE);
        reloadButton.setOnMouseEntered(event -> {
            reloadButton.setStyle("-fx-background-color:rgb(237, 240, 55); -fx-text-fill: red; -fx-padding: 10px; -fx-font-size: 10px;");
        });
        reloadButton.setOnMouseExited(event -> {
            reloadButton.setStyle("-fx-background-color:rgb(237, 240, 55); -fx-text-fill: white; -fx-padding: 10px; -fx-font-size: 10px;");
        });

        // 加入列表
        operationButtons.add(saveButton);
        operationButtons.add(reloadButton);
        return operationButtons;
    }

    // 导航栏操作容器
    private VBox createNavOperationBox(){
        VBox operationBox = new VBox(10);
        ArrayList<Button> operationButtons = createNavOperation();
        operationBox.setStyle("-fx-background: #2C3E50; -fx-padding: 10;");
        operationBox.setMinWidth(130);
        operationBox.getChildren().addAll(operationButtons);
        return operationBox;
    }

    // 创建导航栏
    private VBox createNavBar(ArrayList<Button> buttons,VBox operationBox){
        VBox navBar = new VBox(10);
        navBar.setStyle("-fx-background-color: #2C3E50; -fx-padding: 10;");
        navBar.setMinWidth(150);
        // 将按钮加入导航栏
        navBar.getChildren().addAll(buttons);
        navBar.getChildren().addAll(operationBox);
        return navBar;
    }

    // 创建文本域
    private TextArea creatTextArea(String content){
        TextArea textArea = new TextArea();
        // 设置自动换行
        textArea.setWrapText(true);
        // 设置只读内容
        textArea.setEditable(false);
        // 设置高度
        textArea.setPrefHeight(340);
        // 设置样式
        textArea.setStyle("-fx-border-color: black; -fx-border-width: 2px; -fx-font-size: 14px;");
        // 设置内容
        textArea.setText(content);

        return textArea;
    }

    // 创建图像域
    private ImageView createImageBox(String imagePath,int H,int W){
        // 加载图片
        Image image = new Image(getClass().getResourceAsStream(imagePath));
        // 创建图片显示区域
        ImageView imageView = new ImageView(image);
        
        // 设置宽高和比例
        imageView.setFitHeight(H);
        imageView.setFitWidth(W);
        imageView.setPreserveRatio(false);
        
        return imageView;
    }

    // 彩蛋盒子
    private HBox createEggShitBox(){
        // 初始化彩蛋盒子
        HBox eggBox = new HBox(220);
        eggBox.setAlignment(Pos.CENTER);
        eggBox.setStyle("-fx-padding: 0; -fx-background-color:rgb(244, 244, 244);");
        // 初始化彩蛋按钮
        Button eggButton = new Button("=>开始使用<=");
        // 设置样式
        eggButton.setStyle("-fx-background-color:rgba(171, 129, 14, 0.84); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        eggButton.setOnMouseEntered(event -> {
            eggButton.setStyle("-fx-background-color:rgb(171, 129, 14); -fx-text-fill: red; -fx-font-size: 16px; -fx-padding: 5px;");
        });
        eggButton.setOnMouseExited(event -> {
            eggButton.setStyle("-fx-background-color:rgba(171, 129, 14, 0.84); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        });
        // 触发彩蛋图片
        ImageView imageView = createImageBox("/images/3.jpg", 150, 160);
        
        eggButton.setOnAction(event -> {
            eggShitCount++;
            switch (eggShitCount) {
                case 1:
                    eggButton.setText("没反应?");
                    break;
                case 2:
                    eggButton.setText("用点力?");
                    break;
                case 3:
                    eggButton.setText("菜练懂?");
                    break;
                case 4:
                    eggButton.setText("快来了?");
                    break;
                case 5:
                    // 更新彩蛋次数
                    eggShitCount=0;
                    eggBox.getChildren().remove(eggButton);
                    eggBox.getChildren().add(imageView);
                    break;
                default:
                    break;
            }
        });
        eggBox.getChildren().add(eggButton);
        
        return eggBox;
    }

    // 构建图
    private void generateGraph(DataIO dataIO){
        Graph.createGraph(dataIO);
    }

    // 初始化MainUI控件
    public BorderPane createUI() {
        // 创建根布局
        BorderPane root = new BorderPane();
        // 获取导航栏按钮列表
        ArrayList<Button> navButtons = createNavButtons();
        // 操作容器
        VBox operatioBox = createNavOperationBox();
        // 创建左侧导航栏
        VBox navBar = createNavBar(navButtons,operatioBox);
        // 创建右侧操作区域
        VBox contentArea = new VBox(20);
        contentArea.setStyle("-fx-padding: 20;");
        // 右侧内容居中显示
        contentArea.setAlignment(javafx.geometry.Pos.TOP_CENTER);

        // 初始化图
        //generateGraph(dataIO);

        VBox.setVgrow(villageUI.createUI(), javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(roadUI.createUI(), javafx.scene.layout.Priority.ALWAYS);
        VBox.setVgrow(mapUI.createUI(), javafx.scene.layout.Priority.ALWAYS);

        // 设置刷新按钮事件
        reloadContentArea(contentArea);
        
        // 右侧内容初始文本展示
        Text contentText = new Text("公路村村通");
        contentText.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        contentArea.getChildren().add(contentText);
        // 初始化项目介绍
        TextArea originIntroduction = creatTextArea(introductionContent);
        contentArea.getChildren().add(originIntroduction);
        // 初始化彩蛋盒子
        HBox originEggBox = createEggShitBox();
        contentArea.getChildren().add(originEggBox);
        
        // 处理按钮点击切换内容区标题
        navButtons.get(0).setOnAction(event -> {
            contentText.setText("公路村村通");
            updateButtonStyle(navButtons.get(0));
            // 清除从第二个开始的子组件
            contentArea.getChildren().remove(1, contentArea.getChildren().size());
            TextArea introduction = creatTextArea(introductionContent);
            contentArea.getChildren().add(introduction);
            HBox eggBox = createEggShitBox();
            contentArea.getChildren().add(eggBox);
        });
        navButtons.get(1).setOnAction(event -> {
            contentText.setText("村庄信息");
            updateButtonStyle(navButtons.get(1));
            // 清除从第二个开始的子组件
            contentArea.getChildren().remove(1, contentArea.getChildren().size());
            contentArea.getChildren().add(villageUI.createUI());
        });
        navButtons.get(2).setOnAction(event -> {
            contentText.setText("道路信息");
            updateButtonStyle(navButtons.get(2));
            // 清除从第二个开始的子组件
            contentArea.getChildren().remove(1, contentArea.getChildren().size());
            contentArea.getChildren().add(roadUI.createUI());
        });
        navButtons.get(3).setOnAction(event -> {
            contentText.setText("地图信息");
            updateButtonStyle(navButtons.get(3));
            // 清除从第二个开始的子组件
            contentArea.getChildren().remove(1, contentArea.getChildren().size());
            contentArea.getChildren().add(mapUI.createUI());
        });

        // 设置左侧导航栏
        root.setLeft(navBar);
        // 设置右侧操作区域
        root.setCenter(contentArea); 

        // 返回布局
        return root; 
    }

    // 构建场景
    public Scene createScene() {
        // 创建 UI 和场景
        BorderPane root = createUI();
        Scene scene = new Scene(root, 800, 600);
        // 加载样式文件
        scene.getStylesheets().add(getClass().getResource("/styles/style.css").toExternalForm());
        return scene;
    }
}