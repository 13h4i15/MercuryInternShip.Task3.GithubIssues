package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.AppDatabase;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueDao;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueWithUser;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.EntityConverter;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public final class IssuesViewModel extends AndroidViewModel {
    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<GithubApi.IssueState> selectedIssuesStateSubject
            = BehaviorSubject.createDefault(GithubApi.IssueState.STATE_ALL);
    private final BehaviorSubject<List<Issue>> issuesSubject
            = BehaviorSubject.create();
    private final BehaviorSubject<Boolean> refreshingSubject
            = BehaviorSubject.createDefault(true);

    private final GithubApi api = AppNetworkService.getGithubApi();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private Disposable webDisposable;
    private final AppDatabase database;
    private final IssueDao dao;
    private io.reactivex.disposables.Disposable databaseDisposable;

    public IssuesViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        dao = database.issueDao();
        compositeDisposable.add(selectedIssuesStateSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .distinctUntilChanged()
                .subscribe(state -> {
                    setSelectedIssue(null);
                    obtainIssuesFromDB(state);
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                }));
        compositeDisposable.add(refreshingSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .filter(isRefreshing -> isRefreshing)
                .distinctUntilChanged()
                .subscribe(isRefreshing -> {
                    setSelectedIssue(null);
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                }));
        reloadIssues();
    }

    @Override
    protected void onCleared() {
        dispose();
        super.onCleared();
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

    @NonNull
    public Observable<Boolean> getRefreshingObservable() {
        return refreshingSubject;
    }

    public void setState(@NonNull GithubApi.IssueState state) {
        selectedIssuesStateSubject.onNext(state);
    }

    public void reloadIssues() {
        loadIssues(1);
    }

    private void loadIssues(int page) {
        if (page == 1) {
            refreshingSubject.onNext(true);
        }
        webDisposable = api.getProjectIssuesAsynchronous(
                GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(issues -> {
                    if (page == 1) {
                        database.clearAllTables();
                    }
                    if (!issues.isEmpty()) {
                        dao.insertIssues(issues);
                        loadIssues(page + 1);
                    }
                }, error -> {
                    refreshingSubject.onNext(false);
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });

    }

    private void obtainIssuesFromDB(@NonNull GithubApi.IssueState state) {
        dispose(databaseDisposable);
        io.reactivex.Observable<List<IssueWithUser>> observable;
        if (state.equals(GithubApi.IssueState.STATE_OPEN)) {
            observable = dao.getOpenIssues();
        } else if (state.equals(GithubApi.IssueState.STATE_CLOSED)) {
            observable = dao.getClosedIssues();
        } else {
            observable = dao.getAllIssues();
        }
        databaseDisposable = observable
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .map(EntityConverter::issueWithUserToIssue)
                .subscribe(issues -> {
                    issuesSubject.onNext(issues);
                    refreshingSubject.onNext(false);
                }, error -> {
                    refreshingSubject.onNext(false);
                    issuesSubject.onNext(new ArrayList<>());
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void dispose() {
        dispose(webDisposable);
        dispose(databaseDisposable);
        dispose(compositeDisposable);
    }

    private void dispose(CompositeDisposable compositeDisposable) {
        if (compositeDisposable != null && !compositeDisposable.isDisposed()) {
            compositeDisposable.dispose();
        }
    }

    private void dispose(io.reactivex.disposables.Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
