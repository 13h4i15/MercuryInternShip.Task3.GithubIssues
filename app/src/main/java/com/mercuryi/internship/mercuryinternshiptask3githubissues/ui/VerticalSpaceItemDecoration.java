package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;

final class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent,
                               @NonNull RecyclerView.State state) {
        int dimen = view.getResources().getDimensionPixelSize(R.dimen.issue_item_recycler_distance);
        if (parent.getAdapter() != null &&
                parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1) {
            outRect.bottom = dimen;
        }
    }
}
