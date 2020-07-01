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

import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
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

        compositeDisposable.add(viewModel.getSelectedIssueObservable()
                .distinctUntilChanged()
                .subscribe(issue -> {
                    if (issue.isPresent()) {
                        setToolbarNavigationVisibility(true);
                        createIssueFragment(issue.get());
                    } else {
                        getSupportFragmentManager().popBackStack();
                    }
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                }));
    }

    @Override
    protected void onDestroy() {
        compositeDisposable.dispose();
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
