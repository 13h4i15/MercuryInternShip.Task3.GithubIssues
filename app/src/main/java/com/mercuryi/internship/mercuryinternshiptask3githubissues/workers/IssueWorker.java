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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

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

    private List<Issue> loadIssues() {
        GithubApi api = AppNetworkService.getGithubApi();
        List<Issue> issues = new ArrayList<>();
        for (int page = 1; ; page++) {
            Call<List<Issue>> call = api.getProjectIssues(
                    GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page);
            try {
                Response<List<Issue>> response = call.execute();
                List<Issue> newIssues = response.body();
                if (response.isSuccessful()) {
                    if (newIssues != null && !newIssues.isEmpty()) {
                        issues.addAll(newIssues);
                    } else {
                        break;
                    }
                } else {
                    issues = null;
                    break;
                }
            } catch (IOException exception) {
                Log.e(LOADING_ERROR_LOG_TAG, exception.toString());
                issues = null;
                break;
            }
        }
        return issues;
    }

    private void saveIssues(@Nullable List<Issue> issues) {
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        IssueDao dao = database.issueDao();
        database.clearAllTables();
        if (issues != null) {
            dao.insertIssues(issues);
        }
    }
}
