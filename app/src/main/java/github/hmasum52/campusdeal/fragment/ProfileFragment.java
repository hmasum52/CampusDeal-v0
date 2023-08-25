package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.MainActivity;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.ProfileOptionListAdapter;
import github.hmasum52.campusdeal.databinding.FragmentProfileBinding;
import github.hmasum52.campusdeal.util.ProfileOption;

@AndroidEntryPoint
public class ProfileFragment extends Fragment {

    private FragmentProfileBinding mVB;

    @Inject
    FirebaseUser fUser;

    @Inject
    FirebaseAuth fAuth;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // check if user has profile image
        if(fUser.getPhotoUrl() != null){
            // load the image using glide
            Glide.with(this)
                    .load(fUser.getPhotoUrl())
                    .placeholder(R.drawable.avatar_bg)
                    .into(mVB.profileImage);
        }else{
            // set name initial to nameInitialTV
            mVB.nameInitialsText.setText(fUser.getDisplayName().substring(0,1));
        }

        // set the name to username text view
        mVB.usernameTv.setText(fUser.getDisplayName());

        // set the email to email text view
        mVB.emailTv.setText(fUser.getEmail());

        // option recycler view init
        optionRecyclerViewInit();

        // sign out button init
        // sign out button click listener
        mVB.signOutBtn.setOnClickListener(v -> {
            signOut();
        });
    }

    private void signOut(){
        // sign out the user
        // https://firebase.google.com/docs/auth/android/firebaseui#sign_out
        // if sign in with google then also sign out from google
        // https://stackoverflow.com/q/70101036/13877490
        // https://stackoverflow.com/questions/38707133/google-firebase-sign-out-and-forget-user-in-android-app
        AuthUI.getInstance()
                .signOut(requireContext())
                .addOnCompleteListener(task -> {
                    // navigate to onboarding fragment
                    MainActivity.navigateToNewStartDestination(ProfileFragment.this.getActivity(), R.id.onBoardingFragment);
                });

    }

    private void optionRecyclerViewInit() {

        // make a list of profile options
        // options: my orders, my wishlist, my cart, my address, my account
        List<ProfileOption> profileOptionList = new ArrayList<ProfileOption>() {
            {
                add(new ProfileOption("My Ads", R.drawable.price_tag_rotate_svgrepo_com, "View your ads"));
                add(new ProfileOption("My Wishlist", R.drawable.baseline_favorite_24, "View your wishlist"));
                add(new ProfileOption("Edit Account", R.drawable.baseline_edit_24,"Edit your account"));
            }
        };

        // set the adapter
        ProfileOptionListAdapter adapter = new ProfileOptionListAdapter(profileOptionList);
        adapter.setOnItemClickListener((profileOption) -> {
            // navigate to the fragment
            switch (profileOption.getTitle()){
                case "My Ads":
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_myAdsFragment);
                    break;
                case "My Wishlist":
                    NavHostFragment.findNavController(this).navigate(R.id.action_profileFragment_to_myWishlistFragment);
                    break;
               /* case "Edit Account":
                    MainActivity.navigateToNewStartDestination(ProfileFragment.this.getActivity(), R.id.editAccountFragment);
                    break;*/
            }
        });
        mVB.optionListRv.setAdapter(adapter);
    }
}