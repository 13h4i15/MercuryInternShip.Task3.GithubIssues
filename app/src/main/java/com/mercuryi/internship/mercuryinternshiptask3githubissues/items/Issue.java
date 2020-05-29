package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class Issue {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("user")
    @Expose
    private User user;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.id= title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
