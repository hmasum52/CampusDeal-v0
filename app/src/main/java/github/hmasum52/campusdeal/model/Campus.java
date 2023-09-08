package github.hmasum52.campusdeal.model;

import com.google.gson.Gson;

import org.parceler.Parcel;

@Parcel
public class Campus {
    private String name;
    private double latitude;
    private double longitude;

    private String type; // school, college, university

    public Campus() {
    }

    public Campus(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
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

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
