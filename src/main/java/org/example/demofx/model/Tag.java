package org.example.demofx.model;

import com.google.gson.annotations.Expose;
import javafx.scene.paint.Color;

import java.util.*;

public class Tag {
    @Expose
    private long tid; // index
    @Expose
    private String name; // tag name
    @Expose
    private String color; // tag color, in 6 hex form
    @Expose
    private boolean hide; // if it's private

    /**
     * Constructor
     * @param name
     * @param color
     * @param hide
     */
    public Tag(long tid, String name, String color, boolean hide) {
        this.tid = tid;
        this.name = name;
        this.color = color;
        this.hide = hide;
    }

    /**
     * Random color
     * @param name
     * @param hide
     */
    public Tag(long tid, String name, boolean hide) {
        this(tid, name, null, hide);
        this.setColor(randomColor());
    }

    // getter & setter
    public long getId() {
        return this.tid;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    private void setColor(String color) {  // !!!!!! private
        this.color = color;
    }
    public String getColor() {
        return this.color;
    }
    public void setHide(boolean hide) {
        this.hide = hide;
    }
    public boolean getHide() {
        return this.hide;
    }


    private List<String> colors = new ArrayList<>(Arrays.asList(
            "00FF00", "00FFFF", "FF0000", "FF00FF", "930093", "00EC00",
            "7D7DFF", "8CEA00", "A6A600", "FF8000", "B87070", "A5A552"
    ));

    private String randomColor() {
        Random rand = new Random();
        return colors.get(rand.nextInt(0, colors.size()));
    }
}
