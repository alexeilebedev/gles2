package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.widget.Toast;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// Renderer for this app
public class Glrend implements GLSurfaceView.Renderer {
    float _zoom = 1.0f;
    float _aspectratio = 1.f;
    Mat4f _mvpmat = new Mat4f();
    Glview _view;
    int _vshader, _fshader, _prog;
    FloatBuffer _vbuf;
    ShortBuffer _drawlist;
    // square -1 .. 1
    static float _square_vs[] = {
            -1.0f,  1.0f, 0.0f,
            -1.0f, -1.0f, 0.0f,
            1.0f, -1.0f, 0.0f,
            1.0f,  1.0f, 0.0f };

    // 2 triangles covering the -1..1 square
    private final short _faces[] = { 0, 1, 2, 0, 2, 3 };

    Glrend(Glview view) {
        _view=view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
        _drawlist = Glutil.toShortBuffer(_faces,null);
        try {
            _vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER, Glutil.loadAsset(_view._home, "vshader.txt"));
            _fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER, Glutil.loadAsset(_view._home, "fshader.txt"));
            _prog = Glutil.compileProgX(_vshader, _fshader);
        } catch (RuntimeException e) {
            Log.e("COMPILE",e.toString());
            //Toast.makeText(_view._home, e.toString(), Toast.LENGTH_LONG).show();
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        _aspectratio = (float)width / height;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        _vbuf = Glutil.toFloatBuffer(_square_vs,_vbuf);
        _mvpmat.setUnit();
        // aspectratio = wid/height
        // so for a screen thats 100 x 200 (vertical android), it's 0.5
        //
        _mvpmat._v[0] *= 1.f / _zoom;
        _mvpmat._v[4+1] *= 1.f / (_zoom* _aspectratio);
        GLES20.glUseProgram(_prog);
        // get handle to vertex shader's vPosition member
        // WHAT DOES THIS DO
        int pos_handle = GLES20.glGetAttribLocation(_prog, "vPosition");
        int mat_handle = GLES20.glGetUniformLocation(_prog, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mat_handle, 1, false, _mvpmat._v, 0);

        //Add attribute array of vertices
        GLES20.glEnableVertexAttribArray(pos_handle);
        int coords_per_vertex=3;
        int vertex_stride = 3*4;
        // what does this do
        GLES20.glVertexAttribPointer(
                pos_handle, coords_per_vertex,
                GLES20.GL_FLOAT, false,
                vertex_stride, _vbuf);

        // Draw a square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, _faces.length,
                GLES20.GL_UNSIGNED_SHORT, _drawlist);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(pos_handle);
        // check error?
    }
}
