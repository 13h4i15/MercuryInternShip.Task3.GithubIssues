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
    private final static String ISSUE_EXTRA = "issue";
    private final static int ISSUE_ACTIVITY_REQUEST_CODE = 0;


    private Issue selectedIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            createListFragment();
        } else {
            selectedIssue = savedInstanceState.getParcelable(ISSUE_EXTRA);
        }

        if (selectedIssue != null) {
            requestIssueItemClickListener().onClick(selectedIssue);
        } else if (isIssueFragmentExist()) {
            createIssueFragment(null);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ISSUE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            selectedIssue = null;
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ISSUE_EXTRA, selectedIssue);
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, IssueListFragment.newInstance());
        fragmentTransaction.commit();
    }

    @NonNull
    public IssueListFragment.OnIssueItemClickListener requestIssueItemClickListener() {
        return issue -> {
            selectedIssue = issue;
            if (!isIssueFragmentExist()) {
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

    private boolean isIssueFragmentExist() {
        FragmentContainerView fragmentContainerView = findViewById(R.id.issue_fragment);
        return fragmentContainerView != null;
    }
}
