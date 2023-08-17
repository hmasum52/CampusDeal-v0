package github.hmasum52.campusdeal.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import dagger.hilt.android.components.ActivityComponent;
import github.hmasum52.campusdeal.MainActivity;

public class LocationFinder {
    //to get the device location
    public final static int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 101;
    public static final String TAG = "DeviceLocationFinder->";
    private final FragmentActivity activity;
    private LatLng deviceLatLng = null;
    private OnDeviceLocationFoundListener onDeviceLocationFoundListener;

    private LocationCallback locationCallback;

    // see : https://developer.android.com/training/permissions/requesting
    // Register the permissions callback, which handles the user's response to the
    // system permissions dialog. Save the return value, an instance of
    // ActivityResultLauncher, as an instance variable.
    private final ActivityResultLauncher<String> requestPermissionLauncher;


    @Inject
    public LocationFinder(FragmentActivity activity) {
        this.activity = activity;

        requestPermissionLauncher = activity
                .registerForActivityResult(new ActivityResultContracts.RequestPermission()
                        , isGranted -> {
                            Log.d(TAG, "DeviceLocationFinder: registerForActivityResult: isGranted: "+isGranted);
                            if (isGranted) {
                                // Permission is granted. Continue the action or workflow in your
                                // app.
                                Log.d(TAG, "DeviceLocationFinder: permission granted");
                                this.getDeviceLocation();
                            } else {
                                // Explain to the user that the feature is unavailable because the
                                // features requires a permission that the user has denied. At the
                                // same time, respect the user's decision. Don't link to system
                                // settings in an effort to convince the user to change their
                                // decision.
                                // required
                                Log.d(TAG, "DeviceLocationFinder: permission not granted");
                                // this.getLocationPermission();
                            }
                        });

        this.getLocationPermission();
        Log.d(TAG, "DeviceLocationFinder: init done");
    }

    public interface OnDeviceLocationFoundListener {
        void onDeviceLocationFound(LatLng latLng);
    }

    public boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isDeviceLocationFound() {
        return deviceLatLng != null;
    }

    public void requestDeviceLocation(OnDeviceLocationFoundListener onDeviceLocationFoundListener) {
        this.onDeviceLocationFoundListener = onDeviceLocationFoundListener;
        Log.d(TAG, "requestDeviceLocation: ");
        if(deviceLatLng!=null){
            onDeviceLocationFoundListener.onDeviceLocationFound(deviceLatLng);
            return;
        }
        Log.d(TAG, "requestDeviceLocation: device location is null");

        this.getDeviceLocation();
    }

    private void getDeviceLocation(){
        // Construct a FusedLocationProviderClient.
        FusedLocationProviderClient mFusedLocationProviderClient
                = LocationServices.getFusedLocationProviderClient(activity);
        /**
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (isPermissionGranted()) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        if (location == null) {
                            Log.d(TAG, "onComplete:location is null");
                            return;
                        }
                        Log.d(TAG, "onComplete:location found");
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        deviceLatLng = currentLocation;
                        if (onDeviceLocationFoundListener != null)
                            onDeviceLocationFoundListener.onDeviceLocationFound(currentLocation);
                    } else {
                        Log.d(TAG, "getDeviceLocation onComplete: location not found");
                    }
                });
            } else {
                this.getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public LatLng getDeviceLatLng() {
        return deviceLatLng;
    }

    /**
     * get the user permission to access device location
     */
    public void getLocationPermission() {
        if (!isPermissionGranted())
            // get the location permission from user
            // this will prompt user a dialog to give the location permission
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }
}


