package org.example.demofx.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.example.demofx.MainApp;

import java.io.IOException;


public class SettingsController{


    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;


    @FXML
    private TextField oldPasswordField;
    @FXML
    private TextField newPasswordField;

    private boolean isWhite = true;
    @FXML
    private AnchorPane settingsPage;
    @FXML
    private Label settingsLabel;
    @FXML
    private Label themeLabel;
    @FXML
    private  Label oldPassWordLabel;
    @FXML
    private Label languageLabel;
    @FXML
    private Label aboutUsLabel;
    @FXML
    private Label newPasswordLabel;
    @FXML
    private ImageView settingsImageView;
    @FXML
    private ImageView forgetPasswordView;
    @FXML
    private Label outputLabel;

    //修改的部分
    @FXML
    private ToggleButton themeToggleButton;
    @FXML
    private Circle thumb; // 创建一个圆形滑块
    @FXML
    private StackPane thumbContainer;

    //修改的部分

    @FXML
    public void handleThemeToggleAction(){
        if(isWhite){
            //变成黑色主题
            settingsPage.setStyle("-fx-background-color: black;");
            settingsLabel.setTextFill(Color.WHITE);
            themeLabel.setTextFill(Color.WHITE);
            oldPassWordLabel.setTextFill(Color.WHITE);
            newPasswordLabel.setTextFill(Color.WHITE);
            languageLabel.setTextFill(Color.WHITE);
            aboutUsLabel.setTextFill(Color.WHITE);
            settingsImageView.setImage(new Image(getClass().getResourceAsStream("/icons/设置齿轮_白.png")));
            forgetPasswordView.setImage(new Image(getClass().getResourceAsStream("/icons/问号_白色.png")));
            confirmButton.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: black; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 5 12 5 12; " +
                            "-fx-font-size: 14px; " +
                            "-fx-effect: dropshadow( three-pass-box, rgba(0,0,0,0.8), 5, 0.0, 0, 1);"
            );
            cancelButton.setStyle(
                    "-fx-background-color: white; " +
                            "-fx-text-fill: black; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 5 12 5 12; " +
                            "-fx-font-size: 14px; " +
                            "-fx-effect: dropshadow( three-pass-box, rgba(0,0,0,0.8), 5, 0.0, 0, 1);"
            );
            //创建动画实例
            TranslateTransition transition = new TranslateTransition(Duration.millis(200), thumb);
            transition.setToX(20);
            transition.play();
        }else {
            //变成白色主题
            settingsPage.setStyle("-fx-background-color: white;");
            settingsLabel.setTextFill(Color.BLACK);
            themeLabel.setTextFill(Color.BLACK);
            oldPassWordLabel.setTextFill(Color.BLACK);
            newPasswordLabel.setTextFill(Color.BLACK);
            languageLabel.setTextFill(Color.BLACK);
            aboutUsLabel.setTextFill(Color.BLACK);
            settingsImageView.setImage(new Image(getClass().getResourceAsStream("/icons/设置齿轮_黑.png")));
            forgetPasswordView.setImage(new Image(getClass().getResourceAsStream("/icons/问号_黑.png")));
            confirmButton.setStyle(
                    "-fx-background-color: #3baae7; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 5 12 5 12; " +
                            "-fx-font-size: 14px; " +
                            "-fx-effect: dropshadow( three-pass-box, rgba(0,0,0,0.8), 5, 0.0, 0, 1);"
            );
            cancelButton.setStyle(
                    "-fx-background-color: #3baae7; " +
                            "-fx-text-fill: white; " +
                            "-fx-background-radius: 10px; " +
                            "-fx-padding: 5 12 5 12; " +
                            "-fx-font-size: 14px; " +
                            "-fx-effect: dropshadow( three-pass-box, rgba(0,0,0,0.8), 5, 0.0, 0, 1);"
            );
            //创建动画实例
            TranslateTransition transition = new TranslateTransition(Duration.millis(200), thumb);
            transition.setToX(0);
            transition.play();
        }
        isWhite = !isWhite;
    }

    @FXML
    public void handleConfirmAction() {
        String oldTryPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();
        try {
            if(!oldTryPassword.isEmpty()) {
                if (MainController.getController().checkPassword(oldTryPassword)) {
                    MainController.getController().setPassword(newPassword);
                    Stage stage = (Stage) confirmButton.getScene().getWindow();
                    stage.close();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Modify success", ButtonType.OK);
                    alert.setTitle("Tips");
                    alert.showAndWait();
                } else {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Modify Failed", ButtonType.OK);
                    alert.setTitle("Tips");
                    // 显示对话框
                    alert.showAndWait();
                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Old password cannot be empty", ButtonType.OK);
                alert.setTitle("Tips");
                // 显示对话框
                alert.showAndWait();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    public void handleCancelAction(){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void handleForgetPasswordAction(javafx.scene.input.MouseEvent mouseEvent) {
        try {
            Stage forgetPassword = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecurityQuestion.fxml"));

//            FXMLLoader fxmlLoader = new FXMLLoader(SettingsApp.class.getResource("TagAddView.fxml"));

            Scene scene = new Scene(fxmlLoader.load(), 362, 476);
            forgetPassword.setTitle("SecurityQuestion");
            forgetPassword.setScene(scene);
            forgetPassword.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}