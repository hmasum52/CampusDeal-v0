package github.hmasum52.campusdeal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardDealRequestBinding;
import github.hmasum52.campusdeal.model.DealRequest;
import github.hmasum52.campusdeal.util.Util;

public class DealRequestListAdapter extends RecyclerView.Adapter<DealRequestListAdapter.ViewHolder>{
    private static final String TAG = "DealRequestListAdapter";

    private boolean isBuyer = false;

    public void setBuyer(boolean isBuyer) {
        this.isBuyer = isBuyer;
    }

    private final DiffUtil.ItemCallback<DealRequest> diffCallback = new DiffUtil.ItemCallback<DealRequest>() {
        @Override
        public boolean areItemsTheSame(@NonNull DealRequest oldItem, @NonNull DealRequest newItem) {
            return oldItem.getAdId().equals(newItem.getAdId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull DealRequest oldItem, @NonNull DealRequest newItem) {
            return oldItem.equals(newItem);
        }
    };

    public AsyncListDiffer<DealRequest> differ = new AsyncListDiffer<>(this, diffCallback);

    RecyclerItemClickListener<DealRequest> onDealRequestClickListener;

    public DealRequestListAdapter(RecyclerItemClickListener<DealRequest> onDealRequestClickListener) {
        this.onDealRequestClickListener = onDealRequestClickListener;
    }

    @NonNull
    @Override
    public DealRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deal_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealRequestListAdapter.ViewHolder holder, int position) {
        // get deal request
        DealRequest dealRequest = differ.getCurrentList().get(position);

        // set data to the view

        // set buyer info
        String buyerInfo = dealRequest.getBuyerName()+" has requested to buy ";
        if(isBuyer) buyerInfo = "You have requested "+dealRequest.getSellerName()+" to buy ";
        holder.mVB.buyerInfoTv.setText(buyerInfo);

        // set ad title
        holder.mVB.adTitleTv.setText(dealRequest.getTitle());

        // set time
        String time = Util.calculateTimeAlgo(dealRequest.getDate());
        holder.mVB.timeTv.setText(time);

        // review request
        holder.mVB.reviewBtn.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: review btn clicked");
            if(onDealRequestClickListener != null) onDealRequestClickListener.onItemClick(dealRequest);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardDealRequestBinding mVB;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardDealRequestBinding.bind(itemView);
        }
    }
}
