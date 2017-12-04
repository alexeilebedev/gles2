package com.alexeilebedev.gles2;

import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

// Wrapper for GLSurfaceView
public class Glview extends GLSurfaceView {
    Home _home;
    Mandelrend _rend;
    boolean _buttondown = false;

    Glview(Home home) {
        super(home);
        _home = home;
        setEGLContextClientVersion(2);
        _rend = new Mandelrend(this);
        setRenderer(_rend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        showEvent(event);
        boolean invalidate = false;
        switch (event.getAction()) {
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
                _buttondown = false;
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
        _rend._zoom._mvpmat.leftMul(v1);
        return String.format("x:%f  y:%f  screenx:%f  screeny:%f"
                , x
                , y
                , v1._x*_rend._zoom._width
                , v1._y*_rend._zoom._height);
    }

    private void showEvent(MotionEvent event) {
        int x = (int)event.getX();
        int y = (int)event.getY();
        _rend._zoom.updateMvp();
        Log.d("MOTION", String.format("x:%d y:%d", x,y)
                + "  " + mapPoint(_rend._zoom._x,_rend._zoom._y));
    }
}
