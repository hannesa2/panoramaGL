/*
 * PanoramaGL library
 * Version 0.2 beta
 * Copyright (c) 2010 Javier Baez <javbaezga@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.panoramagl

import com.panoramagl.computation.PLMath
import com.panoramagl.enumerations.PLCameraAnimationType
import com.panoramagl.ios.NSTimer
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLRange.Companion.PLRangeMake
import com.panoramagl.structs.PLRotation
import com.panoramagl.utils.PLUtils
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

class PLCamera : PLRenderableElementBase, PLICamera {
    /**
     * member variables
     */
    private var mIsNotLocked = false
    private var mIsFovEnabled = false
    private var mFov = 0f
    private var mInitialFov = 0f
    private var mFovSensitivity = 0f
    protected var fovSensitivityByDisplayPPI: Float = 0f
        private set
    private var mFovRange: PLRange? = null
    private var mMinDistanceToEnableFov = 0
    private var mRotationSensitivity = 0f
    protected var rotationSensitivityByDisplayPPI: Float = 0f
        private set
    private var mZoomLevels = 0
    private var mInitialLookAt: PLRotation? = null
    private var mLookAtRotation: PLRotation? = null
    override var isAnimating: Boolean = false
        private set
    private var mAnimationType: PLCameraAnimationType? = null
    private var mAnimationTimer: NSTimer? = null
    override var internalListener: PLCameraListener? = null
    private var mListener: PLCameraListener? = null
    private var mRotationMatrix: FloatArray? = null

    constructor() : super()

    constructor(camera: PLICamera) : super() {
        this.clonePropertiesOf(camera)
    }

    override fun initializeValues() {
        mIsFovEnabled = true
        mIsNotLocked = mIsFovEnabled
        mFovRange = PLRangeMake(PLConstants.kDefaultFovMinValue, PLConstants.kDefaultFovMaxValue)
        mInitialFov = PLMath.normalizeFov(PLConstants.kDefaultFov, mFovRange)
        mFov = mInitialFov
        this.setInternalFovSensitivity(PLConstants.kDefaultFovSensitivity)
        mMinDistanceToEnableFov = PLConstants.kDefaultMinDistanceToEnableFov
        this.setInternalRotationSensitivity(PLConstants.kDefaultRotationSensitivity)
        mZoomLevels = PLConstants.kDefaultZoomLevels
        mInitialLookAt = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f)
        mLookAtRotation = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f)
        this.isAnimating = false
        mAnimationType = PLCameraAnimationType.PLCameraAnimationTypeNone
        mAnimationTimer = null
        super.initializeValues()
        this.isReverseRotation = true
    }

    /**
     * reset methods
     */
    override fun reset() {
        this.reset(Unit)
    }

    override fun reset(sender: Any) {
        if (mIsNotLocked) {
            super.reset()
            this.internalStopAnimation(sender)
            this.setInternalFov(sender, mInitialFov, false, true, false)
            this.internalLookAt(sender, mInitialLookAt!!.pitch, mInitialLookAt!!.yaw, false, true, false)
            if (this.internalListener != null) internalListener!!.didReset(sender, this)
            if (mListener != null) mListener!!.didReset(sender, this)
        }
    }

    override var isLocked: Boolean
        get() = !mIsNotLocked
        set(isLocked) {
            mIsNotLocked = !isLocked
        }

    override var isFovEnabled: Boolean
        get() = mIsFovEnabled
        set(isFovEnabled) {
            if (mIsNotLocked) mIsFovEnabled = isFovEnabled
        }

    override var initialFov: Float
        get() = mInitialFov
        set(initialFov) {
            if (mIsNotLocked) mInitialFov = PLMath.normalizeFov(initialFov, mFovRange)
        }

    override var fov: Float
        get() = mFov
        set(fov) {
            if (mIsNotLocked)
                this.setInternalFov(Unit, fov, false, true, false)
        }

    override var fovFactor: Float
        get() = ((mFov - mFovRange!!.max) / (mFovRange!!.max - mFovRange!!.min) + 1.0f)
        set(fovFactor) {
            this.setFovFactor(Unit, fovFactor, false)
        }

    override var fovSensitivity: Float
        get() = mFovSensitivity
        set(fovSensitivity) {
            if (mIsNotLocked && fovSensitivity >= PLConstants.kFovSensitivityMinValue && fovSensitivity <= PLConstants.kFovSensitivityMaxValue) this.setInternalFovSensitivity(
                fovSensitivity
            )
        }

    protected fun setInternalFovSensitivity(fovSensitivity: Float) {
        if (mFovSensitivity != fovSensitivity) {
            mFovSensitivity = fovSensitivity
            this.fovSensitivityByDisplayPPI = PLConstants.kDisplayPPIBaseline / PLUtils.getDisplayPPI() * fovSensitivity
        }
    }

    override var fovRange: PLRange?
        get() = mFovRange
        set(range) {
            this.setFovRange(range!!.min, range.max)
        }

    override fun setFovRange(min: Float, max: Float) {
        if (mIsNotLocked && max >= min) {
            mFovRange!!.setValues(
                (if (min < PLConstants.kFovMinValue) PLConstants.kFovMinValue else min),
                (if (max > PLConstants.kFovMaxValue) PLConstants.kFovMaxValue else max)
            )
            this.initialFov = mInitialFov
            this.setInternalFov(Unit, mFov, false, true, false)
        }
    }

    override var fovMin: Float
        get() = mFovRange!!.min
        set(min) {
            if (mIsNotLocked && mFovRange!!.max >= min) {
                mFovRange!!.min = (if (min < PLConstants.kFovMinValue) PLConstants.kFovMinValue else min)
                this.initialFov = mInitialFov
                this.setInternalFov(Unit, mFov, false, true, false)
            }
        }

    override var fovMax: Float
        get() = mFovRange!!.max
        set(max) {
            if (mIsNotLocked && max >= mFovRange!!.min) {
                mFovRange!!.max = (if (max > PLConstants.kFovMaxValue) PLConstants.kFovMaxValue else max)
                this.initialFov = mInitialFov
                this.setInternalFov(Unit, mFov, false, true, false)
            }
        }

    override var minDistanceToEnableFov: Int
        get() = mMinDistanceToEnableFov
        set(distance) {
            if (mIsNotLocked && distance > 0) mMinDistanceToEnableFov = distance
        }

    override var rotationSensitivity: Float
        get() = mRotationSensitivity
        set(rotationSensitivity) {
            if (mIsNotLocked && rotationSensitivity >= PLConstants.kRotationSensitivityMinValue && rotationSensitivity <= PLConstants.kRotationSensitivityMaxValue) this.setInternalRotationSensitivity(
                rotationSensitivity
            )
        }

    protected fun setInternalRotationSensitivity(rotationSensitivity: Float) {
        if (mRotationSensitivity != rotationSensitivity) {
            mRotationSensitivity = rotationSensitivity
            this.rotationSensitivityByDisplayPPI = PLConstants.kDisplayPPIBaseline / PLUtils.getDisplayPPI() * rotationSensitivity
        }
    }

    override var zoomFactor: Float
        get() = (1.0f - this.fovFactor)
        set(zoomFactor) {
            this.setZoomFactor(Unit, zoomFactor, false)
        }

    override var zoomLevel: Int
        get() = (if (mFovRange!!.min != mFovRange!!.max) Math.round((mFovRange!!.max - mFov) / ((mFovRange!!.max - mFovRange!!.min) / mZoomLevels)) else 0)
        set(zoomLevel) {
            this.setZoomLevel(Unit, zoomLevel, false)
        }

    override var zoomLevels: Int
        get() = mZoomLevels
        set(zoomLevels) {
            if (mIsNotLocked && zoomLevels > 0) mZoomLevels = zoomLevels
        }

    override var initialLookAt: PLRotation?
        get() = mInitialLookAt
        set(rotation) {
            if (rotation != null) {
                this.initialPitch = rotation.pitch
                this.initialYaw = rotation.yaw
            }
        }

    override fun setInitialLookAt(pitch: Float, yaw: Float) {
        this.initialPitch = pitch
        this.initialYaw = yaw
    }

    override var initialPitch: Float
        get() = mInitialLookAt!!.pitch
        set(pitch) {
            if (mIsNotLocked) mInitialLookAt!!.pitch = this.getRotationAngleNormalized(pitch, this.pitchRange)
        }

    override var initialYaw: Float
        get() = mInitialLookAt!!.yaw
        set(yaw) {
            if (mIsNotLocked) mInitialLookAt!!.yaw = this.getRotationAngleNormalized(yaw, this.yawRange)
        }

    override val lookAtRotation: PLRotation?
        get() {
            val rotation = this.rotation
            return (if (this.isReverseRotation) mLookAtRotation!!.setValues(rotation) else mLookAtRotation!!.setValues(
                -rotation.pitch,
                -rotation.yaw,
                -rotation.roll
            ))
        }

    protected var animationTimer: NSTimer?
        get() = mAnimationTimer
        set(timer) {
            if (mAnimationTimer != null) {
                mAnimationTimer!!.invalidate()
                mAnimationTimer = null
            }
            mAnimationTimer = timer
        }

    override fun setVisible(isVisible: Boolean) {
        if (mIsNotLocked) super.setVisible(isVisible)
    }

    override var x: Float
        get() = super.x
        set(value) {
            if (mIsNotLocked)
                this.setInternalX(value)
        }

    override var y: Float
        get() = super.y
        set(value) {
            if (mIsNotLocked)
                this.setInternalY(value)
        }

    override var z: Float
        get() = super.z
        set(value) {
            if (mIsNotLocked)
                this.setInternalZ(value)
        }

    override var pitch: Float
        get() = super.pitch
        set(value) {
            if (mIsNotLocked)
                this.setInternalPitch(value)
        }

    override var yaw: Float
        get() = super.yaw
        set(value) {
            if (mIsNotLocked)
                this.setInternalYaw(value)
        }

    override var roll: Float
        get() = super.roll
        set(value) {
            if (mIsNotLocked) {
                mRotationMatrix = null
                this.setInternalRoll(value)
            }
        }

    override var listener: PLCameraListener?
        get() = mListener
        set(listener) {
            if (mIsNotLocked) mListener = listener
        }

    /**
     * animation methods
     */
    protected fun internalStartAnimation(sender: Any, timer: NSTimer?, type: PLCameraAnimationType): Boolean {
        if (!this.isAnimating) {
            this.isAnimating = true
            mAnimationType = type
            this.animationTimer = timer
            if (this.internalListener != null) internalListener!!.didBeginAnimation(sender, this, type)
            if (mListener != null) mListener!!.didBeginAnimation(sender, this, type)
            return true
        }
        return false
    }

    protected fun internalStopAnimation(sender: Any): Boolean {
        if (this.isAnimating) {
            this.isAnimating = false
            this.animationTimer = null
            if (this.internalListener != null) internalListener!!.didEndAnimation(sender, this, mAnimationType!!)
            if (mListener != null) mListener!!.didEndAnimation(sender, this, mAnimationType!!)
            mAnimationType = PLCameraAnimationType.PLCameraAnimationTypeNone
            return true
        }
        return false
    }

    override fun stopAnimation(): Boolean {
        return (if (mIsNotLocked) this.internalStopAnimation(Unit) else false)
    }

    override fun stopAnimation(sender: Any): Boolean {
        return (if (mIsNotLocked) this.internalStopAnimation(sender) else false)
    }

    /**
     * conversion methods
     */
    protected fun convertFovFactorToFov(fovFactor: Float): Float {
        return (mFovRange!!.max - ((1.0f - fovFactor) * (mFovRange!!.max - mFovRange!!.min)))
    }

    /**
     * fov methods
     */
    override fun setFov(fov: Float, animated: Boolean): Boolean {
        return this.setFov(Unit, fov, animated)
    }

    override fun setFov(sender: Any, fov: Float, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            if (animated) {
                if (!this.isAnimating && mIsFovEnabled) {
                    val newFov = PLMath.normalizeFov(fov, mFovRange)
                    if (mFov != newFov) {
                        this.internalStartAnimation(
                            sender,
                            NSTimer.scheduledTimerWithTimeInterval(
                                PLConstants.kCameraFovAnimationTimerInterval,
                                object : NSTimer.Runnable {
                                    override fun run(target: NSTimer?, userInfo: Array<Any?>) {
                                        val camera = userInfo[0] as PLCamera
                                        val data: PLFovAnimatedData = userInfo[1] as PLFovAnimatedData
                                        data.currentFov += data.fovStep
                                        camera.setInternalFov(data.sender!!, data.currentFov, true, false, true)
                                        data.currentStep++
                                        if (data.currentStep >= data.maxStep) {
                                            camera.internalStopAnimation(data.sender!!)
                                            camera.setInternalFov(data.sender!!, data.maxFov, true, true, true)
                                        }
                                    }
                                },
                                arrayOf<Any>(
                                    this,
                                    PLFovAnimatedData.PLFovAnimatedDataMake(sender, this, newFov, PLConstants.kCameraFovAnimationMaxStep)
                                ),
                                true
                            ),
                            PLCameraAnimationType.PLCameraAnimationTypeFov
                        )
                        return true
                    }
                }
            } else return this.setInternalFov(sender, fov, false, true, false)
        }
        return false
    }

    protected fun setInternalFov(sender: Any, fov: Float, skipFovEnabled: Boolean, fireEvent: Boolean, isEventAnimated: Boolean): Boolean {
        if (skipFovEnabled || mIsFovEnabled) {
            val newFov = PLMath.normalizeFov(fov, mFovRange)
            if (mFov != newFov) {
                mFov = newFov
                if (fireEvent) {
                    if (this.internalListener != null) internalListener!!.didFov(sender, this, newFov, isEventAnimated)
                    if (mListener != null) mListener!!.didFov(sender, this, newFov, isEventAnimated)
                }
                return true
            }
        }
        return false
    }

    override fun setFovFactor(fovFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f) this.setFov(
            Unit,
            this.convertFovFactorToFov(fovFactor),
            animated
        ) else false)
    }

    override fun setFovFactor(sender: Any, fovFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f) this.setFov(
            sender,
            this.convertFovFactorToFov(fovFactor),
            animated
        ) else false)
    }

    override fun addFov(distance: Float): Boolean {
        return this.addFov(Unit, distance)
    }

    override fun addFov(sender: Any, distance: Float): Boolean {
        return (if (mIsNotLocked) this.setInternalFov(
            sender,
            mFov - (distance / PLConstants.kDisplayPPIBaseline * this.fovSensitivityByDisplayPPI),
            false,
            true,
            false
        ) else false)
    }

    /**
     * zoom methods
     */
    override fun setZoomFactor(zoomFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f) this.setFovFactor(Unit, 1.0f - zoomFactor, animated) else false)
    }

    override fun setZoomFactor(sender: Any, zoomFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f) this.setFovFactor(sender, 1.0f - zoomFactor, animated) else false)
    }

    override fun setZoomLevel(zoomLevel: Int, animated: Boolean): Boolean {
        return this.setZoomLevel(Unit, zoomLevel, animated)
    }

    override fun setZoomLevel(sender: Any, zoomLevel: Int, animated: Boolean): Boolean {
        if (mIsNotLocked && zoomLevel >= 0 && zoomLevel <= mZoomLevels) return this.setFov(
            sender,
            mFovRange!!.max - (((mFovRange!!.max - mFovRange!!.min) / mZoomLevels) * zoomLevel),
            animated
        )
        return false
    }

    override fun zoomIn(animated: Boolean): Boolean {
        return this.setZoomLevel(Unit, this.zoomLevel + 1, animated)
    }

    override fun zoomIn(sender: Any, animated: Boolean): Boolean {
        return this.setZoomLevel(sender, this.zoomLevel + 1, animated)
    }

    override fun zoomOut(animated: Boolean): Boolean {
        return this.setZoomLevel(Unit, this.zoomLevel - 1, animated)
    }

    override fun zoomOut(sender: Any, animated: Boolean): Boolean {
        return this.setZoomLevel(sender, this.zoomLevel - 1, animated)
    }

    /**
     * rotation matrix methods
     */
    override fun hasRotationMatrix(): Boolean {
        return mRotationMatrix != null
    }

    override fun getRotationMatrix(): FloatArray? {
        return mRotationMatrix
    }

    override fun setRotationMatrix(matrix: FloatArray?) {
        if (mIsNotLocked && matrix != null && matrix.size == 16) {
            if (mRotationMatrix == null) {
                mRotationMatrix = FloatArray(16)
            }
            System.arraycopy(matrix, 0, mRotationMatrix, 0, 16)
        }
    }

    override fun clearRotationMatrix() {
        mRotationMatrix = null
    }

    /**
     * lookat methods
     */
    override fun lookAt(rotation: PLRotation): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            return this.internalLookAt(Unit, rotation.pitch, rotation.yaw, false, true, false)
        }
        return false
    }

    override fun lookAt(sender: Any, rotation: PLRotation): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            return this.internalLookAt(sender, rotation.pitch, rotation.yaw, false, true, false)
        }
        return false
    }

    override fun lookAt(rotation: PLRotation, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
        }
        return this.lookAt(Unit, rotation.pitch, rotation.yaw, animated)
    }

    override fun lookAt(sender: Any, rotation: PLRotation, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
        }
        return this.lookAt(sender, rotation.pitch, rotation.yaw, animated)
    }

    override fun lookAt(pitch: Float, yaw: Float): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            return this.internalLookAt(Unit, pitch, yaw, false, true, false)
        }
        return false
    }

    override fun lookAt(sender: Any, pitch: Float, yaw: Float): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            return this.internalLookAt(sender, pitch, yaw, false, true, false)
        }
        return false
    }

    override fun lookAt(pitch: Float, yaw: Float, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
        }
        return this.lookAt(Unit, pitch, yaw, animated)
    }

    override fun lookAt(sender: Any, pitch: Float, yaw: Float, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            if (animated) {
                if (!this.isAnimating && this.isPitchEnabled && this.isYawEnabled) {
                    this.internalStartAnimation(
                        sender,
                        NSTimer.scheduledTimerWithTimeInterval(
                            PLConstants.kCameraLookAtAnimationTimerInterval,
                            object : NSTimer.Runnable {
                                override fun run(target: NSTimer?, userInfo: Array<Any?>) {
                                    val camera = userInfo[0] as PLCamera
                                    val data: PLLookAtAnimatedData = userInfo[1] as PLLookAtAnimatedData
                                    data.currentPitch += data.pitchStep
                                    data.currentYaw += data.yawStep
                                    camera.internalLookAt(data.sender!!, data.currentPitch, data.currentYaw, true, false, true)
                                    data.currentStep++
                                    if (data.currentStep >= data.maxStep) {
                                        camera.internalStopAnimation(data.sender!!)
                                        camera.internalLookAt(data.sender!!, data.maxPitch, data.maxYaw, true, true, true)
                                    }
                                }
                            },
                            arrayOf<Any>(
                                this,
                                PLLookAtAnimatedData.PLLookAtAnimatedDataMake(
                                    sender,
                                    this,
                                    pitch,
                                    yaw,
                                    PLConstants.kCameraLookAtAnimationMaxStep
                                )
                            ),
                            true
                        ),
                        PLCameraAnimationType.PLCameraAnimationTypeLookAt
                    )
                    return true
                }
            } else return this.internalLookAt(sender, pitch, yaw, false, true, false)
        }
        return false
    }

    protected fun internalLookAt(
        sender: Any,
        pitch: Float,
        yaw: Float,
        skipRotationEnabled: Boolean,
        fireEvent: Boolean,
        isEventAnimated: Boolean
    ): Boolean {
        var pitch = pitch
        var yaw = yaw
        if (skipRotationEnabled || (this.isPitchEnabled && this.isYawEnabled)) {
            if (!this.isReverseRotation) {
                pitch = -pitch
                yaw = -yaw
            }
            this.setInternalPitch(pitch)
            this.setInternalYaw(yaw)
            if (fireEvent) {
                pitch = this.pitch
                yaw = this.yaw
                if (this.internalListener != null) internalListener!!.didLookAt(sender, this, pitch, yaw, isEventAnimated)
                if (mListener != null) mListener!!.didLookAt(sender, this, pitch, yaw, isEventAnimated)
            }
            return true
        }
        return false
    }

    /**
     * lookat and fov combined methods
     */
    override fun lookAtAndFov(pitch: Float, yaw: Float, fov: Float, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
        }
        return this.lookAtAndFov(Unit, pitch, yaw, fov, animated)
    }

    override fun lookAtAndFov(sender: Any, pitch: Float, yaw: Float, fov: Float, animated: Boolean): Boolean {
        if (mIsNotLocked) {
            mRotationMatrix = null
            if (animated) {
                if (!this.isAnimating && this.isPitchEnabled && this.isYawEnabled && mIsFovEnabled) {
                    this.internalStartAnimation(
                        sender,
                        NSTimer.scheduledTimerWithTimeInterval(
                            PLConstants.kCameraLookAtAnimationTimerInterval,
                            object : NSTimer.Runnable {
                                override fun run(target: NSTimer?, userInfo: Array<Any?>) {
                                    val camera = userInfo[0] as PLCamera
                                    val data: PLLookAtAndFovAnimatedData = userInfo[1] as PLLookAtAndFovAnimatedData
                                    data.currentPitch += data.pitchStep
                                    data.currentYaw += data.yawStep
                                    data.currentFov += data.fovStep
                                    camera.internalLookAt(data.sender!!, data.currentPitch, data.currentYaw, true, false, true)
                                    camera.setInternalFov(data.sender!!, data.currentFov, true, false, true)
                                    data.currentStep++
                                    if (data.currentStep >= data.maxStep) {
                                        camera.internalStopAnimation(data.sender!!)
                                        camera.internalLookAt(data.sender!!, data.maxPitch, data.maxYaw, true, true, true)
                                        camera.setInternalFov(data.sender!!, data.maxFov, true, true, true)
                                    }
                                }
                            },
                            arrayOf<Any>(
                                this,
                                PLLookAtAndFovAnimatedData.PLLookAtAndFovAnimatedDataMake(
                                    sender,
                                    this,
                                    pitch,
                                    yaw,
                                    fov,
                                    PLConstants.kCameraLookAtAnimationMaxStep
                                )
                            ),
                            true
                        ),
                        PLCameraAnimationType.PLCameraAnimationTypeLookAt
                    )
                    return true
                }
            } else return (this.internalLookAt(sender, pitch, yaw, false, true, false) && this.setInternalFov(sender, fov, false, true, false))
        }
        return false
    }

    override fun lookAtAndFovFactor(pitch: Float, yaw: Float, fovFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f) this.lookAtAndFov(
            Unit,
            pitch,
            yaw,
            this.convertFovFactorToFov(fovFactor),
            animated
        ) else false)
    }

    override fun lookAtAndFovFactor(sender: Any, pitch: Float, yaw: Float, fovFactor: Float, animated: Boolean): Boolean {
        return if (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f) this.lookAtAndFov(
            sender,
            pitch,
            yaw,
            this.convertFovFactorToFov(fovFactor),
            animated
        ) else
            false
    }

    override fun lookAtAndZoomFactor(pitch: Float, yaw: Float, zoomFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f) this.lookAtAndFov(
            Unit,
            pitch,
            yaw,
            this.convertFovFactorToFov(1.0f - zoomFactor),
            animated
        ) else false)
    }

    override fun lookAtAndZoomFactor(sender: Any, pitch: Float, yaw: Float, zoomFactor: Float, animated: Boolean): Boolean {
        return (if (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f) this.lookAtAndFov(
            sender,
            pitch,
            yaw,
            this.convertFovFactorToFov(1.0f - zoomFactor),
            animated
        ) else false)
    }

    /**
     * rotate methods
     */
    override fun rotate(pitch: Float, yaw: Float) {
        this.rotate(Unit, pitch, yaw)
    }

    override fun rotate(sender: Any, pitch: Float, yaw: Float) {
        var pitch = pitch
        var yaw = yaw
        if (mIsNotLocked) {
            super.rotate(pitch, yaw)
            pitch = this.pitch
            yaw = this.yaw
            val roll = this.roll
            if (this.internalListener != null) internalListener!!.didRotate(sender, this, pitch, yaw, roll)
            if (mListener != null) mListener!!.didRotate(sender, this, pitch, yaw, roll)
        }
    }

    override fun rotate(pitch: Float, yaw: Float, roll: Float) {
        this.rotate(Unit, pitch, yaw, roll)
    }

    override fun rotate(sender: Any, pitch: Float, yaw: Float, roll: Float) {
        var pitch = pitch
        var yaw = yaw
        var roll = roll
        if (mIsNotLocked) {
            mRotationMatrix = null
            super.rotate(pitch, yaw, roll)
            pitch = this.pitch
            yaw = this.yaw
            roll = this.roll
            if (this.internalListener != null) internalListener!!.didRotate(sender, this, pitch, yaw, roll)
            if (mListener != null) mListener!!.didRotate(sender, this, pitch, yaw, roll)
        }
    }

    override fun rotate(startPoint: CGPoint, endPoint: CGPoint) {
        if (mIsNotLocked) {
            mRotationMatrix = null
        }
        this.rotate(Unit, startPoint, endPoint)
    }

    override fun rotate(sender: Any, startPoint: CGPoint, endPoint: CGPoint) {
        if (mIsNotLocked) {
            val yOffset = endPoint.y - startPoint.y
            val xOffset = startPoint.x - endPoint.x
            val didRotatePitch = (yOffset != 0.0f)
            val didRotateYaw = (xOffset != 0.0f)
            if (didRotatePitch || didRotateYaw) {
                mRotationMatrix = null
                val rotationSensitivity = mFov / PLConstants.kFovBaseline * this.rotationSensitivityByDisplayPPI
                if (didRotatePitch) this.pitch += ((yOffset / PLConstants.kMaxDisplaySize * rotationSensitivity))
                if (didRotateYaw) this.yaw += ((xOffset / PLConstants.kMaxDisplaySize * rotationSensitivity))
                val pitch = this.pitch
                val yaw = this.yaw
                val roll = this.roll
                if (this.internalListener != null)
                    internalListener!!.didRotate(sender, this, pitch, yaw, roll)
                if (mListener != null)
                    mListener!!.didRotate(sender, this, pitch, yaw, roll)
            }
        }
    }

    override fun internalClear() = Unit

    override fun rotate(gl: GL10) {
        if (mRotationMatrix != null) {
            gl.glMultMatrixf(mRotationMatrix, 0)
        } else {
            super.rotate(gl)
        }
    }

    override fun beginRender(gl: GL10, renderer: PLIRenderer) {
        this.rotate(gl)
        this.translate(gl)
    }

    override fun internalRender(gl: GL10, renderer: PLIRenderer) = Unit

    override fun endRender(gl: GL10, renderer: PLIRenderer) = Unit

    override fun clonePropertiesOf(pLIObject: PLIObject): Boolean {
        if (mIsNotLocked && super.clonePropertiesOf(pLIObject)) {
            if (pLIObject is PLICamera) {
                this.fovRange = pLIObject.fovRange
                this.fovSensitivity = pLIObject.fovSensitivity
                this.minDistanceToEnableFov = pLIObject.minDistanceToEnableFov
                this.initialFov = pLIObject.initialFov
                this.isFovEnabled = pLIObject.isFovEnabled
                this.setInternalFov(Unit, pLIObject.fov, true, false, false)
                this.rotationSensitivity = pLIObject.rotationSensitivity
                this.zoomLevels = pLIObject.zoomLevels
                this.initialLookAt = pLIObject.initialLookAt
                this.listener = pLIObject.listener
            }
            return true
        }
        return false
    }

    override fun clone(): PLICamera {
        return PLCamera(this)
    }

    /**
     * dealloc methods
     */
    @Throws(Throwable::class)
    override fun finalize() {
        this.internalStopAnimation(Unit)
        mLookAtRotation = null
        mInitialLookAt = mLookAtRotation
        mFovRange = null
        mListener = null
        this.internalListener = mListener
        mRotationMatrix = null
        super.finalize()
    }

    /**
     * internal classes declaration
     */
    protected open class PLAnimatedDataBase(var sender: Any?) {
        var currentStep: Int = 0
        var maxStep: Int = 0

        /**
         * dealloc methods
         */
        @Throws(Throwable::class)
        protected open fun finalize() {
            sender = null
        }
    }

    protected class PLFovAnimatedData(sender: Any?, camera: PLCamera, fov: Float, defaultMaxStep: Int) : PLAnimatedDataBase(sender) {
        var currentFov: Float = camera.fov
        var maxFov: Float
        var fovStep: Float

        /**
         * init methods
         */
        init {
            maxFov = PLMath.normalizeFov(fov, camera.fovRange)
            val fovDiff = maxFov - currentFov
            val maxDiff = PLConstants.kFovMaxValue - abs(fovDiff)
            maxStep = max(sqrt((defaultMaxStep * defaultMaxStep * abs(1.0f - maxDiff * maxDiff / PLConstants.kFovMax2Value)).toDouble()).toInt(), 1)
            fovStep = fovDiff / maxStep
        }

        companion object {
            fun PLFovAnimatedDataMake(sender: Any?, camera: PLCamera, fov: Float, defaultMaxStep: Int): PLFovAnimatedData {
                return PLFovAnimatedData(sender, camera, fov, defaultMaxStep)
            }
        }
    }

    protected open class PLLookAtAnimatedData(sender: Any?, camera: PLCamera, pitch: Float, yaw: Float, defaultMaxStep: Int) :
        PLAnimatedDataBase(sender) {
        /**
         * member variables
         */
        var currentPitch: Float
        var maxPitch: Float
        var pitchStep: Float
        var currentYaw: Float
        var maxYaw: Float
        var yawStep: Float = 0f

        /**
         * init methods
         */
        init {
            val rotation = camera.lookAtRotation
            currentPitch = rotation!!.pitch
            currentYaw = rotation.yaw
            maxPitch = camera.getRotationAngleNormalized(pitch, camera.pitchRange)
            maxYaw = camera.getRotationAngleNormalized(yaw, camera.yawRange)
            val pitchDiff = camera.getRotationAngleNormalized(maxPitch - currentPitch, camera.pitchRange)
            val yawDiff = camera.getRotationAngleNormalized(maxYaw - currentYaw, camera.yawRange)
            val maxDiff = PLConstants.kRotationMaxValue - abs(if (abs(pitchDiff) > abs(yawDiff)) pitchDiff else yawDiff)
            maxStep =
                max(sqrt((defaultMaxStep * defaultMaxStep * abs(1.0f - maxDiff * maxDiff / PLConstants.kRotationMax2Value)).toDouble()).toInt(), 1)
            pitchStep = pitchDiff / maxStep
            if (yawDiff > 180.0f) yawStep = (yawDiff - 360.0f) / maxStep
            else if (yawDiff < -180.0f) yawStep = (360.0f - yawDiff) / maxStep
            else yawStep = yawDiff / maxStep
        }

        companion object {
            fun PLLookAtAnimatedDataMake(sender: Any?, camera: PLCamera, pitch: Float, yaw: Float, defaultMaxStep: Int): PLLookAtAnimatedData {
                return PLLookAtAnimatedData(sender, camera, pitch, yaw, defaultMaxStep)
            }
        }
    }

    protected class PLLookAtAndFovAnimatedData(sender: Any?, camera: PLCamera, pitch: Float, yaw: Float, fov: Float, defaultMaxStep: Int) :
        PLLookAtAnimatedData(sender, camera, pitch, yaw, defaultMaxStep) {
        /**
         * member variables
         */
        var currentFov: Float
        var maxFov: Float
        var fovStep: Float

        /**
         * init methods
         */
        init {
            currentFov = camera.fov
            maxFov = PLMath.normalizeFov(fov, camera.fovRange)
            fovStep = (maxFov - currentFov) / maxStep
        }

        companion object {
            fun PLLookAtAndFovAnimatedDataMake(
                sender: Any?,
                camera: PLCamera,
                pitch: Float,
                yaw: Float,
                fov: Float,
                defaultMaxStep: Int
            ): PLLookAtAndFovAnimatedData {
                return PLLookAtAndFovAnimatedData(sender, camera, pitch, yaw, fov, defaultMaxStep)
            }
        }
    }
}