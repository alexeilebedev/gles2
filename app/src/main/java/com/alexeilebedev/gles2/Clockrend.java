package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// @TODO: extract clockprog!
public class Clockrend implements GLSurfaceView.Renderer {
    Clockview _clockview;
    Clockprog _clockprog;

    Clockrend(Clockview clockview) {
        _clockview = clockview;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
        _clockprog=new Clockprog();
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        _clockprog.onSurfaceChanged(unused,width,height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        _clockprog.draw();
    }
}
