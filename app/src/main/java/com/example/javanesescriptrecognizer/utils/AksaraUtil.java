package com.example.javanesescriptrecognizer.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.javanesescriptrecognizer.Constants;
import com.example.javanesescriptrecognizer.data.models.AksaraClasses.*;

public class AksaraUtil {
    @Nullable
    public static JavaneseScript mapClassStringToType(@NonNull String string) {
        for (int i = 0; i < Constants.JAVANESE_SCRIPT_CLASSES.length; i++) {
            if (Constants.JAVANESE_SCRIPT_CLASSES[i].toLowerCase().contains(string.toLowerCase())) {
                return Constants.JAVANESE_SCRIPT_TYPES[i];
            }
        }
        return null;
    }
}
