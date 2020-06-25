package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import java.util.List;

import io.reactivex.Observable;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

@Dao
public abstract class IssueDao {
    @Transaction
    @Query("SELECT * FROM Issue ORDER BY NUMBER DESC;")
    public abstract Observable<List<IssueWithUser>> getAllIssues();

    @Transaction
    @Query("SELECT * FROM Issue WHERE STATE = 'open' ORDER BY NUMBER DESC;")
    public abstract Observable<List<IssueWithUser>> getOpenIssues();

    @Transaction
    @Query("SELECT * FROM Issue WHERE STATE = 'closed' ORDER BY NUMBER DESC;")
    public abstract Observable<List<IssueWithUser>> getClosedIssues();

    @Transaction
    public void insertIssues(List<Issue> issues) {
        for (Issue issue : issues) {
            insert(EntityConverter.userToEntity(issue.getUser()));
            insert(EntityConverter.issueToEntity(issue));
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(IssueEntity issue);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(UserEntity user);
}
