package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.Toast;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.web.AppNetworkService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipeRefreshLayout;
    private IssueListFragment issueListFragment;
    private Call call;
    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_to_refresh);
        SwipeRefreshLayout.OnRefreshListener refreshListener = this::loadIssueList;
        swipeRefreshLayout.setOnRefreshListener(refreshListener);

        refreshListener.onRefresh();
    }

    @Override
    protected void onDestroy() {
        if (call != null) call.cancel();
        super.onDestroy();
    }

    private void loadIssueList() {
        if (issueListFragment == null) {
            createListFragment();
        }
        currentPage = 0;
        loadIssueListPages(currentPage);
    }

    private void loadIssueListPages(int page) {
        if (call != null && !call.isExecuted())return;
        swipeRefreshLayout.setRefreshing(true);
        call = AppNetworkService.getGithubApi().getProjectIssues(
                "alibaba", "atlas", "open", page);
        call.enqueue(new Callback<List<Issue>>() {
            @Override
            public void onResponse(@NonNull Call<List<Issue>> call, @NonNull Response<List<Issue>> response) {
                swipeRefreshLayout.setRefreshing(false);
                List<Issue> result = response.body();
                if (result == null || result.size() == 0) {
                    if (currentPage == 0) {
                        createEmptyListFragment();
                    }
                    return;
                }
                if (issueListFragment != null) {
                    ++currentPage;
                    issueListFragment.addIssueList(result);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Issue>> call, @NonNull Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                createEmptyListFragment();
                Toast.makeText(MainActivity.this, R.string.loading_error_toast, Toast.LENGTH_SHORT).show();
                t.getMessage();
            }
        });
    }

    private void createListFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        issueListFragment = new IssueListFragment();
        issueListFragment.setScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!recyclerView.canScrollVertically(1)) {
                    loadIssueListPages(currentPage);
                }
            }
        });
        fragmentTransaction.replace(R.id.list_fragment, issueListFragment);
        fragmentTransaction.commit();
    }

    private void createEmptyListFragment() {
        issueListFragment = null;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.list_fragment, new EmptyListMessageFragment());
        fragmentTransaction.commit();
    }
}
