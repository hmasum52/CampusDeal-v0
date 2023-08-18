package github.hmasum52.campusdeal.fragment;

import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.adapter.PostAdImageRVAdapter;
import github.hmasum52.campusdeal.adapter.CategoryListBottomSheetAdapter;
import github.hmasum52.campusdeal.databinding.BottomDialogCategoryListBinding;
import github.hmasum52.campusdeal.databinding.FragmentPostAdBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.AdLocation;
import github.hmasum52.campusdeal.util.Constants;
import github.hmasum52.campusdeal.util.LoadingDialogBar;
import github.hmasum52.campusdeal.util.LocationFinder;

@AndroidEntryPoint
public class PostAdFragment extends Fragment {

    @Inject
    FirebaseAuth fAuth;

    @Inject
    FirebaseFirestore fStore;
    
    @Inject
    FirebaseStorage fStorage;

    private FragmentPostAdBinding mVB;

    private PostAdImageRVAdapter adapter;

    private LoadingDialogBar uploadDialog;

    @Inject
    Geocoder geocoder;

    @Inject
    LocationFinder locationFinder;

    private AdLocation adLocation;


    // pick image from gallery // https://developer.android.com/training/data-storage/shared/photopicker
    ////                // Registers a photo picker activity launcher in multi-select mode.
    ////                //  the app lets the user select up to 5 media files.
    ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                // Callback is invoked after the user selects media items or closes the
                // photo picker.
                if (!uris.isEmpty()) {
                    Log.d("PhotoPicker", "Number of items selected: " + uris.size());
                    // if uris size is greater than 5 then show error
                    if(uris.size()>5){
                        Toast.makeText(getContext(), "You can select up to 5 images", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    for (Uri uri : uris) {
                        Log.d("PhotoPicker", "Uri: " + uri.toString());
                        if(adapter!=null){
                            Log.d("PhotoPicker", "adding uri to recycler view");
                            adapter.addImage(uri);
                        }
                    }
                } else {
                    Log.d("PhotoPicker", "No media selected");
                }
            });


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentPostAdBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    void updateLocation(@NonNull AdLocation location){
        this.adLocation = location;
        mVB.selectedLocationTv.setText(location.getFullAddress());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // use device location
        locationFinder.requestDeviceLocation(latLng -> {
            AdLocation location =  locationFinder.getAdLocation(latLng);
            if(location != null){
                mVB.usingDeviceLocationTv.setText(getContext().getString(R.string.using_device_location_tap_to_change));
                updateLocation(location);
            }
        });


        // get NavController
        // https://www.youtube.com/watch?v=WBbsvqSu0is // parcelable
        // https://code.tutsplus.com/how-to-pass-data-between-activities-with-android-parcelable--cms-29559t
        // https://www.youtube.com/watch?v=sNeRRewIF2s
        // https://stackoverflow.com/questions/51326437/navigation-component-popbackstack-with-arguments
        // https://github.com/johncarl81/parceler
        NavController navController = NavHostFragment.findNavController(this);
        if(navController.getCurrentBackStackEntry() != null){
            navController.getCurrentBackStackEntry()
                    .getSavedStateHandle()
                    .getLiveData("location")
                    .observe(getViewLifecycleOwner(), parcelable -> {
                        mVB.usingDeviceLocationTv.setText(getContext().getString(R.string.tap_to_change));
                        AdLocation adLocation =  Parcels.unwrap((Parcelable) parcelable);
                        Log.d("PostAdFragment", "onViewCreated: "+adLocation.toString());
                        updateLocation(adLocation);
                    });
        }



        // back button listener
        mVB.backButtonIv.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // add ad image recycler view adapter
        initAdImageSelectRecyclerView();

        // https://www.section.io/engineering-education/bottom-sheet-dialogs-using-android-studio/
        // open bottom dialog on category card click
        mVB.selectCategoryCard.setOnClickListener(v -> {
            CategoryListBottomSheetFragment bottomSheetFragment = new CategoryListBottomSheetFragment();
            bottomSheetFragment.setOnItemClickListener(position -> {
                // set category name
                mVB.selectedCategoryTv.setText(Constants.categoryList.get(position));
                // set category icon
                mVB.selectedCategoryIcon.setImageDrawable(
                        ResourcesCompat.getDrawable(
                                getResources(),
                                Constants.categoryIconList.get(position),
                                null
                        )
                );
                // dismiss the bottom sheet
                bottomSheetFragment.dismiss();
            });
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });

        // location input button click listener
        mVB.selectLocationCard.setOnClickListener(v -> {
            // go to google map fragment
            NavHostFragment.findNavController(this).navigate(R.id.action_addProductFragment_to_mapLocationInputFragment);
        });
        
        // post ad button click listener
        mVB.adPostBtn.setOnClickListener(v -> {
            uploadAdToFirebase();
        });
    }

    private void uploadAdToFirebase() {

        // validate the input
        boolean ok = validateInput();
        if(!ok){
            return;
        }
        uploadDialog = new LoadingDialogBar(getContext(), R.layout.loading_dialog);
        uploadDialog.showDialog("Posting Ad", R.id.loading_msg_tv);
        // 1st upload all the images to firebase storage
        // upload image then call uploadAdToFireStore method
        uploadImages(new ArrayList<>());
    }

    private boolean validateInput() {
        if(adapter.getImageUriList().isEmpty()){
            Toast.makeText(getContext(), "Please select at least one image", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check if the title is empty
        if(mVB.titleEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a title", Toast.LENGTH_SHORT).show();
            // show error in title edit text
            mVB.titleEt.setError("Please enter a title");
            return false;
        }

        // check if the description is empty
        if(mVB.descriptionEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a description", Toast.LENGTH_SHORT).show();
            // show error in description edit text
            mVB.descriptionEt.setError("Please enter a description");
            return false;
        }

        // check if the price is empty
        if(mVB.priceEt.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Please enter a price", Toast.LENGTH_SHORT).show();
            // show error in price edit text
            mVB.priceEt.setError("Please enter a price");
            return false;
        }

        // check if is equal R.string.select_category
        if(mVB.selectedCategoryTv.getText().toString().equals(getString(R.string.select_category))){
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return false;
        }

        // check if location is selected
        if(adLocation == null){
            Toast.makeText(getContext(), "Please select a location", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    // https://stackoverflow.com/questions/46272309/upload-multiple-images-to-firebase-storage
    // upload images recursively
    private void uploadImages(@NonNull List<String> imageDownloadUrls){
        // upload all the images to firebase storage and get the download url in a arraylist
        // upload the image in ad/<category_name> directory
        // https://firebase.google.com/docs/storage/android/upload-files
        StorageReference storageRef = fStorage.getReference("images/ad/"+mVB.selectedCategoryTv.getText().toString());
        Uri uri = adapter.getImageUriList().get(imageDownloadUrls.size());

        // create a new file name
        String fileName = UUID.randomUUID().toString();

        // create a new file reference
        StorageReference fileRef = storageRef.child(fileName);

        // upload the file

        fileRef.putFile(uri)
                .addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
                    // get the download url
                    fileRef.getDownloadUrl().addOnSuccessListener((Uri downloadUri) -> {
                        // add the download url to the list
                        Log.d("PostAdFragment", "uploadImagesToFirebaseStorage: "+downloadUri.toString());
                        imageDownloadUrls.add(downloadUri.toString());

                        // //if same size so all image is uploaded, then sent list of url to to some method to upload the ad
                        if(imageDownloadUrls.size() == adapter.getImageUriList().size()){
                            // upload the ad to firebase firestore
                            uploadAdToFireStore(imageDownloadUrls);
                        }else{
                            // upload the next image
                            uploadImages(imageDownloadUrls);
                        }
                    });
                })
                .addOnFailureListener(e -> {
                    Log.d("PostAdFragment", "uploadImagesToFirebaseStorage: "+e.getMessage());
                    Toast.makeText(getContext(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                    uploadDialog.hideDialog();
                });
    }

    private void uploadAdToFireStore(List<String> imageDownloadUrls) {

        // make an Ad object
        String id = UUID.randomUUID().toString();
        String title = mVB.titleEt.getText().toString();
        String description = mVB.descriptionEt.getText().toString();
        String category = mVB.selectedCategoryTv.getText().toString();
        double price = Double.parseDouble(mVB.priceEt.getText().toString());
        boolean negotiable = mVB.negotiableSwitch.isChecked();
        String sellerId = fAuth.getUid();

        Ad ad = new Ad(id, title, description, category, price, negotiable, sellerId, new Date(), imageDownloadUrls, adLocation);

        // upload the ad to firebase firestore
        fStore.collection("ads")
                .document(id)
                .set(ad)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Ad posted successfully", Toast.LENGTH_SHORT).show();
                    // clear the input fields
                    mVB.titleEt.setText("");
                    mVB.descriptionEt.setText("");
                    mVB.priceEt.setText("");
                    mVB.negotiableSwitch.setChecked(false);
                    mVB.selectedCategoryTv.setText(getString(R.string.select_category));
                    mVB.selectedCategoryIcon.setImageDrawable(
                            ResourcesCompat.getDrawable(
                                    getResources(),
                                    R.drawable.ic_widgets_24,
                                    null
                            )
                    );
                    adapter.getImageUriList().clear();
                    adapter.notifyDataSetChanged();
                    // go to home fragment
                    NavHostFragment.findNavController(this).popBackStack();
                    uploadDialog.hideDialog();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Failed to post ad", Toast.LENGTH_SHORT).show();
                    Log.d("PostAdFragment", "uploadAdToFirebase: "+e.getMessage());
                    uploadDialog.hideDialog();
                });
    }

    void initAdImageSelectRecyclerView(){
        adapter = new PostAdImageRVAdapter(this);
        mVB.imageRv.setAdapter(adapter);
        adapter.setOnImageAddListener(() -> {
            // pick image from gallery
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    // media type image video
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE) // ignore the error
                    .build());
        });
    }

    // https://www.youtube.com/watch?v=bXzNIUKYHF0
    public static class CategoryListBottomSheetFragment extends BottomSheetDialogFragment {
        // view binding
        private BottomDialogCategoryListBinding mVB;

        private CategoryListBottomSheetAdapter.OnItemClickListener listener;

        public void setOnItemClickListener(CategoryListBottomSheetAdapter.OnItemClickListener listener){
            this.listener = listener;
        }

        // on create view0

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            mVB = BottomDialogCategoryListBinding.inflate(inflater, container, false);
            return mVB.getRoot();
        }

        // on view created
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // set up recycler view
            // adapter
            CategoryListBottomSheetAdapter adapter = new CategoryListBottomSheetAdapter(Constants.categoryList);
            adapter.setOnItemClickListener(listener);
            mVB.categoryListRv.setAdapter(adapter);
        }
    }
}
