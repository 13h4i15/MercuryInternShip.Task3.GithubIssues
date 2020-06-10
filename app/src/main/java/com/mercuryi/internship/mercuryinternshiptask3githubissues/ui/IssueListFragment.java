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
    private final static String ISSUE_EXTRA = "issue";

    private OnIssueItemClickListener itemClickListener;
    private Issue selectedIssue;
    private IssueRecyclerViewAdapter adapter;
    private Disposable disposable;

    public static IssueListFragment newInstance() {
        return new IssueListFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IssueListFragmentContainer) {
            itemClickListener = ((IssueListFragmentContainer) context).getIssueItemClickListener();
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

        if (savedInstanceState != null) selectedIssue = savedInstanceState.getParcelable(ISSUE_EXTRA);

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IssueRecyclerViewAdapter(selectedIssue);

        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(adapter);
        if (itemClickListener != null) adapter.setOnItemClickListener(itemClickListener);

        IssuesViewModel viewModel = new ViewModelProvider(requireActivity()).get(IssuesViewModel.class);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
            adapter.clearIssues();
            viewModel.reloadIssues();
        };
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        Observable<List<Issue>> observable = viewModel.getIssuesObservable();
        TextView textView = root.findViewById(R.id.empty_list_message);
        disposable = observable.subscribe(issues -> {
            swipeRefreshLayout.setRefreshing(false);
            if (!issues.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                adapter.addToIssues(issues);
            } else if (adapter.getItemCount() == 0) {
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
                if (!recyclerView.canScrollVertically(1)) viewModel.loadNewIssues();
            }
        });

        if (selectedIssue != null) itemClickListener.onClick(selectedIssue);
    }

    @Override
    public void onDestroyView() {
        if (disposable != null && !disposable.isDisposed()) disposable.dispose();
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ISSUE_EXTRA, selectedIssue);
    }

    public void setSelectedIssue(@Nullable Issue selectedIssue) {
        this.selectedIssue = selectedIssue;
        if (adapter != null) adapter.setSelectedIssue(selectedIssue);
    }

    public interface IssueListFragmentContainer {
        OnIssueItemClickListener getIssueItemClickListener();
    }

    public interface OnIssueItemClickListener {
        void onClick(Issue issue);
    }
}