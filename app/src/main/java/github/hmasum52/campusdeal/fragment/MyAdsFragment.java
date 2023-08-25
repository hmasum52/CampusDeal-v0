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

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.SearchResultAdapter;
import github.hmasum52.campusdeal.databinding.FragmentMyAdsBinding;
import github.hmasum52.campusdeal.model.Ad;

@AndroidEntryPoint
public class MyAdsFragment extends Fragment{
    public static final String TAG = "MyAdFragment";

    // view binding
    private FragmentMyAdsBinding mVB;

    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth fAuth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentMyAdsBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
        SearchResultAdapter adapter = new SearchResultAdapter();
        mVB.adListRv.setAdapter(adapter);

        adapter.setRecyclerItemClickListener((ad)->{
            Bundle bundle = new Bundle();
            bundle.putParcelable("ad", Parcels.wrap(ad));
            NavHostFragment.findNavController(this).navigate(R.id.action_myAdsFragment_to_adDetailsFragment, bundle);
        });


        mVB.backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        mVB.loadingCpb.setVisibility(View.VISIBLE);

        // fetch all the ads of the userd
        db.collection("ads")
                .whereEqualTo("sellerId", fAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    mVB.loadingCpb.setVisibility(View.GONE);
                    if(queryDocumentSnapshots.isEmpty()){
                        updateNoItemUI();
                    }else{
                        List<Ad> adList = queryDocumentSnapshots.toObjects(Ad.class);
                        Log.d(TAG, "updateUI: "+adList.size());
                        mVB.noItemPlaceholder.getRoot().setVisibility(View.GONE);
                        mVB.adListRv.setVisibility(View.VISIBLE);
                        adapter.differ.submitList(adList);
                    }
                }).addOnFailureListener(
                e -> {
                    mVB.loadingCpb.setVisibility(View.GONE);
                    updateNoItemUI();
                    }
                );
    }

    private void updateNoItemUI() {
        mVB.noItemPlaceholder.getRoot().setVisibility(View.VISIBLE);
        mVB.adListRv.setVisibility(View.GONE);

        String msg = "You have not posted any ad yet";
        mVB.noItemPlaceholder.messageTv.setText(msg);
        String tips = "Get start by posting your first ad.";
        mVB.noItemPlaceholder.tipsTv.setText(tips);
    }

}
