package github.hmasum52.campusdeal.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentViewPagerAdapter extends FragmentStateAdapter {
    // all the category fragments
    // we will fetch categories from firebase
    // will dynamically make the this list
    private ArrayList<Fragment> fragmentList;

    public FragmentViewPagerAdapter(
            ArrayList<Fragment> fragmentList,
            FragmentManager fragmentManager,
            Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragmentList = fragmentList;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
