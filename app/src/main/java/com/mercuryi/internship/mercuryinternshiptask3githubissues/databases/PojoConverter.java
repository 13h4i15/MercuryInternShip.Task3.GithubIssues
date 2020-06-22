package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.TypeConverter;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.User;

import java.util.ArrayList;
import java.util.List;

public class PojoConverter {

    @TypeConverter
    @NonNull
    public static IssuePojo issueToPojo(@NonNull Issue issue) {
        return new IssuePojo(issue.getId(), issue.getNumber(), issue.getTitle(),
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
    public static List<Issue> userWithIssuesToList(@NonNull UserWithIssues userWithIssues) {
        List<Issue> issues = new ArrayList<>();
        User user = pojoToUser(userWithIssues.getUser());
        if (userWithIssues.getIssues() != null) {
            for (IssuePojo issuePojo : userWithIssues.getIssues()) {
                issues.add(new Issue(issuePojo.getId(), issuePojo.getNumber(), issuePojo.getTitle(),
                        issuePojo.getBody(), user));
            }
        }
        return issues;
    }
}
