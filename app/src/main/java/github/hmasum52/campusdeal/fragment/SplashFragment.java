package github.hmasum52.campusdeal.fragment;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.MainActivity;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentSplashBinding;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.PromptDialog;

@AndroidEntryPoint
public class SplashFragment extends Fragment {
    public static final String TAG = "SplashFragment";

    @Inject
    FirebaseAuth auth;

    private FragmentSplashBinding mVB;


    @Inject
    FirebaseFirestore db;
    protected PromptDialog loadingDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentSplashBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingDialog = new PromptDialog(getActivity(), R.layout.dialog_loading);
        mVB.cdLogoLottie.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animation) {
                // ignore
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                // check if user is logged in
                if(auth.getCurrentUser() != null) {
                    // user is logged in
                    // go to home fragment using
                    /*MainActivity.navigateToNewStartDestination(
                            getActivity(),
                            R.id.action_splashFragment_to_homeFragment,
                            R.id.homeFragment // new start destination
                    );*/
                    createUserIsNotExists();
                }else{
                    // user is not logged in
                    // go to login fragment
                    MainActivity.navigateToNewStartDestination(
                            getActivity(),
                            R.id.action_splashFragment_to_onBoardingFragment,
                            R.id.onBoardingFragment // new start destination
                    );
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animation) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animation) {

            }
        });
    }


    protected void showErrorMessage(){
        Snackbar.make(mVB.getRoot(), "Error while signing in", Snackbar.LENGTH_SHORT)
                .setDuration(1000)
                .setBackgroundTint(
                        ResourcesCompat.getColor(getResources(), R.color.c_red, null)
                )
                .setTextColor(
                        ResourcesCompat.getColor(getResources(), R.color.c_white, null)
                )
                .show();
        loadingDialog.hideDialog();
    }

    protected void createUserIsNotExists() {
        loadingDialog.showDialog("Please wait...", R.id.message_tv);

        // check if the user data is in users firestore collection
        // if not then create one
        Log.d(TAG, "createUserIsNotExists: checking if user exists in firestore user collection");
        // Successfully signed in
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Log.d(TAG, "createUserIsNotExists: user is null");
            showErrorMessage();
            return;
        }
        Log.d("createUserIsNotExists: firebase auth user display name: ", fUser.getDisplayName());
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        showErrorMessage();
                        return;
                    }
                    // check if the user exists

                    if(!task.getResult().exists()){ // user does not exists
                        Log.d(TAG, "onComplete: user does not exists. Creating new user");
                        // create a new user
                        db.collection(Constants.USER_COLLECTION)
                                .document(fUser.getUid())
                                .set(new User(fUser))
                                .addOnSuccessListener((aVoid) ->{
                                    Log.d(TAG, "onSuccess: user saved successfully in firestore");
                                    // navigate to home fragment
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_onBoardingFragment_to_homeFragment);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "onFailure: user saved failed in firestore");
                                    showErrorMessage();
                                });
                    }else {
                        // user exists: get user data
                        User user = task.getResult().toObject(User.class);
                        if(user == null){
                            showErrorMessage();
                            return;
                        }
                        Log.d(TAG, "User already exists. User name is "+user.getName());

                        loadingDialog.hideDialog();
                        navigate(user);
                    }
                });
    }

    protected void navigate(User user){
        // check if user has completed profile
        if(user.checkIfProfileIsComplete()){
            // user is not logged in
            // go to login fragment
            MainActivity.navigateToNewStartDestination(
                    getActivity(),
                    R.id.action_splashFragment_to_onBoardingFragment,
                    R.id.onBoardingFragment // new start destination
            );
        }else{
            // navigate to complete profile fragment
            // user is not logged in
            // go to login fragment
            MainActivity.navigateToNewStartDestination(
                    getActivity(),
                    R.id.action_splashFragment_to_completeProfileFragment,
                    R.id.completeProfileFragment // new start destination
            );
        }
    }
}
