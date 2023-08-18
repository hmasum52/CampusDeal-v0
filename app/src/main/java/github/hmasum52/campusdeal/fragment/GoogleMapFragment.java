package github.hmasum52.campusdeal.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentGoogleMapBinding;
import github.hmasum52.campusdeal.model.AdLocation;
import github.hmasum52.campusdeal.util.LocationFinder;

@AndroidEntryPoint
public class GoogleMapFragment extends Fragment
        implements OnMapReadyCallback, LocationListener
, GoogleMap.OnCameraMoveListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener
{
    public static final String TAG = "GoogleMapFragment";

    @Inject
    LocationFinder locationFinder;

    @Inject
    Geocoder geocoder;

    // binding
    private FragmentGoogleMapBinding mVB;

    private GoogleMap map;

    private AdLocation adLocation;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentGoogleMapBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVB.pickLocationBtn.setOnClickListener(v -> {
            // send location to post ad fragment
            if(adLocation != null){
                // https://developer.android.com/guide/components/activities/parcelables-and-bundles
                //Bundle bundle = new Bundle();
               // bundle.putParcelable("location", Parcels.wrap(adLocation));
               // getParentFragmentManager().setFragmentResult("location", bundle);
               // getParentFragmentManager().popBackStack();
                NavController navController = NavHostFragment.findNavController(this);
                if(navController.getPreviousBackStackEntry() != null){
                    navController.getPreviousBackStackEntry()
                            .getSavedStateHandle()
                            .set("location", Parcels.wrap(adLocation));
                    navController.popBackStack();
                }

            }else{
                Toast.makeText(getActivity(), "Pick a location", Toast.LENGTH_SHORT).show();
            }
        });
    }


    void updateAddress(LatLng location){
        map.clear();
        map.addMarker(
                new MarkerOptions()
                        .position(location)
                        .title("Your Location")
        );
        Address address = null;
        try {
            List<Address> addressList = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            if (addressList.size() > 0){
                address = addressList.get(0);
                // make address text from address object
                Log.d(TAG, "findAddress: " + address.toString());
                String addressText = "";

                // add address line
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressText += address.getAddressLine(i) + ", ";
                }


                adLocation = new AdLocation(
                        address.getAddressLine(0),
                        address.getLocality(),
                        address.getSubAdminArea(),
                        address.getAdminArea(),
                        address.getCountryName(),
                        address.getCountryCode(),
                        location.latitude,
                        location.longitude
                );

                mVB.addressTv.setText(adLocation.getFullAddress());
            }
        } catch (IOException e) {
            // make toast
            Toast.makeText(getActivity(), "Can't find address", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "findAddress: ", e);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // init map
        Log.d(TAG, "initMap: ");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        try {
            mapFragment.getMapAsync(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        mVB.mapPin.setVisibility(View.GONE);
        map = googleMap;

        googleMap.setOnCameraIdleListener(this);
        googleMap.setOnCameraMoveListener(this);
        googleMap.setOnCameraMoveStartedListener(this);

        locationFinder.requestDeviceLocation(latLng -> {
            Log.d(TAG, "onMapReady: location found");

            enableUserLocation(googleMap);
            // add marker
            // find address
            updateAddress(latLng);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));

        });
    }

    private void enableUserLocation(@NonNull GoogleMap mMap) {
        if (ActivityCompat.checkSelfPermission(getActivity()
                , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Log.d(TAG, "enableUserLocation: ");
        mMap.setMyLocationEnabled(true);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // get address
        try {
            updateAddress(new LatLng(location.getLatitude(), location.getLongitude()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCameraIdle() {
        mVB.mapPin.setVisibility(View.GONE);
        updateAddress(map.getCameraPosition().target);
    }

    @Override
    public void onCameraMove() {
        //updateAddress(map.getCameraPosition().target);
    }

    @Override
    public void onCameraMoveStarted(int i) {
        mVB.mapPin.setVisibility(View.VISIBLE);
    }
}
