package github.hmasum52.campusdeal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardCategoryListItemBinding;
import github.hmasum52.campusdeal.util.Constants;

public class CategoryListBottomSheetAdapter extends RecyclerView.Adapter<CategoryListBottomSheetAdapter.ViewHolder>{

    private final List<String> categoryList;

    public CategoryListBottomSheetAdapter(List<String> categoryList) {
        this.categoryList = categoryList;
        Log.d("CategoryListBottomSheetAdapter", "CategoryListBottomSheetAdapter: "+categoryList.size());
    }



    @NonNull
    @Override
    public CategoryListBottomSheetAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.card_category_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryListBottomSheetAdapter.ViewHolder holder, int position) {
        holder.itemView.setOnClickListener(v -> {

        });

        holder.mVB.categoryNameTv.setText(categoryList.get(position));
        holder.mVB.categoryIcon.setImageDrawable(
                ResourcesCompat.getDrawable(
                        holder.itemView.getContext().getResources(),
                        Constants.categoryIconList.get(position),
                        null
                )
        );
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    // view holder private
    class ViewHolder extends RecyclerView.ViewHolder{
        CardCategoryListItemBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardCategoryListItemBinding.bind(itemView);
        }
    }
}
