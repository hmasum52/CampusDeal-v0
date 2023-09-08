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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.parceler.Parcels;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.DealHistoryAdapter;
import github.hmasum52.campusdeal.adapter.DealRequestListAdapter;
import github.hmasum52.campusdeal.databinding.FragmentActiveDealsBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.Deal;
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

    @Inject
    FirebaseUser fUser;

    private FragmentActiveDealsBinding mVB;

    private DealRequestListAdapter dealRequestListAdapter;

    private DealHistoryAdapter dealHistoryAdapter;

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
        position = Util.getViewPagerFragmentIndex(this, 4);

        dealRequestListAdapter = new DealRequestListAdapter(this::navigateToAdsFragmentFromDealRequest);
        dealHistoryAdapter = new DealHistoryAdapter(this::navigateToAdsFragmentFromDealHistory);


        Log.d(TAG, "onViewCreated: position = "+position);
        switch (position){
            case 0: // request for you
                // define adapter
                Log.d(TAG, "onViewCreated: Request for you");
                mVB.dealListRv.setAdapter(dealRequestListAdapter);
                updateRequestForYouUI();
                break;
            case 1: // your request
                Log.d(TAG, "onViewCreated: Your request");
                dealRequestListAdapter.setBuyer(true);
                mVB.dealListRv.setAdapter(dealRequestListAdapter);
                updateYourRequestUI();
                break;
            case 2: // my purchase
                Log.d(TAG, "onViewCreated: My purchase");
                dealHistoryAdapter.setBuyer(true);
                fetchDealHistory(Constants.USER_BUY_HISTORY_COLLECTION);
                mVB.dealListRv.setAdapter(dealHistoryAdapter);
                break;
            case 3: // my sell
                Log.d(TAG, "onViewCreated: My sell");
                fetchDealHistory(Constants.USER_DEALS_COLLECTION);
                mVB.dealListRv.setAdapter(dealHistoryAdapter);
                break;
        }

    }

    private void navigateToAdsFragmentFromDealRequest(DealRequest dealRequest) {
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

    private void navigateToAdsFragmentFromDealHistory(Deal deal){
        Log.d(TAG, "navigateToAdsFragmentFromDealHistory: called");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.AD_KEY, Parcels.wrap(deal.getAd()));
        NavHostFragment.findNavController(this)
                .navigate(
                        R.id.action_dealRequestFragment_to_adDetailsFragment,
                       bundle
                );
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

    private void updateYourSellUI() {
        Log.d(TAG, "updateYourSellUI: called");
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
                    Log.d(TAG, "onViewCreated: total deal requests = "+queryDocumentSnapshots.size());
                    if(queryDocumentSnapshots.isEmpty()){
                        updateNoItemUI(noDataMsg, noDataTips);
                    }else{
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.dealListRv.setVisibility(View.VISIBLE);
                        dealRequestListAdapter.differ.submitList(queryDocumentSnapshots.toObjects(DealRequest.class));
                    }
                }).addOnFailureListener(
                        e -> {
                            mVB.loadingPb.setVisibility(View.GONE);
                            Log.d(TAG, "onViewCreated: "+e.getMessage());
                            updateNoItemUI("Error getting data!", "Please check your internet connection");
                        }
                );
    }


    private void fetchDealHistory(String collection){
        // fetch all the deals
        // from users/<userId>/<collection>
        // where <collection> is either "deals" or "buy_history"
        // and <userId> is the current user id
        Log.d(TAG, "fetchDealHistory: fetching deal history from collection: "+collection);

        // fetch all the deals
        db.collection(Constants.USER_COLLECTION)
                .document(fUser.getUid())
                .collection(collection)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    Log.d(TAG, "fetchDealHistory: total deals = "+queryDocumentSnapshots.size());
                    if(queryDocumentSnapshots.isEmpty()){
                        updateNoItemUI("No deal history", "You can see all the deals you have made here");
                    }else{
                        // hide no item placeholder
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.dealListRv.setVisibility(View.VISIBLE);
                        // update the recycler view
                        dealHistoryAdapter.differ.submitList(queryDocumentSnapshots.toObjects(Deal.class));
                    }
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "fetchDealHistory: "+e.getMessage());
                    // show no item placeholder
                    mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
                    mVB.dealListRv.setVisibility(View.GONE);
                });
    }

}
