package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

public class IssueListFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issue_list, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        IssueRecyclerViewAdapter issueRecyclerViewAdapter = new IssueRecyclerViewAdapter();
        issueRecyclerViewAdapter.setOnIssueItemClickListener(getOnIssueItemClickListener());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(issueRecyclerViewAdapter);

        IssuesViewModel issuesViewModel = new ViewModelProvider(this,
                new IssuesViewModelFactory(getActivity().getApplication())).get(IssuesViewModel.class);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = issuesViewModel::reloadIssues;
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        LiveData<List<Issue>> issueLiveData = issuesViewModel.getIssues();
        TextView textView = root.findViewById(R.id.empty_list_message);
        issueLiveData.observe(getViewLifecycleOwner(), issues -> {
            swipeRefreshLayout.setRefreshing(false);
            if (issues.size() == 0) {
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                issueRecyclerViewAdapter.setIssueList(issues);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    issuesViewModel.loadNewIssues();
                }
            }
        });

        return root;
    }

    @NonNull
    private OnIssueItemClickListener getOnIssueItemClickListener() {
        return issue -> {
            FrameLayout frameLayout = getActivity().findViewById(R.id.issue_fragment);
            if (frameLayout == null) {
                Intent intent = new Intent(getActivity(), IssueActivity.class);
                intent.putExtra(IssueActivity.ISSUE_EXTRA, issue);
                startActivity(intent);
            } else {
                FragmentTransaction fragmentTransaction = getParentFragmentManager().beginTransaction();
                fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.issue_fragment, IssueFragment.newInstance(issue));
                fragmentTransaction.commit();
            }
        };
    }

    public interface OnIssueItemClickListener {
        void onClick(Issue issue);
    }
}