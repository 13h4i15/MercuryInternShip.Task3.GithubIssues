package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

public class IssueWithUser {
    @Embedded
    private IssueEntity issue;

    @Relation(
            parentColumn = "user_login",
            entityColumn = "login",
            entity = UserEntity.class
    )
    private UserEntity user;

    public IssueWithUser() {
    }

    public void setUser(@NonNull UserEntity user) {
        this.user = user;
    }

    public void setIssue(@NonNull IssueEntity issue) {
        this.issue = issue;
    }

    @NonNull
    public IssueEntity getIssue() {
        return issue;
    }

    @NonNull
    public UserEntity getUser() {
        return user;
    }
}
