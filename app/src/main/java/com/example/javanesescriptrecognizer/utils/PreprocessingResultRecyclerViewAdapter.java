package com.example.javanesescriptrecognizer.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javanesescriptrecognizer.R;
import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import java.util.List;

public class PreprocessingResultRecyclerViewAdapter
        extends RecyclerView.Adapter<PreprocessingResultRecyclerViewAdapter.MyViewHolder>{
    private static final String TAG = "PreprocessingResultRVA";
    private final List<ProcessResult> mDataset;
    private final ContextProvider mContextProvider;
    private static MyClickListener myClickListener;

    public List<ProcessResult> getDataset() {
        return mDataset;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        ImageView ivImage;
        TextView tvTitle;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.preprocessing_tv_title);
            ivImage = itemView.findViewById(R.id.preprocessing_iv_image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();

            myClickListener.onItemClick(position, view);
        }
    }

    public PreprocessingResultRecyclerViewAdapter(
            PreprocessingResultRecyclerViewAdapter.ContextProvider myContextProvider,
            List<ProcessResult> myDataset
    ) {
        mDataset = myDataset;
        mContextProvider = myContextProvider;
    }

    @NonNull
    @Override
    public PreprocessingResultRecyclerViewAdapter.MyViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        try {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.preprocessing_item, parent, false);

            return new PreprocessingResultRecyclerViewAdapter.MyViewHolder(view);
        } catch (Exception e) {
            Log.e(TAG, "onCreateViewHolder", e);
            throw e;
        }
    }

    @Override
    public void onBindViewHolder(
            @NonNull PreprocessingResultRecyclerViewAdapter.MyViewHolder holder,
            int position
    ) {
        holder.ivImage.setImageBitmap(mDataset.get(position).getImage());
        holder.tvTitle.setText(mDataset.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setOnItemClickListener(
            PreprocessingResultRecyclerViewAdapter.MyClickListener myClickListener
    ) {
        PreprocessingResultRecyclerViewAdapter.myClickListener = myClickListener;
    }

    public interface MyClickListener {
        void onItemClick(int position, View v);
    }

    public interface ContextProvider {
        Context getContext();
    }
}
