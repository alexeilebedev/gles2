package com.alexeilebedev.gles2;

// 4x4 matrix of floats
// homogen x = vec.x * _v[0 ] + vec.y * _v[1 ] + vec.z * _v[2 ] + _v[3 ]
// homogen y = vec.x * _v[4 ] + vec.y * _v[5 ] + vec.z * _v[6 ] + _v[7 ]
// homogen z = vec.x * _v[8 ] + vec.y * _v[9 ] + vec.z * _v[10] + _v[11]
// homogen w = vec.x * _v[12] + vec.y * _v[13] + vec.z * _v[14] + _v[15]
// frag x = homogen x / homogen w * viewport x
// frag y = homogen y / homogen w * viewport y
// frag z = homogen z / homogen w
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
}
