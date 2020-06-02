package com.mercuryi.internship.mercuryinternshiptask3githubissues.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

final class IssuesViewModelFactory implements ViewModelProvider.Factory {
    private Application application;

    public IssuesViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new IssuesViewModel(application);
    }
}
