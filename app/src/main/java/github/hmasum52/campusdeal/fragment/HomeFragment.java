package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.CategoryViewPagerAdapter;
import github.hmasum52.campusdeal.databinding.FragmentHomeBinding;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;

// https://developer.android.com/training/dependency-injection/hilt-android#android-classes
// Hilt can provide dependencies to other Android classes that have
// the @AndroidEntryPoint annotation
@AndroidEntryPoint
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    // view binding
    private FragmentHomeBinding mVB;

    @Inject
    FirebaseFirestore db;


    @Inject
    FirebaseUser fUser;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentHomeBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // https://androidwave.com/viewpager2-with-tablayout-android-example/
        // https://developer.android.com/guide/navigation/advanced/swipe-view-2
        // https://www.geeksforgeeks.org/how-to-implement-dynamic-tablayout-in-android/
        createUserIsNotExists();

        // add product floating action button listener
        mVB.addProductFab.setOnClickListener(v -> {
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_homeFragment_to_addProductFragment);
        });

        // categories
        // Books, Stationary, Electronics, Accessories, Clothes,
        // Tutoring, Musical Instruments, Sports



        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < Constants.categoryList.size(); i++) {
            fragments.add(new CategoryFragment(
                    Constants.categoryList.get(i),
                    ad -> {
                        // open ad details fragment
                        // send ad objecdt to ad details fragment
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("ad", Parcels.wrap(ad));
                        NavHostFragment.findNavController(this)
                                .navigate(R.id.action_homeFragment_to_adDetailsFragment, bundle);
                    }
            ));
        }

        mVB.pager.setOffscreenPageLimit(2);

        mVB.pager.setAdapter(new CategoryViewPagerAdapter(fragments,
                getChildFragmentManager(),
                getLifecycle())
        );

        // tab layout mediator to connect viewpager2 with tablayout
        new TabLayoutMediator(mVB.tabLayout, mVB.pager, ((TabLayout.Tab tab, int position) -> {
            tab.setText(Constants.categoryList.get(position));
        })).attach();
    }


    private void createUserIsNotExists() {
        // check if the user data is in users firestore collection
        // if not then create one
        Log.d(TAG, db == null ? "db is null" : "db is not null");
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        // error occurred
                        // TODO:
                        // show error message
                        return;
                    }
                    // check if the user exists
                    Log.d(TAG, " user exists in fire store user collection.");
                    if(!task.getResult().exists()){ // user does not exists
                        // create a new user
                        db.collection(Constants.USER_COLLECTION)
                                .document(fUser.getUid())
                                .set(new User(fUser))
                                .addOnSuccessListener((aVoid) ->{
                                    Log.d(TAG, "onSuccess: user saved successfully in firestore");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "onFailure: user saved failed in firestore");
                                });
                    }else {
                        // user exists: get user data
                        User user = task.getResult().toObject(User.class);
                        Log.d(TAG, "onComplete: user data: "+user.toString());
                    }
                });
    }
}