package github.hmasum52.campusdeal.model;

import androidx.annotation.NonNull;

import org.parceler.Parcel;

@Parcel
public class AdLocation {
    String addressLine;
    String locality;
    String subAdminArea;
    String adminArea;
    String countryName;
    String countryCode;
    double latitude;
    double longitude;

    public AdLocation() {
    }

    public AdLocation(String addressLine, String locality, String subAdminArea, String adminArea, String countryName, String countryCode, double latitude, double longitude) {
        this.addressLine = addressLine;
        this.locality = locality;
        this.subAdminArea = subAdminArea;
        this.adminArea = adminArea;
        this.countryName = countryName;
        this.countryCode = countryCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public String getLocality() {
        return locality;
    }

    public String getSubAdminArea() {
        return subAdminArea;
    }

    public String getAdminArea() {
        return adminArea;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getFullAddress(){
        return addressLine + ", " + locality + ", " + subAdminArea + ", " + adminArea + ", " + countryName;
    }

    @NonNull
    @Override
    public String toString() {
        return getFullAddress();
    }
}
