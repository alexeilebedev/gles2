package com.alexeilebedev.gles2;

/**
 * Created by alexei on 12/3/17.
 */

public class Vec4f {
    float _x,_y,_z,_w;

    Vec4f() {
        _x=0.f;
        _y=0.f;
        _z=0.f;
        _w=0.f;
    }
    Vec4f(float x, float y, float z, float w) {
        _x=x;
        _y=y;
        _z=z;
        _w=w;
    }
}
