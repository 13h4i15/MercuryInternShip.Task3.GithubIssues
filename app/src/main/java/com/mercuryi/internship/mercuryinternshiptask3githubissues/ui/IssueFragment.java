package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

public class IssueFragment extends Fragment {
    private final static String EXTRA_PARCELABLE_ISSUE_KEY = "selectedIssue";

    public static IssueFragment newInstance(@NonNull Issue selectedIssue) {
        IssueFragment issueFragment = new IssueFragment();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_PARCELABLE_ISSUE_KEY, selectedIssue);
        issueFragment.setArguments(args);
        return issueFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        if (getArguments() != null && getArguments().getParcelable(EXTRA_PARCELABLE_ISSUE_KEY) != null) {
            TextView title = root.findViewById(R.id.issue_fragment_title);
            TextView body = root.findViewById(R.id.issue_fragment_body);
            Issue selectedIssue = getArguments().getParcelable(EXTRA_PARCELABLE_ISSUE_KEY);
            title.setText(selectedIssue.getTitle());
            body.setText(selectedIssue.getBody());
        }
    }
}