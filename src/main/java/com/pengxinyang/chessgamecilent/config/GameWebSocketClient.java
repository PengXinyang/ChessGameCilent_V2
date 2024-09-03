package com.pengxinyang.chessgamecilent.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengxinyang.chessgamecilent.controller.RoomSearchController;
import com.pengxinyang.chessgamecilent.entity.*;
import com.pengxinyang.chessgamecilent.service.ChessService;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class GameWebSocketClient extends WebSocketClient {
    private final ObjectMapper objectMapper;
    @Getter
    @Setter
    private RoomSearchController roomSearchController = null;
    @Getter
    @Setter
    private Integer room = 0;

    public GameWebSocketClient(URI serverUri) {
        super(serverUri);
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("Connected to server");
    }

    @Override
    public void onMessage(String message) {
        if(message.contains("房间已准备好")){
            room = 1;
        }
        else{
            room = 0;
        }
        if(room == 0){
            //说明不是room的websocket
            // 处理接收到的消息
            System.out.println("Received message: " + message);
            Map<String,Integer> data = new HashMap<>();
            // 解析 JSON 字符串为 Map 对象
            try {
                data = objectMapper.readValue(message, Map.class);
                System.out.println("处理象棋移动接受信息: "+data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            // 在 JavaFX 应用线程中更新 UI
            Map<String, Integer> finalData = data;
            Platform.runLater(() -> {
                // 根据收到的消息更新游戏状态或其他 UI 元素
                updateGameState(finalData);
            });
        }
        if(room == 1){
            // 使用 TypeReference 来指定目标类型
            Map<String, Room> data;
            try {
                data = objectMapper.readValue(message, new TypeReference<>() {
                });
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            // 获取房间ID
            Room room1 = data.get("房间已准备好");
            Integer roomId = room1.getRoomId();
            UserConfig.postId = UserSession.getInstance().getUser().getUid();
            if(UserConfig.postId.equals(room1.getUidRed())){
                UserConfig.acceptId = room1.getUidBlack();
            }
            else if(UserConfig.postId.equals(room1.getUidBlack())){
                UserConfig.acceptId = room1.getUidRed();
            }
            // 使用 Platform.runLater 来处理UI更新
            Platform.runLater(() -> roomSearchController.handleServerMessage(message, roomId));
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("Disconnected from server: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    // 更新游戏状态的方法（根据具体实现需求调整）
    private void updateGameState(Map<String,Integer> message) {
        // 解析消息并更新游戏状态
        // 例如：更新棋盘、显示聊天信息等
        Integer fromX = message.get("fromX");
        Integer fromY = message.get("fromY");
        Integer toX = message.get("toX");
        Integer toY = message.get("toY");
        Integer isJiangJun = message.get("isJiangJun");
        if(ChessService.points[fromX][fromY] != null){
            ChessService.points[fromX][fromY].changeChess(message,toX,toY);
            if(isJiangJun == 1){
                showAlert(Alert.AlertType.INFORMATION,"将军！","只需一步！");
            }
        }
        ChessService.isRedTurn = 1-ChessService.isRedTurn;
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        if(title.equals("将军！")){
            alert.setHeaderText("将军！");
        }
        else if(alertType == Alert.AlertType.ERROR){
            alert.setHeaderText(null);
        }
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void sendChessMove(int cid, int roomId, int fromX, int fromY, int toX, int toY, int postId, int acceptId, boolean isJiang, int eat) {
        // 获取CommandType的code值
        int commandCode = CommandType.Chess.getCode();
        int isJiangJun = 0;
        if(isJiang) isJiangJun = 1;
        // 创建 ChessMove 对象并设置属性
        ChessMove chessMove = new ChessMove();
        chessMove.setCid(cid);
        chessMove.setRoomId(roomId);
        chessMove.setFromX(fromX);
        chessMove.setFromY(fromY);
        chessMove.setToX(toX);
        chessMove.setToY(toY);
        chessMove.setPostId(postId);
        chessMove.setAcceptId(acceptId);
        chessMove.setIsJiangJun(isJiangJun);
        chessMove.setEat(eat);

        // 创建包含命令和棋子移动信息的 JSON 字符串
        String message = String.format("{\"code\":%d, \"cid\":%d, \"roomId\":%d, \"fromX\":%d, \"fromY\":%d, \"toX\":%d, \"toY\":%d, \"postId\":%d, \"acceptId\":%d, \"isJiangJun\":%d, \"eat\":%d}",
                commandCode, chessMove.getCid(), chessMove.getRoomId(), chessMove.getFromX(), chessMove.getFromY(), chessMove.getToX(), chessMove.getToY(), chessMove.getPostId(), chessMove.getAcceptId(), chessMove.getIsJiangJun(), chessMove.getEat());
        // 发送消息
        this.send(message);
        System.out.println("Sent message: " + message);
    }

}

