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
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public final class IssuesViewModel extends ViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private final static String userName = "alibaba";
    private final static String projectName = "atlas";

    private final ReplaySubject<List<Issue>> replaySubject = ReplaySubject.create();
    private Disposable disposable;
    private GithubApi api = AppNetworkService.getGithubApi();
    private int page;

    public IssuesViewModel() {
        reloadIssues();
    }

    @Override
    protected void onCleared() {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        super.onCleared();
    }

    public void reloadIssues() {
        page = 1;
        replaySubject.cleanupBuffer();
        loadIssueList();
    }

    public void loadNewIssues() {
        loadIssueList();
    }

    @NonNull
    public Observable<List<Issue>> getIssuesObservable() {
        return replaySubject;
    }

    private void loadIssueList() {
        if (disposable != null && !disposable.isDisposed()) return;
        Single<List<Issue>> single = api.getProjectIssues(
                userName, projectName, GithubApi.STATE_OPEN, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposable = single.subscribe(issues -> {
            if (!issues.isEmpty()) {
                replaySubject.onNext(issues);
                ++page;
            } else {
                replaySubject.onNext(new ArrayList<>());
            }
        }, error -> {
            replaySubject.onNext(new ArrayList<>());
            Log.e(LOADING_ERROR_LOG_TAG, error.toString());
        });
    }
}
