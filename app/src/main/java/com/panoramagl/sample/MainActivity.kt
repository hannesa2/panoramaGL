package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.opengl.GLES20
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.*
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.utils.PLUtils

class MainActivity : AppCompatActivity(), HotSpotListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private val resourceIds = intArrayOf(R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2)

    private val useAcceleratedTouchScrolling = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        plManager = PLManager(this).apply {
            setContentView(binding.contentView)
            onCreate()
            isAccelerometerEnabled = false
            isInertiaEnabled = false
            isZoomEnabled = false
            isAcceleratedTouchScrollingEnabled = useAcceleratedTouchScrolling
            // to see a black screen
            isScrollingEnabled = true
            isInertiaEnabled = true
        }
        changePanorama(0)
        binding.button1.setOnClickListener { changePanorama(0) }
        binding.button2.setOnClickListener { changePanorama(1) }
    }

    override fun onResume() {
        super.onResume()
        plManager.onResume()
        Handler(Looper.getMainLooper()).postDelayed({
            binding.textVersion.text = PLRenderer.versionGL
            binding.textRenderer.text = PLRenderer.rendererGL
        }, 1000)
    }

    override fun onPause() {
        plManager.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        plManager.onDestroy()
        super.onDestroy()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return plManager.onTouchEvent(event)
    }

    private fun changePanorama(index: Int) {
        if (currentIndex == index)
            return
        val image3D = PLUtils.getBitmap(this, resourceIds[index])
        val panorama = PLSphericalPanorama()
        panorama.setImage(PLImage(image3D, false))
        var pitch = 5f
        var yaw = 0f
        var zoomFactor = 0.7f
        if (currentIndex != -1) {
            plManager.panorama.camera?.apply {
                pitch = this.pitch
                yaw = this.yaw
                zoomFactor = this.zoomFactor
            }
        }
        panorama.removeAllHotspots()
        val hotSpotId: Long = 100
        val normalizedX = 500f / image3D.width
        val normalizedY = 700f / image3D.height
        val plHotspot1 = ActionPLHotspot(
            this,
            hotSpotId,
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            0f,
            0f,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        plHotspot1.setPosition(normalizedX, normalizedY)
        val plHotspot2 = ActionPLHotspot(
            this,
            hotSpotId + 1,
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            20f,
            50f,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        panorama.addHotspot(plHotspot1)
        panorama.addHotspot(plHotspot2)
        panorama.camera.lookAtAndZoomFactor(pitch, yaw, zoomFactor, false)
        if (!useAcceleratedTouchScrolling) {
            // If not using the accelerated scrolling, increasing the camera's rotation sensitivity will allow the
            // image to pan faster with finger movement. 180f gives about a ~1:1 move sensitivity.
            // Higher will move the map faster
            // Range 1-270
            panorama.camera.rotationSensitivity = 270f
        }
        plManager.panorama = panorama
        currentIndex = index
    }

    override fun onHotspotClick(identifier: Long) {
        runOnUiThread { Toast.makeText(this@MainActivity, "HotSpotClicked! Id is-> $identifier", Toast.LENGTH_SHORT).show() }
    }
}
