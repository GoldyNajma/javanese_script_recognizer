package com.example.javanesescriptrecognizer.modules.details;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.javanesescriptrecognizer.R;
import com.example.javanesescriptrecognizer.base.BaseFragment;
import com.example.javanesescriptrecognizer.data.models.ProcessResult;
import com.example.javanesescriptrecognizer.data.models.RecognitionResult;
import com.example.javanesescriptrecognizer.utils.IOUtil;
import com.example.javanesescriptrecognizer.utils.PreprocessingResultRecyclerViewAdapter;
import com.example.javanesescriptrecognizer.utils.SegmentationResultRecyclerViewAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DetailsFragment extends BaseFragment<DetailsActivity, DetailsContract.Presenter>
        implements DetailsContract.View {
    private static final String TAG = "DetailsFragment";
    RecyclerView rvSegmentation;
    RecyclerView rvPreprocessing;
    Button btSave;
    private Toast toastMessage;
    private TextView tvModel;
    private RecyclerView.Adapter<SegmentationResultRecyclerViewAdapter.MyViewHolder> mAdapter;
    private RecyclerView.Adapter<PreprocessingResultRecyclerViewAdapter.MyViewHolder> mAdapterP;

    public DetailsFragment() {}

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedStateInstance) {
        super.onCreateView(inflater, container, savedStateInstance);

        fragmentView = inflater.inflate(R.layout.fragment_details, container, false);
        btSave = fragmentView.findViewById(R.id.details_bt_save);
        tvModel = fragmentView.findViewById(R.id.details_tv_model);
        rvSegmentation = fragmentView.findViewById(R.id.details_rv_segmentation);
        rvPreprocessing = fragmentView.findViewById(R.id.details_rv_preprocessing);

        rvPreprocessing.setHasFixedSize(true);
        rvPreprocessing.setLayoutManager(
                new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        );

        rvSegmentation.setHasFixedSize(true);
        rvSegmentation.setLayoutManager(new LinearLayoutManager(activity));
        Intent intent = requireActivity().getIntent();
        RecognitionResult recognitionResult = (RecognitionResult)intent
                .getSerializableExtra("RecognitionResultExtra");
        ArrayList<ProcessResult> preprocessingResults = recognitionResult.getPreprocessingResults();
        ArrayList<ProcessResult> results = recognitionResult.getSegmentationResults();
        setRvPreprocessingAdapter(preprocessingResults);
        setRvSegmentationAdapter(results);

        String caption = recognitionResult.getModelName()
                + "(" + recognitionResult.getDuration() + "ms)";

        tvModel.setText(caption);

        btSave.setOnClickListener(v -> {
            String time = System.currentTimeMillis() + "";
            String reading = recognitionResult.getReading();
            String rootFolderName = recognitionResult.getModelName() + "/" + time + "-" + reading;

            for (ProcessResult result : recognitionResult.getPreprocessingResults()) {
                IOUtil.saveImage(
                    result.getImage(),
                    rootFolderName + "/Preprocessing/",
                    result.getId() + "-" + result.getTitle()
                );
            }
            for (ProcessResult result : recognitionResult.getSegmentationResults()) {
                IOUtil.saveImage(
                    result.getImage(),
                    rootFolderName + "/Segmentation/",
                    result.getId() + "-" + result.getTitle()
                );
            }
            for (ProcessResult result : recognitionResult.getResizeResults()) {
                IOUtil.saveImage(
                        result.getImage(),
                        rootFolderName + "/Resized/",
                        result.getId() + "-" + result.getTitle()
                );
            }
            IOUtil.saveText(
                caption + "\n\nReading:\n" + reading,
                rootFolderName,
                "duration"
            );
            showMessage("Saved to DCIM/JavaneseScriptRecognizer/");
        });

        mPresenter = new DetailsPresenter(this, new DetailsInteractor());
        mPresenter.start();

        mPresenter.loadSegmentationResults();

        return fragmentView;
    }

    private void setRvPreprocessingAdapter(final List<ProcessResult> adapterData) {
        mAdapterP = new PreprocessingResultRecyclerViewAdapter(() -> activity, adapterData);
        rvPreprocessing.setAdapter(mAdapterP);

        ((PreprocessingResultRecyclerViewAdapter) mAdapterP)
                .setOnItemClickListener((position, v) -> {
                    int id = adapterData.get(position).getId();

                    Log.d(TAG,"clicked position : "+ position);
                });
    }

    private void setRvSegmentationAdapter(final List<ProcessResult> adapterData) {
        mAdapter = new SegmentationResultRecyclerViewAdapter(() -> activity, adapterData);
        rvSegmentation.setAdapter(mAdapter);

        ((SegmentationResultRecyclerViewAdapter) mAdapter).setOnItemClickListener((position, v) -> {
            int id = adapterData.get(position).getId();

            Log.d(TAG,"clicked position : "+ position + ", id: " + id);
        });
    }

    @Override
    public void showPreprocessingResults(List<ProcessResult> results) {
        setRvPreprocessingAdapter(results);
    }

    @Override
    public void showSegmentationResults(List<ProcessResult> results) {
        setRvSegmentationAdapter(results);
    }

    public DetailsContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(DetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void startLoading() {
//        pbLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void endLoading() {
//        pbLoading.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(activity, message, Toast.LENGTH_LONG);
        toastMessage.show();
    }
}