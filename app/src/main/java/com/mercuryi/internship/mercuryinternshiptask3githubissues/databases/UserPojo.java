package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")
public class UserPojo {
    @PrimaryKey
    @ColumnInfo(name = "login")
    @NonNull
    private final String login;

    @ColumnInfo(name = "avatar_url")
    private final String avatarUrl;

    public UserPojo(@NonNull String login, @NonNull String avatarUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    @NonNull
    public String getLogin() {
        return login;
    }

    @NonNull
    public String getAvatarUrl() {
        return avatarUrl;
    }
}
