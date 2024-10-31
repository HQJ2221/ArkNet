package org.example.demofx.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.demofx.model.Tag;

import java.io.IOException;

public class TagAddController {
    @FXML
    private Button addButton;
    @FXML
    private Button cancelButton;
    @FXML
    private TextField tagNameInputField;
    @FXML
    private CheckBox isPrivateCheckBox;

    private static ObservableList<Node> tagItems = MainView.getMainView().tagItems;

    public void handleAddAction(ActionEvent actionEvent) {
        if (tagNameInputField.getText().equals("Untagged") || tagNameInputField.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Add Failed");
            alert.setContentText("Tag Name cannot be NULL or Untag");
            alert.showAndWait();
            return;
        }
        try {
            if (MainController.getController().addTag(tagNameInputField.getText(), isPrivateCheckBox.isSelected())) {
                MainView.getMainView().refreshMainView();
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Add Failed");
                alert.setContentText("Tag with same name already exists");
                alert.showAndWait();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void handleDeleteAction(ActionEvent actionEvent) throws IOException {
        Tag deletedTag = DBController.getDBController().getTagByName(tagNameInputField.getText());
        if(MainController.getController().deleteTag(deletedTag)) {
            MainView.getMainView().refreshMainView();
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        }else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Delete Failed");
            alert.setContentText("Tag not exists");
            alert.showAndWait();
        }
    }

    public void handleCancelButton(ActionEvent actionEvent) {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

}
