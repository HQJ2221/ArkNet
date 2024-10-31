package org.example.demofx.model;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Data {
    static private long bm_count = 0;
    static private long tag_count = 0;
    @Expose
    private HashSet<BookMark> bookMarkList;
    @Expose
    private HashSet<Tag> tagList;
    @Expose
    private HashSet<Relation_bm_tag> relationList;

    public String password;
    public String theme; // BLACK or WHITE
    // personal setting Q&A
    public String question ; // encrypted
    private String answer = "1月1日"; // encrypted

    public Data(HashSet<BookMark> bookMarkList, HashSet<Tag> tagList, HashSet<Relation_bm_tag> relationList) {
        this.bookMarkList = bookMarkList;
        this.tagList = tagList;
        this.relationList = relationList;
    }

    public Data() {
        this.bookMarkList = new HashSet<>();
        this.tagList = new HashSet<>();
        this.relationList = new HashSet<>();
        this.theme = "WHITE";
        this.question = encrypt("你的生日？");
        this.answer = encrypt("1月1日");
        this.password = encrypt("123456");
    }

    // save using json and properties
    private static final String CONFIG_FILE = "config.properties";

    /**
     * Save some unique information
     */
    private void saveProperties() {
        try {
            Properties prop = new Properties();
            prop.setProperty("Theme", this.theme);
            prop.setProperty("Question", this.question);
            prop.setProperty("Answer", this.answer);
            prop.setProperty("Num of Bookmark", String.valueOf(bm_count));
            prop.setProperty("Num of Tag", String.valueOf(tag_count));
            prop.setProperty("Password", this.password);
            prop.store(new FileOutputStream(CONFIG_FILE), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveShutdown() {
        saveShutdown("");
    }


    private static final String BM_PATH = "bookmarks.json";
    private static final String TAG_PATH = "tags.json";
    private static final String RELATION_PATH = "relations.json";

    /**
     * end up a DBController to save data to json and properties. String num is the suffix of the file name.
     * @param num
     */
    public void saveShutdown(String num) {
        saveProperties();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonArray jsonArray = new JsonArray();
        for (BookMark data : bookMarkList) {
            JsonElement jsonElement = gson.toJsonTree(data);
            jsonArray.add(jsonElement);
        }
        // bookMarkList
        try (FileWriter writer = new FileWriter(BM_PATH)) {
            gson.toJson(jsonArray, writer);
            System.out.println("Data successfully saved to " + BM_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // tagList
        jsonArray = new JsonArray();
        for (Tag data : tagList) {
            JsonElement jsonElement = gson.toJsonTree(data);
            jsonArray.add(jsonElement);
        }
        try (FileWriter writer = new FileWriter(TAG_PATH)) {
            gson.toJson(jsonArray, writer);
            System.out.println("Data successfully saved to " + TAG_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // relationList
        jsonArray = new JsonArray();
        for (Relation_bm_tag data : relationList) {
            JsonElement jsonElement = gson.toJsonTree(data);
            jsonArray.add(jsonElement);
        }
        try (FileWriter writer = new FileWriter(RELATION_PATH)) {
            gson.toJson(jsonArray, writer);
            System.out.println("Data successfully saved to " + RELATION_PATH);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProperties() {
        try {
            Properties prop = new Properties();
            FileInputStream fis = new FileInputStream(CONFIG_FILE);
            prop.load(fis);
            this.theme = prop.getProperty("Theme");
            this.question = prop.getProperty("Question");
            this.answer = prop.getProperty("Answer");
            bm_count = Long.parseLong(prop.getProperty("Num of Bookmark"));
            tag_count = Long.parseLong(prop.getProperty("Num of Tag"));
            this.password = prop.getProperty("Password");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadStartup() {
        loadStartup("");
    }

    public void loadStartup(String num) {
        loadProperties(); // load properties

        Gson gson = new Gson();
        Type listType = new TypeToken<HashSet<BookMark>>(){}.getType();
        // bookMarkList
        try (FileReader fileReader = new FileReader(BM_PATH)) {
            JsonArray jsonArray = gson.fromJson(fileReader, JsonArray.class);
            bookMarkList = gson.fromJson(jsonArray, listType);
        } catch (Exception e) {
            bookMarkList = new HashSet<>();
        }
        // tagList
        try (FileReader fileReader = new FileReader(TAG_PATH)) {
            JsonArray jsonArray = gson.fromJson(fileReader, JsonArray.class);
            tagList = gson.fromJson(jsonArray, new TypeToken<HashSet<Tag>>(){}.getType());
        } catch (Exception e) {
            tagList = new HashSet<>();
        }
        // relationList
        try (FileReader fileReader = new FileReader(RELATION_PATH)) {
            JsonArray jsonArray = gson.fromJson(fileReader, JsonArray.class);
            relationList = gson.fromJson(jsonArray, new TypeToken<HashSet<Relation_bm_tag>>(){}.getType());
        } catch (Exception e) {
            relationList = new HashSet<>();
        }
    }

    // private methods to check duplications
    private boolean isDuplicate(Relation_bm_tag relation) {
        if (relationList.contains(relation)) {
            return true;
        } else {
            for (Relation_bm_tag r : relationList) {
                if (r.getBid() == relation.getBid() && r.getTid() == relation.getTid()) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isDuplicate(Tag tag) {
        if (tagList.contains(tag)) {
            return true;
        } else {
            for (Tag t : tagList) {
                if (Objects.equals(t.getName(), tag.getName())) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean isDuplicate(BookMark bookmark) {
        if (bookMarkList.contains(bookmark)) {
            return true;
        } else {
            for (BookMark b : bookMarkList) {
                if (Objects.equals(b.getUrl(), bookmark.getUrl())) {
                    return true;
                }
            }
            return false;
        }
    }

    // Add elements
    public boolean addRelation(long bid, long tid) {
        Relation_bm_tag relation = new Relation_bm_tag(bid, tid);
        if (!isDuplicate(relation)) {
            relationList.add(relation);
            Optional<BookMark> foundBookmark = bookMarkList.stream()
                    .filter(bookMark -> bookMark.getId() == bid).findFirst();
            foundBookmark.ifPresent(bookMark -> bookMark.tag_cnt++); // record the tag_cnt
            return true;
        }
        return false;
    }

    public boolean addTag(String name, boolean hide) {
        Tag tag = new Tag(++tag_count, name, hide);
        if (!isDuplicate(tag)) {
            tagList.add(tag);
            return true;
        }
        return false;
    }

    public long addBookMark(String name, String URL, String description, boolean isPrivate) {
        BookMark bookmark = new BookMark(++bm_count, name, URL, isPrivate, description);
        if (!isDuplicate(bookmark)) {
            bookMarkList.add(bookmark);
            return bm_count;
        }
        return -1;
    }

    // Remove elements
    public boolean removeRelation(Relation_bm_tag relation) {
        return relationList.remove(relation);
    }

    public boolean removeTag(Tag tag) {
        return tagList.remove(tag);
    }

    public boolean removeBookMark(BookMark bookmark) {
        return bookMarkList.remove(bookmark);
    }

    // getter & setter
    public String getPassword() {
        return this.password;
    }

    public void setPassword(String pw) {
        this.password = encrypt(pw);
    }

    public String getQuestion() {
        return decrypt(this.question);
    }

    public void setQuestion(String question) {
        this.question = encrypt(question);
    }

    public void setAnswer(String answer) {
        this.answer = encrypt(answer);
    }

    public HashSet<BookMark> getBookMarkList() {
        return bookMarkList;
    }

    public List<BookMark> getPublicBookMarks() {
        return bookMarkList.stream().filter(x -> !x.getIsPrivate()).toList();
    }

    public List<BookMark> getPrivateBookMarks() {
        return bookMarkList.stream().filter(BookMark::getIsPrivate).toList();
    }

    public HashSet<Tag> getTagList() {
        return tagList;
    }

    public HashSet<Relation_bm_tag> getRelationList() {
        return relationList;
    }

    // AES
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "WeAreGoodTeam"; // 替换为您自己的密钥


    private static String encrypt(String input) {
        try {
            byte[] key = getSecretKey();
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt string", e);
        }
    }

    private static String decrypt(String encrypted) {
        try {
            byte[] key = getSecretKey();
            SecretKeySpec secretKey = new SecretKeySpec(key, ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decoded = Base64.getDecoder().decode(encrypted);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt string", e);
        }
    }

    private static byte[] getSecretKey() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        key = Arrays.copyOf(key, 16); // AES密钥长度为128位(16字节)
        return key;
    }

    // validation

    /**
     * this.answer is encrypted, but the input is not encrypted
     * @param answer
     * @return
     */
    public boolean validateAns(String answer) {
        return this.answer.equals(encrypt(answer));
    }

    /**
     * this.password is encrypted. but the input is not.
     * @param pw
     * @return
     */
    public boolean validatePw(String pw) {
        return this.password.equals(encrypt(pw));
    }
}
