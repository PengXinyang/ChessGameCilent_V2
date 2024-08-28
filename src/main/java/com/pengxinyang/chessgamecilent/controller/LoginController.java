package com.pengxinyang.chessgamecilent.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengxinyang.chessgamecilent.config.APIConfig;
import com.pengxinyang.chessgamecilent.entity.ResponseResult;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;
    @FXML
    private Button registerButton;
    @FXML
    private AnchorPane loginAnchorPane;

    @FXML
    public void initialize() {
        loginButton.setOnAction(event -> login());
        registerButton.setOnAction(this::register);
        setBackgroundImage();
        buttonStyle();
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
        loginAnchorPane.setBackground(new Background(background));
        // 设置AnchorPane的宽度和高度为图片的宽度和高度
        loginAnchorPane.setPrefWidth(backgroundImage.getWidth());
        loginAnchorPane.setPrefHeight(backgroundImage.getHeight());
    }
    @FXML
    private void login() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        // 构建POST请求
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("account", username);
        requestBody.put("password", password);
        // 使用 CompletableFuture 执行异步操作
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBodyJson = objectMapper.writeValueAsString(requestBody);
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/user/login")))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                        .build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ResponseResult responseResult = objectMapper.readValue(response.body(), ResponseResult.class);

                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (responseResult.getCode() == 200) {
                        try {
                            Map<String, Object> map = objectMapper.convertValue(responseResult.getData(), new TypeReference<>() {});
                            showAlert(Alert.AlertType.INFORMATION, "登录成功", "欢迎回来! " + map.get("user_name"));
                        } catch (Exception e) {
                            showAlert(Alert.AlertType.ERROR, "登录失败", "错误发生原因: " + e.getMessage());
                        }
                        // 关闭登录窗口
                        Stage stage = (Stage) loginButton.getScene().getWindow();
                        stage.close();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "登录失败", "错误发生原因: " + responseResult.getMessage());
                    }
                });
            } catch (Exception e) {
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "登录失败", "错误发生原因: " + e.getMessage());
                });
            }
        });
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
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
        registerButton.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                // 设置按钮的背景颜色为木头棕色，字体颜色为白色
                registerButton.setStyle(
                        "-fx-background-color: #8B4513;" +  // 背景颜色为木头棕色
                                "-fx-text-fill: white;" +           // 字体颜色为白色
                                "-fx-background-radius: 10;" +      // 设置圆角
                                "-fx-font-size: 14px;"              // 字体大小
                );
            }
        });
    }

    @FXML
    private void register( ActionEvent event) {
        try {
            Stage stage = (Stage) ((javafx.scene.control.Button) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/com/pengxinyang/chessgamecilent/register.fxml"))));
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
