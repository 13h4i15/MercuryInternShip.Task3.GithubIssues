package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.util.Log;

import androidx.annotation.NonNull;
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
import io.reactivex.rxjava3.subjects.ReplaySubject;

public final class IssuesViewModel extends ViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private final static String userName = "alibaba";
    private final static String projectName = "atlas";

    private ReplaySubject<List<Issue>> issuesReplaySubject = ReplaySubject.create();
    private Disposable issuesAllSingleDisposable;
    private int page;

    public IssuesViewModel() {
        reloadIssues();
    }

    public void reloadIssues() {
        page = 1;
        issuesReplaySubject.cleanupBuffer();
        loadIssueList();
    }

    public void loadNewIssues() {
        loadIssueList();
    }

    @NonNull
    public ReplaySubject<List<Issue>> getIssuesReplaySubject() {
        return issuesReplaySubject;
    }

    private void loadIssueList() {
        if (issuesAllSingleDisposable != null && !issuesAllSingleDisposable.isDisposed()) return;

        Single<List<Issue>> issuesPageSingle = AppNetworkService.getGithubApi().getProjectIssues(
                userName, projectName, GithubApi.STATE_OPEN, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        issuesAllSingleDisposable = issuesPageSingle.subscribe(issueList -> {
            if (issueList != null && issueList.size() != 0) {
                issuesReplaySubject.onNext(issueList);
                ++page;
            } else {
                issuesReplaySubject.onNext(new ArrayList<>());
            }
        }, error -> {
            issuesReplaySubject.onNext(new ArrayList<>());
            Log.e(LOADING_ERROR_LOG_TAG, error.toString());
        });
    }
}
