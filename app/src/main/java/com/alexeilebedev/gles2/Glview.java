package com.alexeilebedev.gles2;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class Glview extends GLSurfaceView {
    Home _home;
    Glrend _glrend;
    boolean _buttondown = false;

    Glview(Home home) {
        super(home);
        _home = home;
        setEGLContextClientVersion(2);
        _glrend = new Glrend(this);
        setRenderer(_glrend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    public void autozoom() {
        if (_buttondown) {
            _glrend._zoom *= 1.05f;
        }
    }

    public void endzoom() {
        _glrend._zoom = 1.f;
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
                _buttondown = true;
                invalidate = true;
                break;
            case MotionEvent.ACTION_UP:
                endzoom();
                _buttondown = false;
                break;
        }
        if (invalidate) {
            requestRender();
        }
        return true;
    }
}
