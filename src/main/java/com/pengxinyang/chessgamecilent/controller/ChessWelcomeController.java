package com.pengxinyang.chessgamecilent.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class ChessWelcomeController {
    @FXML
    public AnchorPane anchorPane;
    public Button startButton;
    @FXML
    private Button loginButton;
    @FXML
    private ImageView imageView;
    @FXML
    private Button joinButton;

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengxinyang/chessgamecilent/chess_board_ui.fxml"));
            Parent root = loader.load();

            // 获取控制器
            ChessBoardUIController controller = loader.getController();

            // 传递 roomId 参数
            controller.setRoomId(0);
            Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("象棋游戏开始");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> showLoginWindow());
        setBackgroundImage();
        buttonStyle();
        setTitleImage();
    }
    private void setTitleImage(){
        Image image = new Image(Objects.requireNonNull(getClass().getResource("/img/title.png")).toString());
        imageView.setImage(image);
    }
    private void buttonStyle(){
        // 使用监听器确保 Scene 已经存在
        loginButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // 设置按钮的背景颜色为木头棕色，字体颜色为白色
                loginButton.setStyle(
                        "-fx-background-color: #8B4513;" +  // 背景颜色为木头棕色
                                "-fx-text-fill: white;" +           // 字体颜色为白色
                                "-fx-background-radius: 10;" +      // 设置圆角
                                "-fx-font-size: 14px;"              // 字体大小
                );
            }
        });
        startButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // 设置按钮的背景颜色为木头棕色，字体颜色为白色
                startButton.setStyle(
                        "-fx-background-color: #8B4513;" +  // 背景颜色为木头棕色
                                "-fx-text-fill: white;" +           // 字体颜色为白色
                                "-fx-background-radius: 10;" +      // 设置圆角
                                "-fx-font-size: 14px;"              // 字体大小
                );
            }
        });
        joinButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // 设置按钮的背景颜色为木头棕色，字体颜色为白色
                joinButton.setStyle(
                        "-fx-background-color: #D2B48C;" +  // 背景颜色为木头棕色
                                "-fx-text-fill: white;" +           // 字体颜色为白色
                                "-fx-background-radius: 10;" +      // 设置圆角
                                "-fx-font-size: 14px;"              // 字体大小
                );
            }
        });
    }

    private void setBackgroundImage() {
        // 加载图片
        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResource("/img/chess_welcome.png")).toString());
        // 创建背景图像
        BackgroundImage background = new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,   // 图片不重复
                BackgroundRepeat.NO_REPEAT,   // 图片不重复
                BackgroundPosition.CENTER,    // 图片居中
                new BackgroundSize(100, 100, true, true, false, true) // 自适应大小
        );
        anchorPane.setBackground(new Background(background));
        // 设置AnchorPane的宽度和高度为图片的宽度和高度
        anchorPane.setPrefWidth(backgroundImage.getWidth());
        anchorPane.setPrefHeight(backgroundImage.getHeight());
    }

    private void showLoginWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengxinyang/chessgamecilent/login.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("登录界面");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void joinRoom(ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pengxinyang/chessgamecilent/room.fxml"))));
            stage.setScene(scene);
            stage.setTitle("房间查询");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}