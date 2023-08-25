package github.hmasum52.campusdeal.model;

import androidx.annotation.Nullable;

import com.google.gson.GsonBuilder;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class WishListData {
    private String adId;

    private String title;

    private Date date;

    public WishListData() {

    }

    public WishListData(String adId, String title, Date date) {
        this.adId = adId;
        this.title = title;
        this.date = date;
    }
    // getters
    public String getAdId() {
        return adId;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    // setters
    public void setAdId(String adId) {
        this.adId = adId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "WishListData{" +
                "adId='" + adId + '\'' +
                ", title='" + title + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if(obj instanceof WishListData){
            return new GsonBuilder().create().toJson(this).equals(new GsonBuilder().create().toJson(obj));
        }
        return false;
    }
}
