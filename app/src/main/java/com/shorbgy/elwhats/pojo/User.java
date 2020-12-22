package com.shorbgy.elwhats.pojo;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable{

    private String id;
    private String imageUrl;
    private String username;
    private String status;
    private long date;

    public User() {
    }

    public User(String id, String imageUrl, String username, String status,long date) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.username = username;
        this.date = date;
        this.status = status;
    }

    protected User(Parcel in) {
        id = in.readString();
        imageUrl = in.readString();
        username = in.readString();
        status = in.readString();
        date = in.readLong();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(imageUrl);
        dest.writeString(username);
        dest.writeString(status);
        dest.writeLong(date);
    }
}
