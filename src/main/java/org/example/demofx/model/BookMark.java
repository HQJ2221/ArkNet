package org.example.demofx.model;

import com.google.gson.annotations.Expose;

import java.sql.Timestamp;


public class BookMark {
    @Expose
    private long bid;
    @Expose
    private String name;
    @Expose
    private String url;
    @Expose
    private String description;
    @Expose
    private boolean isPrivate;
    @Expose
    private long time;
    @Expose
    private int cnt; // visit count
    @Expose
    private long rank;  // custom rank
    @Expose
    public int tag_cnt = 0;

    public BookMark(long bid, String name, String url, boolean isPrivate, String description) {
        this.bid = bid;
        this.name = name;
        this.url = url;
        this.description = description;
        this.isPrivate = isPrivate;
        this.time = System.currentTimeMillis();
        this.cnt = 0;
        this.rank = this.bid;
    }

    public BookMark(long bid, String name, String url, boolean isPrivate) {
        this(bid, name, url, isPrivate, "");
    }

    // getter & setter
    public long getId() {
        return this.bid;
    }
    public void setRank(long rank) {
        this.rank = rank;
    }
    public long getRank() {
        return this.rank;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return this.name;
    }
    public void setUrl(String url) { this.url = url; }
    public String getUrl() {
        return this.url;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return this.description;
    }
    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
    public boolean getIsPrivate() {
        return this.isPrivate;
    }
    public int getCnt() {
        return this.cnt;
    }
    public void setCnt(int cnt) {
        this.cnt = cnt;
    }
    public long getTime() {
        return this.time;
    }
    public Timestamp getTimestamp() {
        return new Timestamp(this.time);
    }

    // other methods
    public void visit() {
        this.cnt++;
    }
}
