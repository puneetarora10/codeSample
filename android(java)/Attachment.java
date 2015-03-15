package com.gotsigned.amazing1;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URL;

//
//  to store attachment details
//  Attachment.java
//  GotSigned
//
//  Created by Puneet Arora on 10/5/14.
//  Copyright (c) 2014 Amazing Applications Inc. All rights reserved.
//
public class Attachment implements Parcelable {
    // to store the id of the attachment
    private String aid;
    // to store the name of the attachment
    private String name;
    // to store the numberOfViews
    private String numberOfViews;
    // to store the attachment's type ("audio" or "video")
    private String type;
    // to store attachment's path
    private String path;
    // to store the actual thumbnail
    private Bitmap thumbnail;
    // to store url of thumbnail
    private URL thumbnailURL;
    // to store attachment's talent's email
    private String uploadedByEmail;
    // to store attachment's talent's name
    private String uploadedByName;
    // YES if thumbnail has been downloaded
    private Boolean hasThumbnail;
    // to store attachment's website's url
    private String webURL;

    /**
     * getters
     */
    public String getAid() {
        return aid;
    }

    public String getName() {
        return name;
    }

    public String getNumberOfViews() {
        return numberOfViews;
    }

    public String getType() {
        return type;
    }

    public String getPath() {
        return path;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public URL getThumbnailURL() {
        return thumbnailURL;
    }

    public String getUploadedByEmail() {
        return uploadedByEmail;
    }

    public String getUploadedByName() {
        return uploadedByName;
    }

    public Boolean getHasThumbnail() {
        return hasThumbnail;
    }

    public String getWebURL() {
        return webURL;
    }

    /**
     * setters
     */
    public void setAid(String aid) {
        this.aid = aid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNumberOfViews(String numberOfViews) {
        this.numberOfViews = numberOfViews;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public void setThumbnailURL(URL thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public void setUploadedByEmail(String uploadedByEmail) {
        this.uploadedByEmail = uploadedByEmail;
    }

    public void setUploadedByName(String uploadedByName) {
        this.uploadedByName = uploadedByName;
    }

    public void setHasThumbnail(Boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public void setWebURL(String webURL) {
        this.webURL = webURL;
    }

    // constructor
    public Attachment() {

    }

    // parcelable
    @Override
    public int describeContents() {
        return 0;
    }

    // only writing necessary properties to the parcel
    @Override
    public void writeToParcel(Parcel pc, int flags) {
        pc.writeString(aid);
        pc.writeString(name);
        //pc.writeString(numberOfViews);
        pc.writeString(type);
        pc.writeString(path);
        //pc.writeValue(thumbnail);
        //pc.writeString(thumbnailURL.toString());
        pc.writeString(uploadedByEmail);
        pc.writeString(uploadedByName);
        //pc.writeValue(hasThumbnail);
        pc.writeString(webURL);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Attachment createFromParcel(Parcel in) {
            return new Attachment(in);
        }

        public Attachment[] newArray(int size) {
            return new Attachment[size];
        }
    };

    // only writing necessary properties from the parcel
    private Attachment(Parcel in) {
        aid = in.readString();
        name = in.readString();
        //numberOfViews = in.readString();
        type = in.readString();
        path = in.readString();
        //thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        //try{
        //  thumbnailURL = new URL(in.readString());
        //}
        //catch (Exception e){
        //  Log.d("Attachment","exception while creating URL from parcel"+e);
        //}
        uploadedByEmail = in.readString();
        uploadedByName = in.readString();
        //hasThumbnail = (Boolean)in.readValue(Boolean.class.getClassLoader());
        webURL = in.readString();
    }
}
