package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * View renderer
 */
public class Rend implements GLSurfaceView.Renderer {
    Glview _view;
    Zoom _zoom = new Zoom();
    Mandelprog _mandelprog;
    Gridprog _gridprog;

    Rend(Glview view) {
        _view=view;
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
        try {
            _mandelprog = new Mandelprog(_view._home, _zoom);
            _gridprog = new Gridprog(_view._home, _zoom);
        } catch (RuntimeException ex) {
            Log.e("SHADER",ex.toString());
        }
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        _zoom.updateViewport(width,height);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        _zoom.updateMvp();
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        _mandelprog.draw();
        _gridprog.draw();
    }
}
