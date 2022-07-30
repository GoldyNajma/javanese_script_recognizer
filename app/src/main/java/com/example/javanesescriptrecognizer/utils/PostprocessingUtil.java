package com.example.javanesescriptrecognizer.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.javanesescriptrecognizer.data.models.AksaraClasses.*;
import com.example.javanesescriptrecognizer.data.models.TypeEnums.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PostprocessingUtil {
    private static final String TAG = "PostProcessing";

    @NonNull
    public static List<List<String>> getUnicodes(@NonNull List<List<JavaneseScript>> classes) {
        List<List<String>> unicodes = new ArrayList<>();

        for (List<JavaneseScript> verticalClasses : classes) {
            List<String> verticalUnicodes = new ArrayList<>();

            for (JavaneseScript class_ : verticalClasses) {
                verticalUnicodes.add(class_.getUnicode());
            }
            unicodes.add(verticalUnicodes);
        }

        return unicodes;
    }

    @NonNull
    public static String getUnicodesString(List<List<JavaneseScript>> classes) {
        List<List<String>> unicodes = getUnicodes(classes);
        List<String> horizontalUnicodes = new ArrayList<>();

        for (List<String> verticalUnicodes : unicodes) {
//            horizontalUnicodes.add(" [ " + String.join(" + ", verticalUnicodes) + " ]");
            horizontalUnicodes.add(String.join(" + ", verticalUnicodes));
        }
//        return "[\n" + String.join(" + ", horizontalUnicodes) + "\n]";
        return String.join(" + ", horizontalUnicodes);
    }

    @NonNull
    public static String arrange(@NonNull List<List<JavaneseScript>> classes) {
//        List<List<JavaneseScript>> classes = new ArrayList<>();
//
//        for (JavaneseScript[] result : segmentationResults) {
//            classes.add(Arrays.asList(result));
//        }
//        List<List<JavaneseScript>> classes = Arrays.asList(
//            Arrays.asList(new WyanjanaAksara(WyanjanaType.ba), new Sandhangan(SandhanganType.suku)),
//            Collections.singletonList(new WyanjanaAksara(WyanjanaType.dha)),
//            Collections.singletonList(new Sandhangan(SandhanganType.taling)),
//            Arrays.asList(new WyanjanaAksara(WyanjanaType.la), new WyanjanaPasangan(WyanjanaType.da)),
//            Collections.singletonList(new Sandhangan(SandhanganType.tarung)),
//            Collections.singletonList(new WyanjanaAksara(WyanjanaType.la)),
//            Arrays.asList(new WyanjanaAksara(WyanjanaType.na), new Sandhangan(SandhanganType.pangkon))
//        );
        StringBuilder finalReading = new StringBuilder();
        List<JavaneseScript> talingListTemp = new ArrayList<>();

        Log.d(TAG, "array: ");
        Log.d(TAG, "[");
        for (List<JavaneseScript> horizontallySegmented : classes) {
            Log.d(TAG, "\t[ ");
            for (JavaneseScript verticallySegmented : horizontallySegmented) {
                Log.d(TAG, verticallySegmented + ", ");
            }
            Log.d(TAG, " ], ");
        }
        Log.d(TAG, "]");

         for (int i = 0; i < classes.size(); i++) {
             if (classes.get(i).size() > 1) {
                JavaneseScript firstSandhangan = null;
                JavaneseScript lastSandhangan = null;

                for (JavaneseScript class_ : classes.get(i)) {
                    if (class_.getTypeEnum() == SandhanganType.taling) {
                        firstSandhangan = class_;
                        break;
                    }
                }
                firstSandhangan = firstSandhangan == null ? classes.get(i).get(0) : firstSandhangan;

                classes.get(i).remove(firstSandhangan);
                classes.get(i).add(0, firstSandhangan);

                for (JavaneseScript class_ : classes.get(i)) {
                    Enum type = class_.getTypeEnum();
                    boolean isSandhanganPaten =
                            type == SandhanganType.layar
                            || type == SandhanganType.cecak
                            || type == SandhanganType.wigyan
                            || type == SandhanganType.pangkon
                            || type == SandhanganType.tarung;

                    if (isSandhanganPaten) {
                        lastSandhangan = class_;
                        break;
                    }
                }
                lastSandhangan = lastSandhangan == null
                        ? classes.get(i).get(classes.get(i).size() - 1)
                        : lastSandhangan;

                classes.get(i).remove(lastSandhangan);
                classes.get(i).add(lastSandhangan);
            }
        }

        for (int i = 0; i < classes.size(); i++) {
            List<JavaneseScript> aksaraList = new ArrayList<>();
            List<JavaneseScript> pasanganList = new ArrayList<>();
            List<JavaneseScript> sandhanganList = new ArrayList<>();

            for (int j = 0; j < classes.get(i).size(); j++) {
                JavaneseScript classIJ = classes.get(i).get(j);
                // print('\'${classes[i][j].reading}\' \t=> class = ${classes[i][j]}');
                switch (classIJ.getGroupType()) {
                    case aksara:
                        aksaraList.add(classIJ);
                        break;
                    case pasangan:
                        pasanganList.add(classIJ);
                        break;
                    case sandhangan:
                        if (classIJ.getTypeEnum() == SandhanganType.taling) {
                            talingListTemp.add(classIJ);
                        }
                        sandhanganList.add(classIJ);
                        break;
                    default:
                        break;
                }
            }

            for (JavaneseScript aksara : aksaraList) {
                finalReading.append(aksara.getReading());
            }
            Log.d(TAG, finalReading.toString());
//            print(finalReading);

            for (JavaneseScript pasangan : pasanganList) {
                if (finalReading.length() > 0) {
                    finalReading =
                            new StringBuilder(finalReading.substring(0, finalReading.length() - 1));
                }
                finalReading.append(pasangan.getReading());
            }

            Log.d(TAG, finalReading.toString());
//            print(finalReading);

            for (JavaneseScript sandhangan : sandhanganList) {
                if (sandhangan.getTypeEnum() == SandhanganType.taling) {
                    if (aksaraList.size() > 0 || pasanganList.size() > 0) {
                        int talingIndex = finalReading.length();
                        int talingIndexEnd = finalReading.length();
                        int aksaraReadingCount = 0;
                        int pasanganReadingCount = 0;

                        for (JavaneseScript aksara : aksaraList) {
                            aksaraReadingCount += aksara.getReading().length();
                        }
                        for (JavaneseScript pasangan : pasanganList) {
                            pasanganReadingCount--;
                            pasanganReadingCount += pasangan.getReading().length();
                        }

                        talingIndex -= (aksaraReadingCount + pasanganReadingCount);
                        talingIndexEnd = talingIndex;
                        finalReading = new StringBuilder(finalReading.replace(
                                talingIndex,
                                talingIndexEnd,
                                "" + talingListTemp.size()
                        ));
                    } else {
                        finalReading.append(talingListTemp.size());
                    }
                } else {
                    Enum sandhanganType = sandhangan.getTypeEnum();

                    if (sandhanganType == SandhanganType.tarung) {
                        if (talingListTemp.size() > 0) {
                            finalReading = new StringBuilder(
                                    finalReading
                                            .toString()
                                            .replaceAll("" + talingListTemp.size(), "")
                            );
                            talingListTemp.remove(talingListTemp.size() - 1);
                        }
                    }
                    if (
                            sandhanganType != SandhanganType.layar
                            && sandhanganType != SandhanganType.cecak
                            && sandhanganType != SandhanganType.wigyan
                            && finalReading.length() > 0
                    ) {
                        finalReading = new StringBuilder(
                                finalReading.substring(0, finalReading.length() - 1)
                        );
                    }
                    finalReading.append(sandhangan.getReading());
                }
            }
            Log.d(TAG, finalReading.toString());
//            print(finalReading);
        }

        for (int i = talingListTemp.size(); i > -1; i--) {
            int talingCountIndex = finalReading.indexOf("" + talingListTemp.size());
            int vocalSoundIndex = -1;

            for (int j = 0; j < finalReading.length(); j++) {
                char vowel = finalReading.charAt(j);

                if ("AEIOUaeiou".indexOf(vowel) != -1) {
                    vocalSoundIndex = finalReading.toString().indexOf(vowel, talingCountIndex);
                    if (vocalSoundIndex != -1) {
                        break;
                    }
                }
            }
            Log.d(TAG, "" + vocalSoundIndex + " | " + talingCountIndex);
            Log.d(TAG, "" + talingListTemp.size());
            if (vocalSoundIndex != -1 && !talingListTemp.isEmpty()) {
                finalReading = new StringBuilder(finalReading.replace(
                        vocalSoundIndex,
                        vocalSoundIndex + 1,
                        talingListTemp.get(0).getReading()
                ));
            }
            if (talingCountIndex != -1) {
                finalReading = new StringBuilder(finalReading.replace(
                        talingCountIndex,
                        talingCountIndex + 1,
                        ""
                ));
            }
            if (!talingListTemp.isEmpty()) {
                talingListTemp.remove(talingListTemp.size() - 1);
            }
            Log.d(TAG, finalReading.toString());
        }

        Log.d(TAG, "Final Reading: " + finalReading.toString());
//        print('');
//        print('Final reading: \'$finalReading\'');
        return finalReading.toString();
    }
}
