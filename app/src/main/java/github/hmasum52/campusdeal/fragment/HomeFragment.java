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

import java.util.ArrayList;

import github.hmasum52.campusdeal.adapter.CategoryViewPagerAdapter;
import github.hmasum52.campusdeal.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {

    // view binding
    private FragmentHomeBinding mVB;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // no args
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentHomeBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // https://androidwave.com/viewpager2-with-tablayout-android-example/
        // https://developer.android.com/guide/navigation/advanced/swipe-view-2
        // https://www.geeksforgeeks.org/how-to-implement-dynamic-tablayout-in-android/


        // categories
        // Books, Stationary, Electronics, Accessories, Clothes,
        // Tutoring, Musical Instruments, Sports
        ArrayList<String> categoryList = new ArrayList<String>() {{
            add("Books");
            add("Stationary");
            add("Electronics");
            add("Accessories");
            add("Clothes");
            add("Sports");
            add("Tutoring");
            add("Musical Instruments");
        }};


        ArrayList<Fragment> fragments = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            fragments.add(new CategoryFragment(categoryList.get(i)));
        }

        mVB.pager.setOffscreenPageLimit(2);

        mVB.pager.setAdapter(new CategoryViewPagerAdapter(fragments,
                getChildFragmentManager(),
                getLifecycle()));

        // tab layout mediator to connect viewpager2 with tablayout
        new TabLayoutMediator(mVB.tabLayout, mVB.pager, ((TabLayout.Tab tab, int position) -> {
            tab.setText(categoryList.get(position));
        })).attach();
    }
}