package com.mercuryi.internship.mercuryinternshiptask3githubissues.web;

import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AppNetworkService {
    private final static String BASE_URL = "https://api.github.com";

    private static AppNetworkService INSTANCE;
    private final GithubApi githubApi;

    private AppNetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        githubApi = retrofit.create(GithubApi.class);
    }

    public static GithubApi getGithubApi() {
        return getInstance().githubApi;
    }

    private static AppNetworkService getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AppNetworkService();
        }
        return INSTANCE;
    }
}