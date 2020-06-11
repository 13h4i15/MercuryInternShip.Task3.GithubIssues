package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class MainActivity extends AppCompatActivity implements IssueListFragment.IssueListFragmentContainer {

    private Toolbar toolbar;
    private IssuesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) createListFragment();

        viewModel = new ViewModelProvider(this).get(IssuesViewModel.class);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    @Override
    public void onBackPressed() {
        viewModel.setSelectedIssueId(null);
        setToolbarNavigationVisibility(false);
        super.onBackPressed();
    }

    @Override
    @NonNull
    public IssueListFragment.OnIssueItemSelectListener getIssueItemSelectListener() {
        return issue -> {
            setToolbarNavigationVisibility(true);
            createIssueFragment(issue);
        };
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
        if (isVisible) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }
}
