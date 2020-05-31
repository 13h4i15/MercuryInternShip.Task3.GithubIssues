package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class IssueFragment extends Fragment {
    private Issue issue;

    public IssueFragment() {
    }

    public IssueFragment(@NonNull Issue issue) {
        this.issue = issue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issue, container, false);

        TextView title = root.findViewById(R.id.issue_fragment_title);
        TextView body = root.findViewById(R.id.issue_fragment_body);
        if (issue != null) {
            title.setText(issue.getTitle());
            body.setText(issue.getBody());
        }

        return root;
    }
}