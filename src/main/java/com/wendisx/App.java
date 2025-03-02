package com.wendisx;

import com.wendisx.ui.MainUI;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 创建 MainUI 实例
        MainUI mainUI = new MainUI();
        
        // 获取场景并显示主窗口
        Scene mainScene = mainUI.createScene();
        primaryStage.setTitle("Vcoroad");
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        // 加载运行参数
        launch(args);
    }
}
