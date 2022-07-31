package com.example.javanesescriptrecognizer.modules.details;

import com.example.javanesescriptrecognizer.base.BasePresenter;
import com.example.javanesescriptrecognizer.base.BaseView;
import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import java.util.List;

public interface DetailsContract {
    interface Presenter extends BasePresenter {
        void loadSegmentationResults();
    }

    interface View extends BaseView<Presenter> {
        void showPreprocessingResults(List<ProcessResult> results);
        void showSegmentationResults(List<ProcessResult> results);
    }

    interface Interactor {
    }
}