package github.hmasum52.campusdeal.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.PromptDialog;

public class AdReviewFragment extends AdDetailsFragment{


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.initUI();

       // init deal action button
        initDealActionButton();
    }


    private void initDealActionButton() {
        if(isOwner()){
            setDealButtonTextAndColor("Make Deal", R.color.colorPrimary, R.color.white);
        }
    }
}
