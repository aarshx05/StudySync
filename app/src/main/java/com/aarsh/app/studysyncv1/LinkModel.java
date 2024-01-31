package com.aarsh.app.studysyncv1;

import android.os.Parcel;
import android.os.Parcelable;

public class LinkModel implements Parcelable {
    private String linkId;
    private String linkName;
    private String link;

    public LinkModel() {}

    public LinkModel(String linkId, String linkName, String link) {
        this.linkId = linkId;
        this.link = link;
        this.linkName = linkName;
    }

    protected LinkModel(Parcel in) {
        linkId = in.readString();
        linkName = in.readString();
        link = in.readString();
    }

    public static final Creator<LinkModel> CREATOR = new Creator<LinkModel>() {
        @Override
        public LinkModel createFromParcel(Parcel in) {
            return new LinkModel(in);
        }

        @Override
        public LinkModel[] newArray(int size) {
            return new LinkModel[size];
        }
    };

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(linkId);
        dest.writeString(linkName);
        dest.writeString(link);
    }
}
