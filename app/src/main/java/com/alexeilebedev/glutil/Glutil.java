package com.alexeilebedev.glutil;

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
    // compiler shader of type TYPE with text TEXT
    // and return its id.
    // on failure, exception is thrown
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

    // compile program with shaders VSHADER,FSHADER
    // and return program id. throw exception on error
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

    // load file named FILENAME using context CTX and return the resulting string
    public static String loadAsset(Context ctx, String filename, String want_section) {
        BufferedReader reader = null;
        String cur_section="";
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(ctx.getAssets().open(filename)));
            String line;
            while ((line = reader.readLine()) != null) {
                if (cur_section.equals(want_section)) {
                    builder.append(line);
                    builder.append("\n");
                }
                int idx=line.indexOf("// section");
                if (idx!=-1) {
                    cur_section = line.substring(idx + 11,line.length()).trim();
                }
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

    // load file named FILENAME using context CTX and return the resulting string
    public static String loadAsset(Context ctx, String filename) {
        return loadAsset(ctx,filename,"");
    }

    // convert float array to float buffer
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

    // convert short array to short bufer
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

    // draw triangles as specified in VBUF
    public static void drawTriangles(Vbuf7f vbuf) {
        // starting at current position...
        // give opengl a pointer to vertex attribute, consisting of 3 floats,
        // that will be bound to 'varying attribute' _pos_attr
        // the attribute is a float with stride 28 (since 7 floats per vertex...)
        vbuf._buf.position(0);
        GLES20.glVertexAttribPointer(vbuf._pos_attr, 3, GLES20.GL_FLOAT, false, vbuf._stride, vbuf._buf);
        GLES20.glEnableVertexAttribArray(vbuf._pos_attr);
        // same as above, but for colors
        vbuf._buf.position(3);
        // what does 'normalized' mean for colors???
        GLES20.glVertexAttribPointer(vbuf._color_attr, 4, GLES20.GL_FLOAT, false, vbuf._stride, vbuf._buf);
        GLES20.glEnableVertexAttribArray(vbuf._color_attr);
        // draw 3 vertices
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vbuf._n);
    }

    // set current matrix to that provided by ZOOM
    // ATTR is the name of the matrix attribute in GLES shaders of program PROG
    public static void setMatrix(int prog, Zoom zoom, String attr) {
        int mat_handle = GLES20.glGetUniformLocation(prog, attr);
        GLES20.glUniformMatrix4fv(mat_handle, 1, true, zoom._mvpmat._v, 0);
    }

    // create GLES program file file
    // file must have lines named "// section fshader"
    // and "// section vshader"
    public static Glprog createProgFromFile(Context ctx, String filename) {
        Glprog glprog = new Glprog();
        glprog._vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER, Glutil.loadAsset(ctx, filename, "vshader"));
        glprog._fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER, Glutil.loadAsset(ctx, filename, "fshader"));
        glprog._prog = Glutil.compileProgX(glprog._vshader, glprog._fshader);
        return glprog;
    }
}
