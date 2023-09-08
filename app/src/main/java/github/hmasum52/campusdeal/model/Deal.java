package github.hmasum52.campusdeal.model;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
public class Deal {
    private Ad ad;
    private Date date;
    private DealRequest dealInfo;

    public Deal() {
    }

    public Deal(Ad ad, Date date, DealRequest dealInfo) {
        this.ad = ad;
        this.date = date;
        this.dealInfo = dealInfo;
    }

    public Ad getAd() {
        return ad;
    }

    public void setAd(Ad ad) {
        this.ad = ad;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DealRequest getDealInfo() {
        return dealInfo;
    }

    public void setDealInfo(DealRequest dealInfo) {
        this.dealInfo = dealInfo;
    }


    @NonNull
    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
