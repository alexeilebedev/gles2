package com.alexeilebedev.gles2;

import android.content.Context;
import android.opengl.GLES20;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// Static GLES utilities
public class Glutil {
    public static int compileShaderX(int type, String text) {
        int ret = GLES20.glCreateShader(type);
        if (ret != 0) {
            GLES20.glShaderSource(ret, text);
            GLES20.glCompileShader(ret);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(ret, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0){
                GLES20.glDeleteShader(ret);
                throw new RuntimeException("Error creating program linkStatus:"+compileStatus.toString());
            }
        }
        return ret;
    }

    public static int compileProgX(int vshader, int fshader) {
        int prog = GLES20.glCreateProgram();
        if (prog != 0) {
            GLES20.glAttachShader(prog, vshader);
            GLES20.glAttachShader(prog, fshader);
            GLES20.glLinkProgram(prog);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, linkStatus, 0);
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(prog);
                throw new RuntimeException("Error creating program linkStatus:"+linkStatus.toString());
            }
        }
        return prog;
    }

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

    public static FloatBuffer toFloatBuffer(float[] ary, FloatBuffer prev) {
        FloatBuffer ret=prev;
        if (ret == null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(ary.length * 4);
            bb.order(ByteOrder.nativeOrder());
            ret = bb.asFloatBuffer();
        }
        ret.clear();
        ret.put(ary);
        ret.position(0);
        return ret;
    }

    public static ShortBuffer toShortBuffer(short[] ary, ShortBuffer prev) {
        ShortBuffer ret = prev;
        if (ret == null) {
            ByteBuffer bb = ByteBuffer.allocateDirect(ary.length * 2);
            bb.order(ByteOrder.nativeOrder());
            ret = bb.asShortBuffer();
        }
        ret.clear();
        ret.put(ary);
        ret.position(0);
        return ret;
    }
}
