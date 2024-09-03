module com.pengxinyang.chessgamecilent {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires lombok;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires jdk.compiler;
    requires com.fasterxml.jackson.databind;
    requires java.net.http;
    requires javafx.media;
    requires Java.WebSocket;
    requires org.json;

    opens com.pengxinyang.chessgamecilent to javafx.fxml;
    exports com.pengxinyang.chessgamecilent;
    exports com.pengxinyang.chessgamecilent.controller;
    opens com.pengxinyang.chessgamecilent.controller to javafx.fxml;
    exports com.pengxinyang.chessgamecilent.entity;
    exports com.pengxinyang.chessgamecilent.config;
}