package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.helpers.IssuesDiffUtilCallback;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IssueListFragment extends Fragment {
    private final static String LOADING_ERROR_LOG_TAG = "loading_error";

    private Disposable issuesDisposable, selectedIssueDisposable;

    public static IssueListFragment newInstance() {
        return new IssueListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_issue_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View root, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(root, savedInstanceState);

        IssuesViewModel viewModel = new ViewModelProvider(requireActivity()).get(IssuesViewModel.class);

        RecyclerView recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        IssueRecyclerViewAdapter adapter = new IssueRecyclerViewAdapter();
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemSelectListener(viewModel::setSelectedIssue);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = viewModel::reloadIssues;
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        TextView emptyListMessageView = root.findViewById(R.id.empty_list_message);
        issuesDisposable = viewModel.getIssuesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(issues -> {
                    IssuesDiffUtilCallback diffUtilCallback = new IssuesDiffUtilCallback(adapter.getIssues(), issues);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
                    adapter.setIssues(issues);
                    diffResult.dispatchUpdatesTo(adapter);
                    swipeRefreshLayout.setRefreshing(false);
                    if (adapter.getItemCount() != 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyListMessageView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyListMessageView.setVisibility(View.VISIBLE);
                    }
                }, error -> {
                    Log.e(LOADING_ERROR_LOG_TAG, error.toString());
                    swipeRefreshLayout.setRefreshing(false);
                });

        selectedIssueDisposable = viewModel.getSelectedIssueObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedIssue -> {
                    adapter.setSelectedIssue(selectedIssue.orElse(null));
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
}