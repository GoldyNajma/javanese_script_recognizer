package com.example.javanesescriptrecognizer.utils;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ximgproc.Ximgproc;

import java.util.ArrayList;

public class PreprocessingUtil {
    final private static String TAG = "Preprocessing";

    public static void toBlackBackgroundWhiteForeground(@NonNull Mat src, @NonNull Mat dst) {
        int nonZeroCount = Core.countNonZero(src);
        int pixelCount = src.width() * src.height();

        if (nonZeroCount > pixelCount - nonZeroCount) {
            Core.bitwise_not(src, dst);
        }
    }

    public static void toWhiteBackgroundBlackForeground(@NonNull Mat src, @NonNull Mat dst) {
        toBlackBackgroundWhiteForeground(src, dst);
        Core.bitwise_not(dst, dst);
    }

    @NonNull
    public static Mat binarize(@NonNull Mat src, boolean whiteBackgroundBlackForeground) {
        int thresh = 0;
        int maxVal = 255;
        int thresholdType = Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU;
//        int thresholdType = Imgproc.THRESH_TRUNC + Imgproc.THRESH_OTSU;
        Mat output = new Mat(src.height(), src.width(), CvType.CV_8U);

        Imgproc.cvtColor(src, output, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(output, output, thresh, maxVal, thresholdType);
//        Imgproc.adaptiveThreshold(
//                output,
//                output,
//                255,
//                Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
//                Imgproc.THRESH_BINARY,
//                11,
//                2
//        );

        if (whiteBackgroundBlackForeground) {
            toWhiteBackgroundBlackForeground(output, output);
        } else {
            toBlackBackgroundWhiteForeground(output, output);
        }

        return output;
    }

    @NonNull
    public static Mat binarize(@NonNull Mat src) {
        return binarize(src, true);
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @NonNull
    public static ArrayList<ProcessResult> preprocess(@NonNull Bitmap sourceBitmap) {
        Bitmap inputBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
        final int height = inputBitmap.getHeight();
        final int width = inputBitmap.getWidth();

        ArrayList<ProcessResult> results = new ArrayList<>();

        Mat input = new Mat(height, width, CvType.CV_8UC3);
        Mat output = new Mat(height, width, CvType.CV_8UC3);
        int thinningType = Ximgproc.THINNING_ZHANGSUEN;

        results.add(new ProcessResult(0, inputBitmap, "Input Image"));

        Utils.bitmapToMat(inputBitmap, input);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGRA2BGR);
        Imgproc.GaussianBlur(input, output, new Size(5, 5),  0);
//        Imgproc.medianBlur(input, output, 5);

        Bitmap blurred = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, blurred);
        results.add(new ProcessResult(1, blurred, "Smoothening"));

//        Imgproc.bilateralFilter(input, output, 9, 75, 75);
        output = binarize(output, false);

        toWhiteBackgroundBlackForeground(output, output);
        Bitmap binarized = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, binarized);
        results.add(new ProcessResult(2, binarized, "Binarization"));
        toBlackBackgroundWhiteForeground(output, output);

        Ximgproc.thinning(output, output, thinningType);

        int morphShape = Imgproc.MORPH_ELLIPSE;
        Mat kernel = Imgproc.getStructuringElement(morphShape, new Size(2, 2));
        Imgproc.dilate(output, output, kernel);
//        Imgproc.morphologyEx(output, output, Imgproc.MORPH_OPEN, kernel);

        toWhiteBackgroundBlackForeground(output, output);
        Bitmap thinned = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, thinned);
        results.add(new ProcessResult(3, thinned, "Thinning"));

        return results;
    }
}
