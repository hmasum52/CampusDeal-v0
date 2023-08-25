package github.hmasum52.campusdeal.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcel;

import java.util.Date;
import java.util.Objects;

@Parcel
public class DealRequest {
    private String adId;
    private String title;
    private String buyerId;
    private String buyerName;
    private String sellerId;
    private String sellerName;
    private Date date;

    public DealRequest() {
    }

    public DealRequest(String adId, String title, String buyerId, String buyerName, String sellerId, String sellerName, Date date) {
        this.adId = adId;
        this.title = title;
        this.buyerId = buyerId;
        this.buyerName = buyerName;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.date = date;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "DealRequest{" +
                "adId='" + adId + '\'' +
                ", title='" + title + '\'' +
                ", buyerId='" + buyerId + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", sellerId='" + sellerId + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DealRequest that = (DealRequest) o;
        Gson gson = new GsonBuilder().create();
        return Objects.equals(gson.toJson(this), gson.toJson(that));
    }

}
