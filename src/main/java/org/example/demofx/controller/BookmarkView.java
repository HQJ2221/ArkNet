package org.example.demofx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.demofx.model.BookMark;
import org.jsoup.Jsoup;

import javax.lang.model.util.Elements;
import javax.swing.text.Document;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;


public class BookmarkView {

    @FXML
    private Label bookmarkTitleLabel;
    @FXML
    private Label bookmarkUrlLabel;
    @FXML
    private Label bookmarkDescriptionLabel;
    @FXML
    private ImageView bookmarkIcon;
    @FXML
    private Button bookmarkMoveUpButton;
    @FXML
    private Button bookmarkMoveDownButton;
    @FXML
    private Button bookmarkEditButton;

    private MainController mainController;

    private long id;

    private BookMark bookmark;

    public void initialize() {
        mainController=MainController.getController();
        int status=MainView.getMainView().getStatus();
        //System.out.println(status);
        if(status == 0 || status == 10){
            bookmarkMoveDownButton.setDisable(false);
            bookmarkMoveUpButton.setDisable(false);
        }else{
            bookmarkMoveDownButton.setDisable(true);
            bookmarkMoveUpButton.setDisable(true);
        }
    }

    public void loadWebIcon(BookMark bookmark){
        try {
            setBookmarkIcon(bookmarkIcon, getFaviconUrl(bookmark.getUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onClickBookmarkOpenWebsite() {
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    URI uri = new URI(bookmarkUrlLabel.getText());
                    desktop.browse(uri);

                    //增加书签的cnt
                    mainController.click(bookmark);
                }
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to open website");
                alert.setContentText("Invalid website: " + bookmarkUrlLabel.getText());
                alert.showAndWait();
                e.printStackTrace();
            }
        }
    }

    public void onClickBookmarkMoveUp() throws IOException {
        mainController.exchangeRank(bookmark,1);
        MainView.getMainView().refreshMainView();
    }

    public void onClickBookmarkMoveDown() throws IOException {
        mainController.exchangeRank(bookmark,-1);
        MainView.getMainView().refreshMainView();
    }

    public void onClickBookmarkEdit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookmarkEditView.fxml"));
            Parent root = loader.load();
            BookmarkEditView editController = loader.getController();

            editController.setBookmark(bookmark);
            editController.setEditView(bookmarkTitleLabel.getText(), bookmarkUrlLabel.getText(), bookmarkDescriptionLabel.getText());
            editController.setTags(bookmark);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //setter
    public void setBookmark(BookMark bookmark) {
        this.bookmark = bookmark;
        this.bookmarkTitleLabel.setText(bookmark.getName());
        this.bookmarkUrlLabel.setText(bookmark.getUrl());
        this.bookmarkDescriptionLabel.setText(bookmark.getDescription());
    }

    public void setBookmarkIcon(ImageView bookmarkIcon, String imageUrl) {
        new Thread(() -> {
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();
                    Image image = new Image(inputStream);
                    Platform.runLater(() -> bookmarkIcon.setImage(image));
                    inputStream.close();
                } else {
                    throw new Exception("Failed to download image: Server returned HTTP " + connection.getResponseCode());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 获取网页的 favicon URL。
     * @param webpageUrl 网页的 URL
     * @return favicon 的 URL，如果未找到则返回 null
     */
    public static String getFaviconUrl(String webpageUrl) {
        try {
            // 使用完整类名 org.jsoup.nodes.Document
            org.jsoup.nodes.Document doc = Jsoup.connect(webpageUrl).get();
            // 使用完整类名 org.jsoup.select.Elements
            org.jsoup.select.Elements icons = doc.head().select("link[href][rel~=(?i)^(shortcut\\s)?icon$]");
            if (!icons.isEmpty()) {
                return icons.attr("abs:href");  // 返回第一个找到的 favicon URL
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // 如果没有找到或有异常发生，返回 null
    }
}