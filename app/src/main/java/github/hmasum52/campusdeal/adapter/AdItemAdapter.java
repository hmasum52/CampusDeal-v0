package github.hmasum52.campusdeal.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardAdBinding;
import github.hmasum52.campusdeal.model.Ad;

public class AdItemAdapter extends RecyclerView.Adapter<AdItemAdapter.ViewHolder> {

    List<Ad>   adList;

    public AdItemAdapter(List<Ad> adList) {
        this.adList = adList;
    }

    void setAdList(List<Ad> adList){
        this.adList = adList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_ad, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdItemAdapter.ViewHolder holder, int position) {
        // get Ad
        Ad ad = adList.get(position);

        // set image with glide
        Glide.with(holder.itemView.getContext())
                .load(ad.getImageUriList().get(0))
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

        // set distance
        holder.mVB.distanceTv.setText("1.2 km");

        // set upload date
        holder.mVB.timeTv.setText("1 day ago");

    }

    @Override
    public int getItemCount() {
        return adList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardAdBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardAdBinding.bind(itemView);
        }
    }
}
