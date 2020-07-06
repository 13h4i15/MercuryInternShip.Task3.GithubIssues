package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {IssueEntity.class, UserEntity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private final static String DB_NAME = "issues";

    private static AppDatabase INSTANCE;

    public abstract IssueDao issueDao();

    public static synchronized AppDatabase getInstance(Context appContext) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME).build();
        }
        return INSTANCE;
    }
}
