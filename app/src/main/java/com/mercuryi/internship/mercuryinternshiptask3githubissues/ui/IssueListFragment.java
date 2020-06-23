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
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.helpers.IssuesDiffUtilCallback;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.workers.IssueWorker;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class IssueListFragment extends Fragment {
    private final static String RECYCLER_STATE_EXTRA = "recycler_state";

    private Disposable issuesDisposable, selectedIssueDisposable, refreshingDisposable;
    private RecyclerView recyclerView;
    private Parcelable recyclerState;

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
        WorkManager workManager = WorkManager.getInstance(requireContext().getApplicationContext());

        recyclerView = root.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        IssueRecyclerViewAdapter adapter = new IssueRecyclerViewAdapter();
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration());
        recyclerView.setAdapter(adapter);
        adapter.setOnItemSelectListener(viewModel::setSelectedIssue);

        recyclerState = savedInstanceState != null ?
                savedInstanceState.getParcelable(RECYCLER_STATE_EXTRA) : null;

        SwipeRefreshLayout swipeRefreshLayout = root.findViewById(R.id.swipe_to_refresh);
        swipeRefreshLayout.setOnRefreshListener(viewModel::reloadIssues);

        refreshingDisposable = viewModel.getRefreshingObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(swipeRefreshLayout::setRefreshing);

        TextView emptyListMessageView = root.findViewById(R.id.empty_list_message);
        issuesDisposable = viewModel.getIssuesObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(issues -> {
                    if (issues.isPresent()) {
                        IssuesDiffUtilCallback diffUtilCallback = new IssuesDiffUtilCallback(adapter.getIssues(), issues.get());
                        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
                        adapter.setIssues(issues.get());
                        diffResult.dispatchUpdatesTo(adapter);
                        workManager.cancelUniqueWork(IssueWorker.ISSUE_WORK_NAME);
                        startWork();

                        if (adapter.getItemCount() != 0) {
                            if (recyclerState != null && recyclerView.getLayoutManager() != null) {
                                recyclerView.getLayoutManager().onRestoreInstanceState(recyclerState);
                                recyclerState = null;
                            }
                            recyclerView.setVisibility(View.VISIBLE);
                            emptyListMessageView.setVisibility(View.GONE);
                        } else {
                            recyclerView.setVisibility(View.GONE);
                            emptyListMessageView.setVisibility(View.VISIBLE);
                        }
                    }
                }, error -> {
                    Log.e(Constants.LOADING_ERROR_LOG_TAG, error.toString());
                });

        selectedIssueDisposable = viewModel.getSelectedIssueObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(selectedIssue -> {
                    adapter.setSelectedIssue(selectedIssue.orElse(null));
                }, error -> {
                    Log.e(Constants.ISSUE_SELECTION_ERROR_LOG_TAG, error.toString());
                });
    }

    @Override
    public void onDestroyView() {
        dispose(issuesDisposable);
        dispose(selectedIssueDisposable);
        dispose(refreshingDisposable);
        super.onDestroyView();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (recyclerView != null && recyclerView.getLayoutManager() != null) {
            outState.putParcelable(RECYCLER_STATE_EXTRA, recyclerView.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    private void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void startWork() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                IssueWorker.class, 10, TimeUnit.SECONDS, 10, TimeUnit.SECONDS)
                .setConstraints(constraints)
                .build();
        WorkManager.getInstance(requireContext().getApplicationContext()).enqueueUniquePeriodicWork(
                IssueWorker.ISSUE_WORK_NAME, ExistingPeriodicWorkPolicy.KEEP, workRequest);
    }
}