package com.pengxinyang.chessgamecilent.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengxinyang.chessgamecilent.config.APIConfig;
import com.pengxinyang.chessgamecilent.config.GameWebSocketClient;
import com.pengxinyang.chessgamecilent.controller.LoginController;
import com.pengxinyang.chessgamecilent.service.CheckerBoard;
import com.pengxinyang.chessgamecilent.service.ChessService;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.shape.Rectangle;
import lombok.Getter;
import lombok.Setter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

import static com.pengxinyang.chessgamecilent.config.UserConfig.acceptId;
import static com.pengxinyang.chessgamecilent.config.UserConfig.postId;

@Setter
public abstract class ChessStats extends Rectangle {
    public Integer cid;//棋子id
    public Integer x;//坐标x
    public Integer y;//坐标y
    public Integer color;//颜色
    public Integer ate = 0;//标记是否被吃掉。1 是已经被吃掉
    public boolean isSelected = false;//是否被选中
    public String chessName;//哪种棋子
    public Integer roomId =0;
    @Setter
    @Getter
    private GameWebSocketClient gameWebSocketClient=null;

    public ChessStats(int cid, int x, int y, Integer color, String chessName, Integer roomId) {
        setWidth(4.0 * CheckerBoard.UNIT / 5.0);
        setHeight(4.0 * CheckerBoard.UNIT / 5.0);
        this.cid = cid;
        this.x = x;
        this.y = y;
        this.color = color;
        this.chessName = chessName;
        this.roomId = roomId;
        setX(CheckerBoard.xToPx(x) - getWidth() / 2);
        setY(CheckerBoard.yToPx(y) - getHeight() / 2);
        ChessService.points[x][y] = this;
        ChessService.allChessStatss.add(this);
        setVisible(false);

    } //所有棋子构造时都应调用父类的这个构造函数

    public void beSelected() {
        isSelected = true;
        ChessService.selectedChessStats = this;
        loadImage();
    }
    public void cancelSelected() {
        isSelected = false;
        ChessService.selectedChessStats = null;
        loadImage();
    }
    public abstract void loadImage();

    /**
     * 移动到某个位置
     *
     * @param dstX 移动到横坐标
     * @param dstY 纵坐标
     */
    public ResponseResult moveChess(int dstX, int dstY) throws CanNotMoveToException{
        if (Math.abs(dstX - x) + Math.abs(dstY - y) < 1)
            throw new CanNotMoveToException();
        AtomicReference<ResponseResult> responseResult = new AtomicReference<>(new ResponseResult());
        CompletableFuture.runAsync(() -> {
            try {
                // 创建 HttpClient
                HttpClient client = HttpClient.newHttpClient();

                // 构建请求 URI，将参数附加到 URL 中
                String uri = String.format("http://localhost:8081/move/chess?cid=%d&x=%d&y=%d&room_id=%d", cid, dstX, dstY, roomId);

                // 构建 HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(uri))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.noBody())
                        .build();

                // 发送请求并获取响应
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // 解析响应为 ResponseResult 对象
                ObjectMapper objectMapper = new ObjectMapper();
                responseResult.set(objectMapper.readValue(response.body(), ResponseResult.class));
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (responseResult.get().getCode() != 200) {
                        responseResult.get().setData("重新移动");
                        showAlert(Alert.AlertType.ERROR, "移动棋子失败", "错误发生原因: " + responseResult.get().getMessage());
                        //System.out.println("第一个"+responseResult);
                    } else {
                        Map<String,Integer> map = (Map<String, Integer>) responseResult.get().getData();
                        gameWebSocketClient = LoginController.gameWebSocketClient;
                        System.out.println("判断是否将军："+Objects.equals(responseResult.get().getMessage(), "将军"));
                        if(gameWebSocketClient!=null){
                            gameWebSocketClient.sendChessMove(cid,roomId,x,y,dstX,dstY,
                                    postId,
                                    acceptId,
                                    Objects.equals(responseResult.get().getMessage(), "将军"),
                                    map.get("eat")
                            );
                        }
                        changeChess(map,dstX,dstY);
                        if(Objects.equals(responseResult.get().getMessage(), "将军")){
                            showAlert(Alert.AlertType.INFORMATION,"将军！","只需一步！");
                        }
                    }
                });
            } catch (Exception e) {
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "移动棋子失败", "错误发生原因: " + e.getMessage());
                });
            }
        }).join();
        return responseResult.get();
    }
    public void changeChess(Map<String,Integer> map,int dstX,int dstY){
        if(map.get("eat") == 1 && ChessService.points[dstX][dstY]!=null){
            //说明吃了原来的XY棋子
            ChessService.points[dstX][dstY].die();
        }
        // 更新棋盘UI或执行其他操作
        ChessService.points[x][y] = null;
        ChessService.points[dstX][dstY] = this;
        x = dstX;
        y = dstY;
        //目标点变成当前棋子
        this.setX(CheckerBoard.xToPx(dstX) - getWidth() / 2);
        this.setY(CheckerBoard.yToPx(dstY) - getHeight() / 2);
    }
    public void isDie(){
        AtomicReference<ResponseResult> responseResult = new AtomicReference<>(new ResponseResult());
        CompletableFuture.runAsync(() -> {
            try {
                // 创建 HttpClient
                HttpClient client = HttpClient.newHttpClient();
                Map<String, Integer> requestData = new HashMap<>();
                requestData.put("cid", cid);
                ObjectMapper objectMapper = new ObjectMapper();
                String requestBodyJson = objectMapper.writeValueAsString(requestData);

                // 构建 HttpRequest
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/get/die")))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBodyJson, StandardCharsets.UTF_8))
                        .build();

                // 发送请求并获取响应
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                // 解析响应为 ResponseResult 对象
                responseResult.set(objectMapper.readValue(response.body(), ResponseResult.class));
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (responseResult.get().getCode() != 200) {
                        showAlert(Alert.AlertType.ERROR, "获取棋子死亡状态失败", "错误发生原因: " + responseResult.get().getMessage());
                    } else {
                        // 更新棋盘UI或执行其他操作
                        ate = (Integer) responseResult.get().getData();
                        ChessService.points[x][y] = null;
                        setVisible(false);
                        setDisable(true);
                        //如果是老将，宣告游戏结束
                        if(chessName.equals("将")){
                            showAlert(Alert.AlertType.INFORMATION, "恭喜黑方获胜","黑方大获全胜！！！");
                        }
                        else if(chessName.equals("帅")){
                            showAlert(Alert.AlertType.INFORMATION, "恭喜红方获胜","红方大获全胜！！！");
                        }
                    }
                });
            } catch (Exception e) {
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "移动棋子失败", "错误发生原因: " + e.getMessage());
                });
            }
        });
    }
    public void die() {
        ChessService.points[x][y] = null;
        setVisible(false);
        setDisable(true);
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

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
    public static class CanNotMoveToException extends Exception {
    }
}