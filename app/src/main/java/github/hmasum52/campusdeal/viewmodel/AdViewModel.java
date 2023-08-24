package github.hmasum52.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import github.hmasum52.campusdeal.adapter.RecyclerItemClickListener;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.StateLiveData;

@HiltViewModel
public class AdViewModel extends ViewModel {
    public static final String TAG = "AdViewModel";

    public RecyclerItemClickListener<Ad> onAdClickListener;

    // https://www.youtube.com/watch?v=suC0OM5gGAA
    Map<String, StateLiveData<List<Ad>>> topUrgentAdListMap = new HashMap<>(); // category -> list of ads
    Map<String, StateLiveData<List<Ad>>> allUrgentAdListMap = new HashMap<>();
    Map<String, StateLiveData<List<Ad>>> allAdsMap = new HashMap<>();

    FirebaseFirestore db;

    @Inject
    public AdViewModel(FirebaseFirestore db) {
        this.db = db;
    }

    // --------------------------------------------------------------
    // ------------------ fetch Urgent Ads --------------------------
    // --------------------------------------------------------------

    // fetch urgent ad list from firebase fire store ads collection
    public void fetchUrgentAdList(String category, long limit){
        Log.d(TAG, "fetchUrgentAdList: fetching urgent ad list for category: "+category);
        // this query need indexing
        db.collection("ads")
                .whereEqualTo("category", category)
                .whereEqualTo("urgent", true)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .limit(5)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<Ad> urgentAdList = task.getResult().toObjects(Ad.class);
                if(topUrgentAdListMap.get(category) == null){
                    topUrgentAdListMap.put(category, new StateLiveData<>());
                }
                Objects.requireNonNull(topUrgentAdListMap.get(category)).postSuccess(urgentAdList);
                Log.d(TAG, "fetchUrgentAdList: updated urgent ad list for category: "+category+"total urgent ads = "+urgentAdList.size());
            }else{
                Log.d(TAG, "fetchUrgentAdList: failed to fetch urgent ad list for category: "+category);
                Objects.requireNonNull(topUrgentAdListMap.get(category)).postError(task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "fetchUrgentAdList: failed to fetch urgent ad list for category: "+category);
            Objects.requireNonNull(topUrgentAdListMap.get(category)).postError(e);
        });
    }

    public void fetchAllUrgentAdList(String category){
        fetchUrgentAdList(category, -1L); // -1 means no limit in firebase fire store
    }

    public StateLiveData<List<Ad>> getTopUrgentAdList(String category, long limit){
        // check if the list is already fetched
        if(topUrgentAdListMap.get(category) == null){
            topUrgentAdListMap.put(category, new StateLiveData<>());
            topUrgentAdListMap.get(category).postLoading();
            fetchUrgentAdList(category, limit);
        }
        return topUrgentAdListMap.get(category);
    }

    // get all urgent ad list by category
    // ordered by date
    public StateLiveData<List<Ad>> getAllUrgentAdList(String category){
        // check if the list is already fetched
        if(allUrgentAdListMap.get(category) == null){
            allUrgentAdListMap.put(category, new StateLiveData<>());
            allUrgentAdListMap.get(category).postLoading();
            fetchAllUrgentAdList(category);
        }
        return allUrgentAdListMap.get(category);
    }

    // --------------------------------------------------------------
    // ------------------ fetch New Ads -----------------------------
    // --------------------------------------------------------------

    public void fetchAds(String category, long limit){
        Log.d(TAG, "fetchNewAdList: fetching new ad list for category: "+category);
        // this query need indexing
        db.collection("ads")
                .whereEqualTo("category", category)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .limit(5)
                .get().addOnCompleteListener(task -> {
            if(allAdsMap.get(category) == null){
                allAdsMap.put(category, new StateLiveData<>());
            }
            if(task.isSuccessful()){
                List<Ad> newAdList = task.getResult().toObjects(Ad.class);
                Objects.requireNonNull(allAdsMap.get(category)).postSuccess(newAdList);
                Log.d(TAG, "fetchNewAdList: updated new ad list for category: "+category+"total new ads = "+newAdList.size());
            }else{
                Log.d(TAG, "fetchNewAdList: failed to fetch new ad list for category: "+category);
                Objects.requireNonNull(allAdsMap.get(category)).postError(task.getException());
            }
        }).addOnFailureListener(e -> {
            Log.d(TAG, "fetchNewAdList: failed to fetch new ad list for category: "+category);
            Objects.requireNonNull(allAdsMap.get(category)).postError(e);
        });
    }


    // get all data by category ordered by date
    // add pagination
    public StateLiveData<List<Ad>> getAllAds(String category){
        // check if the list is already fetched
        if(allAdsMap.get(category) == null){
            allAdsMap.put(category, new StateLiveData<>());
            allAdsMap.get(category).postLoading();
            fetchAds(category, -1L);
        }
        return allAdsMap.get(category);
    }

}
