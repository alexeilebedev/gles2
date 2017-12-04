package com.alexeilebedev.gles2;

// Compute a matrix _mvpmat so that circle centered at _x,_y with
// radius _r fits within window _width,_height;
// Radius is clipped to _rmin .. _rmax
public class Zoom {
    Bbox2f _bbox;
    float _rmin=1e-6f, _rmax=10.f, _rdflt=0.5f;
    float _x=0.5f, _y=0.5f, _r=_rdflt;
    Mat4f _mvpmat = new Mat4f();
    int _width, _height;

    Zoom() {
        _bbox=new Bbox2f(-1.f, -1.f, 1.f, 1.f);
    }

    public void updateMvp() {
        _mvpmat.setUnit();
        _mvpmat.modelTranslate(-_x,-_y, 0.f);
        float scale = 0.5f/_r;
        float aspect = (float)_height / _width;
        _mvpmat.modelScale(scale, scale*aspect, 1.f);
        _mvpmat.worldTranslate(0.5f, 0.5f, 0.f);
    }

    public void updateViewport(int width, int height) {
        _width=width;
        _height=height;
    }

    public void zoom(float f) {
        _r = Math.min(Math.max(_r/f, _rmin), _rmax);
    }

    public void endzoom() {
        _r=_rdflt;
    }
}
