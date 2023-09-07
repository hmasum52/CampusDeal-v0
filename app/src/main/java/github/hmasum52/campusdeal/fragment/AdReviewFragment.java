package github.hmasum52.campusdeal.fragment;


import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.model.DealRequest;
import github.hmasum52.campusdeal.util.Constants;

@AndroidEntryPoint
public class AdReviewFragment extends AdDetailsFragment{

    @Inject
    FirebaseFirestore db;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.initUI();

       // init deal action button
        initDealActionButton();

        // fetch buyer info
        updateBuyerInfoUI();
    }


    private void initDealActionButton() {
        if(isOwner()){
            setDealButtonTextAndColor("Make Deal", R.color.colorPrimary, R.color.white);
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
}
