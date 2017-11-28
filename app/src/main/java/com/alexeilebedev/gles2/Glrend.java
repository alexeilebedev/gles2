package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Glrend implements GLSurfaceView.Renderer {
    Fractal _fractal;
    float _zoom = 1.0f;
    float _ratio = 1.f;
    float _X = 0.0f;
    float _Y = 0.0f;
    Glview _view;

    Glrend(Glview view) {
        _view=view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
        _fractal = new Fractal(_view._home);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        _ratio = (float)width / height;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        float[] mMVPMatrix = new float[]{
                -1.0f/_zoom,  0.0f,                0.0f,    0.0f,
                0.0f,        1.0f/(_zoom*_ratio), 0.0f,    0.0f,
                0.0f,        0.0f,                1.0f,    0.0f,
                -_X,         -_Y,                0.0f,    1.0f};
        _fractal.draw(mMVPMatrix);
    }
}
