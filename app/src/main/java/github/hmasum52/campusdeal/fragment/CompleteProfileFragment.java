package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import github.hmasum52.campusdeal.databinding.FragmentCompleteProfileBinding;

public class CompleteProfileFragment extends Fragment {
    private FragmentCompleteProfileBinding mVB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentCompleteProfileBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }
}
