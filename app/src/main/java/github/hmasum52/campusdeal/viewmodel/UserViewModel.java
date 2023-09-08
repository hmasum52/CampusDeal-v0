package github.hmasum52.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.model.Campus;
import github.hmasum52.campusdeal.model.StateLiveData;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;

@HiltViewModel
public class UserViewModel extends ViewModel {
    public static final String TAG = "UserViewModel";

    // user profile live data
    StateLiveData<User> userLiveData = new StateLiveData<>();

    FirebaseFirestore db;

    @Inject
    public UserViewModel(FirebaseFirestore db) {
        Log.d(TAG, "UserViewModel: constructor called");
        this.db = db;
    }

    public StateLiveData<User> getUserLiveData() {
        // check if user is logged in
        if(userLiveData.getValue() == null){
            Log.d(TAG, "getUserLiveData: user is not logged in");
            fetchUserProfile();
        }
        return userLiveData;
    }

    // fetch user profile from firebase
    public StateLiveData<User> fetchUserProfile(){
        Log.d(TAG, "fetchUserProfile: fetching user profile");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Log.d(TAG, "fetchUserProfile: user is not logged in");
            userLiveData.postError(new Exception("User is not logged in"));
            return userLiveData;
        }
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        Log.d(TAG, "fetchUserProfile: failed to fetch user profile");
                        userLiveData.postError(task.getException());
                        return;
                    }
                    // check if the user exists

                    if(!task.getResult().exists()){ // user does not exists
                        Log.d(TAG, "onComplete: user does not exists. Creating new user");
                        userLiveData.postError(new RuntimeException("User does not exists"));
                    }else {
                        // user exists: get user data
                        User user = task.getResult().toObject(User.class);
                        if(user == null){
                            userLiveData.postError(new Exception("User is null"));
                            return;
                        }
                        Log.d(TAG, "User already exists. User name is "+user.getName());

                        // success
                        userLiveData.postSuccess(user);
                    }
                });

        return userLiveData;
    }

    public StateLiveData<Boolean> saveUserData(){
        StateLiveData<Boolean> saveUserDataLiveData = new StateLiveData<>();
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        // create a new user
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .set(new User(fUser))
                .addOnSuccessListener((aVoid) ->{
                    Log.d(TAG, "onSuccess: user saved successfully in firestore");
                    // success
                    saveUserDataLiveData.postSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: user saved failed in firestore");
                    saveUserDataLiveData.postError(e);
                });
        return saveUserDataLiveData;
    }

    public StateLiveData<Boolean> savePhoneNumber(String number){
        StateLiveData<Boolean> numberLiveData = new StateLiveData<>();
        // save phone number to firebase
        Log.d(TAG, "savePhoneNumber: saving phone number to firebase");
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Log.d(TAG, "savePhoneNumber: user is not logged in");
            numberLiveData.postError(new Exception("User is not logged in"));
            return null;
        }

        // save phone number to firebase
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .update("phone", number)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "savePhoneNumber: phone number saved successfully");
                    numberLiveData.postSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "savePhoneNumber: failed to save phone number");
                    numberLiveData.postError(e);
                });

        return numberLiveData;
    }

    // save campus and phone number
    // used in profile complete
    public StateLiveData<Boolean> saveProfileCompleteData(String phone, Campus campus){
        StateLiveData<Boolean> profileCompleteLiveData = new StateLiveData<>();

        // saving profile complete data
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();

        if(fUser == null){
            Log.d(TAG, "saveProfileCompleteData: user is not logged in");
            profileCompleteLiveData.postError(new Exception("User is not logged in"));
            return null;
        }

        // save phone number and campus to firebase
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .update("phone", phone, "campus", campus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "saveProfileCompleteData: phone number and campus saved successfully");
                    profileCompleteLiveData.postSuccess(true);
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "saveProfileCompleteData: failed to save phone number and campus");
                    profileCompleteLiveData.postError(e);
                });

        return profileCompleteLiveData;
    }
}
