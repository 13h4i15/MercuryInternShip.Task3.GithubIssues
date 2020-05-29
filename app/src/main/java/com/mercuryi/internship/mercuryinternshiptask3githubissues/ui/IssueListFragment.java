package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IssueListFragment extends Fragment {

    private Call call;
    private IssueRecyclerViewAdapter issueRecyclerViewAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_issue_list, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        issueRecyclerViewAdapter = new IssueRecyclerViewAdapter(getOnIssueItemClickListener());
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(20));
        recyclerView.setAdapter(issueRecyclerViewAdapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = this::loadIssueList;
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        refreshListener.onRefresh();

        return view;
    }

    private void loadIssueList() {
        issueRecyclerViewAdapter.clearIssueList();
        loadIssueListPages(0);
    }

    private void loadIssueListPages(int page) {
        swipeRefreshLayout.setRefreshing(true);

        if (call != null) call.cancel();
        call = AppNetworkService.getGithubApi().getProjectIssues(
                "alibaba", "atlas", "open", page);

        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(@NonNull Call<List<Issue>> call, @NonNull Response<List<Issue>> response) {
                List<Issue> result = response.body();
                if (result == null || result.size() == 0) {
                    swipeRefreshLayout.setRefreshing(false);
                    return;
                }
                issueRecyclerViewAdapter.addIssueList(result);
                loadIssueListPages(page + 1);
            }

            @Override
            public void onFailure(@NonNull Call<List<Issue>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                t.getMessage();
            }
        });
    }

    private OnIssueItemClickListener getOnIssueItemClickListener() {
        return issue -> {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            fragmentTransaction.replace(R.id.list_fragment, new IssueFragment(issue));
            fragmentTransaction.addToBackStack("tr");
            fragmentTransaction.commit();
        };
    }

    interface OnIssueItemClickListener {
        void onClick(Issue issue);
    }
}
