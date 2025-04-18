package com.pdp;

public class Post {


    private Long id;


    private static String title;


    private transient Long userId;

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

    public Post(String body, String title, Long userId) {
        this.body = body;
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
