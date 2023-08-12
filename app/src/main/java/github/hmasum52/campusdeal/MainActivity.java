package github.hmasum52.campusdeal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.databinding.ActivityMainBinding;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

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
    }
}