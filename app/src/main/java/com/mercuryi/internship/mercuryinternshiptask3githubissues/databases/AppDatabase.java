package com.mercuryi.internship.mercuryinternshiptask3githubissues.databases;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {IssuePojo.class, UserPojo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    private final static String DB_NAME = "issues";

    private static AppDatabase instance;

    public abstract IssueDAO issueDAO();

    public static synchronized AppDatabase getInstance(Context appContext) {
        if (instance == null) {
            instance = Room.databaseBuilder(appContext, AppDatabase.class, DB_NAME).build();
        }
        return instance;
    }
}
