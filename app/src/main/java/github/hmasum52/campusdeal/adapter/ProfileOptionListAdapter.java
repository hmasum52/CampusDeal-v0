package github.hmasum52.campusdeal.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import github.hmasum52.campusdeal.R;
import github.hmasum52.campusdeal.databinding.CardProfileMenuBinding;
import github.hmasum52.campusdeal.util.ProfileOption;

public class ProfileOptionListAdapter extends RecyclerView.Adapter<ProfileOptionListAdapter.ViewHolder>{

    private List<ProfileOption> mProfileOptionList;

    // click listener
    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        mOnItemClickListener = onItemClickListener;
    }

    public ProfileOptionListAdapter(List<ProfileOption> profileOptionList) {
        mProfileOptionList = profileOptionList;
    }

    @NonNull
    @Override
    public ProfileOptionListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_profile_menu, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileOptionListAdapter.ViewHolder holder, int position) {
        ProfileOption profileOption = mProfileOptionList.get(position);
        holder.mVB.optionName.setText(profileOption.getTitle());
        holder.mVB.optionDescription.setText(profileOption.getDescription());

        // set click listener
        holder.itemView.setOnClickListener(v -> {
            if(mOnItemClickListener != null){
                mOnItemClickListener.onItemClick(position, profileOption);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mProfileOptionList.size();
    }

    // View Holder
    class ViewHolder extends RecyclerView.ViewHolder{
        private CardProfileMenuBinding mVB;
        public ViewHolder(@NonNull View view) {
            super(view);
            mVB = CardProfileMenuBinding.bind(view);
        }
    }

    // interface for click listener
    public interface OnItemClickListener{
        void onItemClick(int position, ProfileOption profileOption);
    }
}
