package com.alexeilebedev.gles2;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// Wrapper for GLSurfaceView
public class Mandelview extends GLSurfaceView {
    Home _home;
    Mandelrend _mandelrend;
    boolean _buttondown = false;
    // clock task
    class Task extends TimerTask {
        @Override public void run() {
            if (_buttondown) {
                requestRender();
                _mandelrend._zoom.zoom(1.02f);
                _mandelrend._zoom.animateCenter(0.1f);
            }
        }
    }
    Task _task;

    Mandelview(Home home) {
        super(home);
        _home = home;
        setEGLContextClientVersion(2);
        _mandelrend = new Mandelrend(this);
        setRenderer(_mandelrend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Timer timer = new Timer(true);
        _task=new Task();
        // re-render every 50 milliseconds
        timer.scheduleAtFixedRate(_task,0,20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //showEvent(event);
        boolean invalidate = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                invalidate = true;
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                break;
            case MotionEvent.ACTION_DOWN:
                if (event.getY() > _mandelrend._zoom._height*9/10 && event.getX() < _mandelrend._zoom._width*1/10) {
                    _mandelrend._zoom.reset();
                } else {
                    _mandelrend._zoom.setTargetCenterW(event.getX(),event.getY());
                }
                _buttondown = true;
                invalidate = true;
                break;
            case MotionEvent.ACTION_UP:
                _buttondown = false;
                invalidate = true;
                break;
        }
        if (invalidate) {
            requestRender();
        }
        return true;
    }

    // map model point to screen
    private String mapPoint(float x, float y) {
        Vec4f v1 = new Vec4f(x,y,0,0);
        _mandelrend._zoom._mvpmat.transform(v1);
        return String.format("x:%f  y:%f  screenx:%f  screeny:%f"
                , x
                , y
                , v1._x* _mandelrend._zoom._width
                , v1._y* _mandelrend._zoom._height);
    }

    private void showEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        _mandelrend._zoom.updateMvp();
        Log.d("MOTION"
                , String.format("x:%d y:%d  centerx:%f  centery:%f  xvisi:%f  yvisi:%f"
                    , x,y, _mandelrend._zoom._x, _mandelrend._zoom._y, _mandelrend._zoom._xvisi, _mandelrend._zoom._yvisi)
                + "  " + mapPoint(_mandelrend._zoom._x, _mandelrend._zoom._y)
                + "  mvpmat:" + Arrays.toString(_mandelrend._zoom._mvpmat._v));
    }

    // Mandelbrot shader program
    public static class Mandelprog {
        int _vshader, _fshader, _prog;
        Zoom _zoom;
        static float _vs[] = {
                -10.f,10.f,0.f,
                -10.f,-10.f,0.f,
                10.f,-10.f,0.f,
                10.f,10.f,0.f
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
            GLES20.glUniform1i(niter_handle, 1000);
            GLES20.glEnableVertexAttribArray(pos_handle);
            int coords_per_vertex=3;
            int vertex_stride = 3*4;
            GLES20.glVertexAttribPointer(pos_handle, coords_per_vertex, GLES20.GL_FLOAT, true, vertex_stride, _vs_buf);
            GLES20.glDrawElements(GLES20.GL_TRIANGLES, _faces.length, GLES20.GL_UNSIGNED_SHORT, _faces_buf);
            GLES20.glDisableVertexAttribArray(pos_handle);
        }
    }

    // Renderer -- mostly invoked in its own thread.
    // #AL# But I'm not sure which functions are invoked in what thread.
    public static class Mandelrend implements Renderer {
        Mandelview _mandelview;
        Zoom _zoom = new Zoom();
        Mandelprog _mandelprog;
        Gridprog _gridprog;

        Mandelrend(Mandelview mandelview) {
            _mandelview = mandelview;
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
            try {
                _mandelprog = new Mandelprog(_mandelview._home, _zoom);
                _gridprog = new Gridprog(_mandelview._home, _zoom);
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
}
