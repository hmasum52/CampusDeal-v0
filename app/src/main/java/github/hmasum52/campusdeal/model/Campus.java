package github.hmasum52.campusdeal.model;

import com.google.gson.Gson;

import org.parceler.Parcel;

@Parcel
public class Campus {
    private String name;
    private double latitude;
    private double longitude;

    private String fullAddress;

    private String type; // school, college, university

    public Campus() {
    }

    public Campus(String name, double latitude, double longitude, String fullAddress, String type) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fullAddress = fullAddress;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getType() {
        return type;
    }

    public String getFullAddress() {
        return fullAddress;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
