//package com.example.javanesescriptrecognizer.utils;
//
//import android.graphics.Bitmap;
//import android.util.Log;
//
//import androidx.annotation.NonNull;
//
//import com.example.javanesescriptrecognizer.Constants;
//import com.example.javanesescriptrecognizer.data.models.AksaraClasses;
//import com.example.javanesescriptrecognizer.data.models.AksaraClasses.JavaneseScript;
//import com.example.javanesescriptrecognizer.data.models.ProcessResult;
//
//import org.pytorch.IValue;
//import org.pytorch.Tensor;
//import org.pytorch.torchvision.TensorImageUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ClassificationUtil {
//    private static final String TAG = "CLASSIFICATION";
//    public static final float[] NORM_MEAN_RGB = new float[] {0.5f, 0.5f, 0.5f};
//    public static final float[] NORM_STD_RGB = new float[] {0.5f, 0.5f, 0.5f};
//
//    private float softmax(float input, @NonNull float[] neuronValues) {
//        float total = 0.0f;
//
//        for (float neuronValue : neuronValues) {
//            float exp = (float) Math.exp(neuronValue);
//            total += exp;
//        }
//        return (float) Math.exp(input) / total;
//    }
//
//    @NonNull
//    public static List<List<JavaneseScript>> classify(@NonNull List<List<Bitmap>> images) {
//        List<List<AksaraClasses.JavaneseScript>> aksara = new ArrayList<>();
//
//        for (int k = 0; k < images.size(); k++ ) {
//            List<Bitmap> segmented = images.get(k);
//            ArrayList<JavaneseScript> verticalAksara = new ArrayList<>();
//            StringBuilder resultStrings = new StringBuilder();
//            ProcessResult segRes = null;
//
//            for (int i = 0; i < segmented.size(); i++) {
//                StringBuilder resultString = new StringBuilder();
//                Bitmap segmentedBitmap = Bitmap.createScaledBitmap(
//                        segmented.get(i),
//                        224,
//                        224,
//                        true
//                );
////                Bitmap segmentedBitmap = segmented.get(i);
//
//                //Input Tensor
//                final Tensor input = TensorImageUtils
//                        .bitmapToFloat32Tensor(segmentedBitmap, NORM_MEAN_RGB, NORM_STD_RGB);
//
//                //Calling the forward of the model to run our input
//                assert module != null;
//                final Tensor output = module.forward(IValue.from(input)).toTensor();
//
//                final float[] scores = output.getDataAsFloatArray();
//
//                // Fetch the index of the value with maximum score
//                float maxScore = -Float.MAX_VALUE;
//                int maxScoreIndex = -1;
//                for (int j = 0; j < scores.length; j++) {
////                Log.d(TAG, "Score: \n" + Constants.JAVANESE_SCRIPT_CLASSES[i]
////                        + ": "
////                        + scores[i]);
//                    if (scores[j] > maxScore) {
//                        maxScore = scores[j];
//                        maxScoreIndex = j;
//                    }
//                }
//
//                float maxScoreInPercentage = softmax(maxScore, scores) * 100;
//                for (int j = 0; j < scores.length; j++) {
//                    float currentScore = softmax(scores[j], scores) * 100;
//
//                    Log.d(TAG, "Softmax: \n" + Constants.JAVANESE_SCRIPT_CLASSES[j]
//                            + ": "
//                            + currentScore);
//                }
//
//                //Fetching the name from the list based on the index
//                String recognizedClass = Constants.JAVANESE_SCRIPT_CLASSES[maxScoreIndex];
//                String percentage = Float.toString(maxScoreInPercentage);
//                percentage = percentage.contains(".")
//                        ? percentage
//                        .replaceAll("0*$", "")
//                        .replaceAll("\\.$", "")
//                        : percentage;
//                resultString
//                        .append(recognizedClass)
//                        .append(" (")
//                        .append(percentage)
//                        .append("%), ");
//                resultStrings.append(resultString);
//                verticalAksara.add(AksaraUtil.mapClassStringToType(recognizedClass));
//                segRes = new ProcessResult(
//                        index,
//                        segmented.get(i),
//                        resultString.toString()
//                );
//                segmentationResults.add(segRes);
//                index++;
//            }
//            aksara.add(verticalAksara);
//            stringBuilder
//                    .append("[ ")
//                    .append(resultStrings)
//                    .append("], ");
//        }
//        return aksara;
//    }
//}
