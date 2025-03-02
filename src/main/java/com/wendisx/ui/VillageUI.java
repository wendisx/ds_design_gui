package com.wendisx.ui;

import java.util.List;
import java.util.Optional;

import com.wendisx.model.Point;
import com.wendisx.model.Village;
import com.wendisx.util.DataIO;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * 村庄信息组件类
 */
public class VillageUI {

    // 导入数据工具类
    private DataIO dataIO = new DataIO();
    // 构造函数
    public VillageUI(DataIO dataIO){
        this.dataIO = dataIO;
    }

    // 导入通用组件实例
    private CommonUI commonUI = new CommonUI();

    // 获取村庄数据
    private List<Village> villages = dataIO.getVillages();    

    // 获取总页数
    private int getTotalPage(int sumOfVillages){
        return sumOfVillages%7==0?sumOfVillages/7:sumOfVillages/7+1;
    }

    // 分页显示当前页
    private int currentPage = 1;

    public void setCurrentPage(int page){
        this.currentPage = page;
    }

    // 分页显示总页数
    private int totalPage = getTotalPage(villages.size());

    // 确认取消按钮
    ButtonType confirmButtonType = new ButtonType("确认", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButtonType = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
    
    // 加载指定窗口内部元素
    private void loadItems(VBox villageUI){
        // 清除旧数据
        villageUI.getChildren().remove(2,villageUI.getChildren().size()-1);
        // 更新元素
        int beginInsert = 2;
        // 计算显示数据长度
        int start = (currentPage-1)*7;
        int stop = start+7>villages.size()?villages.size():start+7;
        // 渲染元素
        for(int i=start;i<stop;i++){
            Village curVillage = villages.get(i);
            villageUI.getChildren().add(beginInsert,createDataBox(String.valueOf(i+1), curVillage.getNumber(), curVillage.getName(), curVillage.getPosition()));
            beginInsert++;
        }
    }
    

    // 分页栏
    private HBox createPageBox(VBox villageUI){
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
            // 页数+1
            if(this.currentPage<this.totalPage)this.currentPage++;
            pageInfoText.setText(currentPage+"/"+totalPage);
            loadItems(villageUI);
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
            loadItems(villageUI);
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

    // 数据展示字段标签
    private Label createDataLabel(String text){
        Label villageLabel = new Label(text);
        villageLabel.setStyle("-fx-font-size: 14px;");
        return villageLabel;
    }   

    // 数据字段框(标题)
    private HBox createDataHeadBox(){
        HBox dataHeadBox = new HBox(110);
        dataHeadBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
        // 内容居中
        dataHeadBox.setAlignment(Pos.CENTER);
        // 显示头部字段
        Label number = createDataLabel("序号");
        Label villageNumber = createDataLabel("编号");
        Label villageName = createDataLabel("名称");
        Label villagePosition = createDataLabel("坐标");
        dataHeadBox.getChildren().addAll(number,villageNumber,villageName,villagePosition);
        
        return dataHeadBox;
    }

    // 数据字段框(信息)
    public HBox createDataBox(String number,String villageNumber,String villageName,String villagePosition){
        HBox dataBox = new HBox(108);
        dataBox.setStyle("-fx-padding: 10; -fx-background-color:rgba(172, 170, 170, 0.5);");
        dataBox.setAlignment(Pos.CENTER);

        // 显示信息
        Label numberLabel = createDataLabel(number);
        Label villageNumberLabel = createDataLabel(villageNumber);
        Label villageNameLabel = createDataLabel(villageName);
        Label villagePositionLabel = createDataLabel(villagePosition); 
        dataBox.getChildren().addAll(numberLabel,villageNumberLabel,villageNameLabel,villagePositionLabel);
        return dataBox;
    }

    // 新增村庄和更新村庄数据格式(上传数据格式)
    private Village postForm(TextField... fields){
        String number = fields[0].getText();
        String name = fields[1].getText();
        String positionText = fields[2].getText();
        if(number.isEmpty() || name.isEmpty() || positionText.isEmpty()){
            commonUI.createWarningAlert("提示", null, "必填选项不能为空！").showAndWait();
            return null;
        }
        String description = fields[3].getText();
        String[] positionParts = positionText.split(",");
        if (positionParts.length != 2) {
            commonUI.createWarningAlert("提示", null, "位置格式错误！").showAndWait();
            return new Village("", "", new Point(0,0), null);
        }
        try {
            int x = Integer.parseInt(positionParts[0].trim());
            int y = Integer.parseInt(positionParts[1].trim());
            Point position = new Point(x, y);
            return new Village(number, name, position, description);
        } catch (NumberFormatException e) {
            commonUI.createWarningAlert("提示", null, "位置必须是数字！").showAndWait();
            return new Village("", "", new Point(0, 0), null);
        }
    }

    // 删除村庄和村庄详情数据格式(删除查找数据格式)
    private Village searchForm(TextField... fields){
        String number = fields[0].getText();
        if(number.isEmpty()){
            commonUI.createWarningAlert("提示", null,"必填选项不能为空！").showAndWait();
            return null;
        }
        return new Village(number,"",new Point(0, 0),null);
    }

    // 添加村庄处理
    private boolean addVillageHandle(Village village,String text){
        // 检查空字段
        if(village.getNumber().isEmpty() || village.getName().isEmpty() || village.getPosition().isEmpty()){
            return false;
        }
        
        // TODO: 执行新增一个村庄的处理逻辑(api)
        villages.add(village);
        
        // 更新变更
        MapUI.updateAlterCount();

        // 图数据更新
        MapUI.setVillageOfSum((long)villages.size());
        
        // 新增成功
        commonUI.createInformationAlert(text, null, "新增村庄成功！").showAndWait();
        return true;
    }

    // 查找指定编号村庄
    private Village checkVillageNumber(String villageNumber){
        for(Village village:villages){
            // 找到相同编号的村庄
            if(villageNumber.equals(village.getNumber())){
                return village;
            }
        }
        return null;
    }

    // 更新村庄处理
    private boolean updateVillageHandle(Village village,String text){
        // 检查空字段
        if(village.getNumber().isEmpty() || village.getName().isEmpty() || village.getPosition().isEmpty()){
            return false;
        }

        // TODO: 优化如何做到更新编号？
        Village toBeUpdateVillage = checkVillageNumber(village.getNumber());
        if(toBeUpdateVillage==null){
            commonUI.createWarningAlert("提示", null, "更新村庄原信息不存在！").showAndWait();
            return false;
        }else{
            // 更新信息
            toBeUpdateVillage.setName(village.getName());
            toBeUpdateVillage.setDescription(village.getDescription());
            toBeUpdateVillage.setPosition(village.getPositionObject());
        }

        // 更新变更
        MapUI.updateAlterCount();
        
        // 更新成功
        commonUI.createInformationAlert(text, null, "更新村庄成功！").showAndWait();
        return true;
    }

    // 查找指定编号村庄索引
    private int checkVillageNumberIndex(String villageNumber){
        for(int i=0;i<villages.size();i++){
            // 找到指定编号索引地村庄，返回索引
            if(villageNumber.equals(villages.get(i).getNumber())){
                return i;
            }
        }
        return -1;
    }

    // 删除村庄处理
    private boolean deleteVillageHandle(Village village,String text){
        // 检查
        if(village.getNumber().isEmpty()){
            return false;
        }

        // TODO: 执行删除村庄处理逻辑
        int toBeDeletedVillageIndex = checkVillageNumberIndex(village.getNumber()); 
        if(toBeDeletedVillageIndex==-1){
            commonUI.createWarningAlert("提示", null, "村庄原信息不存在！").showAndWait();
            return false;
        }else{
            villages.remove(toBeDeletedVillageIndex);
        }

        // 更新变更
        MapUI.updateAlterCount();

        // 更新村庄数量
        MapUI.setVillageOfSum(villages.size());
        
        // 删除成功
        commonUI.createInformationAlert(text, null,"删除村庄成功！").showAndWait();
        return true;
    }

    // 村庄详情处理
    private boolean searchVillageHandle(Village village,String text){
        // 检查有效字段
        if(village.getNumber().isEmpty()){
            return false;
        }

        // TODO: 执行搜索处理逻辑(api)
        Village searchVillage = checkVillageNumber(village.getNumber());
        if(searchVillage==null){
            commonUI.createWarningAlert("提示", null, "查找村庄信息不存在！").showAndWait();
            return false;
        }else{
            Stage popStage = new Stage();
            HBox villageInfoHeadBox = new HBox(100);
            HBox villageDescriptionHeadBox = new HBox(100);
            HBox infoBox = new HBox(110);
            villageInfoHeadBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
            villageDescriptionHeadBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(76, 231, 228, 0.74);");
            infoBox.setStyle("-fx-padding: 5; -fx-background-color:rgba(155, 152, 152, 0.8);");
            villageInfoHeadBox.setAlignment(Pos.CENTER);
            villageDescriptionHeadBox.setAlignment(Pos.CENTER);
            infoBox.setAlignment(Pos.CENTER);
            Label numberLabel = createDataLabel("村庄编号");
            Label namLabel = createDataLabel("村庄名称");
            Label positionLabel = createDataLabel("村庄坐标");
            Label descriptionLabel = createDataLabel("村庄简介");

            Label number = createDataLabel(searchVillage.getNumber());
            Label name = createDataLabel(searchVillage.getName());
            Label position = createDataLabel(searchVillage.getPosition());
            String description = searchVillage.getDescription();

            VBox root = new VBox(20);
            
            TextArea descArea = new TextArea();
            descArea.setWrapText(true);
            descArea.setEditable(false);
            descArea.setPrefHeight(100);
            descArea.setPrefWidth(150);
            descArea.setText(description);

            
            villageInfoHeadBox.getChildren().addAll(numberLabel,namLabel,positionLabel);
            villageDescriptionHeadBox.getChildren().add(descriptionLabel);
            infoBox.getChildren().addAll(number,name,position);
            root.getChildren().addAll(villageInfoHeadBox,infoBox,villageDescriptionHeadBox,descArea);
            Scene detailsScene = new Scene(root,400,300);
            popStage.setScene(detailsScene);
            popStage.showAndWait();
        }
        
        // 搜索成功
        commonUI.createInformationAlert(text, null, "操作成功！").showAndWait();
        return true;
    }
        

    // 事件按钮
    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        // 设置按钮显示事件
        button.setOnMouseEntered(event -> {
            button.setStyle("-fx-background-color:rgba(52, 152, 219, 0.75); -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        button.setOnMouseExited(event -> {
            button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 6px;");
        });
        button.setOnAction(event -> {
            // 创建村庄表单输入控件
            TextField numberField = new TextField();
            numberField.setPromptText("请输入村庄编号");

            TextField nameField = new TextField();
            nameField.setPromptText("请输入村庄名称");

            TextField positionField = new TextField();
            positionField.setPromptText("请输入村庄位置(格式: x,y)");

            TextField descriptionField = new TextField();
            descriptionField.setPromptText("请输入村庄描述");
            // 创建布局
            VBox formlayout = new VBox(10);
            if(text.equals("新增村庄") || text.equals("更新村庄")){
                formlayout.getChildren().addAll(
                    new Label("编号*:"),numberField,
                    new Label("名称*:"),nameField,
                    new Label("位置*:"),positionField,
                    new Label("描述"),descriptionField
                );

            }else if(text.equals("删除村庄") || text.equals("村庄详情")){
                formlayout.getChildren().addAll(
                    new Label("村庄编号*:"),numberField
                );
            }
            // 创建基本弹窗
            Dialog<Village> operationDialog = createBasicDialog(formlayout, text);
            operationDialog.getDialogPane().getButtonTypes().addAll(cancelButtonType,confirmButtonType);
            // 设置返回数据格式
            operationDialog.setResultConverter(dialogButton -> {
                if(dialogButton == confirmButtonType){
                    Village village = null;
                    if(text.equals("新增村庄") || text.equals("更新村庄")){
                        village = postForm(numberField,nameField,positionField,descriptionField);
                    }else if(text.equals("删除村庄") || text.equals("村庄详情")){
                        village = searchForm(numberField);
                    }
                    return village;
                }else{
                    return null;
                }
            });
            // 显示弹窗，等待操作
            boolean isHandled = false;
            while (!isHandled) {
                Optional<Village> selectedOperation = operationDialog.showAndWait();
                if(selectedOperation.isPresent()){
                    Village village = selectedOperation.get();
                    if(village!=null){
                        if (text.equals("新增村庄")) {
                            isHandled = addVillageHandle(village, text);
                        } else if (text.equals("更新村庄")) {
                            isHandled = updateVillageHandle(village, text);
                        } else if (text.equals("删除村庄")) {
                            isHandled = deleteVillageHandle(village, text);
                        } else if (text.equals("村庄详情")) {
                            isHandled = searchVillageHandle(village,text);
                        }
                    }
                }else{
                    isHandled = true;
                }
            }
            dataIO.setVillages(villages);
        });
        return button;
    }

    // 数据处理弹窗
    private Dialog<Village> createBasicDialog(VBox formlayout,String title){
        Dialog<Village> villageDialog = new Dialog<>();
        villageDialog.setTitle(title);

        villageDialog.getDialogPane().setContent(formlayout);
        return villageDialog;
    }

    // 事件按钮框
    private HBox createActionBox(){
        HBox actionBox = new HBox(45);
        actionBox.setStyle("-fx-padding: 10; -fx-background-color:rgb(244, 244, 244);");
        // 内容居中
        actionBox.setAlignment(Pos.CENTER);
        // 增加增、删、改按钮
        Button addVillageButton = createActionButton("新增村庄");
        Button deleteVillageButton = createActionButton("删除村庄");
        Button updateVillageButton = createActionButton("更新村庄");
        Button searchVillageButton = createActionButton("村庄详情");
        // 将按钮加入布局
        actionBox.getChildren().addAll(addVillageButton, deleteVillageButton, updateVillageButton,searchVillageButton);
        return actionBox;
    }

    // 构建village ui控件
    public VBox createUI() {
        // 创建垂直布局
        VBox villageUI = new VBox(10);
        villageUI.setStyle("-fx-padding: 20;");
        villageUI.setAlignment(Pos.CENTER);
        
        // 加入分页栏控件
        villageUI.getChildren().add(createPageBox(villageUI));

        // 加入数据框头控件
        villageUI.getChildren().add(createDataHeadBox());

        // 更新图村庄数据
        MapUI.setVillageOfSum((long)villages.size());

        // 加入初始数据
        for(int i=0;i<7;i++){
            Village curVillage = villages.get(i);
            villageUI.getChildren().add(createDataBox(String.valueOf(i+1),curVillage.getNumber(),curVillage.getName(),curVillage.getPosition()));
        }

        // 加入事件按钮控件
        villageUI.getChildren().add(createActionBox());
        
        // 返回布局
        return villageUI;  
    }

    // 创建village场景
    public Scene createScene() {
        // 创建 UI 和场景
        VBox villageUI = createUI();
        Scene villageScene = new Scene(villageUI, 300, 200);
        return villageScene;
    }
}
