package org.example.demofx.controller;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.example.demofx.model.BookMark;
import org.example.demofx.model.Relation_bm_tag;
import org.example.demofx.model.Tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class MainController {
    private volatile static MainController instance;

    private Stage primaryStage;

    private MainController() {
    }

    public static MainController getController() {
        if (instance == null) {
            synchronized (MainController.class) {
                if (instance == null) {
                    instance = new MainController();
                }
            }
        }
        return instance;
    }

    public void start() {
        Platform.runLater(() -> {
            try {
                // 加载 FXML 文件
                FXMLLoader loader = new FXMLLoader(MainView.class.getResource("MainView.fxml"));
                AnchorPane root = loader.load();

                MainView mainView = loader.getController();
                mainView.setInstance(mainView); // ???

                // 创建场景
                Scene scene = new Scene(root, 960, 640);
                //设置Instance


//                StackPane s = new StackPane();
//                Circle c = new Circle();
//                c.setRadius(20);
//                c.setFill(javafx.scene.paint.Color.web("#3baae7"));
//                Text t = new Text("+");
//                t.setStyle("-fx-font-size: 24px;");
//                t.setFill(javafx.scene.paint.Color.WHITE);
//                s.getChildren().addAll(c, t);
//                s.setLayoutX(860);
//                s.setLayoutY(580);
//                root.getChildren().add(s);
                // 创建舞台
                primaryStage = new Stage();
                primaryStage.setTitle("NetArk");
                try {
                    InputStream iconStream = getClass().getResourceAsStream("/icons/magnet.png");
                    primaryStage.getIcons().add(new Image(iconStream));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();

                primaryStage.setOnCloseRequest(event -> {
                    MainController.getController().end();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void end() {
        DBController.getDBController().end("");
    }

    /* Op on Main Frame */

    /**
     * Add Bookmark on Main View
     *
     * @param name
     * @param URL
     * @param description
     * @param isPrivate
     * @return
     */
    public boolean addBookMark(String name, String URL, String description, boolean isPrivate, List<String> tags) {
        long bid = DBController.getDBController().add(name, URL, description, isPrivate);
        if (bid == -1) {
            System.out.println("Add Bookmark failed!");
            return false;
        }
        List<Long> list = DBController.getDBController().getTagIdByName(tags);
        if (list.size() != tags.size()) {
            System.out.println("Invalid tag(s)!");
            return false;
        }
        for (long tid : list) {
            DBController.getDBController().add(bid, tid);
        }
        return true;
    }

    public boolean addTag(String name, boolean hide) {
        return DBController.getDBController().add(name, hide);
    }

    /**
     * get Tag list from Database.
     * Database Controller will handle if status is private.
     *
     * @return Tag List
     */
    public List<Tag> getTagList() {
        return DBController.getDBController().getTagList();
    }

    /* Op on Setting Frame */
    public void setPassword(String pw) {
        DBController.getDBController().setPassword(pw);
    }

    public boolean checkPassword(String pw) {
        return DBController.getDBController().checkPassword(pw);
    }

    public String getQuestion() {
        return DBController.getDBController().getQuestion();
    }

    public boolean checkAnswer(String Ans) {
        return DBController.getDBController().checkAnswer(Ans);
    }

    /* on edit Bookmark */
    public void updateBookmark(BookMark bookmark,
                               String name,
                               String url,
                               String description,
                               boolean isPrivate,
                               List<String> tags) {
        if (bookmark == null) {
            System.out.println("Bookmark is null!");
            return;
        }
        if (!bookmark.getName().equals(name)) {
            bookmark.setName(name);
        }
        if (!bookmark.getUrl().equals(url)) {
            bookmark.setUrl(url);
        }
        if (!bookmark.getDescription().equals(description)) {
            bookmark.setDescription(description);
        }
        if (bookmark.getIsPrivate() != isPrivate) {
            bookmark.setIsPrivate(isPrivate);
        }

        HashSet<Relation_bm_tag> list = DBController.getDBController().getRelationListCopy();
        Iterator<Relation_bm_tag> iterator = list.iterator();

        while (iterator.hasNext()) {
            Relation_bm_tag rel = iterator.next();
            if (rel.getBid() == bookmark.getId()) {
                iterator.remove();  // 使用迭代器的 remove 方法安全移除元素
            }
        }
        List<Long> tagIds = DBController.getDBController().getTagIdByName(tags);
        for (long tid : tagIds) {
            DBController.getDBController().add(bookmark.getId(), tid);
        }
    }

    /**
     * exchange Rank of two bookmarks, with op = 1, exchange up, op = -1, exchange down.
     *
     * @param bookMark
     * @param op
     * @return -1 if BookMark isn't found, 1 if BookMark at top, 2 if BookMark at bottom, 0 if success.
     */
    public void exchangeRank(BookMark bookMark, int op) {
        if (bookMark == null) {
            System.out.println("Bookmark is null!");
        }
        DBController.getDBController().exchangeRank(bookMark, op);
    }

    /**
     * Click Bookmark
     *
     * @param bm
     */
    public void click(BookMark bm) {
        if (bm == null) {
            System.out.println("Bookmark is null!");
            return;
        }
        DBController.getDBController().click(bm);
    }

    ////////////
    /* delete */
    ////////////

    /**
     * Delete Bookmark
     *
     * @param bookmark
     */
    public void deleteBookmark(BookMark bookmark) {
        if (bookmark == null) {
            System.out.println("Bookmark is null!");
            return;
        }
        DBController.getDBController().removeBookmark(bookmark);
    }

    /**
     * Delete Tag
     *
     * @param tag
     */
    public boolean deleteTag(Tag tag) {
        if (tag == null) {
            System.out.println("Tag is null!");
            return false;
        } else {
            return DBController.getDBController().removeTag(tag.getId());
        }
    }

    public void tagSelected(String s, boolean isSelected) throws IOException {
        if (s.equals("Untag")) {
            if (isSelected) {
                int status = MainView.getMainView().getStatus();
                MainView.getMainView().setStatus(status < 10 ? 5 : 15);
            } else {
                int status = MainView.getMainView().getStatus();
                MainView.getMainView().setStatus(status < 10 ? 0 : 10);
            }
        } else {
            Tag tag = DBController.getDBController().getTagByName(s);
            if (isSelected) {
                DBController.getDBController().getSelected().add(tag);
            } else {
                DBController.getDBController().getSelected().remove(tag);
            }
        }
        MainView.getMainView().refreshMainView();
    }
}
