package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.AppDatabase;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueDAO;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.PojoConverter;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.UserWithIssues;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public final class IssuesViewModel extends AndroidViewModel {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";
    private final static String userName = "alibaba";
    private final static String projectName = "atlas";

    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<List<Issue>> issuesSubject = BehaviorSubject.createDefault(new ArrayList<>());
    private final GithubApi api = AppNetworkService.getGithubApi();
    private final IssueDAO dao;
    private Disposable webDisposable;
    private io.reactivex.disposables.Disposable databaseDisposable;
    private int nextPage;

    public IssuesViewModel(@NonNull Application application) {
        super(application);
        dao = AppDatabase.getInstance(application.getApplicationContext()).issueDAO();
        obtainIssuesFromDB();
        reloadIssues();
    }

    @Override
    protected void onCleared() {
        if (webDisposable != null && !webDisposable.isDisposed()) {
            webDisposable.dispose();
        }
        if (databaseDisposable != null && !databaseDisposable.isDisposed()) {
            databaseDisposable.dispose();
        }
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

    public int getCurrentPage() {
        return nextPage - 1;
    }

    private void loadIssueList(int page) {
        if (webDisposable != null && !webDisposable.isDisposed()) return;
        webDisposable = api.getProjectIssues(
                userName, projectName, GithubApi.STATE_OPEN, page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(issues -> {
                    if (!issues.isEmpty()) {
                        if (page == 1) {
                            selectedIssueSubject.onNext(Optional.empty());
                        }
                        dao.insertIssues(issues);
                        nextPage = page + 1;
                    }
                }, error -> {
                    Log.e(LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void obtainIssuesFromDB() {
        databaseDisposable = dao.getAllIssues()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(usersWithIssues -> {
                    List<Issue> issues = new ArrayList<>();
                    for (UserWithIssues user : usersWithIssues) {
                        issues.addAll(PojoConverter.userWithIssuesToList(user));
                    }
                    Collections.sort(issues, (issue1, issue2) -> issue2.getNumber() - issue1.getNumber());
                    issuesSubject.onNext(issues);
                });
    }
}
