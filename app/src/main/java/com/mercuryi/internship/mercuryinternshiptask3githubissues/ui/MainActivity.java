package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class MainActivity extends AppCompatActivity implements IssueListFragment.IssueListFragmentContainer {
    private final static String ISSUE_LIST_FRAGMENT_TAG = "issueListFragment";
    private final static String ISSUE_FRAGMENT_TAG = "issueFragment";

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
        setToolbarNavigationVisability(false);
        super.onBackPressed();
    }

    @Override
    @NonNull
    public IssueListFragment.OnIssueItemSelectListener getIssueItemSelectListener() {
        return issue -> {
            setToolbarNavigationVisability(true);
            createIssueFragment(issue);
        };
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, IssueListFragment.newInstance(), ISSUE_LIST_FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    private void createIssueFragment(@NonNull Issue issue) {
        getSupportFragmentManager().popBackStack();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(isIssueFragmentContainerExist() ? R.id.issue_fragment : R.id.list_fragment,
                IssueFragment.newInstance(issue), ISSUE_FRAGMENT_TAG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private boolean isIssueFragmentContainerExist() {
        FragmentContainerView fragmentContainerView = findViewById(R.id.issue_fragment);
        return fragmentContainerView != null;
    }

    private void setToolbarNavigationVisability(boolean isVisible) {
        if (isVisible) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        } else {
            toolbar.setNavigationIcon(null);
        }
    }
}
