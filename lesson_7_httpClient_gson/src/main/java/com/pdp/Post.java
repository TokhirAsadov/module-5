package com.pdp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

import java.util.Date;

public class Post {

    @Expose
    private Long id;

    @Since(1.0)
    @Expose(serialize = false,deserialize = false)
    private static String title;

//    @SerializedName("user_id")
    @Expose
    @Since(1.2)
    private transient Long userId;

    @Expose
    @Since(2.0)
    private volatile String body;


    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Post(String body, Long id, String title, Long userId) {
        this.body = body;
        this.id = id;
        this.title = title;
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Post{" +
                " id=" + id +
                ", title='" + title + '\'' +
                ",body='" + body + '\'' +
                ", userId=" + userId +
                '}';
    }
}
