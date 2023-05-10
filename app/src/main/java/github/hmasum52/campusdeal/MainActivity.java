package github.hmasum52.campusdeal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import github.hmasum52.campusdeal.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mVB = ActivityMainBinding.inflate(super.getLayoutInflater());
        super.setContentView(mVB.getRoot());
    }
}