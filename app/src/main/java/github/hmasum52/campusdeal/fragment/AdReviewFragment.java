package github.hmasum52.campusdeal.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.model.DealRequest;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.PromptDialog;

@AndroidEntryPoint
public class AdReviewFragment extends AdDetailsFragment{

    @Inject
    FirebaseFirestore db;

    private static final String TAG = "AdReviewFragment";

    private PromptDialog makeDealDialog;
    private PromptDialog loadingDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.initUI();

       // init deal action button
        initDealActionButton();

        // fetch buyer info
        updateBuyerInfoUI();

        initDialogs();
    }

    // init make deal dialog
    private void initDialogs() {
        loadingDialog = new PromptDialog(getContext(), R.layout.dialog_loading);

        makeDealDialog = new PromptDialog(getContext(), R.layout.dialog_cancel_deal_request);
        Button yesBtn = makeDealDialog.findViewById(R.id.yes_btn);
        Button noBtn = makeDealDialog.findViewById(R.id.no_btn);
        // set yestBtn color to primary color
        yesBtn.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.colorPrimary, null));
        yesBtn.setTextColor(ResourcesCompat.getColor(getResources(), R.color.white, null));
        yesBtn.setText("Accept");
        yesBtn.setOnClickListener(v -> {
                    makeDealDialog.hideDialog();
                    loadingDialog.showDialog("Making deal...", R.id.loading_msg_tv);
                    makeDeal();
                });

        // make no button to reject button
        noBtn.setText("Reject");
        noBtn.setOnClickListener(v -> {
            makeDealDialog.hideDialog();
            loadingDialog.showDialog("Rejecting deal...", R.id.loading_msg_tv);
            rejectDeal();
        });

    }


    private void initDealActionButton() {
        if(isOwner()){
            setDealButtonTextAndColor("Proceed", R.color.black, R.color.white);
            mVB.dealActionBtn.setOnClickListener(v -> {
                initDialogs();
                makeDealDialog.showDialog("Are you sure you want to proceed?", R.id.message_tv);
            });
        }
    }

    private void updateBuyerInfoUI() {
        mVB.dealerInfoTv.setText("Buyer Info");
        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(ad.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if(documentSnapshot.exists()){
                        DealRequest dealRequest = documentSnapshot.toObject(DealRequest.class);
                        if(dealRequest==null){
                            Log.d(TAG, "updateRequesterUI: dealRequest is null");
                            return;
                        }
                        // log requester name
                        Log.d(TAG, "updateRequesterUI: requester name: "+dealRequest.getBuyerName());
                        super.fetchUserInfoAndUpdateUI(dealRequest.getBuyerId());
                    }else{
                        Log.d(TAG, "updateRequesterUI: document not found");
                    }
                });
    }

    // make deal function delete the deal request
    // move the ad info to user's deal collection
    // delete the ad from ads collection
    //
    private void makeDeal(){
        // make deal info which includes
        // buyerId, buyerName, sellerId, sellerName, adId, adTitle, adPrice
        // deal datetime
        // ad object as a whole

        loadingDialog.showDialog();

        // delete deal request
        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(ad.getId())
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    loadingDialog.hideDialog();
                    if(documentSnapshot.exists()){
                        DealRequest dealRequest = documentSnapshot.toObject(DealRequest.class);
                        if(dealRequest==null){
                            Log.d(TAG, "makeDeal: dealRequest is null");
                            return;
                        }
                        // log requester name
                        Log.d(TAG, "makeDeal: requester name: "+dealRequest.getBuyerName());
                        // delete deal request
                        deleteDealRequest();
                        // delete ad
                        deleteAd();

                        Map<String, Object> dealInfo = new HashMap<>();
                        dealInfo.put("dealInfo", dealRequest);
                        dealInfo.put("date", new Date());
                        dealInfo.put("ad", ad);

                        // save deal info to user's deal collection
                        db.collection(Constants.USER_COLLECTION)
                                .document(dealRequest.getSellerId())
                                .collection(Constants.USER_DEALS_COLLECTION)
                                .document(ad.getId())
                                .set(dealInfo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "makeDeal: deal info saved to buyer's deal collection");
                                    NavHostFragment.findNavController(this)
                                            .navigate(R.id.action_adReviewFragment_to_dealDoneFragment);
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "makeDeal: failed to save deal info to buyer's deal collection");
                                    // show snakbar
                                    Snackbar.make(mVB.getRoot(), "Failed to make deal", Snackbar.LENGTH_LONG)
                                            .setDuration(1000).show();
                                });
                        // save deal info to buyer's buy history collection
                        db.collection(Constants.USER_COLLECTION)
                                .document(dealRequest.getBuyerId())
                                .collection(Constants.USER_BUY_HISTORY_COLLECTION)
                                .document(ad.getId())
                                .set(dealInfo)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d(TAG, "makeDeal: deal info saved to buyer's buy history collection");
                                })
                                .addOnFailureListener(e -> {
                                    Log.d(TAG, "makeDeal: failed to save deal info to buyer's buy history collection");
                                });

                    }else{
                        Log.d(TAG, "makeDeal: document not found");
                        // show snakbar
                        Snackbar.make(mVB.getRoot(), "Failed to make deal", Snackbar.LENGTH_LONG)
                                .setDuration(1000).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "makeDeal: failed to fetch deal request");
                    // show snakbar
                    Snackbar.make(mVB.getRoot(), "Failed to make deal", Snackbar.LENGTH_LONG)
                            .setDuration(1000).show();
                    loadingDialog.hideDialog();
                });
    }

    private void deleteDealRequest(){
        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(ad.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "makeDeal: deal request deleted");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "makeDeal: failed to delete deal request");
                });
    }

    private void deleteAd(){
        db.collection(Constants.ADS_COLLECTION)
                .document(ad.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "makeDeal: deal request deleted");
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "makeDeal: failed to delete deal request");
                });
    }

    private void rejectDeal() {
        // delete deal request
        db.collection(Constants.DEAL_REQUEST_COLLECTION)
                .document(ad.getId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "makeDeal: deal request deleted");
                    // go back to home fragment
                    loadingDialog.hideDialog();
                    // show snakbar
                    Snackbar.make(mVB.getRoot(), "Deal Rejected!", Snackbar.LENGTH_LONG)
                            .setDuration(1000).show();
                    NavHostFragment.findNavController(this).popBackStack();
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "makeDeal: failed to delete deal request");
                    loadingDialog.hideDialog();
                    // show snakbar
                    Snackbar.make(mVB.getRoot(), "Failed to reject deal", Snackbar.LENGTH_LONG)
                            .setDuration(1000).show();
                });
    }
}
