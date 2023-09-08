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
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import org.parceler.Parcels;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentEditProfileBinding;
import github.hmasum52.campusdeal.model.AdLocation;
import github.hmasum52.campusdeal.model.Campus;
import github.hmasum52.campusdeal.model.StateData;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.PromptDialog;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

public class EditProfileFragment extends Fragment {
    private static final String TAG = "EditProfileFragment";

    private FragmentEditProfileBinding mVB;

    private UserViewModel userVB;

    private Campus selectedCampus;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVB = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentEditProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // init back button
        mVB.backBtn.setOnClickListener(v -> {
            getActivity().onBackPressed();
        });

        mVB.locationEt.setOnClickListener(v -> {
            // show location picker
            // navigate to GoogleMapFragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.mapLocationInputFragment);
        });


        userVB.getUserLiveData().observe(getViewLifecycleOwner(), userStateData -> {
           if(userStateData.getStatus() == StateData.DataStatus.SUCCESS){
               User user = userStateData.getData();
               if(user == null){
                   Snackbar.make(mVB.getRoot(), "Error getting user data", Snackbar.LENGTH_SHORT)
                            .setDuration(1000).show();
                   return;
               }
               selectedCampus = user.getCampus();
               if(mVB.nameEt.getText().toString().isEmpty())
                    updateUI(user);
           }
        });

        NavController navController = NavHostFragment.findNavController(this);
        if(navController.getCurrentBackStackEntry() != null){
            navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("location")
                    .observe(getViewLifecycleOwner(), parcelable -> {
                        AdLocation adLocation =  Parcels.unwrap((Parcelable) parcelable);
                        Log.d("PostAdFragment", "onViewCreated: "+adLocation.toString());
                        mVB.locationEt.setText(adLocation.getFullAddress());

                        selectedCampus.setLatitude(adLocation.getLatitude());
                        selectedCampus.setLongitude(adLocation.getLongitude());
                        selectedCampus.setFullAddress(adLocation.getFullAddress());
                    });
        }

        PromptDialog loadingDialog = new PromptDialog(getActivity(), R.layout.dialog_loading);

        // update profile
        mVB.updateProfileBtn.setOnClickListener(v -> {
            if(validate()){
                loadingDialog.showDialog("Please wait", R.id.message_tv);
                userVB.updateProfile(mVB.nameEt.getText().toString(),
                        mVB.phoneNumberEt.getText().toString(),
                        selectedCampus)
                        .observe(requireActivity(), updateProfileStateData -> {
                            loadingDialog.hideDialog();
                            if(updateProfileStateData.getStatus() == StateData.DataStatus.SUCCESS) {
                                Snackbar.make(mVB.getRoot(), "Profile updated successfully", Snackbar.LENGTH_SHORT)
                                        .setDuration(1000).show();
                                //getActivity().onBackPressed();
                            }else{
                                Snackbar.make(mVB.getRoot(), "Error updating profile", Snackbar.LENGTH_SHORT)
                                        .setDuration(1000).show();
                            }
                        });
            }
        });
    }

    private void updateUI(User user){
        mVB.nameEt.setText(user.getName());
        mVB.phoneNumberEt.setText(user.getPhone());
        mVB.campusNameEt.setText(user.getCampus().getName());
        mVB.campusTypeEt.setText(user.getCampus().getType(), false);
        mVB.locationEt.setText(user.getCampus().getFullAddress());
    }


    private boolean validate(){
        // check if name is empty
        if(mVB.nameEt.getText().toString().isEmpty()){
            mVB.nameEt.setError("Name can't be empty");
            return false;
        }

        // check if phone number is empty
        if(mVB.phoneNumberEt.getText().toString().isEmpty()){
            mVB.phoneNumberEt.setError("Phone number can't be empty");
            return false;
        }

        // phone number must be 11 digit
        if(mVB.phoneNumberEt.getText().toString().length() != 11){
            mVB.phoneNumberEt.setError("Phone number must be 11 digit");
            return false;
        }

        // check if campus name is empty
        if(mVB.campusNameEt.getText().toString().isEmpty()){
            mVB.campusNameEt.setError("Campus name can't be empty");
            return false;
        }

        selectedCampus.setName(mVB.campusNameEt.getText().toString());

        // check if campus type is empty
        if(mVB.campusTypeEt.getText().toString().isEmpty()){
            mVB.campusTypeEt.setError("Campus type can't be empty");
            return false;
        }

        selectedCampus.setType(mVB.campusTypeEt.getText().toString());

        // check if location is empty
        if(mVB.locationEt.getText().toString().isEmpty()){
            mVB.locationEt.setError("Location can't be empty");
            return false;
        }

        return true;
    }
}
