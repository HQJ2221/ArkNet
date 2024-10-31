package org.example.demofx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckModel;
import org.example.demofx.model.BookMark;
import org.example.demofx.model.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class BookmarkEditView {

    @FXML
    private AnchorPane bookmarkEditAnchorPane;
    @FXML
    private TextField bookmarkEditUrlTextField;
    @FXML
    private TextField bookmarkEditTitleTextField;
    @FXML
    private TextField bookmarkEditDescriptionTextField;
    @FXML
    private CheckBox bookmarkEditPrivateCheckBox;
    @FXML
    private Button bookmarkEditSaveButton;
    @FXML
    private Button bookmarkEditCancelButton;
    @FXML
    private Button bookmarkEditDeleteButton;
    @FXML
    private CheckComboBox<String> bookmarkEditChooseTag;

    private MainController mainController;

    private MainView mainView;

    private DBController dbController;

    private BookMark bookmark;

    public void initialize() {
        dbController = DBController.getDBController();
        mainView = MainView.getMainView();
    }

    public void setEditView(String title, String url, String description) {
        this.bookmarkEditTitleTextField.setText(title);
        this.bookmarkEditUrlTextField.setText(url);
        this.bookmarkEditDescriptionTextField.setText(description);
        this.bookmarkEditPrivateCheckBox.setSelected(bookmark.getIsPrivate());
    }

    public void setTags(BookMark bookmark) {
        //加载Tag
        mainController = MainController.getController();
        List<Tag> tags = mainController.getTagList();
        ArrayList<String> tagString = new ArrayList<>();
        for (Tag tag : tags) {
            if(!tag.getName().equals("Untag")) {
                tagString.add(tag.getName());
            }
        }
        ObservableList<String> observableList = FXCollections.observableArrayList(tagString);
        bookmarkEditChooseTag.getItems().addAll(observableList);
        //获取CheckModel
        CheckModel<String> checkModel = bookmarkEditChooseTag.getCheckModel();

        //加载已有的标签
        List<Tag> selectedTag = dbController.getTagOfBookmark(bookmark);
        for (Tag tag : selectedTag) {
            checkModel.check(tag.getName());
        }
    }

    public void setBookmark(BookMark bookmark) {
        this.bookmark = bookmark;
    }

    public void onClickEditSave() throws IOException {//调用BookmarkListView的方法？
//        bookmark.setName(bookmarkEditTitleTextField.getText());
//        bookmark.setDescription(bookmarkEditDescriptionTextField.getText());
//        bookmark.setUrl(bookmarkEditUrlTextField.getText());
//        bookmark.setIsPrivate(bookmarkEditPrivateCheckBox.isSelected());

        //处理标签
        ObservableList<String> selectedItems = bookmarkEditChooseTag.getCheckModel().getCheckedItems();
        List<String> selectedTagsString = new ArrayList<>(selectedItems);

        mainController.updateBookmark(bookmark,
                bookmarkEditTitleTextField.getText(),
                bookmarkEditUrlTextField.getText(),
                bookmarkEditDescriptionTextField.getText(),
                bookmarkEditPrivateCheckBox.isSelected(),
                selectedTagsString);


        close();

        //随后需要刷新页面
        mainView.refreshMainView();

    }

    public void onClickEditCancel() {
        close();
    }

    public void onClickEditDelete() throws IOException {//直接调用BookmarkListView的删除方法
        mainController.deleteBookmark(bookmark);
        close();
        mainView.refreshMainView();
    }

    public void close() {
        Stage stage = (Stage) bookmarkEditAnchorPane.getScene().getWindow();
        stage.close();
    }
}