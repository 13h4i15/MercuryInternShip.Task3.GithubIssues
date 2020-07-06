package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.User;

import java.util.ArrayList;
import java.util.List;

public class EntityConverter {
    @NonNull
    public static IssueEntity issueToEntity(@NonNull Issue issue) {
        return new IssueEntity(issue.getId(), issue.getNumber(), issue.getState(), issue.getTitle(),
                issue.getBody(), issue.getUser().getLogin());
    }

    @NonNull
    public static UserEntity userToEntity(@NonNull User user) {
        return new UserEntity(user.getLogin(), user.getAvatarUrl());
    }

    @NonNull
    public static User entityToUser(@NonNull UserEntity user) {
        return new User(user.getLogin(), user.getAvatarUrl());
    }

    @NonNull
    public static Issue issueWithUserToIssue(@NonNull IssueWithUser issueWithUser) {
        User user = entityToUser(issueWithUser.getUser());
        IssueEntity issueEntity = issueWithUser.getIssue();
        return new Issue(issueEntity.getId(), issueEntity.getNumber(), issueEntity.getState(),
                issueEntity.getTitle(), issueEntity.getBody(), user);
    }

    @NonNull
    public static List<Issue> issueWithUserToIssue(@NonNull List<IssueWithUser> issuesWithUser) {
        List<Issue> issues = new ArrayList<>();
        for (IssueWithUser issue : issuesWithUser) {
            issues.add(issueWithUserToIssue(issue));
        }
        return issues;
    }
}
