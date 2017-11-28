package com.alexeilebedev.gles2;

import android.content.Context;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;


public class Fractal {
    int _vshader, _fshader, _prog;
    FloatBuffer vertexBuffer;
    ShortBuffer drawListBuffer;
    static float squareCoords[] = {
            -1.0f,  1.0f, 0.0f,   // top left
            -1.0f, -1.0f, 0.0f,   // bottom left
            1.0f, -1.0f, 0.0f,   // bottom right
            1.0f,  1.0f, 0.0f }; // top right

    private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw vertices

    Fractal(Context ctx) {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        _vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER,Assets.loadAsset(ctx,"vshader.txt"));
        _fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER,Assets.loadAsset(ctx,"fshader.txt"));
        _prog = Glutil.compileProg(_vshader, _fshader);
    }
    void draw(float[] matrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(_prog);
        // get handle to vertex shader's vPosition member
        int pos_handle = GLES20.glGetAttribLocation(_prog, "vPosition");
        int mat_handle = GLES20.glGetUniformLocation(_prog, "uMVPMatrix");

        //Pass uniform transformation matrix to shader
        GLES20.glUniformMatrix4fv(mat_handle, 1, false, matrix, 0);

        //Add attribute array of vertices
        GLES20.glEnableVertexAttribArray(pos_handle);
        int coords_per_vertex=3;
        int vertex_stride = 3*4;
        GLES20.glVertexAttribPointer(
                pos_handle, coords_per_vertex,
                GLES20.GL_FLOAT, false,
                vertex_stride, vertexBuffer);

        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(pos_handle);
        // check error?
    }
}
