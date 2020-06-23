package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.User;

public class PojoConverter {
    @TypeConverter
    @NonNull
    public static IssuePojo issueToPojo(@NonNull Issue issue) {
        return new IssuePojo(issue.getId(), issue.getNumber(), issue.getState(), issue.getTitle(),
                issue.getBody(), issue.getUser().getLogin());
    }

    @TypeConverter
    @NonNull
    public static UserPojo userToPojo(@NonNull User user) {
        return new UserPojo(user.getLogin(), user.getAvatarUrl());
    }

    @TypeConverter
    @NonNull
    public static User pojoToUser(@NonNull UserPojo user) {
        return new User(user.getLogin(), user.getAvatarUrl());
    }

    @TypeConverter
    @NonNull
    public static Issue issueWithUserToIssue(@NonNull IssueWithUser issueWithUser) {
        User user = pojoToUser(issueWithUser.getUser());
        IssuePojo issuePojo = issueWithUser.getIssue();
        return new Issue(issuePojo.getId(), issuePojo.getNumber(), issuePojo.getState(),
                issuePojo.getTitle(), issuePojo.getBody(), user);
    }
}
