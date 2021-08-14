package com.panoramagl

import com.panoramagl.enumerations.PLCameraAnimationType

internal class PLInternalCameraListener(private var view: PLManager) : PLCameraListener, PLIReleaseView {

    override fun didBeginAnimation(sender: Any, camera: PLICamera, type: PLCameraAnimationType) {
        when (type) {
            PLCameraAnimationType.PLCameraAnimationTypeLookAt -> view.isValidForCameraAnimation = true
            else -> Unit
        }
        view.listener?.onDidBeginCameraAnimation(view, sender, camera, type)
    }

    override fun didEndAnimation(sender: Any, camera: PLICamera, type: PLCameraAnimationType) {
        when (type) {
            PLCameraAnimationType.PLCameraAnimationTypeLookAt -> view.isValidForCameraAnimation = false
            else -> Unit
        }
        view.listener?.onDidEndCameraAnimation(view, sender, camera, type)
    }

    override fun didLookAt(
        sender: Any,
        camera: PLICamera,
        pitch: Float,
        yaw: Float,
        animated: Boolean
    ) {
        if (sender !== view)
            view.updateInitialSensorialRotation()
        view.listener?.onDidLookAtCamera(view, sender, camera, pitch, yaw, animated)
    }

    override fun didRotate(sender: Any, camera: PLICamera, pitch: Float, yaw: Float, roll: Float) {
        if (sender !== view) view.updateInitialSensorialRotation()
        view.listener?.onDidRotateCamera(view, sender, camera, pitch, yaw, roll)
    }

    override fun didFov(sender: Any, camera: PLICamera, fov: Float, animated: Boolean) {
        view.listener?.onDidFovCamera(view, sender, camera, fov, animated)
    }

    override fun didReset(sender: Any, camera: PLICamera) {
        if (sender !== view) view.updateInitialSensorialRotation()
        view.listener?.onDidResetCamera(view, sender, camera)
    }

    override fun releaseView() {
        //view = null
    }

}
