package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.SearchResultAdapter;
import github.hmasum52.campusdeal.databinding.FragmentMyWishlistBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.util.Constants;

@AndroidEntryPoint
public class MyWishlistFragment extends Fragment {
    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    private static final String TAG = "MyWishlistFragment";

    private FragmentMyWishlistBinding mVB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentMyWishlistBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // updateNoItemUI();

        mVB.backBtn.setOnClickListener(v -> {
            Log.d(TAG, "onViewCreated: back button clicked");
            NavHostFragment.findNavController(this).popBackStack();
        });

        SearchResultAdapter adapter = new SearchResultAdapter();
        mVB.wishlistRv.setAdapter(adapter);

        adapter.setRecyclerItemClickListener((Ad ad)->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("ad", Parcels.wrap(ad));
            NavHostFragment.findNavController(this).navigate(R.id.action_myWishlistFragment_to_adDetailsFragment, bundle);
        });

        // fetch wishlist data from fire store
        // fetch all document id from users/<uid>/wishlist collection
        db.collection(Constants.USER_COLLECTION)
                .document(auth.getUid())
                .collection(Constants.WISHLIST_COLLECTION)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if(queryDocumentSnapshots.isEmpty()){
                        updateNoItemUI();
                    }else{
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.wishlistRv.setVisibility(View.VISIBLE);

                        // get all the ad id from the wishlist collection
                        List<String> adIdList = new ArrayList<>();
                        for (int i = 0; i < queryDocumentSnapshots.size(); i++) {
                            adIdList.add(queryDocumentSnapshots.getDocuments().get(i).getId());
                        }

                        Log.d(TAG, "onViewCreated: adIdList: "+adIdList);

                        // now fetch all the ads from ads collection
                        db.collection(Constants.ADS_COLLECTION)
                                .whereIn("id", adIdList)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots1 -> {
                                    mVB.loadingCpb.setVisibility(View.GONE);
                                    List<Ad> adList = queryDocumentSnapshots1.toObjects(Ad.class);
                                    Log.d(TAG, "onViewCreated: adList: "+adList.size());
                                    adapter.differ.submitList(adList);
                                })
                                .addOnFailureListener(e -> {
                                    mVB.loadingCpb.setVisibility(View.GONE);
                                    Log.d(TAG, "onViewCreated: failed to fetch wishlist data");
                                });
                    }
                });
    }


    private void updateNoItemUI() {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.wishlistRv.setVisibility(View.GONE);

        String msg = "You haven't added any item to your wishlist yet.";
        mVB.noItemPlaceholder.messageTv.setText(msg);
        String tips = "You can add an item to your wishlist by clicking on the heart icon on the top right corner of an ad";
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }
}
