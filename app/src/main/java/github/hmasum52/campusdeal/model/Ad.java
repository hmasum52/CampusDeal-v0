package github.hmasum52.campusdeal.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import io.grpc.internal.JsonParser;

@Parcel
public class Ad {
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private boolean negotiable;

    private boolean urgent;

    private String sellerId;

    private String sellerName;
    private Date uploadDate;
    private List<String> imageUriList;

    private AdLocation adLocation;

    public Ad() {
    }

    public Ad(
            String id,
            String title,
            String description,
            String category,
            double price,
            boolean negotiable,
            boolean urgent,
            String sellerId,
            String sellerName,
            Date uploadDate,
            List<String> imageUriList,
            AdLocation adLocation
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.negotiable = negotiable;
        this.urgent = urgent;
        this.sellerId = sellerId;
        this.sellerName = sellerName;
        this.uploadDate = uploadDate;
        this.imageUriList = imageUriList;
        this.adLocation = adLocation;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public boolean isNegotiable() {
        return negotiable;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public String getSellerId() {
        return sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public List<String> getImageUriList() {
        return imageUriList;
    }

    public AdLocation getAdLocation() {
        return adLocation;
    }

    public LatLng locationLatLng(){
        return new LatLng(adLocation.getLatitude(), adLocation.getLongitude());
    }

    @Override
    public String toString() {
        return "Ad{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", negotiable=" + negotiable +
                ", sellerId='" + sellerId + '\'' +
                ", sellerName='" + sellerName + '\'' +
                ", uploadDate=" + uploadDate +
                ", imageUriList=" + imageUriList +
                ", adLocation=" + adLocation +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ad ad = (Ad) o;
       // convert to json string and compare
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this).equals(gson.toJson(ad));
    }
}
