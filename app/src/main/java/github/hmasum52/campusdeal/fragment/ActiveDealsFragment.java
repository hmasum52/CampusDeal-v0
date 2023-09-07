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
import com.google.firebase.firestore.Query;

import org.parceler.Parcels;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.DealRequestListAdapter;
import github.hmasum52.campusdeal.adapter.RecyclerItemClickListener;
import github.hmasum52.campusdeal.databinding.FragmentActiveDealsBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.DealRequest;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.Util;

@AndroidEntryPoint
public class ActiveDealsFragment extends Fragment {
    private static final String TAG = "ActiveDealsFragment";

    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    private FragmentActiveDealsBinding mVB;

    private DealRequestListAdapter adapter;

    // index of this fragment in the viewpager
    int position = 0;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentActiveDealsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("ActiveDealsFragment", "onViewCreated: "+this );
        position = Util.getViewPagerFragmentIndex(this, 2);

        // define adapter
        adapter = new DealRequestListAdapter(this::navigateToAdsFragment);
        mVB.dealListRv.setAdapter(adapter);

        Log.d(TAG, "onViewCreated: position = "+position);
        if(position == 0) {
            updateRequestForYouUI();
        }
        else if(position == 1){
            adapter.setBuyer(true);
            updateYourRequestUI();
        }
    }

    private void navigateToAdsFragment(DealRequest dealRequest) {
        int action = position == 0 ? R.id.action_dealRequestFragment_to_adReviewFragment
                : R.id.action_dealRequestFragment_to_adDetailsFragment;
        db.collection(Constants.ADS_COLLECTION)
                .document(dealRequest.getAdId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        Ad ad = documentSnapshot.toObject(Ad.class);

                        Bundle bundle = new Bundle();
                        bundle.putParcelable(Constants.AD_KEY, Parcels.wrap(ad));
                        NavHostFragment.findNavController(this)
                                .navigate(
                                        action,
                                        bundle
                                );
                    }
                });
    }


    private void updateRequestForYouUI() {
        Log.d(TAG, "updateRequestForYouUI: called");
        String noDataMsg = "No request for you";
        String noDataTips = "You can see all the request you have received here";
        fetchDataFromFireStore("sellerId",noDataMsg, noDataTips);
    }

    private void updateYourRequestUI() {
        Log.d(TAG, "updateYourRequestUI: called");
        String noDataMsg = "You haven't made any request yet!";
        String noDataTips = "You can see all the request you have sent here";
        fetchDataFromFireStore("buyerId", noDataMsg, noDataTips);
    }

    private void updateNoItemUI(String msg, String tips) {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.dealListRv.setVisibility(View.GONE);

        mVB.noItemPlaceholder.messageTv.setText(msg);
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }

    private void fetchDataFromFireStore(String field, String noDataMsg, String noDataTips){
        mVB.loadingPb.setVisibility(View.VISIBLE);
        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .whereEqualTo(field, auth.getUid())
                .orderBy("date", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVB.loadingPb.setVisibility(View.GONE);
                    if(queryDocumentSnapshots.isEmpty()){
                        updateNoItemUI(noDataMsg, noDataTips);
                    }else{
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.dealListRv.setVisibility(View.VISIBLE);
                        adapter.differ.submitList(queryDocumentSnapshots.toObjects(DealRequest.class));
                    }
                }).addOnFailureListener(
                        e -> {
                            mVB.loadingPb.setVisibility(View.GONE);
                            Log.d(TAG, "onViewCreated: "+e.getMessage());
                            updateNoItemUI("Error getting data!", "Please check your internet connection");
                        }
                );
    }


}
