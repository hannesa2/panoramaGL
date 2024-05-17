package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.*
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.utils.PLUtils
import timber.log.Timber

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

        binding.contentView.setOnLongClickListener {
            val x = it.x
            val y = it.y
            val screenWidth = resources.displayMetrics.widthPixels
            val screenHeight = resources.displayMetrics.heightPixels
            val normalizedX = x / screenWidth
            val normalizedY = y / screenHeight

            Timber.d("$normalizedX/$normalizedY $screenWidth/$screenHeight")
            addHotspotAt(normalizedX, normalizedY, screenWidth, screenHeight)
            false
        }
    }

    private fun addHotspotAt(normalizedX: Float, normalizedY: Float, screenWidth: Int, screenHeight: Int) {
        // These values would ideally come from your panorama viewer or camera settings
        val currentYaw = plManager.panorama.camera.yaw
        val currentPitch = plManager.panorama.camera.pitch
        val fov = plManager.panorama.camera.fov  // Current FOV considering zoom
        val aspectRatio = screenWidth.toFloat() / screenHeight

        // Convert normalized screen coordinates to changes in yaw and pitch
        val yawChange = (normalizedX - 0.5f) * fov
        val pitchChange = (0.5f - normalizedY) * (fov / aspectRatio)

        // Calculate new yaw and pitch for the hotspot
        val hotspotYaw = (currentYaw + yawChange) % 360
        val hotspotPitch = currentPitch + pitchChange
        // Ensure pitch stays within bounds [-90, 90]
        val clampedPitch = hotspotPitch.coerceIn(-90f, 90f)

        // Now, create your hotspot at this new yaw and pitch
        createHotspot(hotspotPitch = clampedPitch, hotspotYaw = hotspotYaw)
    }

    private fun createHotspot(hotspotPitch: Float, hotspotYaw: Float) {
        val hotspot = ActionPLHotspot(
            this,
            System.currentTimeMillis(),
            PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)),
            hotspotPitch,
            hotspotYaw,
            PLConstants.kDefaultHotspotSize,
            PLConstants.kDefaultHotspotSize
        )
        plManager.panorama.addHotspot(hotspot)
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
        plManager.startSensorialRotation()
    }

    override fun onHotspotClick(identifier: Long) {
        runOnUiThread { Toast.makeText(this@MainActivity, "HotSpotClicked! Id is-> $identifier", Toast.LENGTH_SHORT).show() }
    }
}
