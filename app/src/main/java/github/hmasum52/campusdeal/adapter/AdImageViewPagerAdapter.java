package github.hmasum52.campusdeal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.AdImageViewBinding;

public class AdImageViewPagerAdapter extends RecyclerView.Adapter<AdImageViewPagerAdapter.ViewHolder>{
    public static final String TAG = "AdImageViewPagerAdapter";

    private final List<String> imageUriList;

    public AdImageViewPagerAdapter(List<String> imageUriList) {
        this.imageUriList = imageUriList;
    }

    private final DiffUtil.ItemCallback<String> diffCallback = new DiffUtil.ItemCallback<String>() {
        @Override
        public boolean areItemsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areContentsTheSame(@NonNull String oldItem, @NonNull String newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<String> differ = new AsyncListDiffer<>(this, diffCallback);

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.ad_image_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: "+ differ.getCurrentList().size());
        Log.d(TAG, "onBindViewHolder: " + position);
        String imageUrl = differ.getCurrentList().get(position);
        Log.d(TAG, "onBindViewHolder: " + imageUrl);
        Log.d(TAG, "onBindViewHolder: " + imageUriList.get(position));

        // render image with glide
        Glide.with(holder.mVB.getRoot())
                .load(imageUrl)
                .into(holder.mVB.adImageView);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        AdImageViewBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = AdImageViewBinding.bind(itemView);
        }
    }
}
