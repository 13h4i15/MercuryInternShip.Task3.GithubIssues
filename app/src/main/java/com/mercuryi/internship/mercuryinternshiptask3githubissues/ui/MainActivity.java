package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import io.reactivex.rxjava3.disposables.Disposable;

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

        issuesDisposable = viewModel.getIssuesObservable().subscribe(issues -> {
            if (!issues.isEmpty() && viewModel.getCurrentPage() == 1) {
                getSupportFragmentManager().popBackStack();
            }
        });

        selectedIssueDisposable = viewModel.getSelectedIssueObservable().subscribe(selectedIssue -> {
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
