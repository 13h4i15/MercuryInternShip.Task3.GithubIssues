package com.mercuryi.internship.mercuryinternshiptask3githubissues.web;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface GithubApi {
    String STATE_OPEN = "open";
    int ITEMS_ON_PAGE_COUNT = 30;

    @GET("/repos/{user}/{project}/issues")
    Single<List<Issue>> getProjectIssues(@Path("user") String userName, @Path("project") String projectName,
                                             @Query("state") String state, @Query("page") int page);
}
