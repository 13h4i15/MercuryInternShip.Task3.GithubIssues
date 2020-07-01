package com.mercuryi.internship.mercuryinternshiptask3githubissues.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.AppDatabase;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueDao;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IssueWorker extends Worker {
    public final static String ISSUE_WORK_NAME = "issue_work";
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    public IssueWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        saveIssues(loadIssues());
        return Result.success();
    }

    @Nullable
    private List<Issue> loadIssues() {
        GithubApi api = AppNetworkService.getGithubApi();
        List<Issue> issues = new ArrayList<>();
        int page = 1;
        while (true) {
            Optional<List<Issue>> issuesPage = api.getProjectIssues(
                    GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page++)
                    .map(Optional::of)
                    .doOnError(error -> Log.e(LOADING_ERROR_LOG_TAG, error.toString()))
                    .onErrorReturnItem(Optional.empty())
                    .blockingGet();
            if (!issuesPage.isPresent()) {
                return null;
            } else if (issuesPage.get().isEmpty()) {
                break;
            } else {
                issues.addAll(issuesPage.get());
            }
        }
        return issues;
    }

    private void saveIssues(@Nullable List<Issue> issues) {
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        IssueDao dao = database.issueDao();
        if (issues != null) {
            database.clearAllTables();
            dao.insertIssues(issues);
        }
    }
}
