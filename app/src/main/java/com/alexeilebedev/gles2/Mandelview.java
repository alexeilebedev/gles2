package com.alexeilebedev.gles2;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;

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
}
