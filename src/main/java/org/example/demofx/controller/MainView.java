package org.example.demofx.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.example.demofx.model.BookMark;
import org.example.demofx.model.Tag;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MainView (View Controller)
 */
public class MainView {

    @FXML
    Button MainEnablePrivateButton;
    @FXML
    Button MainResetTagsButton;
    @FXML
    Button MainAddTagButton;
    @FXML
    Label MainTitleLabel;
    @FXML
    TextField MainSearchTextField;
    @FXML
    Button MainOpenSettingsButton;
    @FXML
    Button MainSearchButton;
    @FXML
    Circle MainAddBookmarkButton;
    @FXML
    ComboBox<String> MainSortComboBox;
    Set<TagController> tagControllerSet = new HashSet<>();
    HashMap<Tag, Node> tagNodeHashMap = new HashMap<>();

    @FXML
    ListView tagList;
    ObservableList<Node> tagItems;

    @FXML
    VBox ListView;

    private static int status = 0;

    private static MainView instance;

    /* AddView */
    private BookmarkAddView bookmarkAddView;

    public MainView() {
    }

    public static MainView getMainView() {
        if (instance == null) {
            instance = new MainView();
        }
        return instance;
    }

    public void initialize() throws IOException {
        status = 0;
        refreshMainView();
    }

    public void loadBookmarks(List<BookMark> bookMarkList) {
        try {
            //关联到BookmarkListView
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookmarkListView.fxml"));
            ScrollPane root = loader.load();
            BookmarkListView controller = loader.getController();
            controller.setMainView(this);
            for (BookMark bookMark : bookMarkList) {
                controller.addBookmark(bookMark);
            }
            ListView.getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    Tag untag = new Tag(-1,"Untag",false);
    private void loadTags(List<Tag> tags) throws IOException{
        if(!tagNodeHashMap.containsKey(untag)) {
            FXMLLoader ULoader = new FXMLLoader(getClass().getResource("TagView.fxml"));
            Node UTag = ULoader.load();
            TagController UController = ULoader.getController();
            UController.setTag(untag);
            tagControllerSet.add(UController);
            tagNodeHashMap.put(untag,UTag);
        }
        tagItems.add(tagNodeHashMap.get(untag));
        for (int i = 0; i < tags.size(); i++) {
            if(!tags.get(i).getName().equals("Untag")) {
                if(!tagNodeHashMap.containsKey(tags.get(i))) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("TagView.fxml"));
                    Node tag = loader.load();
                    TagController controller = loader.getController();
                    controller.setTag(tags.get(i));
                    tagControllerSet.add(controller);
                    tagNodeHashMap.put(tags.get(i),tag);
                }
                tagItems.add(tagNodeHashMap.get(tags.get(i)));
            }
        }
    }

    public void onClickMainEnablePrivateButton() throws IOException {
        if (status >= 10) {
            setStatus(0);
            inversePrivateButton();
            DBController.getDBController().setPublic(true);
            refreshMainView();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("OpenPrivateView.fxml"));
                Parent root = loader.load();
                OpenPrivateController controller = loader.getController();
                Stage stage = new Stage();
                Scene scene = new Scene(root, 390, 290);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void inversePrivateButton() {
        if (status >= 10) {
            MainEnablePrivateButton.setText("Public");
        } else {
            MainEnablePrivateButton.setText("Private");
        }
    }

    public void onClickMainResetTagsButton() throws IOException {
        for (TagController tagController : tagControllerSet) {
            tagController.resetTag();
        }
        status = DBController.getDBController().getPublic() ? 0 : 10;
        DBController.getDBController().resetSelected();
        refreshMainView();
    }

    public void onClickMainAddTagButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("TagAddView.fxml"));
            Parent root = loader.load();
            TagAddController controller = loader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root, 430, 280);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onClickMainSearchButton() {
        DBController dbc = DBController.getDBController();
        String s = MainSearchTextField.getText();

        //随后根据搜索结果刷新MainView
        List<BookMark> list = dbc.search(s);
        ListView.getChildren().clear();
        loadBookmarks(list);

        //首先修改MainTitleLabel
        MainTitleLabel.setText(list.size() + " results");
    }

    public void onClickMainOpenSettingsButton() {
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SettingsView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 390, 430);
            stage.setTitle("Settings");
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void onClickMainAddBookmarkButton() {
        // open add bookmark dialog
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("BookmarkAddView.fxml"));
            Parent root = loader.load();
            BookmarkAddView controller = loader.getController();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onMainSortComboBoxAction() throws IOException {
        refreshMainView();
    }

    public void setInstance(MainView instance) {
        this.instance = instance;
    }

    public void refreshMainView() throws IOException {
        List<BookMark> list;
        boolean DBPublic = DBController.getDBController().getPublic();
        //根据排序方式排序
        String selectedOption = MainSortComboBox.getSelectionModel().getSelectedItem();
        if (selectedOption == null) {
            selectedOption = "Custom sort";
        }
        if (status != 5 && status != 15) { // not untagged
            switch (selectedOption) {
                case "In ascending order of addition":
                    setStatus(DBPublic ? 1 : 11);
                    break;
                case "In descending order of addition":
                    setStatus(DBPublic ? 2 : 12);
                    break;
                case "In ascending order of clicks":
                    setStatus(DBPublic ? 3 : 13);
                    break;
                case "In descending order of clicks":
                    setStatus(DBPublic ? 4 : 14);
                    break;
                default:
                    setStatus(DBPublic ? 0 : 10);
                    break;
            }
        }
        list = getStatusList(status);
        ListView.getChildren().clear();
        loadBookmarks(list);

        tagItems = FXCollections.observableArrayList(); // 初始化列表
        tagList.setItems(tagItems); // 将ListView的items属性绑定到ObservableList
        tagList.getItems().clear();
        List<Tag> tags = DBController.getDBController().getTagList();
        loadTags(tags);

        System.out.println(status);
    }

    public List<BookMark> getStatusList(int status) {
        switch (status) {
            case 1, 11:
                return DBController.getDBController().getBookMarkListByTimeAsc(); // In ascending order of addition
            case 2, 12:
                return DBController.getDBController().getBookMarkListByTimeDesc(); // In descending order of addition
            case 3, 13:
                return DBController.getDBController().getBookMarkListByClickAsc(); // In ascending order of clicks
            case 4, 14:
                return DBController.getDBController().getBookMarkListByClickDesc(); // In descending order of clicks
            case 5, 15:
                return DBController.getDBController().getUntaggedBookmark();
            case 10:
                return DBController.getDBController().getBookMarkByTags();
            default:
                return DBController.getDBController().getBookMarkListByRankDesc(); // Custom sort
        }
    }

    public void setStatus(int num) {
        status = num;
    }

    public int getStatus() {
        return status;
    }
}