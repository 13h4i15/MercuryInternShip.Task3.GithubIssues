package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.GithubApi;

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

        if (savedInstanceState == null) createListFragment();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        viewModel = new ViewModelProvider(this).get(IssuesViewModel.class);

        issuesDisposable = viewModel.getIssuesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(issues -> {
                    if (!issues.isEmpty() && viewModel.getCurrentPage() == 1) {
                        getSupportFragmentManager().popBackStack();
                    }
                }, error -> {
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });

        selectedIssueDisposable = viewModel.getSelectedIssueObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedIssue -> {
                    selectedIssue.ifPresent(issue -> {
                        setToolbarNavigationVisibility(true);
                        createIssueFragment(issue);
                    });
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
