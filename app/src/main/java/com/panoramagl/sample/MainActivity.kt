package com.panoramagl.sample

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.PLConstants
import com.panoramagl.PLImage
import com.panoramagl.PLManager
import com.panoramagl.PLSphericalPanorama
import com.panoramagl.hotspots.ActionPLHotspot
import com.panoramagl.hotspots.HotSpotListener
import com.panoramagl.sample.databinding.ActivityMainBinding
import com.panoramagl.utils.PLUtils

class MainActivity : AppCompatActivity(), HotSpotListener {

    private lateinit var binding: ActivityMainBinding

    private lateinit var plManager: PLManager
    private var currentIndex = -1
    private val resourceIds = intArrayOf(R.raw.sighisoara_sphere, R.raw.sighisoara_sphere_2)
    private val buttonClickListener = View.OnClickListener { v ->
        when (v.id) {
            R.id.button_1 -> changePanorama(0)
            R.id.button_2 -> changePanorama(1)
            else -> Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        plManager = PLManager(this).apply {
            setContentView(binding.contentView)
            onCreate()
            isAccelerometerEnabled = false
            isInertiaEnabled = false
            isZoomEnabled = false
        }
        changePanorama(0)
        binding.button1.setOnClickListener(buttonClickListener)
        binding.button2.setOnClickListener(buttonClickListener)
    }

    override fun onResume() {
        super.onResume()
        plManager.onResume()
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
            plManager.panorama?.camera?.apply {
                pitch = this.pitch
                yaw = this.yaw
                zoomFactor = this.zoomFactor
            }
        }
        panorama.removeAllHotspots()
        val hotSpotId: Long = 100
        val normalizedX = 500f / image3D.width
        val normalizedY = 700f / image3D.height
        val plHotspot1 = ActionPLHotspot(this, hotSpotId, PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)), 0f, 0f, PLConstants.kDefaultHotspotSize, PLConstants.kDefaultHotspotSize)
        plHotspot1.setPosition(normalizedX, normalizedY)
        val plHotspot2 = ActionPLHotspot(this, hotSpotId + 1, PLImage(BitmapFactory.decodeResource(resources, R.raw.hotspot)), 20f, 50f, PLConstants.kDefaultHotspotSize, PLConstants.kDefaultHotspotSize)
        panorama.addHotspot(plHotspot1)
        panorama.addHotspot(plHotspot2)
        panorama.camera.lookAtAndZoomFactor(pitch, yaw, zoomFactor, false)
        plManager.panorama = panorama
        currentIndex = index
    }

    override fun onClick(identifier: Long) {
        runOnUiThread { Toast.makeText(this@MainActivity, "HotSpotClicked! Id is-> $identifier", Toast.LENGTH_SHORT).show() }
    }
}
