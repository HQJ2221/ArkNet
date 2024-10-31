package org.example.demofx.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.example.demofx.MainApp;
import org.example.demofx.model.BookMark;

import java.util.Arrays;
import java.util.List;

public class BookmarkListView {

    private List<BookMark> bookmarks;
    @FXML
    private VBox BookmarkListVBox;

    private MainView mainView;

    public void initialize() {

    }

    public void addBookmark(BookMark bookmark) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookmarkView.fxml"));
            AnchorPane root = loader.load();
            BookmarkView controller = loader.getController();
            controller.setBookmark(bookmark);
            BookmarkListVBox.getChildren().add(root);
            //controller.loadWebIcon(bookmark);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainView(MainView mainView) {
        this.mainView = mainView;
    }
}