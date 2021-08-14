package com.panoramagl

import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLRotation

interface PLICamera : PLIRenderableElement {
    fun reset(sender: Any?)
    var isLocked: Boolean
    var isFovEnabled: Boolean
    var initialFov: Float
    var fov: Float
    var fovFactor: Float
    var fovSensitivity: Float
    var fovRange: PLRange?
    fun setFovRange(min: Float, max: Float)
    var fovMin: Float
    var fovMax: Float
    var minDistanceToEnableFov: Int
    var rotationSensitivity: Float
    var zoomFactor: Float
    var zoomLevel: Int
    var zoomLevels: Int
    var initialLookAt: PLRotation?
    fun setInitialLookAt(pitch: Float, yaw: Float)
    var initialPitch: Float
    var initialYaw: Float
    val lookAtRotation: PLRotation?
    val isAnimating: Boolean
    var internalListener: PLCameraListener?
    var listener: PLCameraListener?

    /**
     * animation methods
     */
    fun stopAnimation(): Boolean
    fun stopAnimation(sender: Any?): Boolean

    /**
     * fov methods
     */
    fun setFov(fov: Float, animated: Boolean): Boolean
    fun setFov(sender: Any?, fov: Float, animated: Boolean): Boolean
    fun setFovFactor(fovFactor: Float, animated: Boolean): Boolean
    fun setFovFactor(sender: Any?, fovFactor: Float, animated: Boolean): Boolean
    fun addFov(distance: Float): Boolean
    fun addFov(sender: Any?, distance: Float): Boolean

    /**
     * zoom methods
     */
    fun setZoomFactor(zoomFactor: Float, animated: Boolean): Boolean
    fun setZoomFactor(sender: Any?, zoomFactor: Float, animated: Boolean): Boolean
    fun setZoomLevel(zoomLevel: Int, animated: Boolean): Boolean
    fun setZoomLevel(sender: Any?, zoomLevel: Int, animated: Boolean): Boolean
    fun zoomIn(animated: Boolean): Boolean
    fun zoomIn(sender: Any?, animated: Boolean): Boolean
    fun zoomOut(animated: Boolean): Boolean
    fun zoomOut(sender: Any?, animated: Boolean): Boolean

    /**
     * lookat methods
     */
    fun lookAt(rotation: PLRotation?): Boolean
    fun lookAt(sender: Any?, rotation: PLRotation?): Boolean
    fun lookAt(rotation: PLRotation?, animated: Boolean): Boolean
    fun lookAt(sender: Any?, rotation: PLRotation?, animated: Boolean): Boolean
    fun lookAt(pitch: Float, yaw: Float): Boolean
    fun lookAt(sender: Any?, pitch: Float, yaw: Float): Boolean
    fun lookAt(pitch: Float, yaw: Float, animated: Boolean): Boolean
    fun lookAt(sender: Any?, pitch: Float, yaw: Float, animated: Boolean): Boolean

    /**
     * lookat and fov combined methods
     */
    fun lookAtAndFov(pitch: Float, yaw: Float, fov: Float, animated: Boolean): Boolean
    fun lookAtAndFov(sender: Any?, pitch: Float, yaw: Float, fov: Float, animated: Boolean): Boolean
    fun lookAtAndFovFactor(pitch: Float, yaw: Float, fovFactor: Float, animated: Boolean): Boolean
    fun lookAtAndFovFactor(
        sender: Any?,
        pitch: Float,
        yaw: Float,
        fovFactor: Float,
        animated: Boolean
    ): Boolean

    fun lookAtAndZoomFactor(pitch: Float, yaw: Float, zoomFactor: Float, animated: Boolean): Boolean
    fun lookAtAndZoomFactor(
        sender: Any?,
        pitch: Float,
        yaw: Float,
        zoomFactor: Float,
        animated: Boolean
    ): Boolean

    /**
     * rotate methods
     */
    fun rotate(sender: Any?, pitch: Float, yaw: Float)
    fun rotate(sender: Any?, pitch: Float, yaw: Float, roll: Float)
    fun rotate(startPoint: CGPoint?, endPoint: CGPoint?)
    fun rotate(sender: Any?, startPoint: CGPoint?, endPoint: CGPoint?)

    /**
     * clone methods
     */
    fun clone(): PLICamera
}
