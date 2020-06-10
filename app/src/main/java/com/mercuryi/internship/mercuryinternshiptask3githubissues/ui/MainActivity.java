package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class MainActivity extends AppCompatActivity implements IssueListFragment.IssueListFragmentContainer {
    private final static int ISSUE_ACTIVITY_REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) createListFragment();

        if (isIssueFragmentContainerExist()) createIssueFragment(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IssueListFragment issueListFragment = (IssueListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.list_fragment);
        if (requestCode == ISSUE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK
                && issueListFragment != null) {
            issueListFragment.setSelectedIssue(null);
        }
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, IssueListFragment.newInstance());
        fragmentTransaction.commit();
    }

    @Override
    @NonNull
    public IssueListFragment.OnIssueItemClickListener getIssueItemClickListener() {
        return issue -> {
            IssueListFragment issueListFragment = (IssueListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.list_fragment);
            if (issueListFragment == null) return;
            issueListFragment.setSelectedIssue(issue);
            if (!isIssueFragmentContainerExist()) {
                Intent intent = new Intent(this, IssueActivity.class);
                intent.putExtra(IssueActivity.ISSUE_EXTRA, issue);
                startActivityForResult(intent, ISSUE_ACTIVITY_REQUEST_CODE);
            } else {
                createIssueFragment(issue);
            }
        };
    }

    private void createIssueFragment(Issue issue) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.replace(R.id.issue_fragment, IssueFragment.newInstance(issue));
        fragmentTransaction.commit();
    }

    private boolean isIssueFragmentContainerExist() {
        FragmentContainerView fragmentContainerView = findViewById(R.id.issue_fragment);
        return fragmentContainerView != null;
    }
}
