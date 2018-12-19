package com.example.dineshkumar.diary.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Dinesh Kumar on 2/21/2018.
 */

public class Diary implements Parcelable{
    String title;
    String desc;
    String createdDate;
    String modifiedDate;
    String category;

    public Diary(String title, String desc, String createdDate, String modifiedDate, String category) {
        this.title = title;
        this.desc = desc;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
        this.category = category;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public void setModifiedDate(String modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getModifiedDate() {
        return modifiedDate;
    }

     public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    Diary(Parcel in)
    {
        this.title = in.readString();
        this.desc = in.readString();
        this.createdDate = in.readString();
        this.modifiedDate = in.readString();
        this.category = in.readString();
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Diary createFromParcel(Parcel in) {
            return new Diary(in);
        }

        public Diary[] newArray(int size) {
            return new Diary[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(this.title);
        parcel.writeString(this.desc);
        parcel.writeString(this.createdDate);
        parcel.writeString(this.modifiedDate);
        parcel.writeString(this.category);
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public static Creator getCREATOR() {
        return CREATOR;
    }
}
