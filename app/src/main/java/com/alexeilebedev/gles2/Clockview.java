package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import android.opengl.Matrix;
import android.view.MotionEvent;

import com.alexeilebedev.glutil.Glprog;
import com.alexeilebedev.glutil.Glutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// UI component of clock
public class Clockview extends GLSurfaceView {
    Home _home;
    Clockrend _glrend;
    // clock task
    class Task extends TimerTask {
        @Override public void run() {
            requestRender();
        }
    }
    Task _task;

    Clockview(Home home) {
        super(home);
        _home=home;
        setEGLContextClientVersion(2);
        _glrend = new Clockrend(this);
        setRenderer(_glrend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Timer timer = new Timer(true);
        _task=new Task();
        timer.scheduleAtFixedRate(_task,0,50);
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


    // @TODO: extract clockprog!
    public static class Clockrend implements Renderer {
        Clockview _clockview;

        Clockrend(Clockview clockview) {
            _clockview = clockview;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
            //7=elements per vertex, 3=vertices
            _tri1 = ByteBuffer.allocateDirect(7*3* 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
            // eyez must be >= 1.0 to work
            Matrix.setLookAtM(_viewmat, 0
                    , 0.0f, 0.0f, 2.0f
                    , 0.0f, 0.0f, -5.0f
                    , 0.0f, 1.0f, 0.0f);
            _glprog = Glutil.createProgFromFile(_clockview._home, "clock.shader");
            _mvpmat_handle = GLES20.glGetUniformLocation(_glprog._prog, "u_MVPMatrix");
            _position_handle = GLES20.glGetAttribLocation(_glprog._prog, "a_Position");
            _color_handle = GLES20.glGetAttribLocation(_glprog._prog, "a_Color");
            GLES20.glUseProgram(_glprog._prog);
            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
            final float ratio = (float) width / height;
            Matrix.frustumM(_projmat, 0, -ratio, ratio, -1.0f, 1.0f, 1.0f, 10.0f);
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            Matrix.setIdentityM(_modelmat, 0);
            Matrix.multiplyMM(_mvpmat, 0, _viewmat, 0, _modelmat, 0);
            Matrix.multiplyMM(_mvpmat, 0, _projmat, 0, _mvpmat, 0);
            GLES20.glUniformMatrix4fv(_mvpmat_handle, 1, false, _mvpmat, 0);

            LocalDateTime ldt = LocalDateTime.now();
            long millis = System.currentTimeMillis() % 1000;
            float secs = ldt.getSecond() + (float)millis*0.001f;
            int min = ldt.getMinute();
            int hour = ldt.getHour();

            // compose triangle vertices
            _tri1.clear();
            putRad(secs / 60.0f, 1.f);
            putColor(255,0,0);

            putRad((float)min / 60, 1.0f);
            putColor(0,255,0);

            putRad((float)(hour % 12) / 12, 1.0f);
            putColor(0,0, 255);

            _tri1.position(0);
            drawTriangle(_tri1);
        }

        private int _mvpmat_handle;
        private int _position_handle;
        private int _color_handle;
        private float[] _projmat = new float[16];
        private float[] _modelmat = new float[16];
        private float[] _viewmat = new float[16];
        private float[] _mvpmat = new float[16];
        private FloatBuffer _tri1;
        Glprog _glprog;

        private void drawTriangle(final FloatBuffer tribuf) {
            int stride_bytes = 7 * 4;
            tribuf.position(0);
            GLES20.glVertexAttribPointer(_position_handle, 3, GLES20.GL_FLOAT, false, stride_bytes, tribuf);
            GLES20.glEnableVertexAttribArray(_position_handle);
            tribuf.position(3);
            GLES20.glVertexAttribPointer(_color_handle, 4, GLES20.GL_FLOAT, false, stride_bytes, tribuf);
            GLES20.glEnableVertexAttribArray(_color_handle);
            GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
        }

        public void putVertex(float x, float y, float z) {
            _tri1.put(x);
            _tri1.put(y);
            _tri1.put(z);
        }

        public void putColor(int r, int g, int b) {
            _tri1.put(r/255.f);
            _tri1.put(g/255.f);
            _tri1.put(b/255.f);
            _tri1.put(0.5f);// alpha
        }

        public void putRad(float angle, float radius) {
            // clocks go clockwise from PI/2
            angle = (float)(Math.PI * 0.5 - angle * Math.PI * 2);
            float x = (float)(Math.cos(angle) *radius);
            float y = (float)(Math.sin(angle) *radius);
            putVertex(x,y,0.f);
        }

    }
}
