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
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public final class IssuesViewModel extends AndroidViewModel {

    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<GithubApi.IssueState> selectedIssuesStateSubject
            = BehaviorSubject.createDefault(GithubApi.IssueState.STATE_OPEN);
    private final BehaviorSubject<List<Issue>> issuesSubject
            = BehaviorSubject.createDefault(new ArrayList<>());
    private final GithubApi api = AppNetworkService.getGithubApi();
    private final List<Issue> issues = new ArrayList<>();
    private final AppDatabase database;
    private final IssueDAO dao;
    private Disposable webDisposable;
    private io.reactivex.disposables.Disposable databaseDisposable;
    private int nextPage;

    public IssuesViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        dao = database.issueDAO();
        obtainIssuesFromDB();
        reloadIssues();
    }

    @Override
    protected void onCleared() {
        dispose();
        super.onCleared();
    }

    public void setSelectedIssue(@Nullable Issue issue) {
        if (!selectedIssueSubject.getValue().isPresent() || !selectedIssueSubject.getValue().get().equals(issue))
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

    public void setState(@NonNull GithubApi.IssueState state) {
        if (!state.equals(selectedIssuesStateSubject.getValue())) {
            selectedIssuesStateSubject.onNext(state);
        }
    }

    public void reloadIssues() {
        issues.clear();
        loadIssues(1);
    }

    private void loadIssues(int page) {
        if (webDisposable != null && !webDisposable.isDisposed()) return;
        webDisposable = api.getProjectIssues(
                GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(issues -> {
                    if (!issues.isEmpty()) {
                        if (page == 1) {
                            database.clearAllTables();
                        }
                        dao.insertIssues(issues);
                        loadIssues(page + 1);
                    }
                }, error -> {
                    issuesSubject.onError(error);
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void obtainIssuesFromDB() {
        dispose();
        databaseDisposable = dao.getAllIssues()
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(usersWithIssues -> {
                    List<Issue> issues = new ArrayList<>();
                    for (UserWithIssues user : usersWithIssues) {
                        issues.addAll(PojoConverter.userWithIssuesToList(user));
                    }
                    Collections.sort(issues, (issue1, issue2) -> issue2.getNumber() - issue1.getNumber());
                    nextPage = (issues.size()) / GithubApi.ISSUES_ON_PAGE + 1;
                    issuesSubject.onNext(issues);
                }, error -> {
                    issuesSubject.onError(error);
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void dispose() {
        if (databaseDisposable != null && !databaseDisposable.isDisposed()) {
            databaseDisposable.dispose();
        }
        if (webDisposable != null && !webDisposable.isDisposed()) {
            webDisposable.dispose();
        }
    }
}
