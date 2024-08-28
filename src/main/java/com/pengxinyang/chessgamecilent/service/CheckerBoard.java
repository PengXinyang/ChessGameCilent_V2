package com.pengxinyang.chessgamecilent.service;

public class CheckerBoard {
    //棋盘的一些常量
    public static final int UNIT = 79;//一个棋格大小
    public static final int LEFT_X = 35;//距左边界
    public static final int RIGHT_X = LEFT_X + 8 * UNIT;//距右边界
    public static final int TOP_Y = 30;//距上边界
    public static final int BOTTOM_Y = TOP_Y + 9 * UNIT;//距下边界

    //将棋盘上的坐标转化为像素值
    public static double xToPx(int x) {
        return LEFT_X + x * UNIT;
    }

    public static double yToPx(int y) {
        return TOP_Y + y * UNIT;
    }

    //将像素值转化为最近的坐标值
    public static int pxToX(double x_Px) {
        double t = (x_Px - LEFT_X) / UNIT;
        long ans = Math.round(t);
        return (int) ans;
    }

    public static int pxToY(double y_Px) {
        double t = (y_Px - TOP_Y) / UNIT;
        long ans = Math.round(t);
        return (int) ans;
    }
}
