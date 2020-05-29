package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class User {
    @SerializedName("login")
    @Expose
    private String login;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
