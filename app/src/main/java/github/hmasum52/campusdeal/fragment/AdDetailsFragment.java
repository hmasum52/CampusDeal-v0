package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.text.DateFormat;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.AdImageViewPagerAdapter;
import github.hmasum52.campusdeal.databinding.FragmentAdDetailsBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;

@AndroidEntryPoint
public class AdDetailsFragment extends Fragment {
    private FragmentAdDetailsBinding mVB;

    @Inject
    FirebaseFirestore db;

    private Ad ad;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // get AdTitle from arguments
        if(getArguments()!=null){
           // Parcels to get Ad object from argument
              ad = Parcels.unwrap(getArguments().getParcelable("ad"));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentAdDetailsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        AdImageViewPagerAdapter adapter = new AdImageViewPagerAdapter(ad.getImageUriList());
        mVB.imageVp.setAdapter(adapter);
        adapter.differ.submitList(ad.getImageUriList());

        // add dot indicator to view pager
        // https://github.com/AdrianKuta/ViewPagerDotsIndicator
        new TabLayoutMediator(mVB.dotIndicator, mVB.imageVp, (tab, position) -> {
        }).attach();

        // set upload date in dd MMM yyyy format
        mVB.uploadDate.setText(DateFormat.getDateInstance(DateFormat.MEDIUM).format(ad.getUploadDate()));

        mVB.distance.setText("1.2 km");

        // make comma separated price
        String price = String.format("BDT %.2f", ad.getPrice());
        mVB.price.setText(price);

        mVB.title.setText(ad.getTitle());

        mVB.description.setText(ad.getDescription());

       // get ownerInfo
        getOwnerInfo();
    }

    private  void getOwnerInfo(){
     // fetch owner info form fire store users collection
        db.collection(Constants.USER_COLLECTION)
                .document(ad.getSellerId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    // get user object from documentSnapshot
                    User user = documentSnapshot.toObject(User.class);
                    // set owner name
                    mVB.ownerName.setText(user.getName());
                    // set owner email
                    mVB.ownerEmail.setText(user.getEmail());
                    // set owner profile image
                    Glide.with(this).load(user.getProfileImageUrl()).into(mVB.ownerAvatar);
                })
                .addOnFailureListener(e -> {
                    // if failed to get owner info
                    // set owner name to unknown
                    mVB.ownerName.setText("Unknown");
                });
    }
}