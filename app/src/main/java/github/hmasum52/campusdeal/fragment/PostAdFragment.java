package github.hmasum52.campusdeal.fragment;

import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.AddProductImageRVAdapter;
import github.hmasum52.campusdeal.databinding.FragmentPostAdBinding;

@AndroidEntryPoint
public class PostAdFragment extends Fragment {

    @Inject
    FirebaseFirestore fStore;

    private FragmentPostAdBinding mVB;

    private  AddProductImageRVAdapter adapter;

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // back button listener
        mVB.backButtonIv.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack();
        });

        // add product image recycler view adapter
        adapter = new AddProductImageRVAdapter(this);
        mVB.imageRv.setAdapter(adapter);
        adapter.setOnImageAddListener(() -> {
            // pick image from gallery
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    // media type image video
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE) // ignore the error
                    .build());
        });
    }
}
