package github.hmasum52.campusdeal.adapter;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.Date;
import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardAdBinding;
import github.hmasum52.campusdeal.model.Ad;

public class AdItemAdapter extends RecyclerView.Adapter<AdItemAdapter.ViewHolder> {

    List<Ad>  adList;

    public AdItemAdapter(List<Ad> adList) {
        this.adList = adList;
    }

    void setAdList(List<Ad> adList){
        this.adList = adList;
        notifyDataSetChanged();
    }

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
    public void onBindViewHolder(@NonNull AdItemAdapter.ViewHolder holder, int position) {
        // get Ad
        Ad ad = adList.get(position);

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
        String timeAlgo = calculateTimeAlgo(ad.getUploadDate());
        holder.mVB.timeTv.setText(timeAlgo);

        // set click listener
        holder.itemView.setOnClickListener(v -> {
            if(recyclerItemClickListener!=null){
                recyclerItemClickListener.onItemClick(ad);
            }
        });

    }

    private String calculateTimeAlgo(Date uploadDate) {
        long diff = new Date().getTime() - uploadDate.getTime();
        long diffSeconds = diff / 1000;
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays  = diff / (24 * 60 * 60 * 1000);
        long diffWeeks = diff / (7 * 24 * 60 * 60 * 1000);
        long diffMonths = diff / (30L * 24 * 60 * 60 * 1000);
        long diffYears = diff / (365L * 24 * 60 * 60 * 1000);

        if(diffSeconds < 60){
            return diffSeconds+" sec ago";
        }else if(diffMinutes < 60){
            return diffMinutes+" min ago";
        }else if(diffHours < 24){
            return diffHours+" hours ago";
        }else if(diffDays < 7){
            return diffDays+" days ago";
        }else if(diffWeeks < 4){
            return diffWeeks+" weeks ago";
        }else if(diffMonths < 12){
            return diffMonths+" months ago";
        }else {
            return diffYears+" years ago";
        }
    }

    /**
     * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     *
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     * @returns Distance in Meters
     *
     */
    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance)/1000.0; // return in km
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
