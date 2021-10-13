package com.panoramagl

import android.hardware.SensorEvent
import android.view.MotionEvent
import com.panoramagl.enumerations.PLCameraAnimationType
import com.panoramagl.hotspots.PLIHotspot
import com.panoramagl.ios.UITouch
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.ios.structs.UIAcceleration
import com.panoramagl.loaders.PLILoader
import com.panoramagl.structs.PLPosition
import com.panoramagl.transitions.PLITransition

@Suppress("UNUSED_PARAMETER")
abstract class PLViewListener {
    fun onTouchesBegan(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onTouchesMoved(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onTouchesEnded(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onShouldBeginTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    fun onDidBeginTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onShouldMoveTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    fun onDidMoveTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onShouldEndTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    fun onDidEndTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    fun onShouldAccelerate(view: PLIView?, acceleration: UIAcceleration?, event: SensorEvent?): Boolean {
        return true
    }

    fun onDidAccelerate(view: PLIView?, acceleration: UIAcceleration?, event: SensorEvent?) {}
    fun onShouldBeginInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    fun onDidBeginInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onShouldRunInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    fun onDidRunInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onDidEndInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onShouldBeingScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    fun onDidBeginScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onDidEndScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onShouldBeginZooming(view: PLIView?): Boolean {
        return true
    }

    fun onDidBeginZooming(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    fun onShouldRunZooming(view: PLIView?, distance: Float, isZoomIn: Boolean, isZoomOut: Boolean): Boolean {
        return true
    }

    fun onDidRunZooming(view: PLIView?, distance: Float, isZoomIn: Boolean, isZoomOut: Boolean) {}
    fun onDidEndZooming(view: PLIView?) {}
    fun onShouldReset(view: PLIView?): Boolean {
        return true
    }

    fun onDidReset(view: PLIView?) {}
    fun onDidBeginCameraAnimation(view: PLIView?, sender: Any?, camera: PLICamera?, type: PLCameraAnimationType?) {}
    fun onDidEndCameraAnimation(view: PLIView?, sender: Any?, camera: PLICamera?, type: PLCameraAnimationType?) {}
    fun onDidResetCamera(view: PLIView?, sender: Any?, camera: PLICamera?) {}
    fun onDidLookAtCamera(view: PLIView?, sender: Any?, camera: PLICamera?, pitch: Float, yaw: Float, animated: Boolean) {}
    fun onDidRotateCamera(view: PLIView?, sender: Any?, camera: PLICamera?, pitch: Float, yaw: Float, roll: Float) {}
    fun onDidFovCamera(view: PLIView?, sender: Any?, camera: PLICamera?, fov: Float, animated: Boolean) {}
    fun onDidOverElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidClickElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidOutElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidOverHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidClickHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidOutHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    fun onDidBeginTransition(view: PLIView?, transition: PLITransition?) {}
    fun onDidProcessTransition(view: PLIView?, transition: PLITransition?, progressPercentage: Int) {}
    fun onDidStopTransition(view: PLIView?, transition: PLITransition?, progressPercentage: Int) {}
    fun onDidEndTransition(view: PLIView?, transition: PLITransition?) {}
    fun onDidBeginLoader(view: PLIView?, loader: PLILoader?) {}
    fun onDidCompleteLoader(view: PLIView?, loader: PLILoader?) {}
    fun onDidStopLoader(view: PLIView?, loader: PLILoader?) {}
    fun onDidErrorLoader(view: PLIView?, loader: PLILoader?, error: String?) {}
}