package com.mercuryi.internship.mercuryinternshiptask3githubissues.application;

import android.app.Application;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.workers.IssueWorker;

import java.util.concurrent.TimeUnit;

public class IssuesApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                IssueWorker.class, 15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(
                IssueWorker.ISSUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }
}
