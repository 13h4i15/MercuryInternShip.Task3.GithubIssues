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

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;

public final class IssuesViewModel extends AndroidViewModel {
    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<GithubApi.IssueState> selectedIssuesStateSubject
            = BehaviorSubject.createDefault(GithubApi.IssueState.STATE_ALL);
    private final BehaviorSubject<List<Issue>> issuesSubject
            = BehaviorSubject.create();
    private final BehaviorSubject<Boolean> refreshingSubject
            = BehaviorSubject.createDefault(false);
    private final GithubApi api = AppNetworkService.getGithubApi();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private final AppDatabase database;
    private final IssueDao dao;

    private Disposable databaseDisposable;

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
        loadIssues();
    }

    @Override
    protected void onCleared() {
        compositeDisposable.dispose();
        super.onCleared();
    }

    public void setSelectedIssue(@Nullable Issue issue) {
        selectedIssueSubject.onNext(Optional.ofNullable(issue));
    }

    public Optional<Issue> getSelectedIssue() {
        return selectedIssueSubject.getValue();
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
        refreshingSubject.onNext(true);
        loadIssues();
    }

    private void loadIssues() {
        compositeDisposable.add(Completable.fromAction(() -> {
            int page = 1;
            List<Issue> issuesPage;
            do {
                issuesPage = api.getProjectIssues(
                        GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page++)
                        .blockingGet();
                if (page == 1) {
                    setSelectedIssue(null);
                    database.clearAllTables();
                }
                dao.insertIssues(issuesPage);
            } while (!issuesPage.isEmpty());
        }).subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(() -> {
                    refreshingSubject.onNext(false);
                }, error -> {
                    refreshingSubject.onNext(false);
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                }));
    }

    private void obtainIssuesFromDB(@NonNull GithubApi.IssueState state) {
        if (databaseDisposable != null && !databaseDisposable.isDisposed()) {
            databaseDisposable.dispose();
        }
        Observable<List<IssueWithUser>> observable;
        if (state.equals(GithubApi.IssueState.STATE_OPEN)) {
            observable = dao.getOpenIssues();
        } else if (state.equals(GithubApi.IssueState.STATE_CLOSED)) {
            observable = dao.getClosedIssues();
        } else {
            observable = dao.getAllIssues();
        }
        databaseDisposable = observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(EntityConverter::issueWithUserToIssue)
                .subscribe(issuesSubject::onNext, error -> {
                    issuesSubject.onNext(new ArrayList<>());
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });
    }
}
