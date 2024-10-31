package org.example.demofx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SecurityQuestionController implements Initializable {

    @FXML
    private Label question;
    @FXML
    private TextField questionAnswerInputField;
    @FXML
    private TextField resetPasswordInputField;

    @FXML
    void handleConfirmAction() {
//        if(mainController.checkAnswer(questionAnswerInputField.getText())){
//            mainController.setPassword(resetPasswordInputField.getText());
//            answerOutputLabel.setText("Password is reset successfully");
//        }else{
//            answerOutputLabel.setText("answer is wrong");
//        }

        if (MainController.getController().checkAnswer(questionAnswerInputField.getText())) {
            if (resetPasswordInputField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Password is empty");
                alert.showAndWait();
            } else {
                MainController.getController().setPassword(resetPasswordInputField.getText());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Information Dialog");
                alert.setHeaderText(null);
                alert.setContentText("Password is reset successfully");
                alert.showAndWait();
                Stage stage = (Stage) questionAnswerInputField.getScene().getWindow();
                stage.close();
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error Dialog");
            alert.setHeaderText(null);
            alert.setContentText("Answer is wrong");
            alert.showAndWait();
        }
    }

    @FXML
    private Button cancelButton;

    @FXML
    void handleCancelAction() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        question.setText(MainController.getController().getQuestion());
    }
}
