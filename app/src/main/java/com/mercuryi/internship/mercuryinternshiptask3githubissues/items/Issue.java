package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class Issue implements Parcelable {
    @SerializedName("id")
    private final String id;

    @SerializedName("title")
    private final String title;

    @SerializedName("body")
    private final String body;

    @SerializedName("user")
    private final User user;

    public Issue(@NonNull String id, @NonNull String title, @NonNull String body, @NonNull User user) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.user = user;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    @NonNull
    public String getId() {
        return id;
    }

    @NonNull
    public String getBody() {
        return body;
    }

    @NonNull
    public User getUser() {
        return user;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(body);
        dest.writeParcelable(user, flags);
    }

    public static final Parcelable.Creator<Issue> CREATOR
            = new Parcelable.Creator<Issue>() {
        @NonNull
        public Issue createFromParcel(@NonNull Parcel in) {
            return new Issue(in.readString(), in.readString(), in.readString(),
                    in.readParcelable(User.class.getClassLoader()));
        }

        @NonNull
        public Issue[] newArray(int size) {
            return new Issue[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Issue issue = (Issue) o;
        return id.equals(issue.id) &&
                title.equals(issue.title) &&
                body.equals(issue.body) &&
                user.equals(issue.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, body, user);
    }
}
