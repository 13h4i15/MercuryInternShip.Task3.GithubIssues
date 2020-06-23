package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Relation;

public class IssueWithUser {
    @Embedded
    private IssuePojo issue;

    @Relation(
            parentColumn = "user_login",
            entityColumn = "login",
            entity = UserPojo.class
    )
    private UserPojo user;

    public IssueWithUser() {
    }

    public void setUser(@NonNull UserPojo user) {
        this.user = user;
    }

    public void setIssue(@NonNull IssuePojo issue) {
        this.issue = issue;
    }

    @NonNull
    public IssuePojo getIssue() {
        return issue;
    }

    @NonNull
    public UserPojo getUser() {
        return user;
    }
}
