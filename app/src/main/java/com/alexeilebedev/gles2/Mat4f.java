package com.alexeilebedev.gles2;

// 4x4 matrix of floats
// The matrix is a row matrix, the first row is the transformation for vertex x, etc.
// First 4 elements are the first row.

// result x = vec.x * _v[0 ] + vec.y * _v[1 ] + vec.z * _v[2 ] + _v[3 ]
// result y = vec.x * _v[4 ] + vec.y * _v[5 ] + vec.z * _v[6 ] + _v[7 ]
// result z = vec.x * _v[8 ] + vec.y * _v[9 ] + vec.z * _v[10] + _v[11]
// result w = vec.x * _v[12] + vec.y * _v[13] + vec.z * _v[14] + _v[15]
// normalized device x = result x / result w * viewport x
// normalized device y = result y / result w * viewport y
// normalized device z = result z / result w
// <effects of model-view-projection matrix end here>

// window x = normalized device x * viewport x
// window y = normalized device y * viewport y

// model -> world -> normalize device -> window ?

public class Mat4f {
    float[] _v = new float[16];

    Mat4f() {
    }

    public void setUnit() {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                _v[i * 4 + j] = (i == j ? 1.f : 0.f);
            }
        }
    }

    // move each input vec by x,y,z before multiplying by matrix
    // result x = (vec.x + x) * _v[0 ] + (vec.y + y) * _v[1 ] + (vec.z + z) * _v[2 ] + _v[3 ]
    // result y = (vec.x + x) * _v[4 ] + (vec.y + y) * _v[5 ] + (vec.z + z) * _v[6 ] + _v[7 ]
    // result z = (vec.x + x) * _v[8 ] + (vec.y + y) * _v[9 ] + (vec.z + z) * _v[10] + _v[11]
    // result w = (vec.x + x) * _v[12] + (vec.y + y) * _v[13] + (vec.z + z) * _v[14] + _v[15]
    void modelTranslate(float x, float y, float z) {
        _v[3 ] += x * _v[0 ] + y * _v[1 ] + z * _v[2 ];
        _v[7 ] += x * _v[4 ] + y * _v[5 ] + z * _v[6 ];
        _v[11] += x * _v[8 ] + y * _v[9 ] + z * _v[10];
        _v[15] += x * _v[12] + y * _v[13] + z * _v[14];
    }

    // result x = vec.x * _v[0 ] + vec.y * _v[1 ] + vec.z * _v[2 ] + _v[3 ] + x
    // result y = vec.x * _v[4 ] + vec.y * _v[5 ] + vec.z * _v[6 ] + _v[7 ] + y
    // result z = vec.x * _v[8 ] + vec.y * _v[9 ] + vec.z * _v[10] + _v[11] + z
    // result w = vec.x * _v[12] + vec.y * _v[13] + vec.z * _v[14] + _v[15] + w
    void worldTranslate(float x, float y, float z) {
        _v[3 ] += x;
        _v[7 ] += y;
        _v[11] += z;
    }

    // result x = (vec.x * _v[0 ] + vec.y * _v[1 ] + vec.z * _v[2 ] + _v[3 ]) * x
    // result y = (vec.x * _v[4 ] + vec.y * _v[5 ] + vec.z * _v[6 ] + _v[7 ]) * y
    // result z = (vec.x * _v[8 ] + vec.y * _v[9 ] + vec.z * _v[10] + _v[11]) * z
    // result w = vec.x * _v[12] + vec.y * _v[13] + vec.z * _v[14] + _v[15]
    void worldScale(float x, float y, float z) {
        _v[0 ] *= x;
        _v[1 ] *= x;
        _v[2 ] *= x;
        _v[3 ] *= x;

        _v[4 ] *= y;
        _v[5 ] *= y;
        _v[6 ] *= y;
        _v[7 ] *= y;

        _v[8 ] *= z;
        _v[9 ] *= z;
        _v[10] *= z;
        _v[11] *= z;
    }

    // result x = vec.x * x * _v[0 ] + vec.y * y * _v[1 ] + vec.z * z * _v[2 ] + _v[3 ]
    // result y = vec.x * x * _v[4 ] + vec.y * y * _v[5 ] + vec.z * z * _v[6 ] + _v[7 ]
    // result z = vec.x * x * _v[8 ] + vec.y * y * _v[9 ] + vec.z * z * _v[10] + _v[11]
    // result w = vec.x * x * _v[12] + vec.y * y * _v[13] + vec.z * z * _v[14] + _v[15]
    void modelScale(float x, float y, float z) {
        _v[0 ] *= x;
        _v[4 ] *= x;
        _v[8 ] *= x;
        _v[12] *= y;
        _v[1 ] *= y;
        _v[5 ] *= y;
        _v[9 ] *= z;
        _v[13] *= z;
        _v[2 ] *= z;
        _v[6 ] *= z;
        _v[10] *= z;
        _v[14] *= z;
    }

    void leftMul(Vec4f vec) {
        float x = _v[0 ]*vec._x + _v[1 ]*vec._y + _v[2 ]*vec._z + _v[3 ]*vec._w;
        float y = _v[4 ]*vec._x + _v[5 ]*vec._y + _v[6 ]*vec._z + _v[7 ]*vec._w;
        float z = _v[8 ]*vec._x + _v[9 ]*vec._y + _v[10]*vec._z + _v[11]*vec._w;
        float w = _v[12]*vec._x + _v[13]*vec._y + _v[14]*vec._z + _v[15]*vec._w;
        vec._x=x;
        vec._y=y;
        vec._z=z;
        vec._w=w;
    }
}
