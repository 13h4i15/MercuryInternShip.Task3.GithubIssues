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
    private IssueListFragment.OnIssueItemSelectListener itemSelectListener;
    private String selectedIssueId;

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
        holder.itemView.setSelected(issue.getId().equals(selectedIssueId));
        holder.issueTitle.setText(issue.getTitle());
        holder.issueId.setText(issue.getId());
        holder.issueUserLogin.setText(issue.getUser().getLogin());
        loadUserAvatarByUrl(issue.getUser().getAvatarUrl(), holder.userAvatarImage);
    }

    @Override
    public int getItemCount() {
        return issues.size();
    }

    private static void loadUserAvatarByUrl(@NonNull String imageUrl, @NonNull ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(imageView);
    }


    public void setOnItemSelectListener(@NonNull IssueListFragment.OnIssueItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public void setSelectedIssueId(@NonNull String issueId) {
        this.selectedIssueId = issueId;
        notifyDataSetChanged();
    }

    public void addToIssues(@NonNull List<Issue> issueList) {
        this.issues.addAll(issueList);
        notifyDataSetChanged();
    }

    public void clearIssues() {
        this.issues.clear();
        notifyDataSetChanged();
    }

    @Nullable
    public Issue getIssueById(@NonNull String id) {
        // streams and filter from api 24, current min api 21, because of this just looping;
        for (Issue issue : issues) {
            if (issue.getId().equals(id)) return issue;
        }
        return null;
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