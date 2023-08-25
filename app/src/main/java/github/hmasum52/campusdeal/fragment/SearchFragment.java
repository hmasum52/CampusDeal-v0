package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Filter;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.SearchResultAdapter;
import github.hmasum52.campusdeal.databinding.FragmentSearchBinding;
import github.hmasum52.campusdeal.model.Ad;

// https://stackoverflow.com/questions/38618953/how-to-do-a-simple-search-in-string-in-firebase-database
// https://stackoverflow.com/questions/46568142/google-firestore-query-on-substring-of-a-property-value-text-search
@AndroidEntryPoint
public class SearchFragment extends Fragment {

    private static final String TAG = "SearchFragment";

    private FragmentSearchBinding mVB;

    @Inject
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mVB = FragmentSearchBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SearchResultAdapter searchResultAdapter = new SearchResultAdapter();
        mVB.searchResultRv.setAdapter(searchResultAdapter);


        mVB.searchTextEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Log.d(TAG, "beforeTextChanged: "+s.toString());
                mVB.searchResultRv.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               // Log.d(TAG, "onTextChanged: "+s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                //Log.d(TAG, "afterTextChanged: "+s.toString());
                // search in firebase by this string
                String queryString = s.toString();

                if(queryString.isEmpty()){
                    mVB.searchResultRv.setVisibility(View.GONE);
                    searchResultAdapter.differ.submitList(Collections.emptyList());
                    return;
                }

                Log.d(TAG, "afterTextChanged: "+queryString);

                // https://stackoverflow.com/a/62868153/13877490
                //https://stackoverflow.com/a/75877483/13877490
                db.collection("ads")
                        .orderBy("title")
                        .where(Filter.or(
                                // query as it is
                                Filter.and(
                                        Filter.greaterThanOrEqualTo("title", queryString),
                                        Filter.lessThanOrEqualTo("title", queryString + "\uf8ff")
                                ),
                                // capitalize first letter
                                Filter.and(
                                        Filter.greaterThanOrEqualTo("title", queryString.substring(0,1).toUpperCase() + queryString.substring(1)),
                                        Filter.lessThanOrEqualTo("title", queryString.substring(0,1).toUpperCase() + queryString.substring(1) + "\uf8ff")
                                ),
                                // lowercase
                                Filter.and(
                                        Filter.greaterThanOrEqualTo("title", queryString.toLowerCase()),
                                        Filter.lessThanOrEqualTo("title", queryString.toLowerCase() + "\uf8ff")
                                )

                         )
                        )
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                mVB.searchResultRv.setVisibility(View.VISIBLE);
                                // get ads
                                Log.d(TAG, "onSuccess: "+queryDocumentSnapshots.size());
                                // make adList
                                List<Ad> adList = queryDocumentSnapshots.toObjects(Ad.class);
                                searchResultAdapter.differ.submitList(adList);
                            }
                        }).addOnFailureListener(e -> {
                            Log.d(TAG, "onFailure: "+e.getMessage());
                            mVB.searchResultRv.setVisibility(View.VISIBLE);
                        });

            }
        });
    }
}