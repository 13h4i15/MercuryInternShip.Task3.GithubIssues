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

import io.reactivex.disposables.CompositeDisposable;

public class IssueListFragment extends Fragment {
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RecyclerView recyclerView;

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

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        IssueRecyclerViewAdapter adapter = new IssueRecyclerViewAdapter();
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemSelectListener(viewModel::setSelectedIssue);

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(viewModel::reloadIssues);

        compositeDisposable.add(viewModel.getRefreshingObservable()
                .distinctUntilChanged()
                .subscribe(swipeRefreshLayout::setRefreshing));

        TextView emptyListMessageView = root.findViewById(R.id.empty_list_message);
        compositeDisposable.add(viewModel.getIssuesObservable()
                .subscribe(issues -> {
                    IssuesDiffUtilCallback diffUtilCallback = new IssuesDiffUtilCallback(adapter.getIssues(), issues);
                    DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
                    adapter.setIssues(issues);
                    diffResult.dispatchUpdatesTo(adapter);
                    if (adapter.getItemCount() != 0) {
                        recyclerView.setVisibility(View.VISIBLE);
                        emptyListMessageView.setVisibility(View.GONE);
                    } else {
                        recyclerView.setVisibility(View.GONE);
                        emptyListMessageView.setVisibility(View.VISIBLE);
                    }
                }, error -> {
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                }));

        compositeDisposable.add(viewModel.getSelectedIssueObservable()
                .distinctUntilChanged()
                .subscribe(selectedIssue -> {
                    adapter.setSelectedIssue(selectedIssue.orElse(null));
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                }));
    }

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }
}