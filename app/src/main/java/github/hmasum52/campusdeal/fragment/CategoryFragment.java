package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.AdItemAdapter;
import github.hmasum52.campusdeal.databinding.FragmentCategoryBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.StateData;
import github.hmasum52.campusdeal.viewmodel.AdViewModel;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    // view binding
    private FragmentCategoryBinding mVB;
    private String categoryName;

    private static final String TAG = "CategoryFragment";
    private AdViewModel adViewModel;

    public CategoryFragment(){

    }

    public CategoryFragment(String name) {
        // Required empty public constructor
        this.categoryName = name;
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
        // get the category name from savedStateHandle
        restoreSavedState();

        // shared view model
        // get the top 5 urgent ad list
        adViewModel.getTopUrgentAdList(categoryName, 5).observe(requireActivity(), this::updateTopUrgentAdRecyclerView);

        // get all Product
        adViewModel.getAllAds(categoryName).observe(requireActivity(), this::updateAllAdRecyclerView);
    }



    // restore data from savedStateHandle
    void restoreSavedState(){
        SavedStateHandle savedStateHandle = NavHostFragment.findNavController(this)
                .getCurrentBackStackEntry()
                .getSavedStateHandle();
        if(categoryName == null && savedStateHandle.contains("category")){
            categoryName = savedStateHandle.get("category");
        }
    }

    // update top urgent Ad RecyclerView
    void updateTopUrgentAdRecyclerView(@NonNull StateData<List<Ad>> ads){
        Log.d(TAG, "updateTopUrgentAdRecyclerView: category name = "+ categoryName);
        switch (ads.getStatus()){
            case SUCCESS:
                Log.d(TAG, "updateTopUrgentAdRecyclerView: total urgent ads = "+ads.getData().size());
                for (Ad a : ads.getData()){
                    Log.d(TAG, "updateTopUrgentAdRecyclerView: "+a.toString());
                }
                mVB.urgentRv.setAdapter(new AdItemAdapter(ads.getData()));
                break;
            case ERROR:
                Log.d(TAG, "updateTopUrgentAdRecyclerView:"+ads.getError().getMessage());
                break;
            case LOADING:
                break;
            case COMPLETE:
                break;
        }
    }

    private void updateAllAdRecyclerView(StateData<List<Ad>> adsSateData) {
        Log.d(TAG, "updateAllAdRecyclerView: category name = "+ categoryName);
        switch (adsSateData.getStatus()){
            case SUCCESS:
                mVB.allAdRv.setAdapter(new AdItemAdapter(adsSateData.getData()));
                break;
            case ERROR:
                Log.d(TAG, "updateAllAdRecyclerView: "+adsSateData.getError().getMessage());
                break;
            case LOADING:
                break;
            case COMPLETE:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        //refresh the list
        adViewModel.fetchUrgentAdList(categoryName, 5);
    }

    @Override
    public void onPause() {
        super.onPause();
        NavHostFragment.findNavController(this)
                .getCurrentBackStackEntry()
                .getSavedStateHandle()
                .set("category", categoryName);
    }
}