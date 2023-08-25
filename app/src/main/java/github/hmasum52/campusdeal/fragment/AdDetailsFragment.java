package github.hmasum52.campusdeal.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.AdImageViewPagerAdapter;
import github.hmasum52.campusdeal.databinding.FragmentAdDetailsBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Constants;

@AndroidEntryPoint
public class AdDetailsFragment extends Fragment {
    public static final String TAG = "AdDetailsFragment";

    private FragmentAdDetailsBinding mVB;

    @Inject
    FirebaseFirestore db;


    @Inject
    FirebaseAuth auth;

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

        mVB.contact.setOnClickListener(v -> {
            startMailSendIntent(
                    "Want to buy "+ad.getTitle(),
                    mVB.ownerEmail.getText().toString(),
                    "Hi, I am interested in your ad for selling "+ad.getTitle()+" on CampusDeal App.");
        });

        // favorite btn
        initFavoriteButton();

        // back btn
        initBackButton();
    }

    private void initBackButton() {
        mVB.backBtnCard.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });
    }

    // https://stackoverflow.com/questions/60925946/firestore-data-modeling-for-library-books-wishlist-data
    // https://stackoverflow.com/questions/74993384/how-to-make-a-wishedproduct-widget-using-firebasefirestore
    private void initFavoriteButton() {
        mVB.favBtnCard.setOnClickListener(v -> {
            Log.d("TAG", "onViewCreated: fav btn clicked");
            if(mVB.favouriteBtn.isSelected()){
                mVB.favouriteBtn.setSelected(false);
                Log.d("TAG", "onViewCreated: checked");
                removeFromWishlist();
            }else{
                mVB.favouriteBtn.setSelected(true);
                Log.d("TAG", "onViewCreated: unchecked");
                addToWishlist();
            }
        });

        // check if ad is in wishlist
        db.collection(Constants.USER_COLLECTION)
                .document(auth.getUid())
                .collection(Constants.WISHLIST_COLLECTION)
                .document(ad.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        mVB.favouriteBtn.setSelected(true);
                        Log.d(TAG, "onViewCreated:initFavoriteButton: ad is in wishlist");
                    }else{
                        mVB.favouriteBtn.setSelected(false);
                        Log.d(TAG, "onViewCreated:initFavoriteButton: ad is not in wishlist");
                    }
                });
    }

    private void addToWishlist(){

        // wishlist object
        // https://stackoverflow.com/questions/60925946/firestore-data-modeling-for-library-books-wishlist-data
        Map<String, Object> wishlistData = new HashMap<>();
        wishlistData.put("adId", ad.getId());
        wishlistData.put("title", ad.getTitle());
        wishlistData.put("date", new Date());


        // add to fire store users/{userId}/wishlist/{adId}
        db.collection(Constants.USER_COLLECTION)
                .document(auth.getUid())
                .collection(Constants.WISHLIST_COLLECTION)
                .document(ad.getId())
                .set(wishlistData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onViewCreated: added to wishlist");
                    // make snake bar
                    Snackbar.make(mVB.getRoot(), "Added to wishlist", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "onViewCreated: failed to add to wishlist");
                    Snackbar.make(mVB.getRoot(), "Failed to add to wishlist", Snackbar.LENGTH_SHORT).show();
                });
    }

    private void removeFromWishlist(){
        // remove from fire store users/{userId}/wishlist/{adId}
        db.collection(Constants.USER_COLLECTION)
                .document(auth.getUid())
                .collection(Constants.WISHLIST_COLLECTION)
                .document(ad.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onViewCreated: removed from wishlist");
                    // make snake bar
                    Snackbar.make(mVB.getRoot(), "Removed from wishlist", Snackbar.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.d("TAG", "onViewCreated: failed to remove from wishlist");
                    Snackbar.make(mVB.getRoot(), "Failed to remove from wishlist", Snackbar.LENGTH_SHORT).show();
                });
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
                    if(user.getProfileImageUrl()!=null)
                        Glide.with(this).load(user.getProfileImageUrl()).into(mVB.ownerAvatar);
                    else mVB.avatarText.setText(user.getName().substring(0,1));
                })
                .addOnFailureListener(e -> {
                    // if failed to get owner info
                    // set owner name to unknown
                    mVB.ownerName.setText("Unknown");
                });
    }

    private void startMailSendIntent(String subject, String to, String body) {
        String[] addresses = {to};

        Intent selectorIntent = new Intent(Intent.ACTION_SENDTO);
        selectorIntent.setData(Uri.parse("mailto:"));

        final Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, addresses);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, body);
        emailIntent.setSelector(selectorIntent);

        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

}
