package github.hmasum52.campusdeal.fragment;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentOnBoardingBinding;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.PromptDialog;

@AndroidEntryPoint
public class OnBoardingFragment extends SplashFragment {
    private static final String TAG = "OnBoardingFragment";

    private FragmentOnBoardingBinding mVB;

    @Inject
    FirebaseAuth auth;


    // firebase auth
    // See: https://developer.android.com/training/basics/intents/result
    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentOnBoardingBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        loadingDialog = new PromptDialog(getContext(), R.layout.dialog_loading);

        //https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.fragment_login)
                .setGoogleButtonId(R.id.google_sign_in_btn)
                .setEmailButtonId(R.id.email_sing_in_btn)
                .build();

        // Choose authentication providers
        // add email and google sign in and forget password
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        // https://github.com/firebase/FirebaseUI-Android/issues/229#issuecomment-236868365
        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setTheme(R.style.Theme_CampusDeal)
                .setAuthMethodPickerLayout(customLayout)
                .build();


        mVB.getStartBtn.setOnClickListener(v -> {
            // check if user is already logged in
            if(auth.getCurrentUser() != null){
                // navigate to home screen
                //NavHostFragment.findNavController(this).navigate(R.id.action_onBoardingFragment_to_homeFragment);
                checkIfUserExist();
                return;
            }
            signInLauncher.launch(signInIntent);
        });
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        if (result.getResultCode() == RESULT_OK) {
            Log.d(TAG, "onSignInResult: sign in successful");
            checkIfUserExist();
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
            showErrorMessage();
        }
    }

    @Override
    protected void navigate(User user){
        // check if user has completed profile
        if(user.checkIfProfileIsComplete()){
            // navigate to home fragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_onBoardingFragment_to_homeFragment);
        }else{
            // navigate to complete profile fragment
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_onBoardingFragment_to_completeProfileFragment);
        }
    }
}