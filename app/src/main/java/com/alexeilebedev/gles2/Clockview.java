package com.alexeilebedev.gles2;

import android.opengl.GLSurfaceView;

import android.view.MotionEvent;

import java.util.Timer;
import java.util.TimerTask;

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
}
