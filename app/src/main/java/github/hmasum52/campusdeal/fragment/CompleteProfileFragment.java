package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.PopupWindow;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.MainActivity;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentCompleteProfileBinding;
import github.hmasum52.campusdeal.model.AdLocation;
import github.hmasum52.campusdeal.model.Campus;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.PromptDialog;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

@AndroidEntryPoint
public class CompleteProfileFragment extends Fragment {
    private static final String TAG = "CompleteProfileFragment";

    @Inject
    FirebaseFirestore db;

    private FragmentCompleteProfileBinding mVB;

    private UserViewModel userVM;

    private AdLocation adLocation;

    private PromptDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(this).get(UserViewModel.class);
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

                Campus campus = new Campus(campusName,
                        adLocation.getLatitude(),
                        adLocation.getLongitude(),
                        adLocation.getFullAddress(),
                        campusType);


                // save data to firebase
                loadingDialog.showDialog("Please wait", R.id.message_tv);
                userVM.saveProfileCompleteData(phoneNumber, campus)
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

        userVM.getUserLiveData().observe(getViewLifecycleOwner(), user -> {
            if(user == null){
                Log.d(TAG, "onViewCreated: user is null");
                return;
            }
            Log.d(TAG, "onViewCreated: "+user.toString());
            String msg = "Hi "+user.getData().getName()+"\nPlease complete your profile";
            mVB.messageTv.setText(msg);
        });

        mVB.campusNameEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String queryString = s.toString();

                Log.d(TAG, "afterTextChanged: "+queryString);

                if(queryString.isEmpty()){
                    return;
                }

                String fieldName = "name";
                // get campus name from campus collection
                db.collection(Constants.CAMPUS)
                        .orderBy(fieldName)
                        .where(
                                Filter.or(
                                        // query as it is
                                        Filter.and(
                                                Filter.greaterThanOrEqualTo(fieldName, queryString),
                                                Filter.lessThanOrEqualTo(fieldName, queryString + "\uf8ff")
                                        ),
                                        // capitalize first letter
                                        Filter.and(
                                                Filter.greaterThanOrEqualTo(fieldName, queryString.substring(0,1).toUpperCase() + queryString.substring(1)),
                                                Filter.lessThanOrEqualTo(fieldName, queryString.substring(0,1).toUpperCase() + queryString.substring(1) + "\uf8ff")
                                        ),
                                        // lowercase
                                        Filter.and(
                                                Filter.greaterThanOrEqualTo(fieldName, queryString.toLowerCase()),
                                                Filter.lessThanOrEqualTo(fieldName, queryString.toLowerCase() + "\uf8ff")
                                        )
                        )
                        )
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            Log.d(TAG, "onSuccess: "+queryDocumentSnapshots.size());
                            // make adList
                            if(queryDocumentSnapshots.size() == 0){
                                Log.d(TAG, "onSuccess: no campus found");
                                return;
                            }

                            // make array of Campus
                            String campusArray[] = new String[queryDocumentSnapshots.size()];
                            // log campus name
                            for(int i=0; i<queryDocumentSnapshots.size(); i++){
                                campusArray[i] = queryDocumentSnapshots.getDocuments().get(i).getString(fieldName);
                                Log.d(TAG, "onSuccess: "+campusArray[i]);
                            }

                            // set campus name to campusNameEt
                           mVB.campusNameEt.setSimpleItems(campusArray); // https://pspdfkit.com/blog/2016/keyboard-handling-on-android/
                            // set click listener
                            mVB.campusNameEt.setOnItemClickListener((parent, view, position, id) -> {
                                Campus campus = queryDocumentSnapshots.getDocuments().get(position).toObject(Campus.class);

                                // set campus type
                                mVB.campusTypeEt.setText(campus.getType());
                                // set location
                                mVB.locationEt.setText(campus.getFullAddress());
                                adLocation = new AdLocation(campus.getLatitude(), campus.getLongitude(), campus.getFullAddress());

                            });
                        });
            }
        });
    }

    private boolean validateData(){
        if(mVB.phoneNumberEt.getText().toString().isEmpty()){
            mVB.phoneNumberEt.setError("Phone number is required");
            return false;
        }

        // phone number must be 11 digit
        if(mVB.phoneNumberEt.getText().toString().length() != 11){
            mVB.phoneNumberEt.setError("Phone number must be 11 digit");
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
