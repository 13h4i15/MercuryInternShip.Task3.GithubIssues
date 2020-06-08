package com.mercuryi.internship.mercuryinternshiptask3githubissues.web;

import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public final class AppNetworkService {
    private final static String BASE_URL = "https://api.github.com";

    private static AppNetworkService instance;
    private final GithubApi githubApi;

    private AppNetworkService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .build();
        githubApi = retrofit.create(GithubApi.class);
    }

    public static GithubApi getGithubApi() {
        return getInstance().githubApi;
    }

    private static AppNetworkService getInstance() {
        if (instance == null) {
            instance = new AppNetworkService();
        }
        return instance;
    }
}