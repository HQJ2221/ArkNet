package org.example.demofx.controller;

import org.example.demofx.model.BookMark;
import org.example.demofx.model.Data;
import org.example.demofx.model.Relation_bm_tag;
import org.example.demofx.model.Tag;

import java.util.*;
import java.util.stream.Collectors;

public class DBController {
    /* Attributes */
    private static DBController instance;
    private MainController mainController;
    private Data data;

    private List<Tag> selected = new ArrayList<>();

    public List<Tag> getSelected() {
        return selected;
    }
    public void resetSelected(){
        selected.clear();
    }

    /* If 1, set public; if 0, set private */
    private boolean isPublic;

    public void setPublic(boolean b) {
        isPublic = b;
    }
    public boolean getPublic() {
        return isPublic;
    }

    /* Constructor */
    private DBController() {
        mainController = MainController.getController();
        this.data = new Data();
        isPublic = true;
        data.loadStartup();
    }

    public static DBController getDBController() {
        if (instance == null) {
            instance = new DBController();
        }
        return instance;
    }

    /**
     * end up a Database Controller to save data to json and properties. String num is the suffix of the file name.
     * @param num
     */
    public void end(String num) {
        data.saveShutdown(num);
    }

    ////////////////////////////
    /* Bookmark List selector */
    ////////////////////////////
    /**
     * This method is used to get a list of bookmarks sorted by rank in descending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByRankDesc() {
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o2.getRank() - o1.getRank()));
        return list;
    }

    /**
     * This method is used to get a list of bookmarks sorted by rank in ascending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByRankAsc() {
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o1.getRank() - o2.getRank()));
        return list;
    }

    /**
     * This method is used to get a list of bookmarks sorted by time in descending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByTimeDesc() {  // get bookmark by time
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o2.getTime() - o1.getTime()));
        return list;
    }

    /**
     * This method is used to get a list of bookmarks sorted by time in ascending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByTimeAsc() {  // get bookmark by time
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o1.getTime() - o2.getTime()));
        return list;
    }

    /**
     * This method is used to get a list of bookmarks sorted by click count in descending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByClickDesc() {  // get bookmark by click
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o2.getCnt() - o1.getCnt()));
        return list;
    }

    /**
     * This method is used to get a list of bookmarks sorted by click count in ascending order.
     * @return list
     */
    public List<BookMark> getBookMarkListByClickAsc() {  // get bookmark by click
        List<BookMark> list = new ArrayList<>(getBookMarkByTags());
        list.sort((o1, o2) -> (int) (o1.getCnt() - o2.getCnt()));
        return list;
    }

    public BookMark getBookMarkById(long bid) {
        List<BookMark> list = new ArrayList<>(
                isPublic ? List.copyOf(data.getPublicBookMarks()) : List.copyOf(data.getPrivateBookMarks())
        );
        Optional<BookMark> foundBookmark = list.stream()
                .filter(bookMark -> bookMark.getId() == bid).findFirst();
        return foundBookmark.orElse(null);
    }

    public List<BookMark> getBookMarkByTag(Tag tag) {
        List<BookMark> bookMarks = new ArrayList<>();
        for (Relation_bm_tag relation : data.getRelationList()) {
            if (relation.getTid() == tag.getId()) {
                bookMarks.add(getBookMarkById(relation.getBid()));
            }
        }
        bookMarks.sort((o1, o2) -> (int) (o2.getRank() - o1.getRank()));
        return bookMarks;
    }

    public List<BookMark> getBookMarkByTags() {
        List<BookMark> list = new ArrayList<>(
                isPublic ? List.copyOf(data.getPublicBookMarks()) : List.copyOf(data.getPrivateBookMarks())
        );
        if (selected.isEmpty()) return list;
        return list.stream()
                .filter(bookmark -> new HashSet<>(getTagOfBookmark(bookmark)).containsAll(selected))
                .toList();
    }

    public List<BookMark> getUntaggedBookmark() {
        List<BookMark> bookMarks = new ArrayList<>();
        List<BookMark> list = new ArrayList<>(
                isPublic ? List.copyOf(data.getPublicBookMarks()) : List.copyOf(data.getPrivateBookMarks())
        );
        for (BookMark bookMark : list) {
            boolean isTagged = false;
            for (Relation_bm_tag relation : data.getRelationList()) {
                if (relation.getBid() == bookMark.getId()) {
                    isTagged = true;
                    break;
                }
            }
            if (!isTagged) {
                bookMarks.add(bookMark);
            }
        }
        bookMarks.sort((o1, o2) -> (int) (o2.getRank() - o1.getRank()));
        return bookMarks;
    }

    //////////////////
    /* Tag selector */
    //////////////////
    public Tag getTagById(Long tid) {
        List<Tag> tagList = new ArrayList<>(List.copyOf(data.getTagList()));
        Optional<Tag> foundTag = tagList.stream() // 将列表转换为流
                .filter(tag -> tag.getId() == tid && tag.getHide() != isPublic) // 过滤出ID匹配的Tag
                .findFirst(); // 查找第一个匹配的元素
        // 找到了具有指定ID的Tag
        return foundTag.orElse(null);
    }

    /**
     * This method is used to get a Tag object by its name.
     *
     * @param name This is the name of the Tag to be returned.
     * @return Tag This returns the Tag object with the specified name. If no such Tag is found, it returns null.
     */
    public Tag getTagByName(String name) {
        // Create a copy of the tag list
        List<Tag> tagList = new ArrayList<>(List.copyOf(data.getTagList()));

        // Convert the list to a stream, filter out the Tag with the specified name and visibility,
        // and find the first matching Tag
        Optional<Tag> foundTag = tagList.stream()
                .filter(tag -> tag.getName().equals(name) && tag.getHide() != isPublic)
                .findFirst();

        // Return the found Tag, or null if no matching Tag was found
        return foundTag.orElse(null);
    }

    public List<Tag> getTagList() {
        return isPublic ? getUnhiddenTag() : getHiddenTag();
    }

    public List<Tag> getUnhiddenTag() {
        List<Tag> tagList = new ArrayList<>(List.copyOf(data.getTagList()));
        tagList.removeIf(Tag::getHide); // keep unhidden tag
        return tagList;
    }

    public List<Tag> getHiddenTag() {
        List<Tag> tagList = new ArrayList<>(List.copyOf(data.getTagList()));
        tagList.removeIf(tag -> !tag.getHide()); // keep unhidden tag
        return tagList;
    }

    /**
     * This method is used to get the tags associated with a specific bookmark.
     *
     * @param bookMark The bookmark for which the tags are to be retrieved.
     * @return A list of tags associated with the given bookmark. If no tags are associated, an empty list is returned.
     */
    public List<Tag> getTagOfBookmark(BookMark bookMark) {
        List<Tag> tagList = new ArrayList<>();
        for (Relation_bm_tag relation : data.getRelationList()) {
            if (bookMark.getId() == relation.getBid()) {
                Tag tag = getTagById(relation.getTid());
                if (tag != null) {
                    tagList.add(tag);
                }
            }
        }
        return tagList;
    }

    /**
     * This method is used to get the IDs of tags by their names.
     *
     * @param tags A list of tag names for which IDs are to be fetched.
     * @return A list of IDs corresponding to the input tag names. If a tag name does not exist, it is ignored.
     */
    public List<Long> getTagIdByName(List<String> tags) {
        List<Long> tagList = new ArrayList<>();
        // Iterate over all tags in the data
        for (Tag tag : data.getTagList()) {
            // For each tag, check if its name is in the input list
            for (String name : tags) {
                // If the tag name matches, add its ID to the output list
                if (tag.getName().equals(name)) {
                    tagList.add(tag.getId());
                }
            }
        }
        // Return the list of IDs
        return tagList;
    }

    ///////////////////////
    /* relation selector */
    ///////////////////////
    /**
     * Returns a copy of the relation list.
     *
     * This method is used to get a copy of the relation list from the data object.
     * The purpose of returning a copy is to prevent external modifications to the original list.
     *
     * @return a copy of the relation list
     */
    public HashSet<Relation_bm_tag> getRelationListCopy() {
        return data.getRelationList();
    }


    ////////////////////
    /* add and remove */
    ////////////////////
    public long add(String name, String URL, String description, boolean isPrivate) {
        return data.addBookMark(name, URL, description, isPrivate);
    }

    public boolean add(String name, boolean hide) {
        return data.addTag(name, hide);
    }

    public boolean add(long bid, long tid) {
        return data.addRelation(bid, tid);
    }

    public boolean removeTagFromBookmark(long bid, long tid) {
        Optional<Relation_bm_tag> found = data.getRelationList().stream()
                .filter(x -> x.getBid() == bid && x.getTid() == tid).findFirst();
        if (found.isPresent()) {
            data.getRelationList().remove(found.get());
            return true;
        }
        return false;
    }

    public boolean removeTag(long tid) {
        Tag tag = getTagById(tid);
        boolean suc = false;
        if (tag != null) {
            for(Relation_bm_tag rel : data.getRelationList()) {
                if (rel.getTid() == tid) {
                    suc = data.getRelationList().remove(rel);
                }
            }
        }
        return suc;
    }
    /**
     * This method is used to remove a bookmark from the bookmark list.
     * It first finds the bookmark with the given id and if it exists, it removes all relations of this bookmark from the relation list.
     *
     * @param bm The bookmark that needs to be removed.
     * @return boolean Returns true if the bookmark and its relations were successfully removed, false otherwise.
     */
    public boolean removeBookmark(BookMark bm) {
        boolean suc = false;
        if (bm != null) {
            List<Relation_bm_tag> list = new ArrayList<>(data.getRelationList());
            List<Relation_bm_tag> removeList = new ArrayList<>();
            for (Relation_bm_tag rel : list) {
                if (rel.getBid() == bm.getId()) {
                    removeList.add(rel);
                }
            }
            suc = data.getRelationList().removeAll(removeList);
            suc = data.getBookMarkList().remove(bm);
        }
        return suc;
    }

    ////////////
    /* update */
    ////////////
    public void exchangeRank(BookMark bm, int op) {
        List<BookMark> list = new ArrayList<>(
                isPublic ? List.copyOf(data.getPublicBookMarks()) : List.copyOf(data.getPrivateBookMarks())
        );

        list.sort((o1, o2) -> (int) (o2.getRank() - o1.getRank()));
        int i = list.indexOf(bm);
        if (i == -1) {
            return;
        }
        if (op == -1) {
            if (i == list.size() - 1) {
                return;
            }
            long temp = list.get(i).getRank();
            list.get(i).setRank(list.get(i + 1).getRank());
            list.get(i + 1).setRank(temp);
        } else if (op == 1) {
            if (i == 0) {
                return;
            }
            long temp = list.get(i).getRank();
            list.get(i).setRank(list.get(i - 1).getRank());
            list.get(i - 1).setRank(temp);
        }
    }

    public void click(BookMark bm) {
        bm.setCnt(bm.getCnt() + 1);
    }

    ////////////////
    /* properties */
    ////////////////
    public void setPassword(String pw) {
        data.setPassword(pw);
    }

    public boolean checkPassword(String pw) {
        return data.validatePw(pw);
    }

    public String getQuestion() {
        return data.getQuestion();
    }

    public boolean checkAnswer(String Ans) {
        return data.validateAns(Ans);
    }

    /**
     * This method is used to search for bookmarks in the bookmark list.
     * It checks if the search string is contained in the name, description, or URL of the bookmark.
     * The results are sorted by rank in descending order.
     *
     * @param str The search string.
     * @return A list of bookmarks that match the search string.
     */
    public List<BookMark> search(String str) {
        Map<Integer, BookMark> map = new HashMap<>();
        List<BookMark> list = new ArrayList<>(
                isPublic ? List.copyOf(data.getPublicBookMarks()) : List.copyOf(data.getPrivateBookMarks())
        );
        List<Relation_bm_tag> relations = new ArrayList<>(List.copyOf(data.getRelationList()));
        for (BookMark bm : list) {
            double score = 0;
            score += countCommonCharsRatio(str, bm.getName()) * 0.5
                    + countCommonCharsRatio(str, bm.getDescription()) * 0.3;
            double t_score = 0;
            for (Relation_bm_tag rel : relations) {
                if (rel.getBid() == bm.getId()) {
                    Tag tag = getTagById(rel.getTid());
                    t_score += countCommonCharsRatio(str, tag.getName());
                }
            }
            score += t_score * 0.2;
            if (score > 0.1) {
                map.put((int) (score * 1000), bm);
            }
        }
        list = map.entrySet().stream().sorted(new Comparator<Map.Entry<Integer, BookMark>>() {
            @Override
            public int compare(Map.Entry<Integer, BookMark> o1, Map.Entry<Integer, BookMark> o2) {
                return o2.getKey() - o1.getKey();
            }
        }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue, LinkedHashMap::new)).values().stream().toList();
        return list;
    }

    public double countCommonCharsRatio(String str, String comp) {
        Set<Character> set1 = str.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        Set<Character> set2 = comp.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        set1.retainAll(set2); // 取两个集合的交集
        return set1.size() / (double) str.length();
    }
}
