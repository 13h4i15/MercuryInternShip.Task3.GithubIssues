package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;

public class MainActivity extends AppCompatActivity implements IssueListFragment.IssueListFragmentContainer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            createListFragment();
        }
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, IssueListFragment.newInstance());
        fragmentTransaction.commit();
    }

    @NonNull
    public IssueListFragment.OnIssueItemClickListener requestIssueItemClickListener() {
        return issue -> {
            FragmentContainerView fragmentContainerView = findViewById(R.id.issue_fragment);
            if (fragmentContainerView == null) {
                Intent intent = new Intent(this, IssueActivity.class);
                intent.putExtra(IssueActivity.ISSUE_EXTRA, issue);
                startActivity(intent);
            } else {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.issue_fragment, IssueFragment.newInstance(issue));
                fragmentTransaction.commit();
            }
        };
    }
}
