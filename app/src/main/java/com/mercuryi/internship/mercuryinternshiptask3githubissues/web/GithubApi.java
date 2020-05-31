package com.mercuryi.internship.mercuryinternshiptask3githubissues.web;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubApi {
    @GET("/repos/{user}/{project}/issues")
    Call<List<Issue>> getProjectIssues(@Path("user") String userName, @Path("project") String projectName,
                                       @Query("state") String state, @Query("page") int page);
}
