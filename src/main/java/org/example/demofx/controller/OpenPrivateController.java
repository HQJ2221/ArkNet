package org.example.demofx.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class OpenPrivateController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button confirmButton;
    @FXML
    private Button cancelButton;
    @FXML
    ImageView forgetPasswordView;

    public void handleConfirmAction() {
        try {
            if (MainController.getController().checkPassword(passwordField.getText())) {

                //打开私密书签（mainController）
                MainView.getMainView().setStatus(10);
                MainView.getMainView().inversePrivateButton(); // switch button text
                DBController.getDBController().setPublic(false);

                MainView.getMainView().refreshMainView();
                Stage stage = (Stage) confirmButton.getScene().getWindow();
                stage.close();
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password Correct!", ButtonType.OK);
                alert.setTitle("Tips");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password Error!\nInput again or Reset Password", ButtonType.OK);
                alert.setTitle("Tips");

                // 显示对话框
                alert.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleCancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public void handleForgetPasswordAction(MouseEvent mouseEvent) {
        try {
            Stage forgetPassword = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SecurityQuestion.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 362, 476);
            forgetPassword.setTitle("SecurityQuestion");
            forgetPassword.setScene(scene);

            forgetPassword.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
