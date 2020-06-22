package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.annotation.NonNull;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public abstract class IssueDAO {
    @Transaction
    @Query("SELECT * FROM USER")
    public abstract Flowable<List<UserWithIssues>> getAllIssues();


    @Transaction
    public void insertIssues(List<Issue> issues) {
        for (Issue issue : issues) {
            insert(PojoConverter.userToPojo(issue.getUser()));
            insert(PojoConverter.issueToPojo(issue));
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(IssuePojo issue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(UserPojo user);
}
