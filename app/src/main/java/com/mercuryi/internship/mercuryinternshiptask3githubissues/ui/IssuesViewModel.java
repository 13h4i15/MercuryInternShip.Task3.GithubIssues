package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

final class IssuesViewModel extends AndroidViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private MutableLiveData<List<Issue>> issues = new MutableLiveData<>();
    private int page;
    private Call call;

    public IssuesViewModel(Application application) {
        super(application);
        if (isIssuesEmpty()) {
            reloadIssues();
        } else {
            page = (issues.getValue().size() - 1) / 30 + 1;
        }
    }

    public void reloadIssues() {
        page = 0;
        loadIssueList(true);
    }

    public void loadNewIssues() {
        loadIssueList(false);
    }

    @NonNull
    public LiveData<List<Issue>> getIssues() {
        return issues;
    }

    private void loadIssueList(boolean isReloading) {
        if (call != null && !call.isExecuted()) return;
        boolean isNeedToSetBlankList = isIssuesEmpty() || isReloading;

        call = AppNetworkService.getGithubApi().getProjectIssues(
                "alibaba", "atlas", GithubApi.STATE_OPEN, page);
        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(@NonNull Call<List<Issue>> call, @NonNull Response<List<Issue>> response) {
                List<Issue> result = response.body();
                if (result != null && result.size() != 0) {
                    List<Issue> resList = isNeedToSetBlankList ? new ArrayList<>() : issues.getValue();
                    resList.addAll(result);
                    issues.setValue(resList);
                    ++page;
                } else if (isNeedToSetBlankList) {
                    issues.setValue(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Issue>> call, @NonNull Throwable t) {
                if (isNeedToSetBlankList) {
                    issues.setValue(new ArrayList<>());
                }
                Log.e(LOADING_ERROR_LOG_TAG, t.getMessage());
            }
        });
    }

    private boolean isIssuesEmpty() {
        return issues.getValue() == null || issues.getValue().size() == 0;
    }
}
