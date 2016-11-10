package com.gipsyz.panoramaglandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;

import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.utils.PLUtils;

public class MainActivity extends AppCompatActivity {

    PLManager plManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        plManager = new PLManager(this);
        plManager.setContentView(R.layout.activity_main);
        plManager.onCreate();

        plManager.setAccelerometerEnabled(false);
        plManager.setInertiaEnabled(false);
        plManager.setZoomEnabled(false);
        
        PLSphericalPanorama panorama = new PLSphericalPanorama();
        panorama.getCamera().lookAtAndZoomFactor(5.f, 0f, 0.8f, false);

        panorama.setImage(new PLImage(PLUtils.getBitmap(this, R.raw.row), false));
        plManager.setPanorama(panorama);


    }

    @Override
    protected void onResume() {
        super.onResume();
        plManager.onResume();
    }

    @Override
    protected void onPause() {
        plManager.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        plManager.onDestroy();
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return plManager.onTouchEvent(event);
    }
}
