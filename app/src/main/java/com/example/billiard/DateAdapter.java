package com.example.billiard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

public class DateAdapter
        extends RecyclerView.Adapter<DateAdapter.ViewHolder> {
    private final ArrayList<Idopont> mIdopontok;
    private final Context mContext;
    private int lastPosition = -1;

    DateAdapter(Context context, ArrayList<Idopont> itemsData) {
        this.mIdopontok = itemsData;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DateAdapter.ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(DateAdapter.ViewHolder holder, int position) {
        Idopont currentItem = mIdopontok.get(position);

        holder.bindTo(currentItem);

        if (holder.getAdapterPosition() > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_row);
            holder.itemView.startAnimation(animation);
            lastPosition = holder.getAdapterPosition();
        }
    }

    @Override
    public int getItemCount() {
        return mIdopontok.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitleText;
        private final TextView mInfoText;
        private final ImageView mItemImage;

        ViewHolder(View itemView) {
            super(itemView);

            mTitleText = itemView.findViewById(R.id.itemTitle);
            mInfoText = itemView.findViewById(R.id.subTitle);
            mItemImage = itemView.findViewById(R.id.itemImage);
        }

        @SuppressLint("SetTextI18n")
        void bindTo(Idopont currentItem) {
            mTitleText.setText((mContext.getString(R.string.idopont) + ": " + currentItem.getInfo()));
            mInfoText.setText(
                    currentItem.isFoglalt() ? "Sajnos már foglalt!" : "Még szabad az időpont, gyorsan csapj le rá!"
            );

            Glide.with(mContext).load(currentItem.getImageResource()).into(mItemImage);
            itemView.findViewById(R.id.lefoglal).setOnClickListener(view -> ((DateListActivity) mContext).lefoglalas(currentItem));
        }
    }
}
