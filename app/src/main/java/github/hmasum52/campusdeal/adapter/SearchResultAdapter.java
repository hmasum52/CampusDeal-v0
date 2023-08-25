package github.hmasum52.campusdeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardAdHorizontalBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.util.Util;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    // Diffutil will be used to update the recyclerview
    // https://developer.android.com/reference/androidx/recyclerview/widget/DiffUtil
    private final DiffUtil.ItemCallback<Ad> diffCallback = new DiffUtil.ItemCallback<Ad>() {
        @Override
        public boolean areItemsTheSame(@NonNull Ad oldItem, @NonNull Ad newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Ad oldItem, @NonNull Ad newItem) {
            return oldItem.equals(newItem);
        }
    };

    // ay list of ads
    public AsyncListDiffer<Ad> differ = new AsyncListDiffer<>(this, diffCallback);

    private RecyclerItemClickListener<Ad> recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener<Ad> recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public SearchResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ad_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultAdapter.ViewHolder holder, int position) {
        // get ad
        Ad ad = differ.getCurrentList().get(position);

        // set image with glide
        // https://futurestud.io/tutorials/glide-image-resizing-scaling
        Glide.with(holder.itemView.getContext())
                .load(ad.getImageUriList().get(0))
                //.override(300, 300)
                //.centerCrop()
                .into(holder.mVB.thumbImage);

        // set location
        holder.mVB.locationTv.setText(ad.getAdLocation().getAdminArea());

        // set title
        holder.mVB.titleTv.setText(ad.getTitle());

        // set price
        String priceDesc = "Tk"+ad.getPrice();
        if(ad.isUrgent()){
            priceDesc += " • Urgent";
        }
        if(ad.isNegotiable()) {
            priceDesc += " • Negotiable";
        }
        holder.mVB.priceDesc.setText(priceDesc);

        // set owner name
        holder.mVB.ownerNameTv.setText(ad.getSellerName());

        // set distance/**/

        holder.mVB.distanceTv.setText("1.2 km");

        // set upload date
        String timeAlgo = Util.calculateTimeAlgo(ad.getUploadDate());
        holder.mVB.timeTv.setText(timeAlgo);

        // set click listener
        holder.itemView.setOnClickListener(v -> {
            if(recyclerItemClickListener!=null){
                recyclerItemClickListener.onItemClick(ad);
            }
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CardAdHorizontalBinding mVB;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardAdHorizontalBinding.bind(itemView);
        }
    }
}
