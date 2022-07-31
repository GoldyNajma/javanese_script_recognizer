package com.example.javanesescriptrecognizer.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.javanesescriptrecognizer.data.models.AksaraClass.*;
import com.example.javanesescriptrecognizer.Constants;

public class AksaraUtil {
    @Nullable
    public static JavaneseScript mapStringToClass(@NonNull String string) {
        for (int i = 0; i < Constants.JAVANESE_SCRIPT_STRINGS.length; i++) {
            if (Constants.JAVANESE_SCRIPT_STRINGS[i].toLowerCase().contains(string.toLowerCase())) {
                return Constants.JAVANESE_SCRIPT_CLASSES[i];
            }
        }
        return null;
    }
}
