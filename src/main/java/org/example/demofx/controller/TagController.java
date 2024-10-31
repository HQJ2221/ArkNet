package org.example.demofx.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import org.example.demofx.model.Tag;

import java.io.IOException;

public class TagController {
    @FXML
    private Label tagName;
    @FXML
    private Circle tagIcon;
    @FXML
    private HBox TagContainer;
    private boolean isSelected = false;

    public void setTag(Tag tag) {
        tagName.setText(tag.getName());
        tagIcon.setFill(Paint.valueOf("#" + tag.getColor()));
    }

    public void resetTag() {
        isSelected = false;
        TagContainer.setStyle("");
    }

    public void handleTagClick(MouseEvent event) throws IOException {
        //在界面右边打开书签
        if (!isSelected) {
            if(tagName.getText().equals("Untag")) {
                MainView.getMainView().onClickMainResetTagsButton();
                isSelected = true;
            }else if(!(MainView.getMainView().getStatus() == 5 || MainView.getMainView().getStatus() == 15)){
                isSelected = true;
            }
        }else {
            isSelected = false;
        }
        if (isSelected) {
            TagContainer.setStyle("-fx-background-color:Grey;" + "-fx-text-fill: BLUE; ");
        } else {
            TagContainer.setStyle("");
        }
        MainController.getController().tagSelected(tagName.getText(), isSelected);
    }
}
