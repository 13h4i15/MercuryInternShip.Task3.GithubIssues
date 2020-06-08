package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class IssueActivity extends AppCompatActivity {
    public final static String ISSUE_EXTRA = "issue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.toolbar_background));
        }

        Issue issue = getIntent().getParcelableExtra(ISSUE_EXTRA);
        if (issue != null) {
            getSupportActionBar().setTitle(issue.getTitle());
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.issue_fragment, IssueFragment.newInstance(issue));
            fragmentTransaction.commit();
        }
    }

    @Override
    protected void onDestroy() {
        setResult(RESULT_CANCELED);
        finish();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
