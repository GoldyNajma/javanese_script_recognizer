package com.example.javanesescriptrecognizer.utils;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javanesescriptrecognizer.data.models.ProcessResult;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.ximgproc.Ximgproc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PreprocessingUtil {
    final private static String TAG = "Preprocessing";

    public static Bitmap binarize(@NonNull Bitmap source) {
        Bitmap inputBitmap = source.copy(source.getConfig(), true);
        final int height = inputBitmap.getHeight();
        final int width = inputBitmap.getWidth();
//        Mat inputMat = new Mat();
//        Mat outputMat = new Mat();
        Mat inputMat = new Mat(height, width, CvType.CV_8U);
        Mat outputMat = new Mat(height, width, CvType.CV_8U);
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        Log.d(TAG, " Binarization Start...");

        Utils.bitmapToMat(inputBitmap, inputMat);
        Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(
                inputMat,
                outputMat,
          0,
          255,
          Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU
        );
//        Imgproc.adaptiveThreshold(
//                inputMat,
//                outputMat,
//                255,
//                Imgproc.ADAPTIVE_THRESH_MEAN_C,
//                Imgproc.THRESH_BINARY,
//                81,
//                2
//        );
//        Core.bitwise_not(outputMat, outputMat);
//        ArrayList<Integer> nonZeros = new ArrayList<>();
////        ArrayList<Mat> mats = new ArrayList<>();
//        for (int i = 0; i < outputMat.cols(); i++) {
//            nonZeros.add(Core.countNonZero(outputMat.col(i)));
//        }
//        int colStart = 0;
//        int colEnd = -1;
//        boolean isOnLastIndex = false;
//
//        do {
//            for (int j = colEnd + 1; j < nonZeros.size(); j++) {
//                if (j == nonZeros.size() - 1) {
//                    isOnLastIndex = true;
//                    break;
//                }
//                if (nonZeros.get(j) > 0) {
//                    colStart = j;
//                    break;
//                }
//            }
//            for (int j = colStart; j < nonZeros.size(); j++) {
//                if (j == nonZeros.size() - 1) {
//                    isOnLastIndex = true;
//                    break;
//                }
//                if (nonZeros.get(j) == 0) {
//                    colEnd = j - 1;
//                    break;
//                }
//            }
//            Log.d(TAG, "------------------------------");
//            Log.d(TAG, "colStart : " + colStart);
//            Log.d(TAG, "colEnd   : " + colEnd);
//            Mat newOutputMat = outputMat.colRange(new Range(colStart, colEnd));
//            Bitmap newOutputBitmap = Bitmap.createBitmap(newOutputMat.width(), newOutputMat.height(), Bitmap.Config.RGB_565);
//            Core.bitwise_not(newOutputMat, newOutputMat);
//            Utils.matToBitmap(newOutputMat, newOutputBitmap);
//            IOUtil.saveImage(newOutputBitmap, "Segmented_" + colStart + "_" + colEnd);
//        } while (!isOnLastIndex);

//        Photo.fastNlMeansDenoising(outputMat, outputMat);
//        Utils.matToBitmap(outputMat, outputBitmap);
//        Core.bitwise_not(outputMat, outputMat);
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size (3,3));
//        Imgproc.morphologyEx(thresholded, outputMat, Imgproc.MORPH_CLOSE, kernel);

        Utils.matToBitmap(outputMat, outputBitmap);

        Log.d(TAG, " Binarization Done...");
//        IOUtil.saveImage(outputBitmap, "Binarized");

        return outputBitmap;
    }

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

//    @NonNull
//    private static Mat skeleton(@NonNull Mat _img)
//    {
//        boolean done = false;
//        Mat img = _img.clone();
////        CvUtilsFX.showImage(img, "tresh_1");
//
//        Mat imgGray = new Mat();
//        Imgproc.cvtColor(img, imgGray, Imgproc.COLOR_BGR2GRAY);
//
//        Mat tresh = new Mat();
//        Imgproc.threshold(imgGray, tresh, 100, 255, Imgproc.THRESH_BINARY_INV | Imgproc.THRESH_OTSU);
////        CvUtilsFX.showImage(tresh, "tresh_1");
//
//        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
//        Mat eroded = new Mat();
//        Mat temp = new Mat();
//        Mat skel = new Mat (tresh.rows(), tresh.cols(), CvType.CV_8UC1, new Scalar (0));
//
//        int size = _img.cols() * _img.rows();
//        int zeros = 0;
//
//        while(!done)
//        {
//            Imgproc.erode(tresh, eroded, element);
//            Imgproc.dilate(eroded, temp, element);
//            Core.subtract(tresh, temp, temp);
//            Core.bitwise_or(skel, temp, skel);
//            eroded.copyTo(tresh);
//
//            zeros = size - Core.countNonZero(tresh);
//            Log.d(TAG, "zeros: " + zeros + " | size = " + size);
//            if(zeros == size)
//                done = true;
//        }
//        Core.bitwise_not(skel, skel);
//        return skel;
////        CvUtilsFX.showImage(skel, "Skeleton");
//    }

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


//    Mat deskew(Mat img){
//        Moments m = Imgproc.moments(img);
//        if(Math.abs(m.mu02) < 1e-2){
//            return img.clone();
//        }
//        double skew = m.mu11/m.mu02;
//
//        Mat warpMat = (Mat_<float>(2,3) << 1, skew, -0.5*SZ*skew, 0, 1, 0);
//        Mat imgOut = Mat.zeros(img.rows(), img.cols(), img.type());
//        Imgproc.warpAffine(
//                img,
//                imgOut,
//                warpMat,
//                imgOut.size(),
//                Imgproc.WARP_INVERSE_MAP | Imgproc.INTER_LINEAR
//        );
//
//        return imgOut;
//    }

//    public static double getSkewAngle(@NonNull Mat cvImage) {
//        Mat thresh = cvImage.clone();
//
////      Apply dilate to merge text into meaningful lines/paragraphs.
////      Use larger kernel on X axis to merge characters into single line, cancelling out any spaces.
////      But use smaller kernel on Y axis to separate between different blocks of text
//        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(30, 5));
//        Mat dilate = new Mat();
//        Imgproc.dilate(thresh, dilate, kernel, new Point(-1,-1), 5);
//
////      Find all contours
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//        Imgproc.findContours(dilate, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
//        Collections.sort(contours, (contour1, contour2) -> {
//            double contourArea1 = Imgproc.contourArea(contour1);
//            double contourArea2 = Imgproc.contourArea(contour2);
//
//            return Double.compare(contourArea1, contourArea2);
//        });
//
//        Collections.reverse(contours);
//
////      Find largest contour and surround in min area box
//        MatOfPoint2f largestContour = new MatOfPoint2f(contours.get(0).toArray());
//        RotatedRect minAreaRect = Imgproc.minAreaRect(largestContour);
//
////      Determine the angle. Convert it to the value that was originally used to obtain skewed image
//        double angle = minAreaRect.angle;
//        if (angle < -45) {
//            angle += 90;
//        }
//
//        return -1.0 * angle;
//    }
//
////  Rotate the image around its center
//    @NonNull
//    public static Mat rotateImage(@NonNull Mat cvImage, double angle) {
//        Mat newImage = cvImage.clone();
//
//        Size size = newImage.size();
//        Point center = new Point(size.width / 2.0, size.height / 2.0);
//        Mat M = Imgproc.getRotationMatrix2D(center, angle, 1.0);
//        Imgproc.warpAffine(newImage, newImage, M, size, Imgproc.INTER_CUBIC, Core.BORDER_REPLICATE);
//
//        return newImage;
//    }
//
////  Deskew image
//    @NonNull
//    public static Mat deskew(Mat cvImage) {
//        return rotateImage(cvImage, getSkewAngle(cvImage));
//    }

    @NonNull
    public static ArrayList<ProcessResult> preprocess(@NonNull Bitmap sourceBitmap) {
        Bitmap inputBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
        final int height = inputBitmap.getHeight();
        final int width = inputBitmap.getWidth();

        ArrayList<ProcessResult> results = new ArrayList<>();

//        Mat inputMat = new Mat();
//        Mat outputMat = new Mat();
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
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

//        output = deskew(output);
//
//        toWhiteBackgroundBlackForeground(output, output);
//        Bitmap deskewed = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//        Utils.matToBitmap(output, deskewed);
//        results.add(new ProcessResult(2, deskewed, "Deskewed"));
//        toBlackBackgroundWhiteForeground(output, output);

        Ximgproc.thinning(output, output, thinningType);

        int morphShape = Imgproc.MORPH_ELLIPSE;
        Mat kernel = Imgproc.getStructuringElement(morphShape, new Size(2, 2));
        Imgproc.dilate(output, output, kernel);
//        Imgproc.morphologyEx(output, output, Imgproc.MORPH_OPEN, kernel);

        toWhiteBackgroundBlackForeground(output, output);
        Bitmap thinned = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, thinned);
        results.add(new ProcessResult(3, thinned, "Thinning"));

//        Utils.matToBitmap(output, outputBitmap);

        return results;
//        return outputBitmap;
    }
}
