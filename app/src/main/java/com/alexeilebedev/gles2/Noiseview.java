package com.alexeilebedev.gles2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.alexeilebedev.glutil.Glprog;
import com.alexeilebedev.glutil.Glutil;
import com.alexeilebedev.glutil.Vbuf7f;
import com.alexeilebedev.glutil.Zoom;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// UI component of Noise
public class Noiseview extends GLSurfaceView {
    Home _home;
    Noiserend _noiserend;
    // Noise task
    class Task extends TimerTask {
        @Override public void run() {
            requestRender();
        }
    }
    Task _task;

    Noiseview(Home home) {
        super(home);
        _home=home;
        setEGLContextClientVersion(2);
        _noiserend = new Noiserend(home,this);
        setRenderer(_noiserend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Timer timer = new Timer(true);
        _task=new Task();
        timer.scheduleAtFixedRate(_task,0,20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean invalidate = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                invalidate = true;
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                break;
            case MotionEvent.ACTION_DOWN:
                invalidate = true;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        if (invalidate) {
            requestRender();
        }
        return true;
    }


    public static class Noiserend implements Renderer {
        Noiseview _noiseview;
        private Vbuf7f _vbuf;
        Zoom _zoom;
        Glprog _glprog;

        Noiserend(Context ctx, Noiseview noiseview) {
            _noiseview = noiseview;
            _zoom = new Zoom();
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            _zoom.setXYR(0.5f, 0.5f, 0.25f);
            _glprog=Glutil.createProgFromFile(_noiseview._home,"noise.shader");
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
            GLES20.glUseProgram(_glprog._prog);
            _vbuf = new Vbuf7f(100
                    , GLES20.glGetAttribLocation(_glprog._prog, "_pos")
                    , GLES20.glGetAttribLocation(_glprog._prog, "_color"));
            //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            _zoom.updateViewport(width,height);
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            LocalDateTime ldt = LocalDateTime.now();
            long millis = System.currentTimeMillis() % 1000;
            float secs = ldt.getSecond() + (float)millis*0.001f;
            int min = ldt.getMinute();
            int hour = ldt.getHour();

            //breatheZoom(secs, 2.0f, 0.12f);
            _zoom.updateMvp();

            Glutil.setMatrix(_glprog._prog, _zoom, "_mvpmat");
            GLES20.glUniform1f(GLES20.glGetUniformLocation(_glprog._prog, "_seed"), secs);

            // compose triangle vertices & colors
            _vbuf.reset();
            _vbuf.putVertex(0,0,0,0,0, 0,1);
            _vbuf.putVertex(1,0,0,0,0, 0,1);
            _vbuf.putVertex(1,1,0,0,0, 0,1);

            _vbuf.putVertex(0,0,0,0,0, 0,1);
            _vbuf.putVertex(1,1,0,0,0, 0,1);
            _vbuf.putVertex(0,1,0,0,0, 0,1);

            Glutil.drawTriangles(_vbuf);
        }
        void breatheZoom(float secs, float period, float magnitude) {
            _zoom.setXYR(0.5f, 0.5f, 0.13f + (float)(Math.sin(secs*Math.PI*2/period)*magnitude));
        }
    }
}
