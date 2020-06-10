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

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;

public class IssueListFragment extends Fragment {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private Disposable issuesDisposable, selectedIssueDisposable;
    private OnIssueItemSelectListener itemClickListener;
    private IssueRecyclerViewAdapter adapter;
    private boolean isReload = false;

    public static IssueListFragment newInstance() {
        return new IssueListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IssueListFragmentContainer) {
            itemClickListener = ((IssueListFragmentContainer) context).getIssueItemSelectListener();
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

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IssueRecyclerViewAdapter();

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(adapter);

        IssuesViewModel viewModel = new ViewModelProvider(requireActivity()).get(IssuesViewModel.class);
        if (itemClickListener != null) adapter.setOnItemSelectListener(issue -> {
            viewModel.setSelectedIssueId(issue.getId());
        });

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
            isReload = true;
            viewModel.reloadIssues();
        };
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        Observable<List<Issue>> issuesObservable = viewModel.getIssuesObservable();
        TextView textView = root.findViewById(R.id.empty_list_message);
        issuesDisposable = issuesObservable.subscribe(issues -> {
            swipeRefreshLayout.setRefreshing(false);
            if (!issues.isEmpty()) {
                if (isReload) {
                    adapter.clearIssues();
                    getParentFragmentManager().popBackStack();
                }
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                adapter.addToIssues(issues);
            } else if (adapter.getItemCount() == 0) {
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
            isReload = false;
        }, error -> {
            Log.e(LOADING_ERROR_LOG_TAG, error.toString());
            swipeRefreshLayout.setRefreshing(false);
            isReload = false;
        });

        Observable<String> selectedIssueObservable = viewModel.getSelectedIssueObservable();
        selectedIssueDisposable = selectedIssueObservable.subscribe(issueId -> {
            Issue selectedIssue = adapter.getIssueById(issueId);
            adapter.setSelectedIssueId(issueId);
            if (selectedIssue != null && itemClickListener != null) {
                itemClickListener.onSelect(selectedIssue);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) viewModel.loadNewIssues();
            }
        });
    }

    @Override
    public void onDestroyView() {
        if (issuesDisposable != null && !issuesDisposable.isDisposed()) {
            issuesDisposable.dispose();
        }
        if (selectedIssueDisposable != null && !selectedIssueDisposable.isDisposed()) {
            selectedIssueDisposable.dispose();
        }
        super.onDestroyView();
    }

    public interface IssueListFragmentContainer {
        OnIssueItemSelectListener getIssueItemSelectListener();
    }

    public interface OnIssueItemSelectListener {
        void onSelect(@NonNull Issue issue);
    }
}