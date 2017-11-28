package com.alexeilebedev.gles2;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class Assets {
    public static String loadAsset(Context ctx, String filename) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append("\n");
            }
        } catch (Exception e) {
        }
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception e){
        }
        return builder.toString();
    }
}
