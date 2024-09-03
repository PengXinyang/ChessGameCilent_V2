package com.pengxinyang.chessgamecilent.controller;

import com.pengxinyang.chessgamecilent.service.ChessService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.Data;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

@Data
public class ChessBoardUIController {
    private double xOffset = 0;
    private double yOffset = 0;
    private Integer roomId = 0;

    static public int girdW = 81;
    static public int canvasW = 9*girdW;
    static public int girdH = 78;
    static public int canvasH = 10*girdH;

    public Pane checkerBoard;
    public GridPane checkerBoardGrid;
    public Button startButton;
    public Button reStartButton;
    public Button showRecordButton;
    public Button saveRecordButton;
    private ChessService chineseChessService = new ChessService(this);

    private void initView() {
        chineseChessService.initialize();
    }

    private void initEvent() {
    }

    private void initService() {
    }

    @FXML
    private void StartButtonOnAction(ActionEvent event) {
        chineseChessService.StartButtonOnAction();
    }

    @FXML
    private void restartButtonOnAction(ActionEvent event) {//重新开始按钮的响应函数
        chineseChessService.initialize();
    }

    @FXML
    private void showRecordButtonOnAction(ActionEvent event) {
        chineseChessService.showRecordButtonOnAction();
    }

    @FXML
    private void saveRecordButtonOnAction(ActionEvent event) throws Exception {
        chineseChessService.saveRecordButtonOnAction();
    }

    @FXML
    private void checkerBoardOnPressed(MouseEvent e) { //画布的鼠标点击响应函数
        chineseChessService.checkerBoardOnPressed(e);
    }

    public void setRoomId(Integer roomId){
        this.roomId = roomId;
        //System.out.println("在controller层设置roomId："+roomId);
        chineseChessService.setRoomId(roomId);
        initView();
        initEvent();
        initService();
        setBoard();
    }

    @FXML
    public void initialize() {

        //reveal();
    }

    public void setBoard(){
        Image img = new Image(Objects.requireNonNull(getClass().getResource("/img/chess_board.png")).toString());
        checkerBoard.setBackground(new Background(new BackgroundImage(img, null, null, null, null)));
        checkerBoard.setMinWidth(canvasW);  // Set appropriate width
        checkerBoard.setMinHeight(canvasH); // Set appropriate height
        checkerBoardGrid.setMinWidth(canvasW);
        checkerBoardGrid.setMinHeight(canvasH+30);
    }

    private void reveal(){
        Image chessChessStatsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BA.gif")));
        ImageView chessChessStatsView = new ImageView(chessChessStatsImage);
        chessChessStatsView.setFitWidth(40);  // 设置图片的宽度
        chessChessStatsView.setFitHeight(40); // 设置图片的高度
        //canvas.getChildren().add(chessChessStatsView);
        GridPane.setRowIndex(chessChessStatsView, 0); // 设置棋子的位置行
        GridPane.setColumnIndex(chessChessStatsView, 0); // 设置棋子的位置列
        chessChessStatsView.setOnMouseDragged(event -> {
            chessChessStatsView.setLayoutX(event.getSceneX() - chessChessStatsView.getFitWidth() / 2);
            chessChessStatsView.setLayoutY(event.getSceneY() - chessChessStatsView.getFitHeight() / 2);
        });
    }
    private void drag(){
        // Set up mouse drag event for window movement
        checkerBoardGrid.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        checkerBoardGrid.setOnMouseDragged(event -> {
            Stage stage = (Stage) checkerBoardGrid.getScene().getWindow();
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }
}
