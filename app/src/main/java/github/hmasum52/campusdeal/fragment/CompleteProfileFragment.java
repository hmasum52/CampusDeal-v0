package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;

import org.parceler.Parcels;

import github.hmasum52.campusdeal.MainActivity;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentCompleteProfileBinding;
import github.hmasum52.campusdeal.model.AdLocation;
import github.hmasum52.campusdeal.model.Campus;
import github.hmasum52.campusdeal.util.PromptDialog;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

public class CompleteProfileFragment extends Fragment {
    private FragmentCompleteProfileBinding mVB;

    private UserViewModel userVM;

    private AdLocation adLocation;

    private PromptDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentCompleteProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingDialog = new PromptDialog(getActivity(), R.layout.dialog_loading);



        mVB.locationEt.setOnClickListener(v -> {
            // show location picker
            // navigate to GoogleMapFragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.mapLocationInputFragment);
        });


        NavController navController = NavHostFragment.findNavController(this);
        if(navController.getCurrentBackStackEntry() != null){
            navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("location")
                    .observe(getViewLifecycleOwner(), parcelable -> {
                        adLocation =  Parcels.unwrap((Parcelable) parcelable);
                        Log.d("PostAdFragment", "onViewCreated: "+adLocation.toString());
                        mVB.locationEt.setText(adLocation.getFullAddress());
                    });
        }

        mVB.completeProfileBtn.setOnClickListener(v -> {
            if(validateData()){
                // get data ui
                // save data to firebase
                String phoneNumber = mVB.phoneNumberEt.getText().toString();
                String campusName = mVB.campusNameEt.getText().toString();
                String campusType = mVB.campusTypeEt.getText().toString();

                Log.d("CompleteProfileFragment", "onViewCreated: "+phoneNumber);
                Log.d("CompleteProfileFragment", "onViewCreated: "+campusName);
                Log.d("CompleteProfileFragment", "onViewCreated: "+campusType);

                // save data to firebase
                loadingDialog.showDialog("Please wait", R.id.message_tv);
                userVM.saveProfileCompleteData(phoneNumber,
                        new Campus(campusName, adLocation.getLatitude(), adLocation.getLongitude(), adLocation.getFullAddress(), campusType))
                        .observe(requireActivity(),
                                response-> {
                                    switch (response.getStatus()){
                                        case SUCCESS:
                                            Log.d("CompleteProfileFragment", "onViewCreated: profile complete");
                                            // go to home fragment
                                            loadingDialog.hideDialog();
                                            MainActivity.navigateToNewStartDestination(
                                                    getActivity(),
                                                    R.id.action_completeProfileFragment_to_homeFragment,
                                                    R.id.homeFragment // new start destination
                                            );
                                            break;
                                        case ERROR:
                                            Log.d("CompleteProfileFragment", "onViewCreated: "+response.getError().getMessage());
                                            loadingDialog.hideDialog();
                                            Snackbar.make(mVB.getRoot(), response.getError().getMessage(), Snackbar.LENGTH_LONG).show();
                                            break;
                                        case LOADING:
                                            Log.d("CompleteProfileFragment", "onViewCreated: loading");
                                            break;
                                    }
                                });
            }
        });

    }

    private boolean validateData(){
        if(mVB.phoneNumberEt.getText().toString().isEmpty()){
            mVB.phoneNumberEt.setError("Phone number is required");
            return false;
        }

        if(mVB.campusNameEt.getText().toString().isEmpty()){
            mVB.campusNameEt.setError("Campus name is required");
            return false;
        }

        if(mVB.campusTypeEt.getText().toString().isEmpty()){
            mVB.campusTypeEt.setError("Campus type is required");
            return false;
        }

        if(adLocation == null){
            mVB.locationEt.setError("Location is required. Please click the text field to pick a location");
            return false;
        }

        return true;
    }
}
