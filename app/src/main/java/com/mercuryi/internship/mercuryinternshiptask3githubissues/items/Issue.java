package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public final class Issue implements Parcelable {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("user")
    @Expose
    private User user;

    public static final Parcelable.Creator<Issue> CREATOR
            = new Parcelable.Creator<Issue>() {
        @NonNull
        public Issue createFromParcel(@NonNull Parcel in) {
            Issue issue = new Issue();
            issue.setId(in.readString());
            issue.setTitle(in.readString());
            issue.setBody(in.readString());
            return issue;
        }

        @NonNull
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(body);
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @NonNull
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
