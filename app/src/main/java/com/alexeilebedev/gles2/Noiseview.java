package com.alexeilebedev.gles2;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

import com.alexeilebedev.glutil.Glutil;
import com.alexeilebedev.glutil.Vbuf7f;
import com.alexeilebedev.glutil.Zoom;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

// UI component of Noise
public class Noiseview extends GLSurfaceView {
    Home _home;
    Noiserend _glrend;
    // Noise task
    class Task extends TimerTask {
        @Override public void run() {
            requestRender();
        }
    }
    Task _task;

    Noiseview(Home home) {
        super(home);
        _home=home;
        setEGLContextClientVersion(2);
        _glrend = new Noiserend(this);
        setRenderer(_glrend);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        Timer timer = new Timer(true);
        _task=new Task();
        timer.scheduleAtFixedRate(_task,0,20);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean invalidate = false;
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                invalidate = true;
                break;
            case MotionEvent.ACTION_BUTTON_PRESS:
                break;
            case MotionEvent.ACTION_DOWN:
                invalidate = true;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        if (invalidate) {
            requestRender();
        }
        return true;
    }

    public static class Noiseprog {
        private Vbuf7f _vbuf;
        Zoom _zoom;
        int _prog;

        final String _vshader =
                "uniform mat4 _mvpmat; "
                        + "uniform float  _seed; "
                        + "varying vec3 _coord; "
                        + "attribute vec4 _pos; "
                        + "attribute vec4 _color; "
                        //+ "varying vec4 v_Color; "
                        + "void main() {"
                        //+ "   v_Color = _color; "
                        + "   gl_Position = _mvpmat * _pos; "
                        + "   _coord = vec3(_pos.x,_pos.y,_pos.z);"
                        + "}";

        final String _fshader =
                ""
                + "uniform float  _seed; "
                        + "varying vec3 _coord; "
                + "float rand(vec2 co){"
                        + "\n     return abs(sin(co.x/20.0)*cos(co.y/20.0));"
                 //       + "\n     return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);"
                + "\n}"
                + "\nvoid main() { "
                        + "\n   gl_FragColor.r = rand(gl_FragCoord.xy); "
                        + "\n   gl_FragColor.g = gl_FragColor.r;"//-rand(_coord.xy*800.0); "
                        + "\n   gl_FragColor.b = gl_FragColor.r;"
                        + "\n   gl_FragColor.a = 1.0;"
                + "\n} ";

        Noiseprog(Zoom zoom) {
            _zoom=zoom;
            int vshader = Glutil.compileShaderX(GLES20.GL_VERTEX_SHADER, _vshader);
            int fshader = Glutil.compileShaderX(GLES20.GL_FRAGMENT_SHADER, _fshader);
            _prog=Glutil.compileProgX(vshader,fshader);
            _vbuf = new Vbuf7f(100
                    , GLES20.glGetAttribLocation(_prog, "_pos")
                    , GLES20.glGetAttribLocation(_prog, "_color"));
            GLES20.glUseProgram(_prog);

            //GLES20.glEnable(GLES20.GL_BLEND);
            //GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        }

        void breatheZoom(float secs, float period, float magnitude) {
            _zoom.setXYR(0.5f, 0.5f, 0.12f + (float)(Math.sin(secs*Math.PI*2/period)*magnitude));

        }
        public void draw() {
            LocalDateTime ldt = LocalDateTime.now();
            long millis = System.currentTimeMillis() % 1000;
            float secs = ldt.getSecond() + (float)millis*0.001f;
            int min = ldt.getMinute();
            int hour = ldt.getHour();

            //breatheZoom(secs, 10.0f, 0.1119f);

            Glutil.setMatrix(_prog, _zoom, "_mvpmat");
            GLES20.glUniform1f(GLES20.glGetAttribLocation(_prog, "_seed"), secs);

            // compose triangle vertices & colors
            _vbuf.reset();
            _vbuf.putVertex(0,0,0,0,0, 0,1);
            _vbuf.putVertex(1,0,0,0,0, 0,1);
            _vbuf.putVertex(1,1,0,0,0, 0,1);

            _vbuf.putVertex(0,0,0,0,0, 0,1);
            _vbuf.putVertex(1,1,0,0,0, 0,1);
            _vbuf.putVertex(0,1,0,0,0, 0,1);

            Glutil.drawTriangles(_vbuf);
        }
    }

    public static class Noiserend implements Renderer {
        Noiseview _noiseview;
        Noiseprog _noiseprog;
        Zoom _zoom;


        Noiserend(Noiseview Noiseview) {
            _noiseview = Noiseview;
            _zoom = new Zoom();
        }

        @Override
        public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
            GLES20.glClearColor(0.1f, 0.1f, 0.1f, 0.9f);
            _zoom.setXYR(0.5f, 0.5f, 0.25f);
            _noiseprog=new Noiseprog(_zoom);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            _zoom.updateViewport(width,height);
        }

        @Override
        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
            _zoom.updateMvp();
            _noiseprog.draw();
        }
    }
}
