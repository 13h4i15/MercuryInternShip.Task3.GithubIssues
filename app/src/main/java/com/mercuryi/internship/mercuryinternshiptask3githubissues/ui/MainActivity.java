package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.workers.IssueWorker;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Disposable issuesDisposable, selectedIssueDisposable;
    private Toolbar toolbar;
    private IssuesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                IssueWorker.class, 15, TimeUnit.SECONDS, 15, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager workManager = WorkManager.getInstance(getApplicationContext());
        workManager.enqueueUniquePeriodicWork(
                IssueWorker.ISSUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest);

        if (savedInstanceState == null) createListFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        viewModel = new ViewModelProvider(this).get(IssuesViewModel.class);

        issuesDisposable = viewModel.getIssuesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(issues -> {
                    getSupportFragmentManager().popBackStack();
                }, error -> {
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });

        selectedIssueDisposable = viewModel.getSelectedIssueObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .subscribe(issue -> {
                    setToolbarNavigationVisibility(true);
                    createIssueFragment(issue);
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                });
    }

    @Override
    protected void onDestroy() {
        if (issuesDisposable != null && !issuesDisposable.isDisposed()) {
            issuesDisposable.dispose();
        }
        if (selectedIssueDisposable != null && !selectedIssueDisposable.isDisposed()) {
            selectedIssueDisposable.dispose();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        viewModel.setSelectedIssue(null);
        setToolbarNavigationVisibility(false);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_state_selection, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        viewModel.setSelectedIssue(null);
        switch (item.getItemId()) {
            case R.id.action_all_issues:
                viewModel.setState(GithubApi.IssueState.STATE_ALL);
                break;
            case R.id.action_open_issues:
                viewModel.setState(GithubApi.IssueState.STATE_OPEN);
                break;
            case R.id.action_closed_issues:
                viewModel.setState(GithubApi.IssueState.STATE_CLOSED);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, IssueListFragment.newInstance());
        fragmentTransaction.commit();
    }

    private void createIssueFragment(@NonNull Issue issue) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(doesIssueFragmentContainerExist() ? R.id.issue_fragment : R.id.list_fragment,
                IssueFragment.newInstance(issue));
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean doesIssueFragmentContainerExist() {
        return findViewById(R.id.issue_fragment) != null;
    }

    private void setToolbarNavigationVisibility(boolean isVisible) {
        if (toolbar != null) {
            if (isVisible) {
                toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            } else {
                toolbar.setNavigationIcon(null);
            }
        }
    }
}
