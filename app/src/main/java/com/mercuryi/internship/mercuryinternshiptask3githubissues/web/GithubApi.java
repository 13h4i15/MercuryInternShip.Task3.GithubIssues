package com.mercuryi.internship.mercuryinternshiptask3githubissues.web;

import androidx.annotation.NonNull;

import com.mercuryi.internship.mercuryinternshiptask3githubissues.items.Issue;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GithubApi {
    String USERNAME = "alibaba";
    String PROJECT_NAME = "atlas";

    @GET("/repos/{user}/{project}/issues?per_page=100   ")
    Single<List<Issue>> getProjectIssues(@Path("user") @NonNull String userName,
                                         @Path("project") @NonNull String projectName,
                                         @Query("state") @NonNull String state,
                                         @Query("page") int page);

    enum IssueState {
        STATE_OPEN("open"),
        STATE_CLOSED("closed"),
        STATE_ALL("all");

        private final String state;

        IssueState(@NonNull String state) {
            this.state = state;
        }

        @NonNull
        public String getState() {
            return state;
        }
    }
}
