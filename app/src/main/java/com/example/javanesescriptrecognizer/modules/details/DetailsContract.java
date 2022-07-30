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
//        void requestIndexTasks(RequestCallback<IndexTasksResponse> requestCallback, String option);
//        void requestUpdateTaskCompletion(RequestCallback<TaskResponse> requestCallback,
//                                         int taskId, boolean completionStatus);
//        String getToken();
//        void deleteSession();
//        User getUser();
//        void requestLogout(RequestCallback<LogoutResponse> requestCallback);
    }
}