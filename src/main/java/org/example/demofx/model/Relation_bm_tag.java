package org.example.demofx.model;

import com.google.gson.annotations.Expose;

public class Relation_bm_tag {
    @Expose
    private long bid;
    @Expose
    private long tid;
    public Relation_bm_tag(long bid, long tid) {
        this.bid = bid;
        this.tid = tid;
    }

    public long getBid() {
        return this.bid;
    }

    public long getTid() {
        return this.tid;
    }
}
