package com.panoramagl.sample;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.panoramagl.PLConstants;
import com.panoramagl.PLICamera;
import com.panoramagl.PLImage;
import com.panoramagl.PLManager;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.hotspots.ActionPLHotspot;
import com.panoramagl.utils.PLUtils;

public class MainActivity extends AppCompatActivity implements ActionPLHotspot.HotSpotListener {

    private PLManager plManager;
    private int currentIndex = -1;
    private int[] resourceIds = new int[]{R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2};

    private View.OnClickListener buttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.button_1:
                    changePanorama(0);
                    break;
                case R.id.button_2:
                    changePanorama(1);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        plManager = new PLManager(this);
        plManager.setContentView((ViewGroup) findViewById(R.id.content_view));
        plManager.onCreate();

        plManager.setAccelerometerEnabled(false);
        plManager.setInertiaEnabled(false);
        plManager.setZoomEnabled(false);

        changePanorama(0);

        Button button1 = findViewById(R.id.button_1);
        Button button2 = findViewById(R.id.button_2);
        button1.setOnClickListener(buttonClickListener);
        button2.setOnClickListener(buttonClickListener);
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

    private void changePanorama(int index) {
        if (currentIndex == index) return;

        PLSphericalPanorama panorama = new PLSphericalPanorama();
        panorama.setImage(new PLImage(PLUtils.getBitmap(this, resourceIds[index]), false));
        float pitch = 5f;
        float yaw = 0f;
        float zoomFactor = 0.8f;

        if (currentIndex != -1) {
            PLICamera camera = plManager.getPanorama().getCamera();
            pitch = camera.getPitch();
            yaw = camera.getYaw();
            zoomFactor = camera.getZoomFactor();
        }

        panorama.removeAllHotspots();
        long hotSpotId = 100;
        ActionPLHotspot plHotspot1 = new ActionPLHotspot(this, hotSpotId, new PLImage(BitmapFactory.decodeResource(getResources(), R.raw.hotspot)), -20.0f, 0, PLConstants.kDefaultHotspotSize, PLConstants.kDefaultHotspotSize);
        panorama.addHotspot(plHotspot1);

        panorama.getCamera().lookAtAndZoomFactor(pitch, yaw, zoomFactor, false);
        plManager.setPanorama(panorama);
        currentIndex = index;
    }

    @Override
    public void onClick(final long identifier) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "HotSpotClicked! Id is-> " + identifier, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
