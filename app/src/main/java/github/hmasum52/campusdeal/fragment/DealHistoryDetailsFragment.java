package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;

import org.parceler.Parcels;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.model.Deal;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.PromptDialog;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

@AndroidEntryPoint
public class DealHistoryDetailsFragment extends AdReviewFragment{
    public static final String TAG = "DealHistoryDetailsFragm";

    @Inject
    FirebaseUser fUser;

    @Inject
    FirebaseStorage storage;

    private Deal deal;

    private PromptDialog deletePromptDialog;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            // Parcels to get Ad object from argument
            deal = Parcels.unwrap(getArguments().getParcelable(Constants.DEAL_KEY));
            super.ad = deal.getAd();
            Log.d(TAG, "onCreate: deal fo ad: "+deal.getAd().getTitle());
            // log ad id
            Log.d(TAG, "onCreate: ad id: "+ad.getId());
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // show prompt dialog
        deletePromptDialog = new PromptDialog(getContext(), R.layout.dialog_cancel_deal_request);

        if(isBuyer()){
            super.mVB.dealerInfoTv.setText("Buyer");
            super.fetchUserInfoAndUpdateUI(deal.getDealInfo().getBuyerId());

        }else{
            super.mVB.dealerInfoTv.setText("Sale By");
            super.fetchUserInfoAndUpdateUI(deal.getDealInfo().getSellerId());
        }


        mVB.dealActionBtn.setText("Delete Deal");
        mVB.dealActionBtn.setBackgroundColor(
                ResourcesCompat.getColor(getResources(), R.color.c_red, null)
        );

        mVB.dealActionBtn.setOnClickListener(v -> {
            String collection = isBuyer() ? Constants.USER_BUY_HISTORY_COLLECTION : Constants.USER_DEALS_COLLECTION;
            deleteDeal(collection);
        });

    }

    private boolean isBuyer(){
        Log.d(TAG, "isBuyer: deal buyer id: "+deal.getDealInfo().getBuyerId());
        Log.d(TAG, "isBuyer: fUser id: "+fUser.getUid());
        return deal.getDealInfo().getBuyerId().equals(fUser.getUid());
    }

    private void deleteDeal(String collection) {
        Log.d(TAG, "deleteDeal: deleting from collection: "+collection);
        deletePromptDialog.showDialog("Do you want to delete this deal history?", R.id.message_tv);
        deletePromptDialog.getYestButton().setOnClickListener(v -> {
            super.db.collection(Constants.USER_COLLECTION)
                    .document(fUser.getUid())
                    .collection(collection)
                    .document(ad.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        deletePromptDialog.hideDialog();
                        Log.d(TAG, "deleteDeal: deal deleted successfully");
                        Snackbar.make(mVB.getRoot(), "Deal deleted successfully", Snackbar.LENGTH_SHORT)
                                .setDuration(1000).show();
                        NavHostFragment.findNavController(this).popBackStack();
                    }).addOnFailureListener(e -> {
                        Log.d(TAG, "deleteDeal: failed to delete deal");
                        deletePromptDialog.hideDialog();
                        Snackbar.make(mVB.getRoot(), "Failed to delete deal", Snackbar.LENGTH_SHORT)
                                .setDuration(1000).show();
                    });

            // delete all the add image from firebase storage
            for (String image : ad.getImageUriList()) {
                storage.getReferenceFromUrl(image).delete().addOnSuccessListener(aVoid -> {
                    Log.w(TAG, "deleteDeal: image deleted: "+image);
                }).addOnFailureListener(e -> {
                    Log.e(TAG, "deleteDeal: failed to delete image: "+image);
                }) ;
            }
        });
    }

}
