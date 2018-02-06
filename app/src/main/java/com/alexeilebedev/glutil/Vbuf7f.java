package com.alexeilebedev.glutil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

// Vertex buffer with 7 floats -- x,y,z,r,g,b,a
// Used with Glutil's drawTriangles function
public class Vbuf7f {
    FloatBuffer _buf;
    int _pos_attr;
    int _color_attr;
    int _n;
    int _stride;

    public Vbuf7f(int max, int pos_attr, int color_attr) {
        _stride=7*4;
        _buf = ByteBuffer.allocateDirect(_stride *max).order(ByteOrder.nativeOrder()).asFloatBuffer();
        _pos_attr = pos_attr;
        _color_attr = color_attr;
    }

    public void reset() {
        _buf.clear();
        _n=0;
    }

    public void putVertex(float x, float y, float z, float r, float g, float b, float a) {
        _buf.put(x);
        _buf.put(y);
        _buf.put(z);
        _buf.put(r);
        _buf.put(g);
        _buf.put(b);
        _buf.put(a);
        _n++;
    }
}
