package com.pengxinyang.chessgamecilent.service;

import com.pengxinyang.chessgamecilent.entity.ChessStats;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.Objects;

/**
 * 老将
 */
class General extends ChessStats{

    public General(int x, int y, Integer color, Integer roomId){
        super(5,x,y,color,"帅", roomId);
        if(color != 1) {
            this.chessName = "将";
            this.cid = 21;
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RK.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BK.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RKS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BKS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
    @Override
    public void die() {
        super.die();
        String winner;
        if (this.color==1)
            winner = "黑";
        else
            winner = "红";
        Alert gameOverAlert = new Alert(Alert.AlertType.CONFIRMATION);
        gameOverAlert.setTitle("游戏结束");
        gameOverAlert.setHeaderText(null);
        gameOverAlert.setContentText(winner + "方获胜");
        gameOverAlert.setGraphic(null);
        gameOverAlert.showAndWait();
        ChessService.isRedTurn = -1;
    }
}

class Guard extends ChessStats{
    public Guard(int cid, int x, int y, Integer color, Integer roomId){
        super(cid,x,y,color,"士", roomId);
        if(color != 1) {
            this.chessName = "仕";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }

    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RA.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BA.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RAS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BAS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}

class Minister extends ChessStats{
    public Minister(int cid, int x, int y, Integer color, Integer roomId){
        super(cid,x,y,color,"相", roomId);
        if(color != 1) {
            this.chessName = "象";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RB.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BB.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RBS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BBS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}

class Horse extends ChessStats{
    public Horse(int cid, int x, int y, Integer color, Integer roomId){
        super(cid,x,y,color,"马", roomId);
        if(color != 1) {
            this.chessName = "馬";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RN.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BN.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RNS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BNS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}

class Car extends ChessStats{
    public Car(int cid, int x, int y, Integer color, Integer roomId){
        super(cid,x,y,color,"车", roomId);
        if(color != 1) {
            this.chessName = "車";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RR.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BR.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RRS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BRS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}

class Soldier extends ChessStats{
    public Soldier(int cid, int x, int y, Integer color,Integer roomId){
        super(cid,x,y,color,"兵", roomId);
        if(color != 1) {
            this.chessName = "卒";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RP.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BP.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RPS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BPS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}

class Cannon extends ChessStats{
    public Cannon(int cid, int x, int y, Integer color, Integer roomId){
        super(cid,x,y,color,"炮", roomId);
        if(color != 1) {
            this.chessName = "砲";
        }
        loadImage();
        setOnMousePressed(e -> {     //被点击的事件处理
            if (ChessService.selectedChessStats == null) {
                beSelected();
                this.getParent().requestFocus();
            }
        });
    }
    @Override
    public void loadImage() {
        Image image;
        if (!isSelected) {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RC.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BC.GIF")));
        } else {
            if (color == 1)
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/RCS.GIF")));
            else
                image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/chessImages/BCS.GIF")));
        }
        setFill(new ImagePattern(image));
    }
}