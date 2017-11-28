package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class Glrend implements GLSurfaceView.Renderer {
    float _zoom = 1.0f;
    float _ratio = 1.f;
    float _X = 0.0f;
    float _Y = 0.0f;
    Glview _view;
    int _vshader, _fshader, _prog;
    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    static float squareCoords[] = {
            -1.0f,  1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f,  1.0f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    Glrend(Glview view) {
        _view=view;

    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
        // initialize vertex byte buffer for shape coordinates
        // (# of coordinate values * 4 bytes per float)
        ByteBuffer bb = ByteBuffer.allocateDirect(squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        // (# of coordinate values * 2 bytes per short)
        ByteBuffer dlb = ByteBuffer.allocateDirect(drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        _vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER,Assets.loadAsset(_view._home,"vshader.txt"));
        _fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER,Assets.loadAsset(_view._home,"fshader.txt"));
        _prog = Glutil.compileProg(_vshader, _fshader);
    }

    public void onSurfaceChanged(GL10 unused, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        _ratio = (float)width / height;
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        // multiplied by each vector from the left
        float[] mvpmat = new float[]{
                -1.0f/_zoom,  0.0f,                0.0f,    0.0f, // X
                0.0f,        1.0f/(_zoom*_ratio), 0.0f,    0.0f, // Y
                0.0f,        0.0f,                1.0f,    0.0f, // Z
                -_X,         -_Y,                0.0f,    1.0f}; // shift
        // if vec is the original vector
        // new vector = (vec*X + shift0, vec*Y + shift1, vec*Z + shift2)
        // Add program to OpenGL environment
        GLES20.glUseProgram(_prog);
        // get handle to vertex shader's vPosition member
        int pos_handle = GLES20.glGetAttribLocation(_prog, "vPosition");
        int mat_handle = GLES20.glGetUniformLocation(_prog, "uMVPMatrix");
        GLES20.glUniformMatrix4fv(mat_handle, 1, false, mvpmat, 0);

        //Add attribute array of vertices
        GLES20.glEnableVertexAttribArray(pos_handle);
        int coords_per_vertex=3;
        int vertex_stride = 3*4;
        // what does this do
        GLES20.glVertexAttribPointer(
                pos_handle, coords_per_vertex,
                GLES20.GL_FLOAT, false,
                vertex_stride, vertexBuffer);

        // Draw a square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(pos_handle);
        // check error?
    }
}
