package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.ArrayList;
import java.util.List;

final class IssueRecyclerViewAdapter extends RecyclerView.Adapter<IssueRecyclerViewAdapter.ViewHolder> {
    private final List<Issue> issueList = new ArrayList<>();
    private IssueListFragment.OnIssueItemClickListener onIssueItemClickListener;

    public IssueRecyclerViewAdapter(IssueListFragment.OnIssueItemClickListener onIssueItemClickListener) {
        this.onIssueItemClickListener = onIssueItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> onIssueItemClickListener.onClick(issueList.get(holder.getLayoutPosition())));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.issueTitle.setText(issueList.get(position).getTitle());
        holder.issueId.setText(issueList.get(position).getId());
        holder.issueUserLogin.setText(issueList.get(position).getUser().getLogin());
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public void addIssueList(@NonNull List<Issue> issueList) {
        this.issueList.addAll(issueList);
        notifyDataSetChanged();
    }

    public void clearIssueList() {
        issueList.clear();
        notifyDataSetChanged();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView issueTitle, issueId, issueUserLogin;

        public ViewHolder(@NonNull View view) {
            super(view);
            issueTitle = view.findViewById(R.id.issue_title);
            issueId = view.findViewById(R.id.issue_id);
            issueUserLogin = view.findViewById(R.id.issue_user_login);
        }
    }
}