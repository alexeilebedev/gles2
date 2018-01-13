package com.alexeilebedev.gles2;

import android.util.Log;

// Compute a matrix _mvpmat so that circle centered at _x,_y with
// radius _r fits within window _width,_height;
// Radius is clipped to _rmin .. _rmax
// Normalized device coordinates in OpenGL go from -1 to 1
public class Zoom {
    float _rmin=1e-6f, _rmax=10.f, _rdflt=1.8f, _xdflt=-0.4f, _ydflt=0.f;
    float _x=_xdflt, _y=_ydflt, _r=_rdflt;
    float _targetx=_x, _targety=_y;
    // actual amounts visible given current aspect ratio.
    // if screen is rectangular, _xvisi,_yvisi are both equal to _r
    // otherwise one of them (corresponding to the longer window side) is bigger
    float _xvisi=0.5f,_yvisi=0.5f;
    Mat4f _mvpmat = new Mat4f();
    int _width, _height;

    public void updateMvp() {
        _xvisi = _width < _height ? _r : _r*_width/_height;
        _yvisi = _width < _height ? _r*_height/_width : _r;
        _mvpmat.setUnit();
        _mvpmat.translateBefore(-_x,-_y, 0.f);
        _mvpmat.scaleAfter(1.f/_xvisi, 1.f/_yvisi, 1.f);
    }

    public void updateViewport(int width, int height) {
        _width=width;
        _height=height;
    }

    public void zoom(float f) {
        _r = Math.min(Math.max(_r/f, _rmin), _rmax);
    }

    public void animateCenter(float f) {
        // zoom a little towards the target
        _x = _x + (_targetx - _x) * f;
        _y = _y + (_targety - _y) * f;
    }

    public void reset() {
        _r=_rdflt;
        _x=_xdflt;
        _y=_ydflt;
        _targetx=_x;
        _targety=_y;
    }

    public float screenToX(float x) {
        return x * (_xvisi*2.f / _width) + (_x - _xvisi);
    }

    private float screenToY(float y) {
        return (_height-y) * (_yvisi*2.f / _height) + (_y - _yvisi);
    }

    public void setCenterW(float x, float y) {
        _x=screenToX(x);
        _y=screenToY(y);
    }

    public void setTargetCenterW(float x, float y) {
        _targetx = screenToX(x);
        _targety = screenToY(y);
    }
}
