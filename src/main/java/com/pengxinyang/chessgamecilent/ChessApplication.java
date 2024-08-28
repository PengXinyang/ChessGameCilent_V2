package com.pengxinyang.chessgamecilent;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Objects;


public class ChessApplication extends Application {
/*    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource("chess_welcome.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1440, 720);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }*/

    static public int girdW = 81;
    static public int canvasW = 9*girdW;
    static public int girdH = 78;
    static public int canvasH = 10*girdH;
    static public String colorR = "R";
    static public String colorB = "B";
    static public Boolean init = true;
    static public double chessW = 18.0;
    static public String fontName = "STSong";
    // 用于存储鼠标点击时的初始位置
    private double xOffset = 0;
    private double yOffset = 0;

    //static  public Chess[][] Chesses = new Chess[10][9];
    static public  String[][] names = {
            {"車","馬","象","士","將","士","象","馬","車"},
            {"","","","","","","","",""},
            {"","砲","","","","","","砲",""},
            {"卒","","卒","","卒","","卒","","卒"},
            {"","","","","","","","",""},
            {"","","","","","","","",""},
            {"兵","","兵","","兵","","兵","","兵"},
            {"","炮","","","","","","炮",""},
            {"","","","","","","","",""},
            {"車","馬","相","仕","帥","仕","相","馬","車"},
    };
    //static public Chess selectChess = null;
    static public Double fontSize = 18.0;
    @Override
    public void start(Stage primaryStage) throws IOException {
        // 设置窗口为无边框
        FXMLLoader fxmlLoader = new FXMLLoader(ChessApplication.class.getResource("chess_welcome.fxml"));
        AnchorPane anchorPane = fxmlLoader.load();
        primaryStage.setTitle("欢迎来到中国象棋");
        //primaryStage.setScene(new Scene(anchorPane, canvasW, canvasH));
        primaryStage.setScene(new Scene(anchorPane));
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResource("/img/ChessLogo.png")).toString()));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch();
    }
}