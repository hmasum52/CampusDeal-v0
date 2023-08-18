package github.hmasum52.campusdeal.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import github.hmasum52.campusdeal.model.Ad;

@HiltViewModel
public class AdViewModel extends ViewModel {

    Map<String, MutableLiveData<List<Ad>>>   urgentAdListMap = new HashMap<>();


    FirebaseFirestore db;

    @Inject
    public AdViewModel(FirebaseFirestore db) {
        this.db = db;
    }

    // fetch urgent ad list from firebase fire store ads collection
    public void fetchUrgentAdList(String category){
        urgentAdListMap.put(category, new MutableLiveData<>());
        db.collection("ads")
                .whereEqualTo("category", category)
                .whereEqualTo("urgent", true)
                .get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                List<Ad> urgentAdList = task.getResult().toObjects(Ad.class);
                if(urgentAdListMap.get(category) != null)
                    Objects.requireNonNull(urgentAdListMap.get(category)).setValue(urgentAdList);
            }
        });
    }

    public MutableLiveData<List<Ad>> getUrgentAdList(String category){
        // check if the list is already fetched
        if(urgentAdListMap.get(category) == null){
            fetchUrgentAdList(category);
        }

        return urgentAdListMap.get(category);
    }


}
