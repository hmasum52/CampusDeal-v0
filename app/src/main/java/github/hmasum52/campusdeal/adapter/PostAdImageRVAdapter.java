package github.hmasum52.campusdeal.adapter;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardPostAdImageBinding;

// recycler view adapter for add product image
public class PostAdImageRVAdapter extends RecyclerView.Adapter<PostAdImageRVAdapter.ViewHolder>{
    private final List<Uri> imageUriList;

    public PostAdImageRVAdapter(Fragment hostFragment) {
        this.imageUriList = new ArrayList<>();
    }

    // get image uri list
    public List<Uri> getImageUriList() {
        return imageUriList;
    }

    // public add image and update ui
    public void addImage(Uri imageUri){
        imageUriList.add(imageUri);
        notifyDataSetChanged();
    }

    // update list and ui
    public void setImageUriList(List<Uri> imageUriList){
        this.imageUriList.clear();
        this.imageUriList.addAll(imageUriList);
        notifyDataSetChanged();
    }


    // listener
    private OnImageAddListener mOnImageAddListener;

    // add image add listener
    public void setOnImageAddListener(OnImageAddListener onImageAddListener){
        mOnImageAddListener = onImageAddListener;
    }

    // add image add listener interface
    public interface OnImageAddListener{
        void onImageAdd();
    }

    @NonNull
    @Override
    public PostAdImageRVAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_post_ad_image, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdImageRVAdapter.ViewHolder holder, int position) {
        if(position == imageUriList.size()){
            holder.mVB.image.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v -> {
                // if image list length > 5 then show error toast
                if(imageUriList.size() >= 5){
                    Log.d("AddProductImageRVAdapter", "onBindViewHolder: image list length > 5");
                    Toast.makeText(holder.itemView.getContext(), "You can add maximum 5 images", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mOnImageAddListener != null) {
                    Log.d("AddProductImageRVAdapter", "onBindViewHolder: add image button clicked");
                    mOnImageAddListener.onImageAdd();
                }

            });
        }else{
            holder.mVB.image.setVisibility(View.VISIBLE);
            holder.mVB.image.setImageURI(imageUriList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return imageUriList.size()+1; // 1 for add image button
    }


    // view holder private class
    class ViewHolder extends RecyclerView.ViewHolder{
        private CardPostAdImageBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardPostAdImageBinding.bind(itemView);
        }
    }
}
