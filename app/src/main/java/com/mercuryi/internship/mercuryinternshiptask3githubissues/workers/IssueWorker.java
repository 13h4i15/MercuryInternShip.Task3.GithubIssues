package com.mercuryi.internship.mercuryinternshiptask3githubissues.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.AppDatabase;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueDao;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.disposables.Disposable;

public class IssueWorker extends Worker {
    public final static String ISSUE_WORK_NAME = "issue_work";
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private final GithubApi api = AppNetworkService.getGithubApi();
    private final List<Issue> issues = new ArrayList<>();
    private final IssueDao dao;
    private final AppDatabase database;
    private Disposable disposable;

    public IssueWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        database = AppDatabase.getInstance(context.getApplicationContext());
        dao = database.issueDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        issues.clear();
        loadIssues(1);
        return Result.success();
    }

    @Override
    public void onStopped() {
        dispose();
        super.onStopped();
    }

    private void loadIssues(int page) {
        dispose();
        disposable = api.getProjectIssues(
                GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page)
                .subscribe(issues -> {
                    if (!issues.isEmpty()) {
                        this.issues.addAll(issues);
                        loadIssues(page + 1);
                    } else {
                        database.clearAllTables();
                        dao.insertIssues(this.issues);
                    }
                }, error -> {
                    Log.e(LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void dispose() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
