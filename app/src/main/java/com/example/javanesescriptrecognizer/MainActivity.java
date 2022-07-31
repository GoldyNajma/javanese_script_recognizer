package com.example.javanesescriptrecognizer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.javanesescriptrecognizer.data.models.AksaraClass;
import com.example.javanesescriptrecognizer.data.models.ProcessResult;
import com.example.javanesescriptrecognizer.data.models.RecognitionResult;
import com.example.javanesescriptrecognizer.data.models.Result;
import com.example.javanesescriptrecognizer.data.models.ResultCallback;
import com.example.javanesescriptrecognizer.modules.details.DetailsActivity;
import com.example.javanesescriptrecognizer.utils.PostprocessingUtil;
import com.example.javanesescriptrecognizer.utils.PreprocessingUtil;
import com.example.javanesescriptrecognizer.utils.SegmentationUtil;
import com.example.javanesescriptrecognizer.utils.AksaraUtil;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;
import org.pytorch.torchvision.TensorImageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private RecognitionResult recognitionResult;
    private ImageView ivImage;
    private TextView tvReading;
    private TextView tvResult;
    private Button btLoad;
    private Button btResNeXt;
    private Button btVGG;
    private Button btDetail;
    private TextToSpeech textToSpeech;
    private Toast toastMessage;
    private ProgressBar pbLoading;
    private Module resnext = null;
    private Module vgg = null;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e(TAG, "Unable to load OpenCV!");
        } else {
            Log.d(TAG, "OpenCV loaded Successfully!");
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                Intent data = result.getData();
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                ivImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));

                ivImage.setImageURI(null);
                ivImage.setImageURI(selectedImage);
                tvResult.setText("");
                tvReading.setText("");
                recognitionResult = null;
            }
        }
    );

    @NonNull
    public static String fetchModelFile(@NonNull Context context, String modelName) throws IOException {
        File file = new File(context.getFilesDir(), modelName);

        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(modelName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;

                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }

    private float softmax(float input, @NonNull float[] neuronValues) {
        float total = 0.0f;
        for (float neuronValue : neuronValues) {
            float exp = (float) Math.exp(neuronValue);
            total += exp;
        }
        return (float) Math.exp(input) / total;
    }

    // https://stackoverflow.com/a/14532121
    @NonNull
    static Bitmap removeTransparency(@NonNull Bitmap image) {
        Bitmap imageWithBG = Bitmap
                .createBitmap(image.getWidth(), image.getHeight(),image.getConfig());
        imageWithBG.eraseColor(Color.WHITE);
        Canvas canvas = new Canvas(imageWithBG);
        canvas.drawBitmap(image, 0f, 0f, null);
//        image.recycle();
        return imageWithBG;
    }

    private final BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            if (status == LoaderCallbackInterface.SUCCESS) {
                Log.i(TAG, "OpenCV loaded successfully");
            } else {
                super.onManagerConnected(status);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION,this, mLoaderCallback);
        } else {
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    private void showMessage(String message) {
        if (toastMessage != null) {
            toastMessage.cancel();
        }
        toastMessage = Toast.makeText(this, message, Toast.LENGTH_LONG);
        toastMessage.show();
    }

    @NonNull
    private Result<RecognitionResult> doRecognition(String model) {
        if (ivImage.getDrawable() == null) {
            showMessage("No image loaded.");
            return new Result.Error<>(new Exception());
        }
        Bitmap bitmap = null;

        ArrayList<ProcessResult> segmentationResults = new ArrayList<>();
        ArrayList<ProcessResult> preprocessingResults = new ArrayList<>();
        ArrayList<ProcessResult> resizeResult = new ArrayList<>();

        List<Bitmap> horizontallySegmented = new ArrayList<>();
        List<List<Bitmap>> verticallySegmented = new ArrayList<>();

        long startTime = System.nanoTime();
        try {
            bitmap = ((BitmapDrawable) ivImage.getDrawable()).getBitmap();

            bitmap = removeTransparency(bitmap);


            preprocessingResults.addAll(PreprocessingUtil.preprocess(bitmap));
            horizontallySegmented = SegmentationUtil.segmentHorizontally(
                    preprocessingResults.get(preprocessingResults.size() - 1).getImage()
            );
            verticallySegmented = SegmentationUtil.segmentVerticallyCC(horizontallySegmented);
//                verticallySegmented = SegmentationUtil.segmentVertically(horizontallySegmented);

            if (
                    (model.equalsIgnoreCase(Constants.MODEL_RESNEXT) && resnext == null)
                    || (model.equalsIgnoreCase(Constants.MODEL_VGG) && vgg == null)
            ) {
                String modelPath = fetchModelFile(
                        MainActivity.this,
                        model
                );
                Log.d(TAG, modelPath);
                if (model.equalsIgnoreCase(Constants.MODEL_RESNEXT)) {
                    resnext = Module.load(modelPath);
                } else {
                    vgg = Module.load(modelPath);
                }
            }
        } catch (IOException e) {
            showMessage("An error occured. " + e.getMessage());
            return new Result.Error<>(e);
        }

        StringBuilder stringBuilder = new StringBuilder("[ \n");
        List<List<AksaraClass.JavaneseScript>> aksara = new ArrayList<>();
        float[] normMeanRGB = new float[] {0.5f, 0.5f, 0.5f};
        float[] normStdRGB = new float[] {0.5f, 0.5f, 0.5f};

        int index = 0;

        for (int k = 0; k < verticallySegmented.size(); k++ ) {
            List<Bitmap> segmented = verticallySegmented.get(k);
            ArrayList<AksaraClass.JavaneseScript> verticalAksara = new ArrayList<>();
            StringBuilder resultStrings = new StringBuilder();
            ProcessResult segRes = null;

            for (int i = 0; i < segmented.size(); i++) {
                StringBuilder resultString = new StringBuilder();
                Bitmap segmentedBitmap = Bitmap.createScaledBitmap(
                        segmented.get(i),
                        224,
                        224,
                        true
                );

                final Tensor input = TensorImageUtils
                        .bitmapToFloat32Tensor(segmentedBitmap, normMeanRGB, normStdRGB);

                assert resnext != null || vgg != null;
                final Tensor output;
                if ((model.equalsIgnoreCase(Constants.MODEL_RESNEXT))) {
                    output = resnext.forward(IValue.from(input)).toTensor();
                } else {
                    output = vgg.forward(IValue.from(input)).toTensor();
                }

                final float[] scores = output.getDataAsFloatArray();

                float maxScore = -Float.MAX_VALUE;
                int maxScoreIndex = -1;
                for (int j = 0; j < scores.length; j++) {
                    if (scores[j] > maxScore) {
                        maxScore = scores[j];
                        maxScoreIndex = j;
                    }
                }

                float maxScoreInPercentage = softmax(maxScore, scores) * 100;
                for (int j = 0; j < scores.length; j++) {
                    float currentScore = softmax(scores[j], scores) * 100;

                    Log.d(TAG, "Softmax: \n" + Constants.JAVANESE_SCRIPT_STRINGS[j]
                            + ": "
                            + currentScore);
                }

                String recognizedClass = Constants.JAVANESE_SCRIPT_STRINGS[maxScoreIndex];
                String percentage = Float.toString(maxScoreInPercentage);
                percentage = percentage.contains(".")
                        ? percentage
                        .replaceAll("0*$", "")
                        .replaceAll("\\.$", "")
                        : percentage;
                resultString
                        .append(recognizedClass)
                        .append(" (")
                        .append(percentage)
                        .append("%), ");
                resultStrings.append(resultString);
                AksaraClass.JavaneseScript class_ =
                        AksaraUtil.mapStringToClass(recognizedClass);
                verticalAksara.add(class_);
                resizeResult.add(new ProcessResult(i, segmentedBitmap, resultString.toString()));
                segRes = new ProcessResult(
                        index,
                        segmented.get(i),
                        resultString.toString(),
                        "\t" + class_.getUnicode()
                );
                segmentationResults.add(segRes);
                index++;
            }
            aksara.add(verticalAksara);
            stringBuilder
                    .append("[ ")
                    .append(resultStrings)
                    .append("], ");
        }
        String unicodes = PostprocessingUtil.getUnicodesString(aksara);
        String reading = PostprocessingUtil.arrange(aksara);
        long endTime = System.nanoTime();
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime);
        String modelName = model.equalsIgnoreCase(Constants.MODEL_RESNEXT)
                ? "ResNeXt-50"
                : "VGG-16";
        return new Result.Success<>(new RecognitionResult(
                modelName,
                preprocessingResults,
                segmentationResults,
                resizeResult,
                duration,
                reading,
                unicodes
        ));
    }

    public void requestRecognition(
            String model,
            @NonNull ResultCallback<RecognitionResult> resultCallback
    ) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        resultCallback.onLoading();

        executor.execute(() -> {
            Result<RecognitionResult> result = doRecognition(model);

            handler.post(() -> {
                resultCallback.onComplete(result);
            });
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivImage = findViewById(R.id.main_iv_image);
        tvReading = findViewById(R.id.main_tv_reading);
        tvResult = findViewById(R.id.main_tv_result);
        btLoad = findViewById(R.id.main_bt_load);
        btVGG = findViewById(R.id.main_bt_vgg);
        btResNeXt = findViewById(R.id.main_bt_resnext);
        btDetail = findViewById(R.id.main_bt_detail);
        pbLoading = findViewById(R.id.main_pb_loading);

        try {
            vgg = Module.load(fetchModelFile(this, Constants.MODEL_VGG));
            resnext = Module.load(fetchModelFile(this, Constants.MODEL_RESNEXT));
        } catch (Exception e) {
            showMessage("Failed to load model.");
        }
        showMessage("Models loaded");

        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    synchronized (this) {
                        Locale locale = new Locale("in");
                        int result = textToSpeech.setLanguage(locale);

                        if (result == TextToSpeech.LANG_MISSING_DATA
                                || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                            locale = Locale.US;
                            textToSpeech.setLanguage(Locale.US);
                        }

                    }
                } else {
                    showMessage("Text to speech initizalization failed.");
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]  {
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            requestPermissions(permissions, 1);
        }

        btLoad.setOnClickListener(arg0 -> {
            Intent pickImageIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            );
//            pickImageIntent.setDataAndType("image/*");
//            pickImageIntent.putExtra("crop", "true");
//            pickImageIntent.putExtra("outputX", 200);
//            pickImageIntent.putExtra("outputY", 200);
//            pickImageIntent.putExtra("aspectX", 1);
//            pickImageIntent.putExtra("aspectY", 1);
//            pickImageIntent.putExtra("scale", true);
//            pickImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriWhereToStore);
//            pickImageIntent.putExtra("outputFormat",
//
//                    Bitmap.CompressFormat.JPEG.toString());

            activityResultLauncher.launch(pickImageIntent);
        });

        ResultCallback<RecognitionResult> callback = new ResultCallback<RecognitionResult>() {
            @Override
            public void onComplete(Result<RecognitionResult> result) {
                pbLoading.setVisibility(View.GONE);
                btLoad.setEnabled(true);
                btResNeXt.setEnabled(true);
                btVGG.setEnabled(true);
                btDetail.setEnabled(true);
                if (result instanceof Result.Success) {
                    recognitionResult = ((Result.Success<RecognitionResult>) result).data;
                    tvReading.setText(recognitionResult.getReading());
                    tvResult.setText(recognitionResult.getUnicodes());
                    tvResult.append("\n(");
                    tvResult.append(Long.toString(recognitionResult.getDuration()));
                    tvResult.append("ms)");
                    textToSpeech.speak(
                            recognitionResult.getReading(),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            this.hashCode() + ""
                    );
                    showMessage(recognitionResult.getReading());
                } else {
                    showMessage(
                            ((Result.Error<RecognitionResult>) result).exception.getMessage()
                    );
                }
            }

            @Override
            public void onLoading() {
                pbLoading.setVisibility(View.VISIBLE);
                btLoad.setEnabled(false);
                btResNeXt.setEnabled(false);
                btVGG.setEnabled(false);
                btDetail.setEnabled(false);
            }
        };


        btVGG.setOnClickListener(arg0 -> {
            if (ivImage.getDrawable() == null) {
                showMessage("No image loaded.");
                return;
            }
            tvReading.setText("");
            tvResult.setText("");
            requestRecognition(Constants.MODEL_VGG, callback);
        });

        btResNeXt.setOnClickListener(arg0 -> {
            if (ivImage.getDrawable() == null) {
                showMessage("No image loaded.");
                return;
            }
            tvReading.setText("");
            tvResult.setText("");
            requestRecognition(Constants.MODEL_RESNEXT, callback);
        });

        btDetail.setOnClickListener(arg0 -> {
            redirectToDetail();
        });
    }

    private void redirectToDetail() {
        if (recognitionResult != null) {
            Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtra("RecognitionResultExtra", recognitionResult);
            startActivity(intent);
        } else {
            showMessage("No recognition result available.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}