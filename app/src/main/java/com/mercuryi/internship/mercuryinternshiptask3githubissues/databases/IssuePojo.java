package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity(tableName = "issue")
public class IssuePojo {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private final String id;

    @ColumnInfo(name = "number")
    private final Integer number;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "body")
    private final String body;

    @ColumnInfo(name = "user_login")
    private final String userLogin;

    public IssuePojo(@NonNull String id, @NonNull Integer number, @NonNull String title,
                     @NonNull String body, @NonNull String userLogin) {
        this.id = id;
        this.number = number;
        this.title = title;
        this.body = body;
        this.userLogin = userLogin;
    }

    @NonNull
    public String getUserLogin() {
        return userLogin;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public Integer getNumber() {
        return number;
    }

    @NonNull
    public String getId() {
        return id;
    }
}
