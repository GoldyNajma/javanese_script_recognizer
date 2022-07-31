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
    }


    @Override
    public void loadSegmentationResults() {
        view.startLoading();
    }
}