package com.example.javanesescriptrecognizer.data.models;

import com.example.javanesescriptrecognizer.base.BaseModel;

import java.io.Serializable;
import java.util.ArrayList;

public class RecognitionResult extends BaseModel implements Serializable {
    private final String modelName;
    private final ArrayList<ProcessResult> preprocessingResults;
    private final ArrayList<ProcessResult> segmentationResults;
    private final ArrayList<ProcessResult> resizeResults;
    private final long duration;
    private final String reading;
    private final String unicodes;

    public RecognitionResult(
            String modelName,
            ArrayList<ProcessResult> preprocessingResults,
            ArrayList<ProcessResult> segmentationResults,
            ArrayList<ProcessResult> resizeResults,
            long duration,
            String reading,
            String unicodes
    ) {
        this.modelName = modelName;
        this.preprocessingResults = preprocessingResults;
        this.segmentationResults = segmentationResults;
        this.resizeResults = resizeResults;
        this.duration = duration;
        this.reading = reading;
        this.unicodes = unicodes;
    }

    public String getModelName() {
        return modelName;
    }

    public ArrayList<ProcessResult> getPreprocessingResults() {
        return preprocessingResults;
    }

    public ArrayList<ProcessResult> getSegmentationResults() {
        return segmentationResults;
    }

    public ArrayList<ProcessResult> getResizeResults() {
        return resizeResults;
    }

    public long getDuration() {
        return duration;
    }

    public String getReading() {
        return reading;
    }

    public String getUnicodes() {
        return unicodes;
    }
}
