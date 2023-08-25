package github.hmasum52.campusdeal.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import github.hmasum52.campusdeal.adapter.FragmentViewPagerAdapter;
import github.hmasum52.campusdeal.databinding.FragmentDealRequestBinding;
import github.hmasum52.campusdeal.util.Constants;

@AndroidEntryPoint
public class DealRequestFragment extends Fragment {
    @Inject
    FirebaseFirestore db;

    @Inject
    FirebaseAuth auth;

    private FragmentDealRequestBinding mVB;

    private List<String> tabMenuList = new ArrayList<String>() {
        {
            add("Request for you");
            add("Your request");
        }
    };


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mVB = FragmentDealRequestBinding.inflate(inflater, container, false);
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new ActiveDealsFragment());
        fragments.add(new ActiveDealsFragment());

        mVB.pager.setAdapter(new FragmentViewPagerAdapter(
                fragments,
                getChildFragmentManager(),
                getLifecycle()
        ));

        // tab layout mediator to connect viewpager2 with tablayout
        new TabLayoutMediator(mVB.tabLayout, mVB.pager, ((TabLayout.Tab tab, int position) -> {
            tab.setText(tabMenuList.get(position));
        })).attach();
    }
}