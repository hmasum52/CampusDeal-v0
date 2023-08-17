package github.hmasum52.campusdeal.model;

import java.util.Date;
import java.util.List;

public class Ad {
    private String id;
    private String title;
    private String description;
    private String category;
    private double price;
    private boolean negotiable;
    private String sellerId;
    private Date uploadDate;
    private List<String> imageUriList;

    public Ad() {
    }

    public Ad(String id, String title, String description, String category, double price, boolean negotiable, String sellerId, Date uploadDate, List<String> imageUriList) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.negotiable = negotiable;
        this.sellerId = sellerId;
        this.uploadDate = uploadDate;
        this.imageUriList = imageUriList;
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

    public String getSellerId() {
        return sellerId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public List<String> getImageUriList() {
        return imageUriList;
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
                ", uploadDate=" + uploadDate +
                ", imageUriList=" + imageUriList +
                '}';
    }
}
