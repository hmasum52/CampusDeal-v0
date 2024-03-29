package github.hmasum52.campusdeal.adapter;

import android.location.Location;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardAdBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.User;
import github.hmasum52.campusdeal.util.Util;
import github.hmasum52.campusdeal.viewmodel.UserViewModel;

public class AdItemAdapter extends RecyclerView.Adapter<AdItemAdapter.ViewHolder> {
    UserViewModel userViewModel;
    User user;

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

    public void setUser(User user) {
        this.user = user;
    }

    public AsyncListDiffer<Ad> differ = new AsyncListDiffer<>(this, diffCallback);


    private RecyclerItemClickListener<Ad> recyclerItemClickListener;

    public void setRecyclerItemClickListener(RecyclerItemClickListener<Ad> recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @NonNull
    @Override
    public AdItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Override
    public void onBindViewHolder(@NonNull AdItemAdapter.ViewHolder holder, int position) {
        // get Ad
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
        if(user != null){
            holder.mVB.distanceTv.setVisibility(View.VISIBLE);
            String distanceDesc = String.format(Locale.getDefault() , "%.2f", distance(ad))+" km";
            holder.mVB.distanceTv.setText(distanceDesc);
        }else{
            holder.mVB.distanceTv.setVisibility(View.GONE);
        }


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

    // return distance in km
    // https://stackoverflow.com/a/17983974/13877490
    private double distance(Ad ad){
        Location userLocation = new Location("");
        userLocation.setLatitude(user.getCampus().getLatitude());
        userLocation.setLongitude(user.getCampus().getLongitude());

        Location adLocation = new Location("");
        adLocation.setLatitude(ad.getAdLocation().getLatitude());
        adLocation.setLongitude(ad.getAdLocation().getLongitude());

        return userLocation.distanceTo(adLocation)/1000.0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardAdBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardAdBinding.bind(itemView);
        }
    }
}
