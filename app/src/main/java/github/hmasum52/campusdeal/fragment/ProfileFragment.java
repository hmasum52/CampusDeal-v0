package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
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
        fAuth.signOut();
        // navigate to onboarding fragment
        MainActivity.navigateToNewStartDestination(getActivity(), R.id.onBoardingFragment);
    }

    private void optionRecyclerViewInit() {

        // make a list of profile options
        // options: my orders, my wishlist, my cart, my address, my account
        List<ProfileOption> profileOptionList = new ArrayList<ProfileOption>() {
            {
                add(new ProfileOption("My Orders", "View your orders"));
                add(new ProfileOption("My Wishlist", "View your wishlist"));
                add(new ProfileOption("My Cart", "View your cart"));
                add(new ProfileOption("My Address", "View your address"));
                add(new ProfileOption("Edit Account", "View your account"));
            }
        };

        // set the adapter
        ProfileOptionListAdapter adapter = new ProfileOptionListAdapter(profileOptionList);
        adapter.setOnItemClickListener((view, position) -> {
                    // TODO: 8/3/2021 handle click
                });
        mVB.optionListRv.setAdapter(adapter);
    }
}