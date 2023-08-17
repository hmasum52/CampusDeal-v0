package github.hmasum52.campusdeal.dagger;

import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;

import androidx.fragment.app.FragmentActivity;

import java.util.Locale;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.qualifiers.ActivityContext;
import dagger.hilt.android.scopes.ActivityScoped;
import github.hmasum52.campusdeal.MainActivity;
import github.hmasum52.campusdeal.util.LocationFinder;

@Module
@InstallIn(ActivityComponent.class)
public class ActivityModule {

    // provide DeviceLocationFinder
    @Provides
    @ActivityScoped
    public static LocationFinder provideDeviceLocationFinder(FragmentActivity mainActivity){
        return new LocationFinder(mainActivity);
    }

    // Geocoder provider
    @Provides
    @ActivityScoped
    public static Geocoder provideGeocoder(@ActivityContext Context context){
        return new Geocoder(context, Locale.getDefault());
    }
}
