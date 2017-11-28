package com.alexeilebedev.gles2;

import android.opengl.GLES20;

// standard GLES utilities class
public class Glutil {
    public static int compileShaderX(int type, String text) {
        int ret = GLES20.glCreateShader(type);
        if (ret != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(ret, text);
            GLES20.glCompileShader(ret);
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(ret, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            if (compileStatus[0] == 0){
                GLES20.glDeleteShader(ret);
                ret = 0;
            }
        }
        if (ret == 0)  {
            throw new RuntimeException("Error creating shader.");
        }
        return ret;
    }

    public static int compileProg(int vshader, int fshader) {
        int prog = GLES20.glCreateProgram();
        if (prog != 0) {
            GLES20.glAttachShader(prog, vshader);
            GLES20.glAttachShader(prog, fshader);
            GLES20.glLinkProgram(prog);
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(prog, GLES20.GL_LINK_STATUS, linkStatus, 0);
            // If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                GLES20.glDeleteProgram(prog);
                prog = 0;
            }
        }
        if (prog == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return prog;
    }
}
