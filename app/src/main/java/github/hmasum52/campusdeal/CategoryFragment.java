package github.hmasum52.campusdeal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.hmasum52.campusdeal.databinding.FragmentCategoryBinding;


public class CategoryFragment extends Fragment {

    // view binding
    private FragmentCategoryBinding mVB;
    private String name;

    public CategoryFragment(String name) {
        // Required empty public constructor
        this.name = name;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           // get position

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mVB = FragmentCategoryBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return mVB.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mVB.categoryTest.setText(String.valueOf("Category :  "+name));
    }
}