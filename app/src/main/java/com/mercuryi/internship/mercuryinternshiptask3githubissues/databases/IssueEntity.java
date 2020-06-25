package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(tableName = "issue", foreignKeys = {@ForeignKey(
        entity = UserEntity.class,
        parentColumns = "login",
        childColumns = "user_login"
)})
public class IssueEntity {
    @PrimaryKey
    @ColumnInfo(name = "id")
    @NonNull
    private final String id;

    @ColumnInfo(name = "number")
    @NonNull
    private final Integer number;

    @ColumnInfo(name = "state")
    @NonNull
    private final String state;

    @ColumnInfo(name = "title")
    @NonNull
    private final String title;

    @ColumnInfo(name = "body")
    @NonNull
    private final String body;

    @ColumnInfo(name = "user_login")
    @NonNull
    private final String userLogin;

    public IssueEntity(@NonNull String id, @NonNull Integer number, @NonNull String state,
                       @NonNull String title, @NonNull String body, @NonNull String userLogin) {
        this.id = id;
        this.number = number;
        this.state = state;
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
    public String getState() {
        return state;
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
