package github.hmasum52.campusdeal.model;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

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
}
