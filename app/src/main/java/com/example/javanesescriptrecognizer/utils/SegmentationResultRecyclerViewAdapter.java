package com.example.javanesescriptrecognizer.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.javanesescriptrecognizer.R;
import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SegmentationResultRecyclerViewAdapter
        extends RecyclerView.Adapter<SegmentationResultRecyclerViewAdapter.MyViewHolder> {
    private static final String TAG = "SegmentationResultRVA";
    private final List<ProcessResult> mDataset;
    private final ContextProvider mContextProvider;
    private static MyClickListener myClickListener;

    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView ivImage;
        TextView tvTitle;
        TextView tvUnicode;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.segmentation_tv_title);
            tvUnicode = itemView.findViewById(R.id.segmentation_tv_unicode);
            ivImage = itemView.findViewById(R.id.segmentation_iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            myClickListener.onItemClick(position, view);
        }
    }

    public SegmentationResultRecyclerViewAdapter(
            ContextProvider myContextProvider,
            List<ProcessResult> myDataset
    ) {
        mDataset = myDataset;
        mContextProvider = myContextProvider;
    }

    @NotNull
    @Override
    public SegmentationResultRecyclerViewAdapter.MyViewHolder onCreateViewHolder(
            @NotNull ViewGroup parent,
            int viewType
    ) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.segmentation_item, parent, false);

            return new MyViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder", e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(@NotNull MyViewHolder holder, final int position) {
        holder.ivImage.setImageBitmap(mDataset.get(position).getImage());
        holder.tvTitle.setText(mDataset.get(position).getTitle());
        holder.tvUnicode.setText(mDataset.get(position).getUnicode());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        SegmentationResultRecyclerViewAdapter.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public interface ContextProvider {
        Context getContext();
    }
}
