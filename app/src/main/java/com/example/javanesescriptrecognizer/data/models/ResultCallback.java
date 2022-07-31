package com.example.javanesescriptrecognizer.data.models;

public interface ResultCallback<T> {
    void onComplete(Result<T> result);
    void onLoading();
}
