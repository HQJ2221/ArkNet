package org.example.demofx.controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import org.example.demofx.model.BookMark;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Alert;

import org.controlsfx.control.CheckComboBox;


import javafx.scene.control.ComboBox;
import org.example.demofx.model.Tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class BookmarkAddView {
    public Stage bookmarkAddStage;
    public Scene bookmarkAddScene;

    @FXML
    private AnchorPane bookmarkAddAnchorPane;
    @FXML
    private TextField bookmarkAddUrlTextField;
    @FXML
    private TextField bookmarkAddTitleTextField;
    @FXML
    private TextField bookmarkAddDescriptionTextField;
    @FXML
    private Button bookmarkAddCancelButton;
    @FXML
    private Button bookmarkAddButton;
    @FXML
    private CheckBox bookmarkAddPrivateCheckBox;
    @FXML
    private CheckComboBox<String> bookmarkAddChooseTag;

    private BookMark bookmark;

    private MainView mainView;

    private List<Tag> tags;

    private MainController mainController;

    private BookmarkListView bookmarkListView;

    public void initialize(){
        //加载Tag
        mainController=MainController.getController();
        mainView=MainView.getMainView();
        List<Tag> tags=mainController.getTagList();
        ArrayList<String> tagString=new ArrayList<>();
        for (Tag tag : tags) {
            tagString.add(tag.getName());
        }
        ObservableList<String> observableList = FXCollections.observableArrayList(tagString);
        bookmarkAddChooseTag.getItems().addAll(observableList);
    }

    public void setBookmark(BookMark bookmark){
        this.bookmark=bookmark;
    }

    public void setMainView(MainView mainView){
        this.mainView=mainView;
    }

    //处理点击事件
    public void onClickAddBookmarkCancel(){
        close();
    }

    public void onClickAddBookmarkAdd() throws IOException {
        //传新建的bookmark
        String name = bookmarkAddTitleTextField.getText();
        String url = bookmarkAddUrlTextField.getText();
        String description = bookmarkAddDescriptionTextField.getText();

        //如果URL没有
        if(url.isEmpty()){
            // 弹出提示框 提示没有URL
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("NO URL");
            alert.setContentText("You didn't input any URL!");
            alert.showAndWait();
            return;
        }

        //首先判断是否需要填充Title
        if(name.isEmpty()){
            //填充name
            try {
                name = parseHtmlForTag(url, "<title>(.*?)</title>");  // 使用正则表达式查找title
                System.out.println("Title found: " + name);
            } catch(Exception e) {
                System.out.println("Error parsing title: " + e.getMessage());
                e.printStackTrace();
            }
        }

        //再判断是否需要填充Description
        if(description.isEmpty()){
            //填充description
            try {
                description = parseHtmlForTag(url, "<meta\\s+[^>]*name=[\"']description[\"'][^>]*content=[\"'](.*?)[\"'][^>]*>");  // 使用正则表达式查找description
                System.out.println("Description found: " + description);
            } catch(Exception e) {
                System.out.println("Error parsing description: " + e.getMessage());
                e.printStackTrace();
            }
        }

        boolean isPrivate = bookmarkAddPrivateCheckBox.isSelected();
        // 如果succeed为1，说明添加书签成功(用于必要判断，或显示提示框)

        //现在需要考虑标签
        ObservableList<String> selectedItems = bookmarkAddChooseTag.getCheckModel().getCheckedItems();
        List<String> selectedTags = new ArrayList<>(selectedItems);

        boolean succeed = mainController.addBookMark(name, url, description, isPrivate,selectedTags);

        close();

        //弹出提示框 告知用户是否添加成功
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Add Bookmark");
        if(succeed){
            alert.setContentText("Bookmark added successfully!");
        }else{
            alert.setContentText("Bookmark added failed!");
        }
        alert.showAndWait();

        //按下确认后需要刷新页面
        mainView.refreshMainView();

    }

    public void close() {
        Stage stage = (Stage) bookmarkAddAnchorPane.getScene().getWindow();
        stage.close();
    }

    private String parseHtmlForTag(String urlString, String regex) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        StringBuilder html = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                html.append(line);
            }
        }

        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher matcher = pattern.matcher(html.toString());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }
}