package github.hmasum52.campusdeal.fragment;

import static github.hmasum52.campusdeal.model.StateData.DataStatus.LOADING;

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
import androidx.lifecycle.ViewModelProvider;
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
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

@AndroidEntryPoint
public class SplashFragment extends Fragment {
    public static final String TAG = "SplashFragment";

    @Inject
    FirebaseAuth auth;

    private FragmentSplashBinding mVB;


    @Inject
    FirebaseFirestore db;
    protected PromptDialog loadingDialog;

    private UserViewModel userVM;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userVM = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
    }

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
                    Log.d(TAG, "onAnimationEnd: user is logged in");
                    checkIfUserExist();
                }else{
                    // user is not logged in
                    Log.d(TAG, "onAnimationEnd: user is not logged in. Navigating to login onBoardingFragment");
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

    protected void checkIfUserExist() {

        loadingDialog.showDialog("Please wait...", R.id.message_tv);

        // check if the user data is in users firestore collection
        // if not then create one
        Log.d(TAG, "checkIfUserExist: checking if user exists in firestore user collection");
        // Successfully signed in
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fUser == null){
            Log.d(TAG, "checkIfUserExist: user is null");
            showErrorMessage();
            return;
        }
        Log.d("checkIfUserExist: firebase auth user display name: ", fUser.getDisplayName());
        userVM.getUserLiveData().observe(
                requireActivity(),
                userState -> {
                    switch (userState.getStatus()){
                        case LOADING:
                            Log.d(TAG, "checkIfUserExist: loading user data");
                            break;
                        case SUCCESS:
                            Log.d(TAG, "checkIfUserExist: user data loaded successfully");
                            loadingDialog.hideDialog();
                            navigate(userState.getData());
                            break;
                        case ERROR:
                            if(userState.getError().getMessage().equals("User does not exists")) {
                                userVM.saveUserData().observe(
                                        requireActivity(),
                                        saveUserState -> {
                                            switch (saveUserState.getStatus()) {
                                                case LOADING:
                                                    Log.d(TAG, "checkIfUserExist: saving user data");
                                                    break;
                                                case SUCCESS:
                                                    Log.d(TAG, "checkIfUserExist: user data saved successfully");
                                                    loadingDialog.hideDialog();
                                                    navigate(userState.getData());
                                                    break;
                                                case ERROR:
                                                    Log.d(TAG, "checkIfUserExist: error while saving user data");
                                                    showErrorMessage();
                                                    break;
                                            }
                                        }
                                );
                            }
                            Log.d(TAG, "checkIfUserExist: error while loading user data");
                            showErrorMessage();
                            break;
                    }
                }
        );
    }

    protected void navigate(User user){
        // check if user has completed profile
        if(user.checkIfProfileIsComplete()){
            // user is not logged in
            // go to login fragment
            Log.d(TAG, "navigate: user profile is complete");
            MainActivity.navigateToNewStartDestination(
                    getActivity(),
                    R.id.action_splashFragment_to_onBoardingFragment,
                    R.id.onBoardingFragment // new start destination
            );
        }else{
            // navigate to complete profile fragment
            // user is not logged in
            // go to login fragment
            Log.d(TAG, "navigate: user profile is not complete");
            MainActivity.navigateToNewStartDestination(
                    getActivity(),
                    R.id.action_splashFragment_to_completeProfileFragment,
                    R.id.completeProfileFragment // new start destination
            );
        }
    }
}
