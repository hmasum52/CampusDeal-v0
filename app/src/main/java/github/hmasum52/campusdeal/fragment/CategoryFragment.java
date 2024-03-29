package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.AdItemAdapter;
import github.hmasum52.campusdeal.adapter.RecyclerItemClickListener;
import github.hmasum52.campusdeal.databinding.FragmentCategoryBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.StateData;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.Util;
import github.hmasum52.campusdeal.viewmodel.AdViewModel;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

@AndroidEntryPoint
public class CategoryFragment extends Fragment {

    // view binding
    private FragmentCategoryBinding mVB;
    private String categoryName;

    private static final String TAG = "CategoryFragment";
    private AdViewModel adViewModel;

    private AdItemAdapter urgentAdItemAdapter;
    private AdItemAdapter nearestAdItemAdapter;
    private AdItemAdapter allAdItemAdapter;

    private UserViewModel userVB;
    private User user;


    public CategoryFragment(){

    }



    public CategoryFragment(String name) {
        // Required empty public constructor
        this.categoryName = name;
    }

    private RecyclerItemClickListener<Ad> onAdClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: called "+this);
        updateCategoryName();
        // init view models
        adViewModel = new ViewModelProvider(this).get(AdViewModel.class);
        userVB = new ViewModelProvider(this).get(UserViewModel.class);
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
        Log.d(TAG, "onViewCreated: called "+this);
        Log.d(TAG, "onViewCreated: category name = "+ categoryName);

        setLoading(true);


        initAdapters();

        userVB.getUserLiveData().observe(getViewLifecycleOwner(), userStateData -> {
            if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
                user = userStateData.getData();

                // set user to adapts
                urgentAdItemAdapter.setUser(user);
                nearestAdItemAdapter.setUser(user);
                allAdItemAdapter.setUser(user);

                // shared view model
                // get the top 5 urgent ad list
                adViewModel.getTopUrgentAdList(categoryName, 5)
                        .observe(
                                getViewLifecycleOwner(), this::updateTopUrgentAdRecyclerView);

                // get top 5 nearest ad list with in 1 km
                adViewModel.getNearAdList(categoryName, user.makeCampusLatLng(), 1, 5)
                        .observe(getViewLifecycleOwner(), this::updateNearestAdRecyclerView);

                // get all Product
                adViewModel.getAllAds(categoryName).observe(getViewLifecycleOwner(), this::updateAllAdRecyclerView);
            }
        });


    }

    private void initAdapters(){
        onAdClickListener = ad -> {
            // open ad details fragment
            // send ad objecdt to ad details fragment
            Bundle bundle = new Bundle();
            bundle.putParcelable("ad", Parcels.wrap(ad));
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_adDetailsFragment, bundle);
        };

        // set adapters
        allAdItemAdapter = new AdItemAdapter();
        allAdItemAdapter.setRecyclerItemClickListener(onAdClickListener);

        nearestAdItemAdapter = new AdItemAdapter();
        nearestAdItemAdapter.setRecyclerItemClickListener(onAdClickListener);

        urgentAdItemAdapter = new AdItemAdapter();
        urgentAdItemAdapter.setRecyclerItemClickListener(onAdClickListener);

        mVB.urgentRv.setAdapter(urgentAdItemAdapter);
        mVB.nearestAdRv.setAdapter(nearestAdItemAdapter);
        mVB.allAdRv.setAdapter(allAdItemAdapter);
    }

    // get category name from class tag
    private void updateCategoryName(){
        //get fragment tag set by the view pager
        // https://stackoverflow.com/questions/55728719/get-current-fragment-with-viewpager2
        int index = Util.getViewPagerFragmentIndex(this, Constants.CATEGORY_LIST.size());
        categoryName = Constants.CATEGORY_LIST.get(index);
    }

    // update nearest ad RecyclerView
    private void updateNearestAdRecyclerView(@NonNull StateData<List<Ad>> ads){
        Log.d(TAG, "updateNearestAdRecyclerView: category name = "+ categoryName);
        switch (ads.getStatus()){
            case SUCCESS:
                setLoading(false);
                Log.d(TAG, "updateNearestAdRecyclerView: total nearest ads = "+ads.getData().size());
                nearestAdItemAdapter.differ.submitList(ads.getData());
                if(nearestAdItemAdapter.differ.getCurrentList().size()==0){
                    mVB.noNearestItemTv.setVisibility(View.VISIBLE);
                }else{
                    mVB.noNearestItemTv.setVisibility(View.GONE);
                }
                break;
            case ERROR:
                Log.d(TAG, "updateNearestAdRecyclerView:"+ads.getError().getMessage());
                setLoading(false);
                break;
            case LOADING:
                setLoading(true);
                break;
            case COMPLETE:
                setLoading(false);
                break;
        }
    }


    private void setLoading(boolean isLoading){
        if(isLoading) {
            mVB.loadingPb.setVisibility(View.VISIBLE);
            mVB.urgentSellingTv.setVisibility(View.GONE);
            mVB.urgentRv.setVisibility(View.GONE);
            mVB.All.setVisibility(View.GONE);
            mVB.allAdRv.setVisibility(View.GONE);
        }else{
            mVB.loadingPb.setVisibility(View.GONE);
            mVB.urgentSellingTv.setVisibility(View.VISIBLE);
            mVB.urgentRv.setVisibility(View.VISIBLE);
            mVB.All.setVisibility(View.VISIBLE);
            mVB.allAdRv.setVisibility(View.VISIBLE);
        }
    }

    // update top urgent Ad RecyclerView
    void updateTopUrgentAdRecyclerView(@NonNull StateData<List<Ad>> ads){
        Log.d(TAG, "updateTopUrgentAdRecyclerView: category name = "+ categoryName);
        switch (ads.getStatus()){
            case SUCCESS:
                setLoading(false);
                Log.d(TAG, "updateTopUrgentAdRecyclerView: total urgent ads = "+ads.getData().size());
                urgentAdItemAdapter.differ.submitList(ads.getData());
                if(urgentAdItemAdapter.differ.getCurrentList().size()==0){
                    mVB.noUrgentItemTv.setVisibility(View.VISIBLE);
                }else{
                    mVB.noUrgentItemTv.setVisibility(View.GONE);
                }
                break;
            case ERROR:
                Log.d(TAG, "updateTopUrgentAdRecyclerView:"+ads.getError().getMessage());
                setLoading(false);
                break;
            case LOADING:
                setLoading(true);
                break;
            case COMPLETE:
                setLoading(false);
                break;
        }
    }

    private void updateAllAdRecyclerView(@NonNull StateData<List<Ad>> adsSateData) {
        Log.d(TAG, "updateAllAdRecyclerView: category name = "+ categoryName);
        switch (adsSateData.getStatus()){
            case SUCCESS:
                setLoading(false);
                assert adsSateData.getData() != null;
                //setUpWithAdapter(adsSateData.getData(), mVB.allAdRv);
                allAdItemAdapter.differ.submitList(adsSateData.getData());
                if(allAdItemAdapter.differ.getCurrentList().size()==0){
                    mVB.noItemTv.setVisibility(View.VISIBLE);
                }else{
                    mVB.noItemTv.setVisibility(View.GONE);
                }
                break;
            case ERROR:
                setLoading(false);
                Log.d(TAG, "updateAllAdRecyclerView: "+adsSateData.getError().getMessage());
                break;
            case LOADING:
                setLoading(true);
                break;
            case COMPLETE:
                setLoading(false);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: category name = "+ categoryName);

        onAdClickListener = adViewModel.onAdClickListener;

        //refresh the list
        adViewModel.fetchUrgentAdList(categoryName, 5);
        adViewModel.fetchAds(categoryName, -1);
    }

}