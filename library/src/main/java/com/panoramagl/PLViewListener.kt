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
    open fun onTouchesBegan(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onTouchesMoved(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onTouchesEnded(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onShouldBeginTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    open fun onDidBeginTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onShouldMoveTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    open fun onDidMoveTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onShouldEndTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?): Boolean {
        return true
    }

    open fun onDidEndTouching(view: PLIView?, touches: List<UITouch?>?, event: MotionEvent?) {}
    open fun onShouldAccelerate(view: PLIView?, acceleration: UIAcceleration?, event: SensorEvent?): Boolean {
        return true
    }

    open fun onDidAccelerate(view: PLIView?, acceleration: UIAcceleration?, event: SensorEvent?) {}
    open fun onShouldBeginInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    open fun onDidBeginInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onShouldRunInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    open fun onDidRunInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onDidEndInertia(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onShouldBeingScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?): Boolean {
        return true
    }

    open fun onDidBeginScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onDidEndScrolling(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onShouldBeginZooming(view: PLIView?): Boolean {
        return true
    }

    open fun onDidBeginZooming(view: PLIView?, startPoint: CGPoint?, endPoint: CGPoint?) {}
    open fun onShouldRunZooming(view: PLIView?, distance: Float, isZoomIn: Boolean, isZoomOut: Boolean): Boolean {
        return true
    }

    open fun onDidRunZooming(view: PLIView?, distance: Float, isZoomIn: Boolean, isZoomOut: Boolean) {}
    open fun onDidEndZooming(view: PLIView?) {}
    open fun onShouldReset(view: PLIView?): Boolean {
        return true
    }

    open fun onDidReset(view: PLIView?) {}
    open fun onDidBeginCameraAnimation(view: PLIView?, sender: Any?, camera: PLICamera?, type: PLCameraAnimationType?) {}
    open fun onDidEndCameraAnimation(view: PLIView?, sender: Any?, camera: PLICamera?, type: PLCameraAnimationType?) {}
    open fun onDidResetCamera(view: PLIView?, sender: Any?, camera: PLICamera?) {}
    open fun onDidLookAtCamera(view: PLIView?, sender: Any?, camera: PLICamera?, pitch: Float, yaw: Float, animated: Boolean) {}
    open fun onDidRotateCamera(view: PLIView?, sender: Any?, camera: PLICamera?, pitch: Float, yaw: Float, roll: Float) {}
    open fun onDidFovCamera(view: PLIView?, sender: Any?, camera: PLICamera?, fov: Float, animated: Boolean) {}
    open fun onDidOverElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidClickElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidOutElement(view: PLIView?, element: PLISceneElement?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidOverHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidClickHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidOutHotspot(view: PLIView?, hotspot: PLIHotspot?, screenPoint: CGPoint?, scene3DPoint: PLPosition?) {}
    open fun onDidBeginTransition(view: PLIView?, transition: PLITransition?) {}
    open fun onDidProcessTransition(view: PLIView?, transition: PLITransition?, progressPercentage: Int) {}
    open fun onDidStopTransition(view: PLIView?, transition: PLITransition?, progressPercentage: Int) {}
    open fun onDidEndTransition(view: PLIView?, transition: PLITransition?) {}
    open fun onDidBeginLoader(view: PLIView?, loader: PLILoader?) {}
    open fun onDidCompleteLoader(view: PLIView?, loader: PLILoader?) {}
    open fun onDidStopLoader(view: PLIView?, loader: PLILoader?) {}
    open fun onDidErrorLoader(view: PLIView?, loader: PLILoader?, error: String?) {}
}