module org.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;
    requires org.controlsfx.controls;
    requires org.jsoup;

    opens org.example.demofx to javafx.fxml;
    exports org.example.demofx;
    exports org.example.demofx.controller;
    exports org.example.demofx.model;
    opens org.example.demofx.controller to javafx.fxml;

    requires com.google.gson;
    requires java.compiler;
    opens org.example.demofx.model to com.google.gson;
}