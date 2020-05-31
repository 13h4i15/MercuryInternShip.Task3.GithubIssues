package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.R;

public class EmptyListMessageFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_empty_list_message, container, false);
    }
}