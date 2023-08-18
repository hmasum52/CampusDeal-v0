package github.hmasum52.campusdeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.databinding.ActivityMainBinding;
import github.hmasum52.campusdeal.util.LocationFinder;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {
    public static final String TAG = "MainActivity";

    @Inject
    LocationFinder finder;

    private ActivityMainBinding mVB;

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // workflow test
        super.onCreate(savedInstanceState);
        mVB = ActivityMainBinding.inflate(super.getLayoutInflater());
        super.setContentView(mVB.getRoot());

        // set up bottom navigation bar
        navController = Navigation.findNavController(this, R.id.nav_host_frag);
        // after set up with navController, it will automatically handle the navigation
        // when user click on the bottom navigation bar
        NavigationUI.setupWithNavController(mVB.bottomNavigation, navController);

        // set up the listener for destination change
        navController.addOnDestinationChangedListener(this);
    }

    @Override
    public void onDestinationChanged(
            @NonNull NavController navController,
            @NonNull NavDestination navDestination,
            @Nullable Bundle bundle) {
        Map<String, NavArgument> args = navDestination.getArguments();

        new Handler().post( ()-> {
            boolean showBottomNav = args.containsKey("showBottomNav")
                    && Objects.equals(
                            Objects.requireNonNull(args.get("showBottomNav")).getDefaultValue()
                            , true);
            if(showBottomNav){
                mVB.bottomNavigation.setVisibility(View.VISIBLE);
            }else{
                mVB.bottomNavigation.setVisibility(View.GONE);
            }
        });
    }

    public static void navigateToNewStartDestination(Activity activity, Integer fragmentId) {
        MainActivity.navigateToNewStartDestination(activity, fragmentId, fragmentId);
    }
    public static void navigateToNewStartDestination(Activity activity, Integer transition, Integer fragmentId) {
        NavController navController = Navigation.findNavController(activity, R.id.nav_host_frag);
        if(!isValidFragmentId(navController, fragmentId))
            return;
        navController.navigate(transition);
        setStartDestination(navController, fragmentId);
    }

    private static boolean isValidFragmentId(NavController navController, Integer fragmentId){
        return navController.getCurrentDestination().getId()!=fragmentId;
    }

    /**
     * change the start destination of navController\
     * @param fragmentId is the id of fragment we want to set as start destination(ex: R.id.dashboardFrag)
     *
     */
    public static void setStartDestination(NavController navController, Integer fragmentId) {
        NavInflater inflater = navController.getNavInflater();//navHostFragment.getNavController().getNavInflater();
        //NavGraph navGraph = inflater.inflate(R.navigation.main_nav_graph);
        NavGraph navGraph = inflater.inflate(R.navigation.nav_graph);
        navGraph.setStartDestination(fragmentId);
        //navHostFragment.getNavController().setGraph(navGraph);
        navController.setGraph(navGraph);
        Log.d(TAG, "setStartDestination(): new start fragment: " + Objects.requireNonNull(navController.getCurrentDestination()).getLabel());
    }


}