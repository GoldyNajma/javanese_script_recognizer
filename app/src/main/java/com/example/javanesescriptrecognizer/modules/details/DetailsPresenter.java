package com.example.javanesescriptrecognizer.modules.details;

public class DetailsPresenter implements DetailsContract.Presenter {
    private final DetailsContract.View view;
    private final DetailsContract.Interactor interactor;

    public DetailsPresenter(DetailsContract.View view, DetailsContract.Interactor interactor) {
        this.view = view;
        this.interactor = interactor;
    }

    @Override
    public void start() {
//        if (interactor.getToken() == null) {
//            view.redirectToLogin();
//        }
    }


    @Override
    public void loadSegmentationResults() {
        view.startLoading();
//        interactor.requestIndexTasks(new RequestCallback<IndexTasksResponse>() {
//            @Override
//            public void requestSuccess(IndexTasksResponse data) {
//                view.endLoading();
//                view.showUserTasks(data.tasks);
//            }
//
//            @Override
//            public void requestFailed(String errorMessage) {
//                view.endLoading();
//                view.showMessage(errorMessage);
//            }
//        }, option);
    }
}