package com.wendisx.ui;

import java.util.List;

import com.wendisx.model.Graph;
import com.wendisx.model.Point;
import com.wendisx.model.Road;
import com.wendisx.model.Village;
import com.wendisx.model.Graph.Edge;
import com.wendisx.util.DataIO;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MapUI {

    // 导入通用组件
    private CommonUI commonUI = new CommonUI();

    // 变更次数(图操作引起变化)
    private static long alterCount = 0;

    // 更新变更次数
    public static void updateAlterCount(){
        alterCount++;
    }
    public static void resetAlterCount(){
        alterCount = 0;
    }
    // 暴露变更次数
    public static long getAlterCount(){
        return alterCount;
    }
    
    // 村庄总数
    private static long villageOfSum = 0;
    public static void setVillageOfSum(long villageSum){
        villageOfSum = villageSum;
    }
    // 道路总数
    private static long roadOfSum = 0;
    public static void setRoadOfSum(long roadSum){
        roadOfSum = roadSum;
    }

    // 获取数据操作类
    private DataIO dataIO;

    // 获取villages列表和roads列表
    public MapUI(DataIO dataIO){
        this.dataIO = dataIO;
    }

    // 创建确认和取消按钮
    Button comfirmButton = createActionButton("确认");
    Button cancelButton = createActionButton("取消");

    // 定义当前打开窗口
    private Stage currentStage = null;

    // 初始化画板和画笔
    private Canvas canvas = new Canvas();
    private GraphicsContext gc = canvas.getGraphicsContext2D();

    // 数据标签
    private Label createDataLabel(String text){
        Label dataLabel = new Label(text);
        dataLabel.setStyle("-fx-font-size: 14px;");
        return dataLabel;
    } 

    // 图详细信息展示容器(头部--始终显示)
    private HBox createDetailsHeadBox(){
        // 初始化头部容器
        HBox detailsHeadBox = new HBox(120);
        detailsHeadBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
        
        // 信息字段居中
        detailsHeadBox.setAlignment(Pos.CENTER);

        // 构建头部字段 TODO: 添加是否连通，添加孤立节点数等等
        Label alterLabel = createDataLabel("变更");
        Label villageSumLabel = createDataLabel("村庄总数");
        Label roadSumLabel = createDataLabel("道路总数");

        // 加入字段
        detailsHeadBox.getChildren().addAll(alterLabel,villageSumLabel,roadSumLabel);
        
        return detailsHeadBox;
    }

    // 图详细信息展示容器(信息--点击加载)
    private HBox createDetailsBox(String alter,String villageSum,String roadSum){
        // 初始化信息容器
        HBox detailsBox = new HBox(160);
        detailsBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
        
        // 信息居中
        detailsBox.setAlignment(Pos.CENTER);

        // 构建信息显示 TODO: 添加是否连通，添加孤立节点数据等等
        Label alterLabel = createDataLabel(alter);
        Label villageSumLabel = createDataLabel(villageSum);
        Label roadSumLabel = createDataLabel(roadSum);

        // 加入信息行
        detailsBox.getChildren().addAll(alterLabel,villageSumLabel,roadSumLabel);
        
        return detailsBox;
    }

    // 图信息信息手机操作取消按钮执行逻辑
    private void clickCancelButtonHandle(BorderPane navBar){
        // 移除非首个组件
        navBar.getChildren().remove(1,navBar.getChildren().size());
    }

    // 确认事件处理简介
    private void clickConfirmButtonHandle(int operationLabel,TextField... fields){
        // 确认按键传递数据时验证是否合法
        comfirmButton.setOnAction(event -> {
            switch (operationLabel) {
                case 1:
                    // 多源最短路径处理
                    // 获取起始和终点编号 -- 村庄编号!=坐标点编号
                    String beginNumberM = fields[0].getText();
                    String endNumber = fields[1].getText();
                    if(beginNumberM.isEmpty() || endNumber.isEmpty()){
                        commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
                    }else{
                        multiShortHandle(beginNumberM,endNumber);
                    }
                    break;
                case 2:
                    // 遍历全图节点处理
                    String beginNumberV = fields[0].getText();
                    if(beginNumberV.isEmpty()){
                        commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
                    }else{
                        VisiteAllHandle(beginNumberV);
                    }
                    break;
                case 3:
                    // 最短回归路径处理
                    String beginNumberS = fields[0].getText();
                    if(beginNumberS.isEmpty()){
                        commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
                    }else{
                        ShortRegressHandle(beginNumberS);
                    }
                    break;
                default:
                    break;
            }
        });
        
    }
    
    // 多源最短路径提取信息 --1
    private VBox createMultiShortInfoBox(){
        // 提交表单数据输入框
        TextField beginLabField = new TextField();
        beginLabField.setPromptText("输入起点");
        
        TextField endLabField = new TextField();
        endLabField.setPromptText("输入终点");

        // 绑定确认事件
        clickConfirmButtonHandle(1, beginLabField,endLabField);
        
        // 初始化按钮布局
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelButton,comfirmButton);

        // 初始化展示信息布局
        VBox infBox = new VBox(10);
        infBox.getChildren().addAll(
            new Label("起点编号*:"),beginLabField,
            new Label("终点编号*:"),endLabField,
            buttonBox
        );
        return infBox;
    };

    // 检查输入的村庄编号是否合法
    private boolean checkNumber(String number){
        Point pos = getPositionByVillageNumber(number, dataIO.getVillages());
        return number.matches("[A-Za-z0-9]+") && pos!=null;
    }

    // 多源最短路径处理
    private void multiShortHandle(String beginNumber,String endNumber){
        if(checkNumber(beginNumber) && checkNumber(endNumber)){
            // TODO: 得到合法的起始终点编号，调用最短路径算法计算最短路径
            List<Edge> shortestPath = Graph.dijkstra(beginNumber, endNumber);
            if(shortestPath.size()==0){
                commonUI.createWarningAlert("提示", null, "最短路径不存在！").showAndWait();
                return;
            }
            //System.out.println(Graph.getShortestPathLength());
            drawMultiSourceShortestPath(shortestPath);
            commonUI.showShortestPathDialog(shortestPath,Graph.getShortestPathLength());
            commonUI.createInformationAlert("提示", null, "最短路径查找成功！").showAndWait();
        }else{
            commonUI.createWarningAlert("提示", null, "编号不合法！").showAndWait();
        }
    }

    // 遍历全图最短路径信息获取 --2
    private VBox createVisiteAllInfoBox(){
        // 提交表单数据输入框
        TextField beginLabField = new TextField();
        beginLabField.setPromptText("输入起点");
        
        // 初始化按钮布局
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelButton,comfirmButton);

        // 绑定确认事件
        clickConfirmButtonHandle(2, beginLabField);
        
        // 初始化展示信息布局
        VBox infBox = new VBox(10);
        infBox.getChildren().addAll(
            new Label("起点编号*:"),beginLabField,
            buttonBox
        );
        return infBox;
    }

    // 遍历全图最短路径处理
    private void VisiteAllHandle(String beginNumber){
        if(checkNumber(beginNumber)){
            // TODO: 得到合法起始编号，调用最短路径算法计算遍历得到最短路径
            List<Edge> visiteShortestPath = Graph.findShortestTravelPath(beginNumber);
            drawShortestPath(visiteShortestPath);
            commonUI.createInformationAlert("提示", null, "遍历全图最短路径查找成功！").showAndWait();
        }else{
            commonUI.createWarningAlert("提示", null, "编号不合法！").showAndWait();
        }
    }

    // 最短回归路径信息获取 --3
    private VBox createShortRegressInfoBox(){
        // 提交表单数据输入框
        TextField beginLabField = new TextField();
        beginLabField.setPromptText("输入起点");
        
        // 初始化按钮布局
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(cancelButton,comfirmButton);

        // 绑定确认事件
        clickConfirmButtonHandle(3, beginLabField);

        // 初始化展示信息布局
        VBox infBox = new VBox(10);
        infBox.getChildren().addAll(
            new Label("起点编号*:"),beginLabField,
            buttonBox
        );
        return infBox;
    }
    
    //  最短回归路径处理
    private void ShortRegressHandle(String beginNumber){
        if(checkNumber(beginNumber)){
            // TODO: 得到合法起始编号，调用最短路径算法计算最短回归得到最短路径
            // List<Edge> backPath = Graph.greedyTSPBack(beginNumber);
            // drawShortestBackPath(backPath);
            commonUI.createInformationAlert("提示", null, "最短路径查找成功！").showAndWait();
        }else{
            commonUI.createWarningAlert("提示", null, "编号包含非法字符！").showAndWait();
        }
    }
    
    // 事件按钮(mapUI,图)
    private Button createActionButton(String text){
        // 初始化事件按钮
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");

        // 设置按钮显示事件
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color:rgba(52, 152, 219, 0.75); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        return button;
    }

    // 事件操作容器(mapUI)
    private HBox createActionBox(VBox UI){
        // 初始化事件容器
        HBox actionBox = new HBox(30);
        actionBox.setAlignment(Pos.CENTER);
        
        // 创建事件按钮
        Button createGraphButton = createActionButton("构建地图");
        Button relaodGraphInfoButton = createActionButton("刷新信息");

        createGraphButton.setOnAction(event -> {
            if(currentStage!=null){
                currentStage.close();
            }
            Graph.setConnectedStatus(false);
            Graph.createGraph(dataIO);
            Stage newStage = createGraphWindow("Graph");
            BorderPane newPane = createGraphPane();
            Scene newScene = createGraphScene(newPane);
            newStage.setScene(newScene);
            currentStage = newStage;
            newStage.show();
        });

        relaodGraphInfoButton.setOnAction(event -> {
            if(UI.getChildren().size()>2){
                //清除重复数据
                UI.getChildren().remove(2, UI.getChildren().size());
            }

            // 初始化详细信息容器 
            HBox detailsBox = createDetailsBox(String.valueOf(alterCount), String.valueOf(villageOfSum), String.valueOf(roadOfSum));
            // 显示容器
            UI.getChildren().add(detailsBox);
        });
        
        actionBox.getChildren().addAll(createGraphButton,relaodGraphInfoButton);
        
        return actionBox;
    }

    // 绘制点和编号
    private void drawPointAndLabel(GraphicsContext gc,long x,long y,String label){

        // 绘制点
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillOval(x-5, y-5, 5, 5);
        
        // 设置文本颜色和字体
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(8));

        // 点右侧标记编号
        gc.fillText(label, x+3, y);
    }

    // 绘制最小连通方案图
    private void drawMiniConnectedPlan(List<Edge> edges){
        // 红色表示新增方案
        gc.setStroke(javafx.scene.paint.Color.RED);
        gc.setLineWidth(2);

        List<Village> villagesList = dataIO.getVillages();

        for(Edge edge:edges){
            Point begin = getPositionByVillageNumber(edge.getBegin(), villagesList);
            Point end = getPositionByVillageNumber(edge.getEnd(), villagesList);
            gc.strokeLine(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
    }

    // 绘制多源最短路径
    private void drawMultiSourceShortestPath(List<Edge> edges){
        // 绿色表示单源最短路径
        gc.setStroke(javafx.scene.paint.Color.GREEN);
        gc.setLineWidth(2);

        List<Village> villagesList = dataIO.getVillages();

        for(Edge edge:edges){
            Point begin = getPositionByVillageNumber(edge.getBegin(), villagesList);
            Point end = getPositionByVillageNumber(edge.getEnd(), villagesList);
            gc.strokeLine(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
    }

    // 绘制最短遍历全图路径
    private void drawShortestPath(List<Edge> edges){
        // 黄色表示最短遍历全图路径
        gc.setStroke(javafx.scene.paint.Color.YELLOW);
        gc.setLineWidth(2);

        List<Village> villagesList = dataIO.getVillages();

        for(Edge edge:edges){
            Point begin = getPositionByVillageNumber(edge.getBegin(), villagesList);
            Point end = getPositionByVillageNumber(edge.getEnd(), villagesList);
            gc.strokeLine(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
    }

    // 绘制最短回归路径
    private void drawShortestBackPath(List<Edge> edges){
        // 蓝色表示最短回归路径
        gc.setStroke(javafx.scene.paint.Color.BLUE);
        gc.setLineWidth(2);

        List<Village> villagesList = dataIO.getVillages();

        for(Edge edge:edges){
            Point begin = getPositionByVillageNumber(edge.getBegin(), villagesList);
            Point end = getPositionByVillageNumber(edge.getEnd(), villagesList);
            gc.strokeLine(begin.getX(), begin.getY(), end.getX(), end.getY());
        }
    }


    // 绘制线
    private void drawLine(GraphicsContext gc,Point start,Point stop){
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(2);
        // 画线
        gc.strokeLine(start.getX(), start.getY(), stop.getX(), stop.getY());
    }
    

    // 根据村庄编号获取坐标
    private Point getPositionByVillageNumber(String villageNumber,List<Village> villagesList){
        for(Village village:villagesList){
            if(village.getNumber().equals(villageNumber)){
                return village.getPositionObject();
            }
        }
        return null;
    }
    

    // 绘制一条路
    private void drawOneRoad(GraphicsContext gc,String start,String stop,List<Village> villagesList){
        // 获取起点和终点坐标
        Point startPosition = getPositionByVillageNumber(start,villagesList);
        Point stopPosition = getPositionByVillageNumber(stop,villagesList);

        if(startPosition==null || stopPosition==null){
            commonUI.createErrorAlert("提示", null, "图构建错误，道路编号错误！").showAndWait();
            return;
        }

        // 绘制线
        drawLine(gc, startPosition, stopPosition);
        
    }
    

    // 绘制
    // TODO: 参数待确认
    private void drawGraph(GraphicsContext gc,long W,long H){
        // 绘制地图背景
        gc.setFill(javafx.scene.paint.Color.LIGHTBLUE);
        gc.fillRect(0, 0, W, H);

        List<Village> villagesList = dataIO.getVillages();
        List<Road> roadsList = dataIO.getRoads();
        
        // 绘制坐标点(动态变化)
        for(Village village:villagesList){
            drawPointAndLabel(gc, village.getPositionObject().getX(), village.getPositionObject().getY(), village.getNumber());
        }

        // 绘制连线（点与点之间连接）
        for(Road road:roadsList){
            drawOneRoad(gc, road.getBeginNumber(), road.getEndNumber(),villagesList);
        }
        
    }

    // 构建图画板
    private Canvas createGraphCanvas(long W,long H){
        canvas.setWidth(W);
        canvas.setHeight(H);
        // 画图
        drawGraph(gc,W,H);

        return canvas;
    }

    // 构建滚动画板
    private ScrollPane createScrollCanvas(long W,long H){
        // 初始化滚动画板
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(W, H);
        
        // 创建画板
        Canvas canvas = createGraphCanvas(2000,2000);
        
        scrollPane.setContent(canvas);
        scrollPane.setFitToHeight(false);
        scrollPane.setFitToWidth(false);

        return scrollPane;
    }

    // 事件按钮容器(图)
    private VBox createGraphActionBox(BorderPane navBar,BorderPane gp){
        // 初始化容器
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(Double.MAX_VALUE);
        
        // 创建事件按钮
        Button reloadButton = createActionButton("刷新图");
        Button multiShortButton = createActionButton("多源最短路径");
        Button visiteAllNodeButton = createActionButton("遍历全图路径");
        Button miniConnecButton = createActionButton("最小连通方案");
        Button shortRegressButton = createActionButton("最短回归路径");

        //TODO: 事件处理绑定
        // 定义取消逻辑
        cancelButton.setOnAction(event -> {
            clickCancelButtonHandle(navBar);
        });

        // 刷新事件处理
        reloadButton.setOnAction(event -> {
            // 获取画板组件
            ScrollPane scrollCanvas = (ScrollPane)gp.getLeft();
            Canvas canvas = (Canvas)scrollCanvas.getContent();
            // 删除旧内容
            GraphicsContext gc = canvas.getGraphicsContext2D();
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            // 重新创建图同时画图
            drawGraph(gc, 2000, 2000);
        });
        // 多源最短路径处理
        multiShortButton.setOnAction(event -> {
            // 清除非首个组件
            navBar.getChildren().remove(1,navBar.getChildren().size());
            // 显示操作(最短路径操作逻辑)
            VBox actionInfBox = createActionInfoBox(multiShortButton.getText());
            VBox infoBox = createMultiShortInfoBox();
            actionInfBox.getChildren().add(infoBox);
            navBar.setCenter(actionInfBox);
        });
        // 遍历全图路径处理
        visiteAllNodeButton.setOnAction(event -> {
            // 清除非首个组件
            navBar.getChildren().remove(1,navBar.getChildren().size());
            // 显示操作(遍历全图操作逻辑)
            VBox actionInfoBox = createActionInfoBox(visiteAllNodeButton.getText());
            VBox infoBox = createVisiteAllInfoBox();
            actionInfoBox.getChildren().add(infoBox);
            navBar.setCenter(actionInfoBox);
        });
        // 最小连通方案处理
        miniConnecButton.setOnAction(event -> {
            if(Graph.isConnected()){
                commonUI.createInformationAlert("提示", null, "图已连通！").showAndWait();
            }else{
                List<Graph.Edge> miniConnectedPlan = Graph.getMiniConnectedEdges();
                drawMiniConnectedPlan(miniConnectedPlan);
                Graph.createGraph(dataIO);
            }
        });
        // 最短回归路径处理
        shortRegressButton.setOnAction(event -> {
            // 清除非首个组件
            navBar.getChildren().remove(1,navBar.getChildren().size());
            // 显示操作(最短回归操作逻辑)
            VBox actionInfoBox = createActionInfoBox(shortRegressButton.getText());
            VBox infoBox = createShortRegressInfoBox();
            actionInfoBox.getChildren().add(infoBox);
            navBar.setCenter(actionInfoBox);
        });

         reloadButton.setMaxWidth(Double.MAX_VALUE); 
         multiShortButton.setMaxWidth(Double.MAX_VALUE);
         visiteAllNodeButton.setMaxWidth(Double.MAX_VALUE);
         miniConnecButton.setMaxWidth(Double.MAX_VALUE);
         shortRegressButton.setMaxWidth(Double.MAX_VALUE);
        
        buttonBox.getChildren().addAll(reloadButton,multiShortButton,visiteAllNodeButton,miniConnecButton,shortRegressButton);
        
        return buttonBox;
    }

    // 事件信息显示容器(图)
    private VBox createActionInfoBox(String text){
        // 初始化信息显示容器
        VBox actionInfBox = new VBox(10);
        actionInfBox.setAlignment(Pos.CENTER);
        actionInfBox.setMaxHeight(200);
        actionInfBox.setStyle("-fx-border: 2px; -fx-border-color: black; -fx-padding: 5px");

        // 标题字段文本
        Text title = new Text(text);
        actionInfBox.getChildren().add(title);

        return actionInfBox;
    }

    // 构建右侧展示区
    private BorderPane createNavBar(BorderPane gp){
        // 初始化右侧展示区，设置对齐方式
        BorderPane navBar = new BorderPane();
        navBar.setMinWidth(200);
        navBar.setStyle("-fx-padding: 10px;");
        
        // 创建事件按钮容器
        VBox actionBox = createGraphActionBox(navBar,gp);
        
        navBar.setTop(actionBox);
        return navBar;
    }

    // 构建图显示布局
    private BorderPane createGraphPane(){
        // 初始化布局
        BorderPane graphPane = new BorderPane();

        // 创建滚动画板
        ScrollPane scrollCanvas = createScrollCanvas(800,750);

        // 创建右侧展示区
        BorderPane navBar = createNavBar(graphPane);

        graphPane.setLeft(scrollCanvas);
        graphPane.setRight(navBar);
        return graphPane;
    }
    

    // 构建图显示场景
    private Scene createGraphScene(BorderPane layout){
        // 使用布局直接初始化场景
        Scene graphScene = new Scene(layout,1000,750);

        return graphScene;
    }

    // 构建图显示窗口
    private Stage createGraphWindow(String title){
        // 初始化窗口
        Stage window = new Stage();
        window.setTitle(title);
        //window.setScene(scene);

        return window;
    }
    
    // 构建map ui控件
    public VBox createUI(){
        // 创建垂直布局
        VBox mapUI = new VBox(10);
        mapUI.setStyle("-fx-padding: 20;");
        mapUI.setAlignment(Pos.CENTER);

        // 创建图
        Graph.createGraph(dataIO);

        // 初始化事件操作容器
        HBox actionBox = createActionBox(mapUI);

        // 初始化信息展示头部(始终显示)
        HBox detailsHeadBox = createDetailsHeadBox();

        // 初始化信息展示区(始终显示--触发刷新)
        HBox detailsBox = createDetailsBox(String.valueOf(alterCount), String.valueOf(villageOfSum), String.valueOf(roadOfSum));
        

        
        mapUI.getChildren().addAll(actionBox,detailsHeadBox,detailsBox);
        return mapUI;
    }

    // 构建map场景
    public Scene createScene(){
        // 初始化ui
        VBox mapUI = createUI();
        // 构建场景
        Scene mapScene = new Scene(mapUI,300,200);
        return mapScene;
    }
}
