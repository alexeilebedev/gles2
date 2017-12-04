package com.alexeilebedev.gles2;
import android.content.Context;
import android.opengl.GLES20;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

// Shader program to render a grid
// (currently just crosshair)
public class Gridprog {
    int _vshader, _fshader, _prog;
    Zoom _zoom;
    static float _vs[] = {
            -1.0f,  0.0f, 0.0f, // left
            1.0f, 0f, 0.0f, // right
            0.0f, -1.0f, 0.0f, // bottom
            0.0f,  1.0f, 0.0f // top
    };
    FloatBuffer _vs_buf;
    private final short _lines[] = { 0,1, 2,3};
    ShortBuffer _lines_buf;

    Gridprog(Context ctx, Zoom zoom) {
        _zoom = zoom;
        _lines_buf = Glutil.toShortBuffer(_lines, _lines_buf);
        _vs_buf = Glutil.toFloatBuffer(_vs, _vs_buf);
        _vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER, Glutil.loadAsset(ctx, Assets.plain_vshader));
        _fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER, Glutil.loadAsset(ctx, Assets.plain_fshader));
        _prog = Glutil.compileProgX(_vshader, _fshader);
    }

    public void draw() {
        GLES20.glUseProgram(_prog);
        int pos_handle = GLES20.glGetAttribLocation(_prog, "vPosition");
        int mat_handle = GLES20.glGetUniformLocation(_prog, "_mvpmat");
        int col_handle = GLES20.glGetUniformLocation(_prog, "vColor");
        // red
        GLES20.glUniform4f(col_handle, 1.f, 0.f, 0.f, 1.f);

        GLES20.glUniformMatrix4fv(mat_handle, 1, true, _zoom._mvpmat._v, 0);
        GLES20.glEnableVertexAttribArray(pos_handle);
        int coords_per_vertex=3;
        int vertex_stride = 3*4;
        GLES20.glVertexAttribPointer(pos_handle, coords_per_vertex, GLES20.GL_FLOAT, false, vertex_stride, _vs_buf);
        GLES20.glDrawElements(GLES20.GL_LINES, _lines.length, GLES20.GL_UNSIGNED_SHORT, _lines_buf);
        GLES20.glDisableVertexAttribArray(pos_handle);
    }
}
