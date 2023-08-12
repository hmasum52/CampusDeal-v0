package github.hmasum52.campusdeal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavArgument;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.databinding.ActivityMainBinding;

//@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavController.OnDestinationChangedListener {

    private ActivityMainBinding mVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVB = ActivityMainBinding.inflate(super.getLayoutInflater());
        super.setContentView(mVB.getRoot());

        // set up bottom navigation bar
        NavController navController = Navigation.findNavController(this, R.id.nav_host_frag);
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
            boolean showBottomNav = args.containsKey("showBottomNav") && args.get("showBottomNav").getDefaultValue().equals(true);
            if(showBottomNav){
                mVB.bottomNavigation.setVisibility(View.VISIBLE);
            }else{
                mVB.bottomNavigation.setVisibility(View.GONE);
            }
        });
    }
}