package github.hmasum52.campusdeal.viewmodel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class AdPostViewModel extends ViewModel {
    // image uri list
    private final MutableLiveData<List<Uri>> mImageUriList = new MutableLiveData<>();

    // category index
    private final MutableLiveData<Integer> mCategoryIndex = new MutableLiveData<>();


    // should contain exactly one constructor annotated with @Inject
    @Inject
    public AdPostViewModel() {
        // do something with context and savedStateHandle
    }


    // get image uri list live data
    public LiveData<List<Uri>> getImageUriList() {
        return mImageUriList;
    }

    // set image uri list
    public void addToImageUriList(List<Uri> imageUriList) {
        if (mImageUriList.getValue() == null) {
            mImageUriList.setValue(imageUriList);
            return;
        }
        List<Uri> currentImageUriList = mImageUriList.getValue();
        currentImageUriList.addAll(imageUriList);
        mImageUriList.setValue(currentImageUriList);
    }

    // get category index live data
    public LiveData<Integer> getCategoryIndex() {
        return mCategoryIndex;
    }

    // set category index
    public void setCategoryIndex(int categoryIndex) {
        mCategoryIndex.setValue(categoryIndex);
    }
}
