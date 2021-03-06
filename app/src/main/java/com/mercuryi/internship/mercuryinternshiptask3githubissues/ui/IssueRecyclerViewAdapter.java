package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

final class IssueRecyclerViewAdapter extends RecyclerView.Adapter<IssueRecyclerViewAdapter.ViewHolder> {
    private final List<Issue> issues = new ArrayList<>();
    private OnIssueItemSelectListener itemSelectListener;
    private Issue selectedIssue;

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_issue, parent, false);
        ViewHolder holder = new ViewHolder(view);
        view.setOnClickListener(v -> {
            if (!v.isSelected() && itemSelectListener != null) {
                itemSelectListener.onSelect(issues.get(holder.getLayoutPosition()));
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Issue issue = issues.get(position);
        holder.itemView.setSelected(issue.equals(selectedIssue));
        holder.issueTitle.setText(issue.getTitle());
        holder.issueId.setText(issue.getId());
        holder.issueUserLogin.setText(issue.getUser().getLogin());
        loadUserAvatar(issue.getUser().getAvatarUrl(), holder.userAvatarImage);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    public List<Issue> getIssues() {
        return issues;
    }

    private static void loadUserAvatar(@NonNull String imageUrl, @NonNull ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(imageView);
    }

    public void setOnItemSelectListener(@NonNull OnIssueItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public void setSelectedIssue(@Nullable Issue issue) {
        this.selectedIssue = issue;
        notifyDataSetChanged();
    }

    public void setIssues(@NonNull List<Issue> issues) {
        this.issues.clear();
        this.issues.addAll(issues);
    }

    public interface OnIssueItemSelectListener {
        void onSelect(@NonNull Issue issue);
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView issueTitle, issueId, issueUserLogin;
        private final ImageView userAvatarImage;

        public ViewHolder(@NonNull View view) {
            super(view);
            issueTitle = view.findViewById(R.id.issue_title);
            issueId = view.findViewById(R.id.issue_id);
            issueUserLogin = view.findViewById(R.id.issue_user_login);
            userAvatarImage = view.findViewById(R.id.user_avatar_image);
        }
    }
}