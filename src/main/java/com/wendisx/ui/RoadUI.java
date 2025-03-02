package com.wendisx.ui;

import java.util.List;
import java.util.Optional;

import com.wendisx.model.Point;
import com.wendisx.model.Road;
import com.wendisx.model.Village;
import com.wendisx.util.DataIO;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class RoadUI {
       
    // 导入通用ui控件
    private CommonUI commonUI = new CommonUI();

    // 导入数据操作类
    private DataIO dataIO = new DataIO();

    // 构造函数
    public RoadUI(DataIO dataIO){
        this.dataIO = dataIO;
    }

    // 获取道路列表
    private List<Road> roads = dataIO.getRoads();

    // 获取总页数
    private int getTotalPage(int sumOfRoads){
        return sumOfRoads%7==0?sumOfRoads/7:sumOfRoads/7+1;
    }

    // 当前页数
    private int currentPage = 1;

    public void setCurrentPage(int page){
        this.currentPage = page;
    }
    // 总页数
    private int totalPage = getTotalPage(roads.size());

    // 确认取消按钮 
    ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);

    // 加载指定窗口内部元素
    private void loadItems(VBox roadUI){
        // 清除旧数据
        roadUI.getChildren().remove(2,roadUI.getChildren().size()-1);
        // 更新元素
        int beginInsert = 2;
        // 计算显示数据长度
        int start = (currentPage-1)*7;
        int stop = start+7>roads.size()?roads.size():start+7;
        // 渲染元素
        for(int i=start;i<stop;i++){
            Road curRoad = roads.get(i);
            roadUI.getChildren().add(beginInsert,createDataBox(String.valueOf(i+1), curRoad.getBeginNumber(), curRoad.getEndNumber(),String.valueOf(curRoad.getRoadLength())));
            beginInsert++;
        }
    } 

    // 分页控件
    private HBox createPageBox(VBox roadUI){
        HBox pageBox = new HBox(220);
        pageBox.setStyle("-fx-padding: 0; -fx-background-color:rgb(244, 244, 244);");
        
        // 定义按钮
        Button nextPageButton = new Button("下一页");
        nextPageButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        Button prePageButton = new Button("上一页");
        prePageButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        

        // 分页信息
        Text pageInfoText = new Text(currentPage+"/"+totalPage);
        pageInfoText.setStyle("-fx-font-size: 14px; -fx-padding: 5px;");
        
        nextPageButton.setOnAction(event -> {
            if(this.currentPage<this.totalPage)this.currentPage++;
            pageInfoText.setText(currentPage+"/"+totalPage);
            loadItems(roadUI);
        });
        nextPageButton.setOnMouseEntered(event -> {
            nextPageButton.setStyle("-fx-background-color:rgba(52, 152, 219, 0.75); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        });
        nextPageButton.setOnMouseExited(event -> {
            nextPageButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        });
        prePageButton.setOnAction(event -> {
            if(this.currentPage>1)this.currentPage--;
            pageInfoText.setText(currentPage+"/"+totalPage);
            loadItems(roadUI);
        });
        prePageButton.setOnMouseEntered(event -> {
            prePageButton.setStyle("-fx-background-color:rgba(52, 152, 219, 0.75); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        });
        prePageButton.setOnMouseExited(event -> {
            prePageButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 5px;");
        });
        pageBox.getChildren().addAll(prePageButton,pageInfoText,nextPageButton);

        return pageBox;
    }

    // 数据标签展示字段
    private Label createDataLabel(String text){
        Label villageLabel = new Label(text);
        villageLabel.setStyle("-fx-font-size: 14px;");
        return villageLabel;
    }

    // 数据字段框(标题)
    private HBox createDataHeadBox(){
        HBox dataHeadBox = new HBox(90);
        dataHeadBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
        // 内容居中
        dataHeadBox.setAlignment(Pos.CENTER);
        // 显示头部字段
        Label numberLabel = createDataLabel("序号");
        Label beginNumberLabel = createDataLabel("起点编号");
        Label endNumberLabel = createDataLabel("终点编号");
        Label roadLengthLabel = createDataLabel("道路长度");
        dataHeadBox.getChildren().addAll(numberLabel,beginNumberLabel,endNumberLabel,roadLengthLabel);
        
        return dataHeadBox;
    }

    // 数据字段框(信息)
    public HBox createDataBox(String number,String beginNumber,String endNumber,String roadLength){
        HBox dataBox = new HBox(125);
        dataBox.setStyle("-fx-padding: 12; -fx-background-color:rgba(172, 170, 170, 0.5);");
        // 内容居中
        dataBox.setAlignment(Pos.CENTER);
        // 显示信息
        Label numberLabel = createDataLabel(number);
        Label beginNumberLabel = createDataLabel(beginNumber);
        Label endNumberLabel = createDataLabel(endNumber);
        Label roadLengthLabel = createDataLabel(roadLength); 
        dataBox.getChildren().addAll(numberLabel,beginNumberLabel,endNumberLabel,roadLengthLabel);
        return dataBox;
    }

     // 新增道路数据模型
    private Road addPostForm(TextField... fields){
        // 新增道路表单数据获取
        String beginNumber = fields[0].getText();
        String endNumber = fields[1].getText();

        // 检查获取值是否为空
        if(beginNumber.isEmpty() || endNumber.isEmpty()){
            commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
            return null;
        }

        return new Road(roads.size(), beginNumber, endNumber);

    }

    // 更新道路数据模型
    private Road updatePostForm(TextField... fields){
        // 更新道路表单数据获取
        String originNumber = fields[0].getText();
        String beginNumber = fields[1].getText();
        String endNumber = fields[2].getText();
        // 判断所有字段
        if(originNumber.isEmpty() || beginNumber.isEmpty() || endNumber.isEmpty()){
            commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
            return null;
        }
        // 判断序号是否是数字格式
        long number = 0;
        if(!originNumber.isEmpty()){
            try {
                // 转换道路序号为数字
                number = Long.parseLong(originNumber);
            } catch (NumberFormatException e) {
                commonUI.createWarningAlert("提示", null, "序号格式错误！").showAndWait();
                return null;
            }
        }
        // 判断序号是否合法
        if(number<0){
            commonUI.createWarningAlert("提示", null, "序号不合法！").showAndWait();
            return null;
        }
        return new Road(number, beginNumber, endNumber);
    }

    // 删除道路和道路详情数据模型
    private Road searchForm(TextField... fields){
        String originNumber = fields[0].getText();
        long number=0;
        // 判断序号是否为空
        if(originNumber.isEmpty()){
            commonUI.createWarningAlert("提示", null, "必填字段不能为空！").showAndWait();
            return null;
        }
        // 判断序号是否合法
        if(!originNumber.isEmpty()){
            try {
                number = Long.parseLong(originNumber);
            } catch (NumberFormatException e) {
                commonUI.createWarningAlert("提示", null, "序号不合法！").showAndWait();
                return new Road(-1,"","");
            }
        }
        if(number<0){
            commonUI.createWarningAlert("提示", null, "序号不能为负值！").showAndWait();
            return new Road(-1,"","");
        }
        return new Road(number, "", "");
    }

    // 检查编号合法性
    private boolean checkNumberIsValid(String villageNumber){
        return villageNumber.matches("[a-zA-Z0-9]+");
    }
    

    // 新增道路处理
    private boolean addRoadHandle(Road road,String text){
        // TODO: 起始和终止道路编号可能非法
        if(road == null){
            return false;
        }

        // 新增道路逻辑(api)
        if(checkNumberIsValid(road.getBeginNumber()) && checkNumberIsValid(road.getEndNumber())){
            // 判断道路序号是否存在
            List<Village> villages = dataIO.getVillages();
            Point begin = null;
            Point end = null;
            for(Village village:villages){
                if(village.getNumber().equals(road.getBeginNumber())){
                    begin = village.getPositionObject();
                }else if(village.getNumber().equals(road.getEndNumber())){
                    end = village.getPositionObject();
                }
            }
            if(begin==null || end==null){
                commonUI.createWarningAlert("提示", null, "村庄编号不存在！").showAndWait();
                return false;
            }
            road.setRoadNumber(roads.size());
            road.setRoadLength(begin, end);
            roads.add(road);
        }else{
            // 编号非法处理
            commonUI.createWarningAlert("提示", null, "编号非法！").showAndWait();
            return false;
        }

        // 更新更改
        MapUI.updateAlterCount();

        // 更新道路条数
        MapUI.setRoadOfSum(roads.size());

        // 新增成功
        commonUI.createInformationAlert("提示", null, "新增道路成功！").showAndWait();
        return true;
    }

    // 搜索指定序号道路信息
    private Road getRoadInfo(long roadNumber){
        // 判断需要道路是否存在
        if(roadNumber<=0 || roadNumber>roads.size()){
            return null;
        }
        return roads.get((int)roadNumber-1);
    }

    // 更新道路处理
    private boolean updateRoadHandle(Road road,String text){
        // TODO: 检查字段完善
        if(road == null){
            return false;
        }

        // 更新道路逻辑(api)
        Road toBeUpdatedRoad = getRoadInfo(road.getRoadNumber());
        if(toBeUpdatedRoad ==null){
            commonUI.createWarningAlert("提示", null, "指定序号道路信息不存在！").showAndWait();
            return false;
        }

        // 判断道路序号是否存在
        List<Village> villages = dataIO.getVillages();
        Point begin = null;
        Point end = null;
        for(Village village:villages){
            if(village.getNumber().equals(road.getBeginNumber())){
                begin = village.getPositionObject();
            }else if(village.getNumber().equals(road.getEndNumber())){
                end = village.getPositionObject();
            }
        }
        if(begin==null || end==null){
            commonUI.createWarningAlert("提示", null, "村庄编号不存在！").showAndWait();
            return false;
        }
        
        // 更新道路信息
        toBeUpdatedRoad.setBeginNumber(road.getBeginNumber());
        toBeUpdatedRoad.setEndNumber(road.getEndNumber());
        toBeUpdatedRoad.setRoadLength(begin, end);

        // 更新变更
        MapUI.updateAlterCount();

        // 更新成功
        commonUI.createInformationAlert("提示", null, "更新道路成功！").showAndWait();
        return true;
    }

    // 删除道路处理
    private boolean deleteRoadHandle(Road road,String text){
        // TODO: 检查字段完善
        if(road.getRoadNumber()<0){
            return false;
        }

        // 删除道路逻辑(api)
        Road toBeDeletedRoad = getRoadInfo(road.getRoadNumber());
        if(toBeDeletedRoad==null){
            commonUI.createWarningAlert("提示", null, "删除道路信息不存在！").showAndWait();
            return false;
        }else{
            roads.remove((int)road.getRoadNumber()-1);
        }

        // 更新变更
        MapUI.updateAlterCount();

        // 更新道路条数
        MapUI.setRoadOfSum(roads.size());

        // 删除成功
        commonUI.createInformationAlert("提示", null, "删除道路成功！").showAndWait();
        return true;
    }

    // 道路详情处理
    private boolean searchRoadHandle(Road road,String text){
        // TODO: 检查字段完善
        if(road.getRoadNumber()<0){
            return false;
        }

        // 搜索道路逻辑(api)
        Road searchRoad = getRoadInfo(road.getRoadNumber());
        if(searchRoad==null){
            commonUI.createWarningAlert("提示", null, "道路信息不存在！").showAndWait();
            return false;
        }
        Stage popStage = new Stage();
        HBox infoHeadBox = createDataHeadBox();
        HBox infoBox = createDataBox(String.valueOf(searchRoad.getRoadNumber()), String.valueOf(searchRoad.getBeginNumber()), String.valueOf(searchRoad.getEndNumber()), String.valueOf(searchRoad.getRoadLength()));
        VBox root = new VBox(10);
        root.setAlignment(Pos.CENTER);
        root.getChildren().addAll(infoHeadBox,infoBox);

        Scene infoScene = new Scene(root,480,200);
        popStage.setScene(infoScene);
        
        popStage.showAndWait();

        // 搜索成功(详情)
        commonUI.createInformationAlert("提示", null, "操作成功！").showAndWait();
        return true;
    }


    // 事件按钮
    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        // 设置按钮点击事件
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color:rgba(52, 152, 219, 0.75); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        button.setOnAction(event -> {
            // 创建道路表单输入控件
            TextField beginNumberField = new TextField();
            beginNumberField.setPromptText("请输入起点村庄编号");
            
            TextField endNumberField = new TextField();
            endNumberField.setPromptText("请输入终点村庄编号");
            
            TextField numberFeild = new TextField();
            numberFeild.setPromptText("请输入道路序号");

            // 创建布局
            VBox formlayout = new VBox(10);
            if(text.equals("新增道路")){
                formlayout.getChildren().addAll(
                    new Label("起点编号*:"),beginNumberField,
                    new Label("终点编号*:"),endNumberField
                );

            }else if(text.equals("删除道路") || text.equals("道路详情")){
                formlayout.getChildren().addAll(
                    new Label("序号*:"),numberFeild
                );
            }else if(text.equals("更新道路")){
                formlayout.getChildren().addAll(
                    new Label("序号*:"),numberFeild,
                    new Label("起点编号*:"),beginNumberField,
                    new Label("终点编号*:"),endNumberField
                );
            }
            // 创建基本弹窗
            Dialog<Road> operationDialog = createBasicDialog(formlayout, text);
            operationDialog.getDialogPane().getButtonTypes().addAll(cancelButtonType,confirmButtonType);
            // 设置返回数据格式
            operationDialog.setResultConverter(dialogButton -> {
                if(dialogButton == confirmButtonType){
                    if(text.equals("新增道路")){
                        return addPostForm(beginNumberField,endNumberField);
                    }else if(text.equals("删除道路") || text.equals("道路详情")){
                        return searchForm(numberFeild);
                    }else if(text.equals("更新道路")){
                         return updatePostForm(numberFeild,beginNumberField,endNumberField);
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }
            });
            // 显示弹窗，等待操作
            boolean isHandled = false;
            while (!isHandled) {
                Optional<Road> selectedOperation = operationDialog.showAndWait();
                if(selectedOperation.isPresent()){
                    Road road = selectedOperation.get();
                    if(road!=null){
                        if(text.equals("新增道路")){
                            isHandled = addRoadHandle(road,text);
                        }else if(text.equals("更新道路")){
                            isHandled = updateRoadHandle(road,text);
                        }else if(text.equals("删除道路")){
                            isHandled = deleteRoadHandle(road,text);
                        }else if(text.equals("道路详情")){
                            isHandled = searchRoadHandle(road,text);
                        }
                    }
                }else{
                    isHandled = true;
                }
            }
            dataIO.setRoads(roads);
        });
        return button;
    }

    // 数据处理弹窗
    private Dialog<Road> createBasicDialog(VBox formlayout,String title){
        Dialog<Road> roadDialog = new Dialog<>();
        roadDialog.setTitle(title);

        roadDialog.getDialogPane().setContent(formlayout);
        return roadDialog;
    }

    // 事件按钮框
    private HBox createActionBox(){
        HBox actionBox = new HBox(45);
        actionBox.setStyle("-fx-padding: 10; -fx-background-color:rgb(244, 244, 244);");
        // 内容居中
        actionBox.setAlignment(Pos.CENTER);
        // 增加增、删、改按钮
        Button addRoadButton = createActionButton("新增道路");
        Button deleteRoadButton = createActionButton("删除道路");
        Button updateRoadButton = createActionButton("更新道路");
        Button searchRoadButton = createActionButton("道路详情");
        // 将按钮加入布局
        actionBox.getChildren().addAll(addRoadButton, deleteRoadButton, updateRoadButton,searchRoadButton);
        return actionBox;
    }

    // 构建road ui控件
    public VBox createUI(){
        // 创建道路垂直布局
        VBox roadUI = new VBox(10);
        roadUI.setStyle("-fx-padding: 20;");
        roadUI.setAlignment(Pos.CENTER);

        // 初始道路条数
        MapUI.setRoadOfSum(roads.size());

        // 加入分页栏
        roadUI.getChildren().add(createPageBox(roadUI));

        // 加入数据标题栏
        roadUI.getChildren().add(createDataHeadBox());

        // 加入初始数据信息
        for(int i=0;i<7;i++){
            Road curRoad = roads.get(i);
            roadUI.getChildren().add(createDataBox(String.valueOf(curRoad.getRoadNumber()), curRoad.getBeginNumber(), curRoad.getEndNumber(), String.valueOf(curRoad.getRoadLength())));
        }
        
        // 加入事件按钮
        roadUI.getChildren().add(createActionBox());

        return roadUI;
    }

    // 创建road场景
    public Scene createScene(){
        // 创建ui和场景
        VBox roadUI = createUI();
        Scene roadScene = new Scene(roadUI,300,200);
        return roadScene;
    }
}
