package github.hmasum52.campusdeal.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import github.hmasum52.campusdeal.adapter.RecyclerItemClickListener;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.StateLiveData;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.Util;

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
        if(!topUrgentAdListMap.containsKey(category)){
            topUrgentAdListMap.put(category, new StateLiveData<>());
        }
        String userId = FirebaseAuth.getInstance().getUid();
        // this query need indexing
        Query query =  db.collection("ads")
                .where(
                        Filter.and(
                                Filter.equalTo("category", category),
                                Filter.notEqualTo("sellerId", userId),
                                Filter.equalTo("urgent", true)
                        )
                )
                .orderBy("sellerId", Query.Direction.DESCENDING)
                .orderBy("uploadDate", Query.Direction.DESCENDING);

        if(limit>0)
            query.limit(limit);

        query.get().addOnCompleteListener(task -> {
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
        }
        fetchUrgentAdList(category, limit);
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
        Log.d(TAG, "fetchAds: fetching all ad list for category: "+category);
        String userId = FirebaseAuth.getInstance().getUid();
        // this query need indexing
        Query query = db.collection("ads")
                .where(
                        Filter.and(
                                Filter.equalTo("category", category),
                                Filter.notEqualTo("sellerId", userId)
                        )
                )
                .orderBy("sellerId", Query.Direction.DESCENDING)
                .orderBy("uploadDate", Query.Direction.DESCENDING);

        if(limit > 0){
            query = query.limit(limit);
        }

        query.get().addOnCompleteListener(task -> {
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
        }
        fetchAds(category, -1L);
        return allAdsMap.get(category);
    }


    /// ---------------------------------------------------------------------------------
    // ------------------ fetch near a latitude and longitude ----------------------------
    // ---------------------------------------------------------------------------------
    Map<String, StateLiveData<List<Ad>>> nearAdListMap = new HashMap<>();

    public StateLiveData<List<Ad>> fetchNearAds(String category, LatLng latLng, double radius, int limit) {
        StateLiveData<List<Ad>> nearAdListSLD = new StateLiveData<>();

        String userId = FirebaseAuth.getInstance().getUid();

        Log.d(TAG, "fetchNearAds: fetching near ad list for category: " + category);
        Log.d(TAG, "fetchNearAds: lat = " + latLng.latitude + " long = " + latLng.longitude + " radius = " + radius);

        db.collection("ads")
                .where(
                        Filter.and(
                                Filter.equalTo("category", category),
                                Filter.notEqualTo("sellerId", userId)
                        )
                )
                .orderBy("sellerId", Query.Direction.DESCENDING)
                .orderBy("uploadDate", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<Ad> tempList = task.getResult().toObjects(Ad.class);
                        List<Ad> nearAdListData = new ArrayList<>();
                        // sort tempList
                        tempList.sort((o1, o2) -> {
                            double distance1 = Util.distance(latLng, o1.locationLatLng());
                            double distance2 = Util.distance(latLng, o2.locationLatLng());
                            return Double.compare(distance1, distance2);
                        });
                        // add only those ad which are in radius
                        for (Ad ad : tempList) {
                            double distance = Util.distance(latLng, ad.locationLatLng());
                            if (distance <= radius) {
                                nearAdListData.add(ad);
                            }
                        }

                        // limit
                        if(limit != -1){
                            nearAdListData = nearAdListData.subList(
                                    0,
                                    Math.min(Math.max(-1, limit), nearAdListData.size()));
                        }

                        nearAdListSLD.postSuccess(nearAdListData);
                        Log.d(TAG, "fetchNewAdList: updated new ad list for category: "+category+"total new ads = "+nearAdListData.size());

                    }else{
                        Log.d(TAG, "fetchNewAdList: failed to fetch new ad list for category: "+category);
                        nearAdListSLD.postError(task.getException());
                    }
                }).addOnFailureListener(e -> {
                    Log.d(TAG, "fetchNewAdList: failed to fetch new ad list for category: "+category);
                    nearAdListSLD.postError(e);
                });

        return nearAdListSLD;
    }


    // get near ad list by category
    // ordered
    public StateLiveData<List<Ad>> getNearAdList(String category, LatLng latLng, double radius, int limit){
        // check if the list is already fetched
        if(nearAdListMap.get(category) == null){
            nearAdListMap.put(category, new StateLiveData<>());
        }
        nearAdListMap.put(category, fetchNearAds(category, latLng, radius, limit));
        return nearAdListMap.get(category);
    }

}
