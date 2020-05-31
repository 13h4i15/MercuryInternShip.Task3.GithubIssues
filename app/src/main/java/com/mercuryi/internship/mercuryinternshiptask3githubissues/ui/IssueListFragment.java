package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

public class IssueListFragment extends Fragment {
    private IssueRecyclerViewAdapter issueRecyclerViewAdapter;
    private RecyclerView.OnScrollListener scrollListener;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        issueRecyclerViewAdapter = new IssueRecyclerViewAdapter(getOnIssueItemClickListener());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        recyclerView.setAdapter(issueRecyclerViewAdapter);

        recyclerView.addOnScrollListener(scrollListener);

        return view;
    }

    public void clearIssueList() {
        issueRecyclerViewAdapter.clearIssueList();
    }

    public void addIssueList(@NonNull List<Issue> issueList) {
        issueRecyclerViewAdapter.addIssueList(issueList);
    }

    public void setScrollListener(@NonNull RecyclerView.OnScrollListener scrollListener) {
        this.scrollListener = scrollListener;
    }

    @NonNull
    private OnIssueItemClickListener getOnIssueItemClickListener() {
        return issue -> {
            FrameLayout frameLayout = getActivity().findViewById(R.id.issue_fragment);
            if (frameLayout != null) {
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.issue_fragment, new IssueFragment(issue));
                fragmentTransaction.commit();
                return;
            }
            Intent intent = new Intent(getActivity(), IssueActivity.class);
            intent.putExtra(IssueActivity.ISSUE_EXTRA, issue);
            startActivity(intent);
        };
    }

    interface OnIssueItemClickListener {
        void onClick(Issue issue);
    }
}