package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModel;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;
import io.reactivex.rxjava3.subjects.ReplaySubject;

public final class IssuesViewModel extends ViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";
    private final static String userName = "alibaba";
    private final static String projectName = "atlas";

    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final ReplaySubject<List<Issue>> issuesSubject = ReplaySubject.create();
    private final GithubApi api = AppNetworkService.getGithubApi();
    private Disposable disposable;
    private int nextPage;

    public IssuesViewModel() {
        reloadIssues();
    }

    @Override
    protected void onCleared() {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        super.onCleared();
    }

    public void reloadIssues() {
        loadIssueList(1);
    }

    public void loadNewIssues() {
        loadIssueList(nextPage);
    }

    public void setSelectedIssue(@Nullable Issue issue) {
        selectedIssueSubject.onNext(Optional.ofNullable(issue));
    }

    @NonNull
    public Observable<List<Issue>> getIssuesObservable() {
        return issuesSubject;
    }

    @NonNull
    public Observable<Optional<Issue>> getSelectedIssueObservable() {
        return selectedIssueSubject;
    }

    public int getNextPage() {
        return nextPage;
    }

    private void loadIssueList(int page) {
        if (disposable != null && !disposable.isDisposed()) return;
        disposable = api.getProjectIssues(
                userName, projectName, GithubApi.STATE_OPEN, page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(issues -> {
                    if (!issues.isEmpty()) {
                        if (page == 1) {
                            selectedIssueSubject.onNext(Optional.empty());
                            issuesSubject.cleanupBuffer();
                            this.nextPage = 1;
                        }
                        issuesSubject.onNext(issues);
                        ++this.nextPage;
                    } else {
                        issuesSubject.onNext(new ArrayList<>());
                    }
                }, error -> {
                    issuesSubject.onNext(new ArrayList<>());
                    Log.e(LOADING_ERROR_LOG_TAG, error.toString());
                });
    }
}
