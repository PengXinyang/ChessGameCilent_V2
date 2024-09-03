package com.pengxinyang.chessgamecilent;

import com.pengxinyang.chessgamecilent.controller.LoginController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Objects;


public class ChessApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        // 设置窗口为无边框
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource("chess_welcome.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        primaryStage.setTitle("欢迎来到中国象棋");
        primaryStage.setScene(new Scene(anchorPane));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/img/ChessLogo.png")).toString()));
        // 添加窗口关闭事件监听器
        primaryStage.setOnCloseRequest((WindowEvent event) -> {
            if (LoginController.gameWebSocketClient != null && LoginController.gameWebSocketClient.isOpen()) {
                LoginController.gameWebSocketClient.close();
                System.out.println("WebSocket connection closed.");
            }
        });
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}