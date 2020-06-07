package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;
import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

final class IssueRecyclerViewAdapter extends RecyclerView.Adapter<IssueRecyclerViewAdapter.ViewHolder> {
    private final List<Issue> issueList = new ArrayList<>();
    private IssueListFragment.OnIssueItemClickListener onIssueItemClickListener;

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
        Issue issue = issueList.get(position);
        holder.issueTitle.setText(issue.getTitle());
        holder.issueId.setText(issue.getId());
        holder.issueUserLogin.setText(issue.getUser().getLogin());
        picassoImageLoader(issue.getUser().getAvatarUrl(), holder.userAvatarImage);
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    private static void picassoImageLoader(@NonNull String imageUrl, @NonNull ImageView imageView) {
        Picasso.get()
                .load(imageUrl)
                .fit()
                .centerCrop()
                .into(imageView);
    }


    public void setOnIssueItemClickListener(@NonNull IssueListFragment.OnIssueItemClickListener onIssueItemClickListener) {
        this.onIssueItemClickListener = onIssueItemClickListener;
    }

    public void setIssueList(@NonNull List<Issue> issueList) {
        this.issueList.clear();
        this.issueList.addAll(issueList);
        notifyDataSetChanged();
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