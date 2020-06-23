package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.AppDatabase;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueDAO;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.IssueWithUser;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.databases.PojoConverter;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import io.reactivex.Flowable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public final class IssuesViewModel extends AndroidViewModel {
    private final BehaviorSubject<Optional<Issue>> selectedIssueSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<GithubApi.IssueState> selectedIssuesStateSubject
            = BehaviorSubject.createDefault(GithubApi.IssueState.STATE_ALL);
    private final BehaviorSubject<Optional<List<Issue>>> issuesSubject
            = BehaviorSubject.createDefault(Optional.empty());
    private final BehaviorSubject<Boolean> refreshingSubject
            = BehaviorSubject.createDefault(true);

    private final GithubApi api = AppNetworkService.getGithubApi();
    private final AppDatabase database;
    private final IssueDAO dao;
    private final Disposable selectStateDisposable;
    private Disposable webDisposable;
    private io.reactivex.disposables.Disposable databaseDisposable;

    public IssuesViewModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getInstance(application.getApplicationContext());
        dao = database.issueDAO();
        selectStateDisposable = selectedIssuesStateSubject
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(this::obtainIssuesFromDB, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                });
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
    public Observable<Optional<List<Issue>>> getIssuesObservable() {
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
        if (!state.equals(selectedIssuesStateSubject.getValue())) {
            selectedIssuesStateSubject.onNext(state);
        }
    }

    public void reloadIssues() {
        loadIssues(1);
    }

    private void loadIssues(int page) {
        if (page == 1) {
            refreshingSubject.onNext(true);
        }
        dispose(webDisposable);
        webDisposable = api.getProjectIssues(
                GithubApi.USERNAME, GithubApi.PROJECT_NAME, GithubApi.IssueState.STATE_ALL.getState(), page)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(issues -> !issues.isEmpty())
                .subscribe(issues -> {
                    if (page == 1) {
                        database.clearAllTables();
                    }
                    dao.insertIssues(issues);
                    loadIssues(page + 1);
                }, error -> {
                    refreshingSubject.onNext(false);
                    Toast.makeText(getApplication().getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void obtainIssuesFromDB(@NonNull GithubApi.IssueState state) {
        dispose(databaseDisposable);
        Flowable<List<IssueWithUser>> flowable;
        if (state.equals(GithubApi.IssueState.STATE_OPEN)) {
            flowable = dao.getOpenIssues();
        } else if (state.equals(GithubApi.IssueState.STATE_CLOSED)) {
            flowable = dao.getClosedIssues();
        } else {
            flowable = dao.getAllIssues();
        }
        databaseDisposable = flowable
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(io.reactivex.android.schedulers.AndroidSchedulers.mainThread())
                .map(PojoConverter::issueWithUserToIssue)
                .subscribe(issues -> {
                    issuesSubject.onNext(Optional.of(issues));
                    refreshingSubject.onNext(false);
                }, error -> {
                    refreshingSubject.onNext(false);
                    issuesSubject.onNext(Optional.of(new ArrayList<>()));
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });
    }

    private void dispose() {
        dispose(databaseDisposable);
        dispose(webDisposable);
        dispose(selectStateDisposable);
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void dispose(io.reactivex.disposables.Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
