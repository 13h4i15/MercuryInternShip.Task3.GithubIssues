package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

import io.reactivex.rxjava3.subjects.ReplaySubject;

public class IssueListFragment extends Fragment {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";
    private final static String ISSUE_EXTRA = "issue";

    private OnIssueItemClickListener onIssueItemClickListener;
    private Issue selectedIssue;
    private IssueRecyclerViewAdapter issueRecyclerViewAdapter;

    public static IssueListFragment newInstance() {
        return new IssueListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IssueListFragmentContainer) {
            onIssueItemClickListener = ((IssueListFragmentContainer) context).requestIssueItemClickListener();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        if (savedInstanceState != null) {
            selectedIssue = savedInstanceState.getParcelable(ISSUE_EXTRA);
        }

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        issueRecyclerViewAdapter = new IssueRecyclerViewAdapter(selectedIssue);

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(issueRecyclerViewAdapter);
        if (onIssueItemClickListener != null) {
            issueRecyclerViewAdapter.setOnIssueItemClickListener(onIssueItemClickListener);
        }
        IssuesViewModel issuesViewModel = new ViewModelProvider(requireActivity()).get(IssuesViewModel.class);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
            issueRecyclerViewAdapter.clearIssueList();
            issuesViewModel.reloadIssues();
        };
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        ReplaySubject<List<Issue>> issuesReplaySubject = issuesViewModel.getIssuesReplaySubject();
        TextView textView = root.findViewById(R.id.empty_list_message);
        issuesReplaySubject.subscribe(issues -> {
            swipeRefreshLayout.setRefreshing(false);
            if (issues.size() > 0 || issueRecyclerViewAdapter.getItemCount() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                issueRecyclerViewAdapter.addToIssueList(issues);
            } else if (issueRecyclerViewAdapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }, error -> {
            Log.e(LOADING_ERROR_LOG_TAG, error.toString());
            swipeRefreshLayout.setRefreshing(false);
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

        if (selectedIssue != null) {
            onIssueItemClickListener.onClick(selectedIssue);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ISSUE_EXTRA, selectedIssue);
    }

    public void setSelectedIssue(@Nullable Issue selectedIssue) {
        this.selectedIssue = selectedIssue;
        if (issueRecyclerViewAdapter != null) {
            issueRecyclerViewAdapter.setSelectedIssue(selectedIssue);
        }
    }

    public interface IssueListFragmentContainer {
        OnIssueItemClickListener requestIssueItemClickListener();
    }

    public interface OnIssueItemClickListener {
        void onClick(Issue issue);
    }
}