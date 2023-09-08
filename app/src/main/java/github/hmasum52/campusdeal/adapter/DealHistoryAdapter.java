package github.hmasum52.campusdeal.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardDealRequestBinding;
import github.hmasum52.campusdeal.model.Ad;
import github.hmasum52.campusdeal.model.Deal;
import github.hmasum52.campusdeal.model.DealRequest;

public class DealHistoryAdapter extends RecyclerView.Adapter<DealHistoryAdapter.ViewHolder> {
    public static final String TAG = "DealHistoryAdapter";
    private boolean isBuyer = false;

    public void setBuyer(boolean isBuyer) {
        this.isBuyer = isBuyer;
    }

    private final DiffUtil.ItemCallback<Deal> diffCallback = new DiffUtil.ItemCallback<Deal>() {
        @Override
        public boolean areItemsTheSame(@NonNull Deal oldItem, @NonNull Deal newItem) {
            return oldItem.getAd().getId().equals(newItem.getAd().getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Deal oldItem, @NonNull Deal newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    public AsyncListDiffer<Deal> differ = new AsyncListDiffer<>(this, diffCallback);

    RecyclerItemClickListener<Deal> onDealHistoryClickListener;

    public DealHistoryAdapter(RecyclerItemClickListener<Deal> onDealHistoryClickListener) {
        this.onDealHistoryClickListener = onDealHistoryClickListener;
    }


    @NonNull
    @Override
    public DealHistoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_deal_request, parent, false);
        return new DealHistoryAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DealHistoryAdapter.ViewHolder holder, int position) {
        Deal deal = differ.getCurrentList().get(position);

        String dealText = "You sold " + deal.getAd().getTitle() + " to " + deal.getDealInfo().getBuyerName();
        if(isBuyer) dealText = "You bought " + deal.getAd().getTitle() + " from " + deal.getDealInfo().getSellerName();
        String priceText = "at BDT " + deal.getAd().getPrice();
        String dateText = "on " + deal.getDate().toString();

        holder.mVB.buyerInfoTv.setText(dealText);
        holder.mVB.adTitleTv.setText(priceText);
        holder.mVB.timeTv.setText(dateText);
        holder.mVB.reviewBtn.setText("See Details");

        // review request
        holder.mVB.reviewBtn.setOnClickListener(v -> {
            Log.d(TAG, "onBindViewHolder: review btn clicked");
            if(onDealHistoryClickListener != null) onDealHistoryClickListener.onItemClick(deal);
        });
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardDealRequestBinding mVB;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mVB = CardDealRequestBinding.bind(itemView);
        }
    }
}
