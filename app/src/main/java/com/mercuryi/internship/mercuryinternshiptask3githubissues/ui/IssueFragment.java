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
    private Issue selectedIssue;

    public IssueFragment() {
    }

    public IssueFragment(Issue selectedIssue) {
        this.selectedIssue = selectedIssue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issue, container, false);
        TextView title = root.findViewById(R.id.issue_fragment_title);
        TextView body = root.findViewById(R.id.issue_fragment_body);

        if (selectedIssue != null) {
            title.setText(selectedIssue.getTitle());
            body.setText(selectedIssue.getBody());
        }

        return root;
    }
}