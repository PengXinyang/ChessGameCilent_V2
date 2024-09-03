package com.pengxinyang.chessgamecilent.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengxinyang.chessgamecilent.config.APIConfig;
import com.pengxinyang.chessgamecilent.config.GameWebSocketClient;
import com.pengxinyang.chessgamecilent.config.UserConfig;
import com.pengxinyang.chessgamecilent.entity.ResponseResult;
import com.pengxinyang.chessgamecilent.entity.Room;
import com.pengxinyang.chessgamecilent.entity.User;
import com.pengxinyang.chessgamecilent.entity.UserSession;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

public class RoomSearchController {

    @FXML
    private TextField searchField;

    @FXML
    private ListView<String> roomListView;
    private Integer roomId;

    @FXML
    private TextField joinRoomField;

    @FXML
    private Button searchButton;

    @FXML
    private Button joinButton;
    @FXML
    private AnchorPane rootPane;
    private Alert waitingAlert;

    // 初始化方法
    @FXML
    public void initialize() {
        // 初始化代码
    }

    // 处理搜索房间逻辑
    @FXML
    private void handleSearch() {
        AtomicReference<ResponseResult> responseResult = new AtomicReference<>(new ResponseResult());
        String roomName = searchField.getText();
        roomListView.getItems().clear();
        // TODO: 调用后端API或处理搜索逻辑，更新roomListView显示搜索结果
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/get/room_id/by_name") + "?name=" + roomName))
                        .header("Content-Type", "application/json")
                        .GET()  // 使用 GET 请求方法
                        .build();
                // 发送请求
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                ObjectMapper objectMapper = new ObjectMapper();
                // 解析响应为 ResponseResult 对象
                responseResult.set(objectMapper.readValue(response.body(), ResponseResult.class));
                ResponseResult result = responseResult.get();
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (responseResult.get().getCode() != 200) {
                        showAlert(Alert.AlertType.ERROR,"获取房间列表出错",result.getMessage());
                    } else {
                        Map<String,Object> resultMap = (Map<String, Object>) responseResult.get().getData();
                        this.roomId = (Integer) resultMap.get("room_id");
                        List<String> RoomName = (List<String>) resultMap.get("room_name");
                        List<String> RedUserName = (List<String>) resultMap.get("red_name");
                        List<String> blackUserName = (List<String>) resultMap.get("black_name");
                        for (int i = 0; i < RoomName.size(); i++) {
                            String roomInfo = "房间Id名称："+RoomName.get(i)+
                                    "      红方："+RedUserName.get(i)+
                                    "      黑方："+blackUserName.get(i)+"\n";
                            roomListView.getItems().add(roomInfo);
                        }
                    }
                });
            }catch (Exception e) {
                showAlert(Alert.AlertType.ERROR,"获取房间列表出错",e.getMessage());
            }
        }).join();
    }

    // 处理加入房间逻辑
    @FXML
    private void handleJoinRoom() {
        String roomName = searchField.getText();
        // TODO: 调用后端API执行加入房间逻辑
        CompletableFuture.runAsync(() -> {
            try{
                // 获取token和user对象
                String token = UserSession.getInstance().getToken();
                User user = UserSession.getInstance().getUser();
                //处理websocket
                GameWebSocketClient webSocketClient = LoginController.gameWebSocketClient;
                webSocketClient.setRoomSearchController(this);
                // 创建请求体，假设这里是表单格式
                // 创建请求体，假设是表单格式
                String requestBody = String.format("room_name=%s&uid=%d", roomName,user.getUid());
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/join/room")))
                        .header("Authorization", "Bearer " + token) // 如果需要身份验证，可以添加 Authorization 头
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                // 发送请求
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                //System.out.println("发送后解析前");
                // 解析响应为 ResponseResult 对象
                ObjectMapper objectMapper = new ObjectMapper();
                AtomicReference<ResponseResult> responseResult = new AtomicReference<>(objectMapper.readValue(response.body(), ResponseResult.class));
                ResponseResult result = responseResult.get();

                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (result.getCode() != 200) {
                        showAlert(Alert.AlertType.ERROR, "加入房间出错", result.getMessage());
                    } else {
                        // 调用后端API成功，开始WebSocket连接监听
                        System.out.println(result);
                        LinkedHashMap<String, Object> roomMap = (LinkedHashMap<String, Object>) result.getData();
                        // 使用 ObjectMapper 将 map 转换为 Room 对象
                        Room room = objectMapper.convertValue(roomMap, Room.class);
                        // 显示无法关闭的“正在匹配”弹窗
                        waitingAlert = new Alert(Alert.AlertType.INFORMATION);
                        waitingAlert.setTitle("正在匹配");
                        waitingAlert.setHeaderText(null);
                        waitingAlert.setContentText("正在等待其他玩家加入...");
                        waitingAlert.getButtonTypes().clear(); // 清除默认按钮使弹窗无法关闭
                        // 创建 "退出等待" 按钮
                        ButtonType exitButton = new ButtonType("退出等待", ButtonBar.ButtonData.CANCEL_CLOSE);
                        // 添加按钮到 Alert 弹窗
                        waitingAlert.getButtonTypes().setAll(exitButton);
                        webSocketClient.setRoomSearchController(this);
                        // 设置按钮的点击事件
                        waitingAlert.setOnCloseRequest(event -> {
                            // 执行退出房间逻辑
                            int userId = user.getUid(); // 替换为实际的用户ID
                            String requestBody1 = String.format("room_name=%s&uid=%d", roomName, userId);

                            HttpRequest httpRequest1 = HttpRequest.newBuilder()
                                    .uri(URI.create("http://localhost:8081/exit/room"))
                                    .header("Authorization", "Bearer " + token) // 如果需要身份验证，可以添加 Authorization 头
                                    .header("Content-Type", "application/x-www-form-urlencoded")
                                    .POST(HttpRequest.BodyPublishers.ofString(requestBody1, StandardCharsets.UTF_8))
                                    .build();

                            // 异步发送请求
                            CompletableFuture.runAsync(() -> {
                                try {
                                    HttpClient client1 = HttpClient.newHttpClient();
                                    HttpResponse<String> response1 = client1.send(httpRequest1, HttpResponse.BodyHandlers.ofString());
                                    // 处理响应逻辑
                                    AtomicReference<ResponseResult> responseResult1 = new AtomicReference<>(objectMapper.readValue(response1.body(), ResponseResult.class));
                                    ResponseResult result1 = responseResult.get();
                                    if (result1.getCode() != 200) {
                                        Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "退出房间出错", result1.getMessage()));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Platform.runLater(() -> {
                                        showAlert(Alert.AlertType.ERROR, "退出房间出错", e.getMessage());
                                    });
                                }
                            });
                        });
                        waitingAlert.show();
                    }
                });
            }catch (Exception e) {
                Platform.runLater(() ->showAlert(Alert.AlertType.ERROR,"加入房间出现错误",e.getMessage()));
            }
        }).join();
    }

    // 处理服务器消息
    public void handleServerMessage(String message, Integer roomId) {
        try {
            Platform.runLater(()->{
                waitingAlert.close();
            });
            ObjectMapper objectMapper = new ObjectMapper();
            // 将 JSON 字符串解析为 JsonNode 对象
            JsonNode rootNode = objectMapper.readTree(message);

            // 检查是否有 "房间已准备好" 这个字段
            if (rootNode.has("房间已准备好")) {
                JsonNode roomNode = rootNode.get("房间已准备好");

                // 根据需要处理 roomNode，这里可以访问具体的字段
                Integer parsedRoomId = roomNode.get("roomId").asInt();
                String roomName = roomNode.get("roomName").asText();

                // 如果需要根据特定条件处理事件
                if (parsedRoomId.equals(roomId)) {
                    startGame(roomId);
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // 跳转到游戏界面
    private void startGame(Integer roomId) {
        System.out.println("Room is ready, starting the game...");
        // 实现跳转到游戏界面的逻辑
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/pengxinyang/chessgamecilent/chess_board_ui.fxml"));
            Parent gameRoot = loader.load();
            ChessBoardUIController controller = loader.getController();

            // 传递 roomId 参数
            controller.setRoomId(roomId);

            // 创建一个新的场景并将其设置到当前的 Stage
            Scene gameScene = new Scene(gameRoot);

            // 获取当前的 Stage
            Stage currentStage = (Stage) rootPane.getScene().getWindow();  // 使用 rootPane 获取 Stage

            // 确保 currentStage 不为 null
            if (currentStage != null) {
                currentStage.setScene(gameScene);
                currentStage.setTitle("象棋游戏开始：红方： "+ UserConfig.postId + " 黑方： "+UserConfig.acceptId);
                currentStage.show();
            } else {
                System.out.println("currentStage is null");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCreate() {
        // 假设房间名输入在一个 TextField 中
        String roomName = searchField.getText(); // createRoomField 是用户输入房间名的 TextField

        CompletableFuture.runAsync(() -> {
            try {
                // 获取 token 和 user 对象
                String token = UserSession.getInstance().getToken();
                User user = UserSession.getInstance().getUser();

                // 创建请求体，假设是表单格式
                String requestBody = String.format("room_name=%s", roomName);
                HttpRequest httpRequest = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/create/room")))
                        .header("Authorization", "Bearer " + token) // 如果需要身份验证，可以添加 Authorization 头
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                        .build();

                // 发送请求
                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                // 解析响应为 ResponseResult 对象
                ObjectMapper objectMapper = new ObjectMapper();
                ResponseResult result = objectMapper.readValue(response.body(), ResponseResult.class);

                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (result.getCode() != 200) {
                        showAlert(Alert.AlertType.ERROR, "创建房间出错", result.getMessage());
                    } else {
                        // 创建房间成功，跳转到房间界面
                        Room room = (Room) result.getData();
                        startGame(room.getRoomId());
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "创建房间出现错误", e.getMessage()));
            }
        });
    }
}
