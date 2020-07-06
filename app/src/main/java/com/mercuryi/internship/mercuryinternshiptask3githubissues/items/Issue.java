package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public final class Issue implements Parcelable {
    @SerializedName("id")
    private final String id;

    @SerializedName("number")
    private final Integer number;

    @SerializedName("state")
    private final String state;

    @SerializedName("title")
    private final String title;

    @SerializedName("body")
    private final String body;

    @SerializedName("user")
    private final User user;

    public Issue(@NonNull String id, @NonNull Integer number, @NonNull String state
            , @NonNull String title, @NonNull String body, @NonNull User user) {
        this.id = id;
        this.number = number;
        this.state = state;
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

    public String getState() {
        return state;
    }

    @NonNull
    public User getUser() {
        return user;
    }

    @NonNull
    public Integer getNumber() {
        return number;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(state);
        dest.writeString(body);
        dest.writeInt(number);
        dest.writeParcelable(user, flags);
    }

    public static final Parcelable.Creator<Issue> CREATOR
            = new Parcelable.Creator<Issue>() {
        @NonNull
        public Issue createFromParcel(@NonNull Parcel in) {
            return new Issue(in.readString(), in.readInt(), in.readString(), in.readString(), in.readString(),
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
        return getId().equals(issue.getId()) &&
                getNumber().equals(issue.getNumber()) &&
                getState().equals(issue.getState()) &&
                getTitle().equals(issue.getTitle()) &&
                getBody().equals(issue.getBody()) &&
                getUser().equals(issue.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getNumber(), getState(), getTitle(), getBody(), getUser());
    }
}
