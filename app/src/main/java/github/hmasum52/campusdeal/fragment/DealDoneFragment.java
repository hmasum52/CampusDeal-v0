package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.FragmentDealDoneBinding;

public class DealDoneFragment extends Fragment {
    private FragmentDealDoneBinding mVB;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentDealDoneBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // on back button pressed
        // go back to home fragment
        mVB.backBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack(R.id.homeFragment, false);
        });

        mVB.dealDoneBtn.setOnClickListener(v -> {
            NavHostFragment.findNavController(this).popBackStack(R.id.homeFragment, false);
        });
    }
}
