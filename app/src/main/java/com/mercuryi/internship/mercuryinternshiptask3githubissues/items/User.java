package com.mercuryi.internship.mercuryinternshiptask3githubissues.items;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public final class User implements Parcelable {
    @SerializedName("login")
    private final String login;

    @SerializedName("avatar_url")
    private final String avatarUrl;

    public User(@NonNull String login, @NonNull String avatarUrl) {
        this.login = login;
        this.avatarUrl = avatarUrl;
    }

    @NonNull
    public String getLogin() {
        return login;
    }

    @NonNull
    public String getAvatarUrl() {
        return avatarUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(login);
        dest.writeString(avatarUrl);
    }

    public static final Parcelable.Creator<User> CREATOR
            = new Parcelable.Creator<User>() {
        @NonNull
        public User createFromParcel(@NonNull Parcel in) {
            return new User(in.readString(), in.readString());
        }

        @NonNull
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
