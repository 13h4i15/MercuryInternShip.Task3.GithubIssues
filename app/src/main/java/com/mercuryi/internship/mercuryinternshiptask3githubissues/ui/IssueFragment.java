package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

/**
 * A simple {@link Fragment} subclass.
 */
public class IssueFragment extends Fragment {
    private final Issue issue;

    public IssueFragment(Issue issue) {
        this.issue = issue;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issue, container, false);

        Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> Toast.makeText(getContext(), "FF", Toast.LENGTH_LONG).show());
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        TextView title = root.findViewById(R.id.issue_fragment_title);
        TextView body = root.findViewById(R.id.issue_fragment_body);
        title.setText(issue.getTitle());
        body.setText(issue.getBody());
        return root;

    }
}
