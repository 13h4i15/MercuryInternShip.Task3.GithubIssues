package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public final class IssuesViewModel extends ViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private final static String userName = "alibaba";
    private final static String projectName = "atlas";

    private MutableLiveData<List<Issue>> issues = new MutableLiveData<>();
    private int page;
    private Disposable issuesAllSingleDisposable;

    public IssuesViewModel() {
        if (isIssuesEmpty()) {
            reloadIssues();
        } else {
            page = (issues.getValue().size() - 1) / GithubApi.ITEMS_ON_PAGE_COUNT + 1;
        }
    }

    public void reloadIssues() {
        page = 1;
        loadIssueList(true);
    }

    public void loadNewIssues() {
        loadIssueList(false);
    }

    @NonNull
    public LiveData<List<Issue>> getIssues() {
        return issues;
    }

    private void loadIssueList(boolean isReload) {
        if (issuesAllSingleDisposable != null && !issuesAllSingleDisposable.isDisposed()) return;

        Single<List<Issue>> issuesPageObservable = AppNetworkService.getGithubApi().getProjectIssues(
                userName, projectName, GithubApi.STATE_OPEN, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        issuesAllSingleDisposable = issuesPageObservable.subscribe(issueList -> {
            if (issueList != null && issueList.size() != 0) {
                List<Issue> resList = isIssuesEmpty() || isReload ? new ArrayList<>() : issues.getValue();
                resList.addAll(issueList);
                issues.setValue(resList);
                ++page;
            } else if (isIssuesEmpty()) {
                issues.setValue(new ArrayList<>());
            }
        }, error -> {
            if (isIssuesEmpty()) {
                issues.setValue(new ArrayList<>());
            }
            Log.e(LOADING_ERROR_LOG_TAG, error.toString());
        });
    }

    private boolean isIssuesEmpty() {
        return issues.getValue() == null || issues.getValue().isEmpty();
    }
}
