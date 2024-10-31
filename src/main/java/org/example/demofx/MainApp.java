package org.example.demofx;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.demofx.controller.DBController;
import org.example.demofx.controller.MainController;
import org.example.demofx.controller.MainView;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        MainController mainController = MainController.getController();
        DBController.getDBController();
        mainController.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}