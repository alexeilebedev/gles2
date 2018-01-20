package com.alexeilebedev.gles2;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

// Home view
public class Home extends AppCompatActivity {
    GLSurfaceView _glview;
    int _viewid = R.id.mandelbrot;

    @Override
    protected void onCreate(Bundle in) {
        super.onCreate(in);
        //if (in != null) {
        //    _viewid = in.getInt("_viewid", _viewid);
        //} else {
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            _viewid = sharedPref.getInt("_viewid", _viewid);
            Log.w("onCreate", String.format("loading data  viewid:%d",_viewid));
        //}
        rebuildView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        _glview.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        _glview.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w("onDestroy", "saving data...");
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("_viewid", _viewid);
        editor.commit();
    }

    void rebuildView() {
        if (_viewid == R.id.mandelbrot) {
            _glview = new Mandelview(this);
        } else if (_viewid == R.id.clock) {
            _glview = new Clockview(this);
        } else {
            _glview = new Clockview(this);
        }
        setContentView(_glview);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean ret=false;
        switch (item.getItemId()) {
            case R.id.mandelbrot: _viewid = item.getItemId(); ret=true; break;
            case R.id.clock: _viewid=item.getItemId(); ret=true; break;
            default: return super.onOptionsItemSelected(item);
        }
        if (ret) {
            rebuildView();
        }
        return ret;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.mandelbrot).setChecked(R.id.mandelbrot == _viewid);
        menu.findItem(R.id.clock).setChecked(R.id.clock == _viewid);
        return true;
    }


    @Override
    protected void onSaveInstanceState (Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("_viewid",_viewid);
    }
}
