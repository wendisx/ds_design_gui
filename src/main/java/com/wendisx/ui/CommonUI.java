package com.wendisx.ui;

import java.util.List;

import com.wendisx.model.Graph.Edge;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class CommonUI {

    // 添加按钮
    public ButtonType yesButton = new ButtonType("yes");
    public ButtonType noButton = new ButtonType("no");
    
    // 提示弹窗(信息)
    public Alert createInformationAlert(String title,String headerText,String contentText){
        Alert informationAlert = new Alert(AlertType.INFORMATION);
        informationAlert.setTitle(title);
        informationAlert.setHeaderText(headerText);
        informationAlert.setContentText(contentText);

        return informationAlert;

    }

    // 提示弹窗(警告)
    public Alert createWarningAlert(String title,String headerText,String contentText){
        Alert warningAlert = new Alert(AlertType.WARNING);
        warningAlert.setTitle(title);
        warningAlert.setHeaderText(headerText);
        warningAlert.setContentText(contentText);
        
        return warningAlert;
    }

    // 提示弹窗(错误)
    public Alert createErrorAlert(String title,String headerText,String contentText){
        Alert erroAlert = new Alert(AlertType.ERROR);
        erroAlert.setTitle(title);
        erroAlert.setHeaderText(headerText);
        erroAlert.setContentText(contentText);
        
        return erroAlert;
    }
    
    // 验证弹窗
    public Alert createConfirmationAlert(String title,String headerText,String contentText){
        // 确认信息弹窗
        Alert confirmationAlert = new Alert(AlertType.CONFIRMATION);
        confirmationAlert.setTitle(title);
        confirmationAlert.setHeaderText(headerText);
        confirmationAlert.setContentText(contentText);

        confirmationAlert.getButtonTypes().setAll(noButton,yesButton);

        return confirmationAlert;
    }

    // 显示最短路径弹窗--多源最短路径
    public void showShortestPathDialog(List<Edge> path,int length,int task){
        // 初始化弹窗
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("详细信息");
        dialog.setHeaderText(null);

        String pathStr = path.get(0).getBegin()+"--->"+path.get(0).getEnd();
        
        for(int i=1;i<path.size();i++){
            pathStr+=("--->"+path.get(i).getEnd());
        }

        Label pathLabel = new Label(pathStr);
        pathLabel.setStyle("-fx-font-size: 14px;");

        Label lengthLabel = new Label("路径长度："+length);
        lengthLabel.setStyle("-fx-font-size: 14px;");

        VBox vBox = new VBox(10,pathLabel);
        if(task==1)vBox.getChildren().add(lengthLabel);

        vBox.setStyle("-fx-padding: 20; -fx-alignment: center;");
        dialog.getDialogPane().setContent(vBox);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);

        dialog.showAndWait();
    }
}
