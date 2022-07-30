package com.example.javanesescriptrecognizer.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.Ximgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SegmentationUtil {
    final private static String TAG = "Segmentation";
    private static final List<ProcessResult> segmentationResults = new ArrayList<>();

    public static List<ProcessResult> getResults() {
        return segmentationResults;
    };

    public static Bitmap padSymmetric(@NonNull Bitmap src, int x, int y) {
        return padLTRB(src, x, y, x, y);
    }

    public static Bitmap padLTRB(@NonNull Bitmap src, int left, int top, int right, int bottom) {
        Bitmap srcCopy = src.copy(src.getConfig(), true);
        Bitmap outputImage1 = Bitmap.createBitmap(
                srcCopy.getWidth() + right,
                srcCopy.getHeight() + bottom,
                Bitmap.Config.RGB_565
        );
        Bitmap outputImage2;
        int rgb = 255;
        Canvas canvas = new Canvas(outputImage1);

        canvas.drawRGB(rgb, rgb, rgb);
        canvas.drawBitmap(srcCopy, 0, 0, null);

        outputImage2 = Bitmap.createBitmap(
                outputImage1.getWidth() + left,
                outputImage1.getHeight() + top,
                Bitmap.Config.RGB_565
        );

        canvas = new Canvas(outputImage2);
        canvas.drawRGB(rgb, rgb, rgb);
        canvas.drawBitmap(outputImage1, left, top, null);
        return outputImage2;
    }

    private static Bitmap drawSegmentationLine(
            @NonNull Bitmap bitmap,
            int x,
            int y,
            int xend,
            int yend,
            int color
    ) {
        Bitmap bmp = bitmap.copy(bitmap.getConfig(), true);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();
        p.setColor(color);
        c.drawLine(x, y, xend, yend, p);

        return bmp;
    }

    @NonNull
    public static ArrayList<Bitmap> segmentHorizontally(@NonNull Bitmap source) {
        Bitmap inputBitmap = source.copy(source.getConfig(), true);
        final int height = inputBitmap.getHeight();
        final int width = inputBitmap.getWidth();
        Mat inputMat = new Mat(height, width, CvType.CV_8U);
        ArrayList<Integer> nonZeros = new ArrayList<>();
        ArrayList<Bitmap> outputBitmaps = new ArrayList<>();
        int colStart = 0;
        int colEnd = -1;
        boolean isOnLastIndex = false;
        Utils.bitmapToMat(inputBitmap, inputMat);
        inputMat = PreprocessingUtil.binarize(inputMat, false);
//        Bitmap segmentedBitmap = inputBitmap.copy(inputBitmap.getConfig(), true);

        for (int i = 0; i < inputMat.cols(); i++) {
            nonZeros.add(Core.countNonZero(inputMat.col(i)));
            Log.d(TAG, i + ": " + nonZeros.get(i));
        }

        double fullWidth = 0;
        double avgWidth = 0;
        List<Mat> mats = new ArrayList<>();

        boolean ignoreZeroCol = false;
        do {

            for (int j = colEnd + 1; j < nonZeros.size(); j++) {
                if (j == nonZeros.size() - 1) {
                    isOnLastIndex = true;
                    colStart = j;
                    break;
                }
                if (nonZeros.get(j) > 0) {
                    colStart = j;
                    break;
                }
            }
//            if (Math.abs(colStart - colEnd) < 5) {
//                ignoreZeroCol = true;
//            }
            for (int j = colStart; j < nonZeros.size(); j++) {
                if (j == nonZeros.size() - 1) {
                    isOnLastIndex = true;
                    colEnd = j;
                    break;
                }
                if (nonZeros.get(j) == 0) {
                    colEnd = j - 1;
                    break;
                }
            }
            int tempColStart = colStart;
            int tempColEnd = colEnd;

            if (colStart < colEnd) {

//                if (ignoreZeroCol) {
//                    boolean flag = false;
//
//                    for (int j = tempColStart - 1; j >= 0; j--) {
//                        if (nonZeros.get(j) > 0) {
//                            flag = true;
//                        }
//                        if (flag && nonZeros.get(j) == 0) {
//                            tempColStart = j + 1;
//                            break;
//                        }
//                    }
//
//                    if (outputBitmaps.size() > 1) {
//                        Log.d("ignoreignore", "Before: " + outputBitmaps.size() + ", " + mats.size());
//                        outputBitmaps.remove(outputBitmaps.size() - 1);
//                        mats.remove(mats.size() - 1);
//                        Log.d("ignoreignore", "After: " + outputBitmaps.size() + ", " + mats.size());
//                    }
//                }


                Log.d(TAG, "------------------------------");
                Log.d(TAG, "tempColStart : " + tempColStart);
                Log.d(TAG, "tempColEnd   : " + tempColEnd);
                Mat subMat = inputMat.colRange(new Range(tempColStart, tempColEnd)).clone();


                final int subMatWidth = subMat.width();
                final int subMatHeight = subMat.height();
                final int max = Math.max(subMatWidth, subMatHeight);
                final int padX = (max - subMatWidth) / 2;
                final int padY = 0;
//                        (max - subMatHeight) / 2;
                Bitmap subBitmap = Bitmap.createBitmap(
                        subMatWidth,
                        subMatHeight,
                        Bitmap.Config.RGB_565
                );

                fullWidth += Core.countNonZero(subMat);

                Core.bitwise_not(subMat, subMat);
                mats.add(subMat);
                Utils.matToBitmap(subMat, subBitmap);
                Bitmap paddedSubBitmap = padSymmetric(subBitmap, padX, padY);
                outputBitmaps.add(paddedSubBitmap);
            }
        } while (!isOnLastIndex);

        avgWidth = fullWidth / outputBitmaps.size();
        List<Bitmap> delete = new ArrayList<>();

        Log.d("ABCDEFG", outputBitmaps.size() + "");

//        for (int i = 0; i < mats.size(); i++) {
//            Mat mat = mats.get(i);
//
//            if (Core.countNonZero(mat) < avgWidth) {
//                delete.add(outputBitmaps.get(i));
//            }
//        }
//
//        for (Bitmap bitmap : delete) {
//            outputBitmaps.remove(bitmap);
//        }

        Log.d("ABCDEFG", outputBitmaps.size() + "");
        Log.d("ABCDEFG", outputBitmaps.size() + "=======================");
//        segmentationResults.add(
//                new ProcessResult(5, segmentedBitmap, "Vertical Projection Profile")
//        );

        return outputBitmaps;
    }

    @NonNull
    public static List<List<Bitmap>> segmentVertically(@NonNull List<Bitmap> source) {
        List<List<Bitmap>> result = new ArrayList<>();
        int k = 0;
        for (Bitmap bitmap : source) {

            Log.d("combined", k + "");
            Bitmap inputBitmap = bitmap.copy(bitmap.getConfig(), true);

            final int height = inputBitmap.getHeight();
            final int width = inputBitmap.getWidth();
            final Mat inputMat = new Mat(height, width, CvType.CV_8U);
            List<Integer> nonZeros = new ArrayList<>();
            List<Bitmap> outputBitmaps = new ArrayList<>();
            List<Bitmap> paddedOutputBitmaps = new ArrayList<>();
            int rowStart = 0;
            int rowEnd = -1;
            boolean isOnLastIndex = false;
            Utils.bitmapToMat(inputBitmap, inputMat);
            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.adaptiveThreshold(
//                inputMat,
//                inputMat,
//                255,
//                Imgproc.ADAPTIVE_THRESH_MEAN_C,
//                Imgproc.THRESH_BINARY,
//                21,
//                10
//        );
            Imgproc.threshold(
                    inputMat,
                    inputMat,
                    0,
                    255,
                    Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
            );

            Core.bitwise_not(inputMat, inputMat);


//        Mat kernel = new Mat(3, 3, CvType.CV_8U, new Scalar(0));
//        Imgproc.erode(inputMat, inputMat, kernel, new Point(), 1);
//
//        boolean done = false;
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
//        Mat eroded = new Mat();
//        Mat temp = new Mat();
//        Mat skel = new Mat(inputMat.rows(), inputMat.cols(), CvType.CV_8UC1, new Scalar(0));
//
//
//        int size = inputMat.cols() * inputMat.rows();
//        int zeros = 0;
//        int iter = 0;
//
//        for (int i = 0; i < inputMat.cols(); i++) {
//            Log.d(TAG, i + ": " + Core.countNonZero(inputMat.col(i)));
//        }
//
//        while(!done)
//        {
//            Imgproc.erode(inputMat, eroded, element);
//            Imgproc.dilate(eroded, temp, element);
//            Core.subtract(inputMat, temp, temp);
//            Core.bitwise_or(skel, temp, skel);
//            eroded.copyTo(inputMat);
//
//            zeros = size - Core.countNonZero(inputMat);
//            if(zeros == size) {
//                done = true;
//            }
//            Log.d(TAG, "iteration: " + iter++);
//        }

//            Imgproc.connectedComponentsWithStats()
//            output = Imgproc.connectedComponentsWithStats(
//                    thresh, args["connectivity"], CvType.CV_32S);
//            (numLabels, labels, stats, centroids) = output

            double fullWidth = 0;
            double avgWidth = 0;
            List<Mat> mats = new ArrayList<>();


            for (int i = 0; i < inputMat.rows(); i++) {
                nonZeros.add(Core.countNonZero(inputMat.row(i)));
                Log.d(TAG, i + ": " + nonZeros.get(i));
            }

            do {
                for (int j = rowEnd + 1; j < nonZeros.size(); j++) {
                    if (j == nonZeros.size() - 1) {
                        isOnLastIndex = true;
                        rowStart = j;
                        break;
                    }
                    if (nonZeros.get(j) > 0) {
                        rowStart = j;
                        break;
                    }
                }
                for (int j = rowStart; j < nonZeros.size(); j++) {
                    if (j == nonZeros.size() - 1) {
                        isOnLastIndex = true;
                        rowEnd = j;
                        break;
                    }
                    if (nonZeros.get(j) == 0) {
                        rowEnd = j - 1;
                        break;
                    }
                }
                if (rowStart < rowEnd) {
                    Log.d(TAG, "------------------------------");
                    Log.d(TAG, "rowStart : " + rowStart);
                    Log.d(TAG, "rowEnd   : " + rowEnd);
                    Mat subMat = inputMat.rowRange(new Range(rowStart, rowEnd)).clone();
                    final int subMatWidth = subMat.width();
                    final int subMatHeight = subMat.height();
                    final int max = Math.max(subMatWidth, subMatHeight);
                    final int padX = 0;
//                            (max - subMatWidth) / 2;
                    final int padY = (max - subMatHeight) / 2;
                    Bitmap subBitmap = Bitmap.createBitmap(
                            subMatWidth,
                            subMatHeight,
                            Bitmap.Config.RGB_565
                    );
                    fullWidth += Core.countNonZero(subMat);

                    Core.bitwise_not(subMat, subMat);
                    mats.add(subMat);
                    Utils.matToBitmap(subMat, subBitmap);
//                    Bitmap paddedSubBitmap = padSymmetric(subBitmap, padX, padY);
                    outputBitmaps.add(subBitmap);
                }
            } while (!isOnLastIndex);


            avgWidth = fullWidth / outputBitmaps.size();
            List<Bitmap> deletes = new ArrayList<>();

            Log.d("ABCDEFGH", outputBitmaps.size() + "");

            for (int i = 0; i < mats.size(); i++) {
                Mat mat = mats.get(i);

                if (Core.countNonZero(mat) < avgWidth) {
                    deletes.add(outputBitmaps.get(i));
                }
            }

            for (Bitmap delete : deletes) {
                outputBitmaps.remove(delete);
            }

            Log.d("ABCDEFGH", outputBitmaps.size() + "");
            Log.d("ABCDEFGH", outputBitmaps.size() + "=======================");


            int n = outputBitmaps.size();

            if (n > 2) {
                for (int i = 0; i < n; i++) {
                    int quarterHeight = height / 4;

                    Bitmap outputBitmap = outputBitmaps.get(i);
                    Bitmap paddedBitmap;

                    if (i == 0) {
                        paddedBitmap = padLTRB(
                                outputBitmap,
                                0, quarterHeight,
                                0,
                                quarterHeight * 3
                        );
                    } else if (i == 1) {
                        paddedBitmap = padSymmetric(outputBitmap, 0, quarterHeight * 2);
                    } else {
                        paddedBitmap = padLTRB(
                                outputBitmap,
                                0,
                                quarterHeight * 3,
                                0,
                                quarterHeight
                        );
                    }
                    paddedOutputBitmaps.add(paddedBitmap);
                }
            } else if (n > 1) {
                int quarterHeight = height / 4;

                Bitmap firstPaddedBitmap = padLTRB(
                        outputBitmaps.get(0),
                        0,
                        quarterHeight,
                        0,
                        quarterHeight * 3
                );
                Bitmap secondPaddedBitmap = padSymmetric(
                        outputBitmaps.get(1),
                        0,
                        quarterHeight * 2
                );
                paddedOutputBitmaps.add(firstPaddedBitmap);
                paddedOutputBitmaps.add(secondPaddedBitmap);
            } else if (n == 1) {
//                Bitmap bmp = outputBitmaps.get(0);
//                Bitmap paddedBitmap = padSymmetric(bmp, 0, bmp.getHeight() / 2);
                paddedOutputBitmaps.add(inputBitmap);
            }

//            if (k == source.size() - 1) {
//                List<List<Bitmap>> ccResults = segmentVerticallyCC(paddedOutputBitmaps);
//                paddedOutputBitmaps.clear();
//
//                for (List<Bitmap> ccResult : ccResults) {
//                    paddedOutputBitmaps.addAll(ccResult);
//                }
//            }

            result.add(paddedOutputBitmaps);
            k++;
        }

        return result;
    }

    @NonNull
    public static List<List<Bitmap>> segmentVerticallyCC(@NonNull List<Bitmap> source) {
        List<List<Bitmap>> result = new ArrayList<>();

        int j = 0;

        for (Bitmap bitmap : source) {
            Bitmap inputBitmap = bitmap.copy(bitmap.getConfig(), true);
            final int height = inputBitmap.getHeight();
            final int width = inputBitmap.getWidth();
            Mat inputMat = new Mat(height, width, CvType.CV_8U);
            Mat labels = new Mat();
            Mat stats = new Mat();
            Mat centroids = new Mat();
            int connectivity = 8;
            int ltype = CvType.CV_32S;
            int componentsCount;
            Map<Integer, Bitmap> verticalMap = new HashMap<>();
            List<Bitmap> vertical;

            Utils.bitmapToMat(inputBitmap, inputMat);
//            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
            inputMat = PreprocessingUtil.binarize(inputMat, false);

            componentsCount = Imgproc.connectedComponentsWithStats(
                    inputMat,
                    labels,
                    stats,
                    centroids,
                    connectivity,
                    ltype
            );
            List<Integer> combinedIndexes = new ArrayList<>();

            for (int i = 1; i < componentsCount; i++) {
                int w = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
                int h = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
                int prevW = (int)stats.get(i - 1, Imgproc.CC_STAT_WIDTH)[0];
                int prevH = (int)stats.get(i - 1, Imgproc.CC_STAT_HEIGHT)[0];

                if (w == 1 || h == 1) {
                    continue;
                }
                Mat currentLabel = new Mat();
                Core.compare(labels, new Scalar(i), currentLabel, Core.CMP_EQ);
                Log.d("combined", j + ", " + i + ", " + (i - 1) + ", "
                        + Arrays.toString(combinedIndexes.toArray()));

                if (i > 1 && !combinedIndexes.contains(i - 1) && prevW > 1 && prevH > 1) {
//                    double currentWidth = stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
//                    double previousWidth = stats.get(i - 1, Imgproc.CC_STAT_WIDTH)[0];
//                    double largerWidth = Math.max(currentWidth, previousWidth);
//                    double smallerWidth = Math.min(currentWidth, previousWidth);
//                    boolean widthRatioLessThanOneHalf = largerWidth / smallerWidth < 1.5;

                    int previousTop = (int)stats.get(i - 1, Imgproc.CC_STAT_TOP)[0];
                    int previousHeight = (int)stats.get(i - 1, Imgproc.CC_STAT_HEIGHT)[0];
                    int currentTop = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
                    int currentHeight = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
                    int previousBottom = previousTop + previousHeight;
                    int currentBottom = currentTop + currentHeight;
                    double largerHeight = Math.max(currentHeight, previousHeight);
                    double smallerHeight = Math.min(currentHeight, previousHeight);
                    boolean previousInsideCurrent =
                            (previousBottom >= currentTop && previousBottom <= currentBottom)
                                    || (previousTop >= currentTop && previousTop <= currentBottom);
                    boolean currentInsidePrevious =
                            (currentBottom >= previousTop && currentBottom <= previousBottom)
                                    || (currentTop >= previousTop && currentTop <= previousBottom);
                    double threshold = 1.5;
                    boolean heightRatioLessThanThreshold = largerHeight / smallerHeight < threshold;
                    Log.d("combined", "prev: " + previousTop + ", " + previousBottom);
                    Log.d("combined", "curr: " + currentTop + ", " + currentBottom);
                    Log.d("combined", j+ ", " + i + ", " + (i - 1) + ": (" +
                            previousInsideCurrent + " || " +
                            currentInsidePrevious + ") && " +
                            heightRatioLessThanThreshold
                    );

                    if ((
                            (previousInsideCurrent || currentInsidePrevious)
                                    && heightRatioLessThanThreshold
                    )) {
                        Mat previousLabel = new Mat();
                        Core.compare(labels, new Scalar(i - 1), previousLabel, Core.CMP_EQ);
                        Core.bitwise_or(currentLabel, previousLabel, currentLabel);
                        combinedIndexes.add(i - 1);
                        Log.d("combined", "added " + (i - 1) + Arrays.toString(combinedIndexes.toArray()));
//                        combinedIndexes.add(i);
                    }
                }

                Core.multiply(currentLabel, new Scalar(255), currentLabel);

//                int x = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
//                int y = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
//                int w = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
//                int h = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
//
//                if (w == 1 || h == 1) {
//                    continue;
//                }
//
//                Mat currentComponent = new Mat(currentLabel, new Rect(x, y, w, h));

                Mat currentComponent = currentLabel.clone();
                currentLabel.release();
                Bitmap currentComponentBitmap = Bitmap.createBitmap(
                        currentComponent.width(),
                        currentComponent.height(),
                        Bitmap.Config.RGB_565
                );
//                Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_ELLIPSE, new Size(2, 2));
//                Imgproc.dilate(currentComponent, currentComponent, kernel);
                Core.bitwise_not(currentComponent, currentComponent);
                Utils.matToBitmap(currentComponent, currentComponentBitmap);
//                vertical.add(currentComponentBitmap);
                verticalMap.put(i, currentComponentBitmap);
//                int x = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
//                int y = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
//                int w = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
//                int h = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
//
//                if(stats.get(i, CC_STAT_AREA)[0]<100 ||
//                        stats.get(i, CC_STAT_AREA)[0]>(200*200)){
//                    continue;
//                }
//
//                Point pt1 = new Point(x,y);
//                Point pt2 = new Point(x+w, y+h);
//
//                Imgproc.rectangle(dst, pt1, pt2, new Scalar(255, 0, 0), 2);
//
//                MyUtils.MatToImageView(this, "labeling : "+value, tv1, dst, R.id.imageView1);
            }
            Log.d("combined", "verticalMap.keySet() before: " + verticalMap.keySet());

            for (int combinedIndex : combinedIndexes) {
                verticalMap.remove(combinedIndex);
            }
            Log.d("combined", "verticalMap.keySet() after: " + verticalMap.keySet());
            vertical = new ArrayList<>();
            for (Bitmap v : verticalMap.values()) {
                List<Bitmap> segmented = segmentHorizontally(v);

                if (segmented.size() > 1) {
                    vertical.addAll(segmented);
                } else {
                    vertical.add(v);
                }
            }

//            for (int i = 0; i < vertical.size() - 1; i++) {
//                for (int j = i + 1; j < vertical.size(); j++) {
////                    int x = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
////                    int y = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
//                    int iWidth = (int)stats.get(i + 1, Imgproc.CC_STAT_WIDTH)[0];
//                    int iHeight = (int)stats.get(i + 1, Imgproc.CC_STAT_HEIGHT)[0];
//                    int jWidth = (int)stats.get(j + 1, Imgproc.CC_STAT_WIDTH)[0];
//                    int jHeight = (int)stats.get(j + 1, Imgproc.CC_STAT_HEIGHT)[0];
//
//                    if (Math.max(jHeight, iHeight) / Math.min(jHeight, iHeight) > )
//                }
//
//            }
            result.add(vertical);
            j++;
        }

        return result;
    }

//    @NonNull
//    public static List<List<Bitmap>> segmentCC(@NonNull Bitmap source) {
//        List<List<Bitmap>> result = new ArrayList<>();
//        Bitmap bitmap = source.copy(source.getConfig(), true);
//
////        for (Bitmap bitmap : source) {
//            Bitmap inputBitmap = bitmap.copy(bitmap.getConfig(), true);
//            Mat inputMat = new Mat(inputBitmap.getHeight(), inputBitmap.getWidth(), CvType.CV_8U);
//            Mat labels = new Mat();
//            Mat stats = new Mat();
//            Mat centroids = new Mat();
//            int connectivity = 8;
//            int ltype = CvType.CV_32S;
//            int componentsCount;
//            Map<Integer, Bitmap> verticalMap = new HashMap<>();
//            List<Bitmap> vertical;
//
//            Utils.bitmapToMat(inputBitmap, inputMat);
////            Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
//            inputMat = PreprocessingUtil.binarize(inputMat, false);
//
//            componentsCount = Imgproc.connectedComponentsWithStats(
//                    inputMat,
//                    labels,
//                    stats,
//                    centroids,
//                    connectivity,
//                    ltype
//            );
//            List<Integer> combinedIndexes = new ArrayList<>();
//
//            for (int i = 1; i < componentsCount; i++) {
//                Mat currentLabel = new Mat();
//                Core.compare(labels, new Scalar(i), currentLabel, Core.CMP_EQ);
//                int currentLeft = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
//                int currentTop = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
//                int currentWidth = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
//                int currentHeight = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
//                int currentRight = currentLeft + currentWidth;
//                int currentBottom = currentTop + currentHeight;
//                Rect roi = new Rect(currentLeft, currentTop, currentWidth, currentHeight);
//
//                if (i > 1 && !combinedIndexes.contains(i - 1)) {
////                    double currentWidth = stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
////                    double previousWidth = stats.get(i - 1, Imgproc.CC_STAT_WIDTH)[0];
////                    double largerWidth = Math.max(currentWidth, previousWidth);
////                    double smallerWidth = Math.min(currentWidth, previousWidth);
////                    boolean widthRatioLessThanOneHalf = largerWidth / smallerWidth < 1.5;
//
//                    int previousLeft = (int)stats.get(i - 1, Imgproc.CC_STAT_LEFT)[0];
//                    int previousTop = (int)stats.get(i - 1, Imgproc.CC_STAT_TOP)[0];
//                    int previousWidth = (int)stats.get(i - 1, Imgproc.CC_STAT_WIDTH)[0];
//                    int previousHeight = (int)stats.get(i - 1, Imgproc.CC_STAT_HEIGHT)[0];
//                    int previousRight = previousLeft + previousWidth;
//                    int previousBottom = previousTop + previousHeight;
//                    double largerHeight = Math.max(currentHeight, previousHeight);
//                    double smallerHeight = Math.min(currentHeight, previousHeight);
//                    double largerWidth = Math.max(currentWidth, previousWidth);
//                    double smallerWidth = Math.min(currentWidth, previousWidth);
//                    boolean prevOverlapCurr =
//                            (previousBottom > currentTop && previousBottom < currentBottom)
//                                    || (previousTop > currentTop && previousTop < currentBottom)
//                                    || (previousLeft <  );
//                    boolean currentInsidePrevious =
//                            (currentBottom > previousTop && currentBottom < previousBottom)
//                                    || (currentTop > previousTop && currentTop < previousBottom);
//                    double threshold = 1.5;
//                    boolean heightRatioLessThanThreshold = largerHeight / smallerHeight < threshold;
//
//                    if ((previousInsideCurrent || currentInsidePrevious)
//                            && heightRatioLessThanThreshold) {
//                        Mat previousLabel = new Mat();
//                        Core.compare(labels, new Scalar(i - 1), previousLabel, Core.CMP_EQ);
//                        Core.bitwise_or(currentLabel, previousLabel, currentLabel);
//                        combinedIndexes.add(i - 1);
////                        combinedIndexes.add(i);
//                    }
//                }
//
//                Core.multiply(currentLabel, new Scalar(255), currentLabel);
//
////                int x = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
////                int y = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
////                int w = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
////                int h = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
////
////                Mat currentComponent = new Mat(currentLabel, new Rect(x, y, w, h));
//                Mat currentComponent = currentLabel.clone();
//                currentLabel.release();
//                Bitmap currentComponentBitmap = Bitmap.createBitmap(
//                        currentComponent.width(),
//                        currentComponent.height(),
//                        Bitmap.Config.RGB_565
//                );
//                Core.bitwise_not(currentComponent, currentComponent);
//                Utils.matToBitmap(currentComponent, currentComponentBitmap);
////                vertical.add(currentComponentBitmap);
//                verticalMap.put(i, currentComponentBitmap);
////                int x = (int)stats.get(i, Imgproc.CC_STAT_LEFT)[0];
////                int y = (int)stats.get(i, Imgproc.CC_STAT_TOP)[0];
////                int w = (int)stats.get(i, Imgproc.CC_STAT_WIDTH)[0];
////                int h = (int)stats.get(i, Imgproc.CC_STAT_HEIGHT)[0];
////
////                if(stats.get(i, CC_STAT_AREA)[0]<100 ||
////                        stats.get(i, CC_STAT_AREA)[0]>(200*200)){
////                    continue;
////                }
////
////                Point pt1 = new Point(x,y);
////                Point pt2 = new Point(x+w, y+h);
////
////                Imgproc.rectangle(dst, pt1, pt2, new Scalar(255, 0, 0), 2);
////
////                MyUtils.MatToImageView(this, "labeling : "+value, tv1, dst, R.id.imageView1);
//            }
//
//            for (int combinedIndex : combinedIndexes) {
//                verticalMap.remove(combinedIndex);
//            }
//            vertical = new ArrayList<>(verticalMap.values());
//            result.add(vertical);
////        }
//
//        return result;
//    }
}
