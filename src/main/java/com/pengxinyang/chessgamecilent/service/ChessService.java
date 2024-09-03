package com.pengxinyang.chessgamecilent.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengxinyang.chessgamecilent.config.APIConfig;
import com.pengxinyang.chessgamecilent.controller.ChessBoardUIController;
import com.pengxinyang.chessgamecilent.entity.ChessStats;
import com.pengxinyang.chessgamecilent.entity.ResponseResult;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Data
public class ChessService {
    private ChessBoardUIController chessBoardUIController;
    @Getter
    @Setter
    public static ChessStats[][] points;
    public static Integer isRedTurn = 1;//黑红双方轮流下棋
    public static ChessStats selectedChessStats = null;//记录当前选中的棋子名称
    public static ArrayList<ChessStats> allChessStatss = new ArrayList<>();
    private String record;//棋谱
    private Integer roomId = 0;

    public void initialize() {
        chessBoardUIController.getCheckerBoard().getChildren().removeAll();
        allChessStatss.clear();
        record = "";
        chessBoardUIController.getCheckerBoard().getChildren().clear();
        for (ChessStats[] ChessStatss : points = new ChessStats[9][10]) {
            ChessStatss = null;
        }
        isRedTurn = 1;
        selectedChessStats = null;
        //设置背景板
        chessBoardUIController.setBoard();
        //初始化棋子位置
        initPutChessStatss();
        StartButtonOnAction();
    }//初始化

    private void initPutChessStatss() {
        //首先，调用后端初始化数据库的url
        // 使用 CompletableFuture 执行异步操作
        CompletableFuture.runAsync(() -> {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(APIConfig.getApi("/game/start_chess_game?room_id="+roomId)))
                        .header("Content-Type", "application/json")
                        .GET().build();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                ResponseResult responseResult = objectMapper.readValue(response.body(), ResponseResult.class);
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    if (responseResult.getCode() != 200) {
                        showAlert(Alert.AlertType.ERROR, "初始化棋盘失败", "错误发生原因: " + responseResult.getMessage());
                    }
                });
            } catch (Exception e) {
                // 在 JavaFX 应用线程上更新 UI
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.ERROR, "初始化棋盘失败", "错误发生原因: " + e.getMessage());
                });
            }
        });
        //然后，初始化象棋的位置
        initPutPieces();
    }

    private void initPutPieces() //初始化摆放棋子
    {
        //将
        General redShuai = new General(4, 0, 1, roomId);
        General blackJiang = new General(4, 9, 0, roomId);
        //马
        Horse redMa1 = new Horse(2,1, 0, 1, roomId);
        Horse redMa2 = new Horse(8,7, 0, 1, roomId);
        Horse blackMa1 = new Horse(18,1, 9, 0, roomId);
        Horse blackMa2 = new Horse(24,7, 9, 0, roomId);
        //象
        Minister redMinister1 = new Minister(3,2, 0, 1, roomId);
        Minister redMinister2 = new Minister(7,6, 0, 1, roomId);
        Minister blackMinister1 = new Minister(19,2, 9, 0, roomId);
        Minister blackMinister2 = new Minister(23,6, 9, 0, roomId);
        //炮
        Cannon redCannon1 = new Cannon(10,1, 2, 1, roomId);
        Cannon redCannon2 = new Cannon(11,7, 2, 1, roomId);
        Cannon blackCannon1 = new Cannon(26,1, 7, 0, roomId);
        Cannon blackCannon2 = new Cannon(27,7, 7, 0, roomId);
        //兵
        Soldier redSoldier1 = new Soldier(12,0, 3, 1, roomId);
        Soldier redSoldier2 = new Soldier(13,2, 3, 1, roomId);
        Soldier redSoldier3 = new Soldier(14,4, 3, 1, roomId);
        Soldier redSoldier4 = new Soldier(15,6, 3, 1, roomId);
        Soldier redSoldier5 = new Soldier(16,8, 3, 1, roomId);
        Soldier blackSoldier1 = new Soldier(28,0, 6, 0, roomId);
        Soldier blackSoldier2 = new Soldier(29,2, 6, 0, roomId);
        Soldier blackSoldier3 = new Soldier(30,4, 6, 0, roomId);
        Soldier blackSoldier4 = new Soldier(31,6, 6, 0, roomId);
        Soldier blackSoldier5 = new Soldier(32,8, 6, 0, roomId);
        //车
        Car redCar1 = new Car(1,0, 0, 1, roomId);
        Car redCar2 = new Car(9,8, 0, 1, roomId);
        Car blackCar1 = new Car(17,0, 9, 0, roomId);
        Car blackCar2 = new Car(25,8, 9, 0, roomId);
        //士
        Guard redGuard1 = new Guard(4,3, 0, 1, roomId);
        Guard redGuard2 = new Guard(6,5, 0, 1, roomId);
        Guard blackGuard1 = new Guard(20,3, 9, 0, roomId);
        Guard blackGuard2 = new Guard(22,5, 9, 0, roomId);
        chessBoardUIController.getCheckerBoard().getChildren().addAll(allChessStatss);
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void checkerBoardOnPressed(MouseEvent e) //画布的鼠标点击响应函数
    {
        if (selectedChessStats != null) {
            if (e.getButton().equals(MouseButton.SECONDARY)) { //右键取消选中
                selectedChessStats.cancelSelected();
                return;
            }
            int startX = selectedChessStats.x;
            int startY = selectedChessStats.y;
            int targetX = CheckerBoard.pxToX(e.getX());
            int targetY = CheckerBoard.pxToY(e.getY()); //计算目标像素值
            if (Objects.equals(selectedChessStats.color, isRedTurn)) {
                try {
                    ResponseResult responseResult = selectedChessStats.moveChess(targetX, targetY);
                    System.out.println("鼠标点击"+responseResult);
                    if(((Map<String,Object>)responseResult.getData()).isEmpty()){
                        return;
                    }
                    record = record + printRecord(startX, startY, targetX, targetY) + "\t";
                    if (isRedTurn != 1)
                        record = record + "\n";
                    if(roomId == 0){
                        isRedTurn = 1- isRedTurn;                   //回合交替
                    }
                    selectedChessStats.cancelSelected();         //移动后取消选定
                    //playMusic1();
                } catch (Exception ex) {
                           //playMusic2();
                    //selectedPiece.cancelSelected();//否则无法选中 执行完moveto后直接抛出异常cancel掉
                }
            } else {
                //playMusic2();
                selectedChessStats.cancelSelected();
            }
        }
    }
    private String printRecord(int startX, int startY, int dstX, int dstY) //棋谱记录
    {
        String step = selectedChessStats.chessName;
        String start;
        start = getRoad(startX);

        String action, dst;
        if (startY == dstY) {
            action = "平";
            dst = getRoad(dstX);
        } else {
            if (selectedChessStats.color == 1)
                dst = getChineseNumber(Math.abs(startY - dstY));
            else
                dst = Integer.toString(Math.abs(startY - dstY));
            if ((selectedChessStats.color==1 && dstY < startY) || (selectedChessStats.color!=1 && dstY > startY)) {
                action = "进";
            } else action = "退";

        }
        return step + start + action + dst;
    }

    String getChineseNumber(int x) //棋谱记录
    {
        String ret = null;
        switch (x) {
            case 1:
                ret = "一";
                break;
            case 2:
                ret = "二";
                break;
            case 3:
                ret = "三";
                break;
            case 4:
                ret = "四";
                break;
            case 5:
                ret = "五";
                break;
            case 6:
                ret = "六";
                break;
            case 7:
                ret = "七";
                break;
            case 8:
                ret = "八";
                break;
            case 9:
                ret = "九";
                break;
            default:
        }
        return ret;
    }

    private static String getRoad(int x) //棋谱记录
    {
        String road = null;
        if (selectedChessStats.color == 1) {
            switch (x) {
                case 8:
                    road = "一";
                    break;
                case 7:
                    road = "二";
                    break;
                case 6:
                    road = "三";
                    break;
                case 5:
                    road = "四";
                    break;
                case 4:
                    road = "五";
                    break;
                case 3:
                    road = "六";
                    break;
                case 2:
                    road = "七";
                    break;
                case 1:
                    road = "八";
                    break;
                case 0:
                    road = "九";
                    break;
                default:
            }
        } else {
            road = Integer.toString(x + 1);
        }
        return road;
    }

    static void playMusic1() {// 背景音乐播放
        try {
            AudioClip ac = new AudioClip(Objects.requireNonNull(Object.class.getResourceAsStream("/chessAudio/setpiece.wav")).toString());
            ac.play(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void playMusic2() {// 背景音乐播放
        try {
            AudioClip ac = new AudioClip(Objects.requireNonNull(Object.class.getResourceAsStream("/chessAudio/can'tmove.wav")).toString());
            ac.play(1.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveRecordButtonOnAction() throws IOException   //保存棋谱的响应函数
    {
        Date date = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
        File file = new File(ft.format(date) + ".txt");
        synchronized (file) {
            FileWriter fw = new FileWriter(file.getName());
            fw.write(record);
            fw.close();
        }

    }

    public void showRecordButtonOnAction()   //显示棋谱的响应函数
    {
        Alert rec = new Alert(Alert.AlertType.INFORMATION);
        rec.setTitle("棋谱");
        rec.setGraphic(null);
        rec.setHeaderText(null);
        rec.setContentText(record);
        rec.show();
    }

    public void StartButtonOnAction() //开始游戏
    {
        for (ChessStats i : allChessStatss) {
            i.setVisible(true);
        }
    }

    public ChessService(ChessBoardUIController chineseChessController) {
        this.chessBoardUIController = chineseChessController;
    }

}
