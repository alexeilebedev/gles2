package com.alexeilebedev.gles2;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// Mandelbrot shader program
public class Mandelprog  {
    int _vshader, _fshader, _prog;
    Zoom _zoom;
    static float _vs[] = {
            -4.f,4.f,0.f,
            -4.f,-4.f,0.f,
            4.f,-4.f,0.f,
            4.f,4.f,0.f
    };
    FloatBuffer _vs_buf;
    Gridprog _gridprog;
    private final short _faces[] = { 0, 1, 2, 0, 2, 3 };// 2 triangles covering the square
    ShortBuffer _faces_buf;

    Mandelprog(Context ctx, Zoom zoom) {
        _zoom=zoom;
        _vs_buf = Glutil.toFloatBuffer(_vs, _vs_buf);
        _faces_buf = Glutil.toShortBuffer(_faces, _faces_buf);
        _vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER, Glutil.loadAsset(ctx, Assets.mandel_vshader));
        _fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER, Glutil.loadAsset(ctx, Assets.mandel_fshader));
        _prog = Glutil.compileProgX(_vshader, _fshader);
    }

    public void draw() {
        GLES20.glUseProgram(_prog);
        int pos_handle = GLES20.glGetAttribLocation(_prog, "vPosition");
        int mat_handle = GLES20.glGetUniformLocation(_prog, "_mvpmat");
        GLES20.glUniformMatrix4fv(mat_handle, 1, true, _zoom._mvpmat._v, 0);
        int niter_handle = GLES20.glGetUniformLocation(_prog, "_maxiter");
        GLES20.glUniform1i(niter_handle, 600);
        GLES20.glEnableVertexAttribArray(pos_handle);
        int coords_per_vertex=3;
        int vertex_stride = 3*4;
        GLES20.glVertexAttribPointer(pos_handle, coords_per_vertex, GLES20.GL_FLOAT, true, vertex_stride, _vs_buf);
        GLES20.glDrawElements(GLES20.GL_TRIANGLES, _faces.length, GLES20.GL_UNSIGNED_SHORT, _faces_buf);
        GLES20.glDisableVertexAttribArray(pos_handle);
    }
}
