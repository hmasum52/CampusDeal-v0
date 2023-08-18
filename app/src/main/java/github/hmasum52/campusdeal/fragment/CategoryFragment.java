package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.AdItemAdapter;
import github.hmasum52.campusdeal.databinding.FragmentCategoryBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.viewmodel.AdViewModel;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    // view binding
    private FragmentCategoryBinding mVB;
    private String name;

    private static final String TAG = "CategoryFragment";
    private AdViewModel adViewModel;

    public CategoryFragment(){

    }

    public CategoryFragment(String name) {
        // Required empty public constructor
        this.name = name;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // init adViewModel
        adViewModel = new ViewModelProvider(this).get(AdViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentCategoryBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        adViewModel.getUrgentAdList(name).observe(getViewLifecycleOwner(), ads -> {
            Log.d(TAG, "onViewCreated: category name = "+name);
            Log.d(TAG, "onViewCreated: total urgent ads = "+ads.size());
            for (Ad a : ads){
                Log.d(TAG, "onViewCreated: "+a.toString());
            }
            mVB.urgentRv.setAdapter(new AdItemAdapter(ads));
        });
    }
}