package com.mercuryi.internship.mercuryinternshiptask3githubissues.helpers;

import androidx.recyclerview.widget.DiffUtil;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

public class IssuesDiffUtilCallback extends DiffUtil.Callback {
    private final List<Issue> oldList, newList;

    public IssuesDiffUtilCallback(List<Issue> oldList, List<Issue> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Issue oldIssue = oldList.get(oldItemPosition);
        Issue newIssue = newList.get(newItemPosition);
        return oldIssue.equals(newIssue);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return areItemsTheSame(oldItemPosition, newItemPosition);
    }
}
