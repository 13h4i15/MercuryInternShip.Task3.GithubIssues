package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Relation;

import java.util.List;


public class UserWithIssues {
    @Embedded
    private UserPojo user;

    @Relation(
            parentColumn = "login",
            entityColumn = "user_login",
            entity = IssuePojo.class
    )
    private List<IssuePojo> issues;

    @Nullable
    public List<IssuePojo> getIssues() {
        return issues;
    }

    public void setIssues(List<IssuePojo> issues) {
        this.issues = issues;
    }

    public UserPojo getUser() {
        return user;
    }

    public void setUser(UserPojo user) {
        this.user = user;
    }
}
