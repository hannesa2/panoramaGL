/*
 * PanoramaGL library
 * Version 0.2 beta
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

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import com.panoramagl.computation.PLMath
import com.panoramagl.downloaders.PLFileDownloaderManager
import com.panoramagl.downloaders.PLIFileDownloaderManager
import com.panoramagl.enumerations.PLCameraAnimationType
import com.panoramagl.enumerations.PLSensorialRotationType
import com.panoramagl.enumerations.PLTouchEventType
import com.panoramagl.enumerations.PLTouchStatus
import com.panoramagl.ios.NSTimer
import com.panoramagl.ios.UITouch
import com.panoramagl.ios.enumerations.UIDeviceOrientation
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.ios.structs.CGRect
import com.panoramagl.ios.structs.CGSize
import com.panoramagl.ios.structs.UIAcceleration
import com.panoramagl.loaders.PLILoader
import com.panoramagl.loaders.PLLoaderListener
import com.panoramagl.structs.PLRange.Companion.PLRangeMake
import com.panoramagl.structs.PLShakeData
import com.panoramagl.transitions.PLITransition
import com.panoramagl.transitions.PLTransitionListener
import timber.log.Timber
import javax.microedition.khronos.opengles.GL10
import kotlin.math.abs
import kotlin.math.min

@Suppress("unused")
open class PLActivity : AppCompatActivity(), PLIView, SensorEventListener, GestureDetector.OnDoubleTapListener {
    /**
     * member variables
     */
    private var mPanorama: PLIPanorama? = null
    protected var renderer: PLIRenderer? = null
        private set

    private var isRendererCreated = false

    private var validForCameraAnimation = false
    private var internalCameraListener: PLInternalCameraListener? = null

    private var mAnimationTimer: NSTimer? = null
    private var mAnimationInterval = 0f

    private var mAnimationFrameInterval = 0
    private var isAnimating = false

    private var mStartPoint: CGPoint? = null
    private var mEndPoint: CGPoint? = null
    private var mAuxiliarStartPoint: CGPoint? = null
    private var mAuxiliarEndPoint: CGPoint? = null

    private var mIsValidForFov = false
    private var mFovDistance = 0f
    private var mFovCounter = 0

    private var accelerometerEnabled = false
    private var accelerometerLeftRightEnabled = false
    private var accelerometerUpDownEnabled = false
    private var mAccelerometerSensitivity = 0f

    private var mIsValidForSensorialRotation = false
    protected var sensorialRotationType: PLSensorialRotationType? = null
        private set
    private var mSensorialRotationThresholdTimestamp: Long = 0
    private var mSensorialRotationThresholdFlag = false
    private var mSensorialRotationAccelerometerData: FloatArray? = null
    private var mSensorialRotationRotationMatrix: FloatArray? = null
    private var mSensorialRotationOrientationData: FloatArray? = null
    private var mHasFirstGyroscopePitch = false
    private var mHasFirstAccelerometerPitch = false
    private var mHasFirstMagneticHeading = false
    private var mFirstAccelerometerPitch = 0f
    private var mLastAccelerometerPitch = 0f
    private var mAccelerometerPitch = 0f
    private var mFirstMagneticHeading = 0f
    private var mLastMagneticHeading = 0f
    private var mMagneticHeading = 0f
    private var mGyroscopeLastTime: Long = 0
    private var mGyroscopeRotationX = 0f
    private var mGyroscopeRotationY = 0f

    private var mIsValidForScrolling = false
    private var scrollingEnabled = false
    private var mMinDistanceToEnableScrolling = 0

    private var mMinDistanceToEnableDrawing = 0

    private var mIsValidForInertia = false
    private var mIsInertiaEnabled = false
    private var mInertiaTimer: NSTimer? = null
    private var mInertiaInterval = 0f
    private var mInertiaStepValue = 0f

    private var mIsResetEnabled = false
    private var mIsShakeResetEnabled = false
    private var mNumberOfTouchesForReset = 0

    private var mShakeData: PLShakeData? = null
    private var mShakeThreshold = 0f

    private var mIsValidForTransition = false
    private var currentTransition: PLITransition? = null

    private var mIsValidForTouch = false
    private var mTouchStatus: PLTouchStatus? = null

    private var mCurrentDeviceOrientation: UIDeviceOrientation? = null

    private var mFileDownloaderManager: PLIFileDownloaderManager? = null

    protected var progressBar: ProgressBar? = null
        private set

    private var mListener: PLViewListener? = null
    private var mIsZoomEnabled = false
    private var mIsAcceleratedTouchScrollingEnabled = false

    protected fun initializeValues() {
        isRendererCreated = false

        validForCameraAnimation = false
        internalCameraListener = PLInternalCameraListener(this)

        mAnimationInterval = PLConstants.kDefaultAnimationTimerInterval
        mAnimationFrameInterval = PLConstants.kDefaultAnimationFrameInterval
        isAnimating = false

        mStartPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        mEndPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        mAuxiliarStartPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        mAuxiliarEndPoint = CGPoint.CGPointMake(0.0f, 0.0f)

        accelerometerEnabled = false
        accelerometerUpDownEnabled = true
        accelerometerLeftRightEnabled = accelerometerUpDownEnabled
        mAccelerometerSensitivity = PLConstants.kDefaultAccelerometerSensitivity

        sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow

        scrollingEnabled = false
        mMinDistanceToEnableScrolling = PLConstants.kDefaultMinDistanceToEnableScrolling

        mMinDistanceToEnableDrawing = PLConstants.kDefaultMinDistanceToEnableDrawing

        mIsInertiaEnabled = false
        mInertiaInterval = PLConstants.kDefaultInertiaInterval.toFloat()

        mIsResetEnabled = true
        mIsShakeResetEnabled = false
        mNumberOfTouchesForReset = PLConstants.kDefaultNumberOfTouchesForReset

        mShakeData = PLShakeData.PLShakeDataMake(0)
        mShakeThreshold = PLConstants.kShakeThreshold.toFloat()

        mIsValidForTransition = false

        mTouchStatus = PLTouchStatus.PLTouchStatusNone

        mCurrentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait

        mFileDownloaderManager = PLFileDownloaderManager()

        mIsZoomEnabled = true
        mIsAcceleratedTouchScrollingEnabled = true

        this.reset()

        this.panorama = PLBlankPanorama()
    }

    /**
     * reset methods
     */
    override fun reset(): Boolean {
        return this.reset(true)
    }

    override fun reset(resetCamera: Boolean): Boolean {
        if (!mIsValidForTransition) {
            this.stopInertia()
            mIsValidForTouch = false
            mIsValidForInertia = mIsValidForTouch
            mIsValidForScrolling = mIsValidForInertia
            mIsValidForFov = mIsValidForScrolling
            mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
            mAuxiliarStartPoint!!.setValues(mAuxiliarEndPoint!!.setValues(0.0f, 0.0f))
            mFovCounter = 0
            mFovDistance = 0.0f
            if (resetCamera && mPanorama != null) mPanorama!!.camera.reset(this)

            this.updateInitialSensorialRotation()
            return true
        }
        return false
    }

    /**
     * property methods
     */
    override fun getPanorama(): PLIPanorama {
        return mPanorama!!
    }

    override fun setPanorama(panorama: PLIPanorama?) {
        if (!mIsValidForTransition) {
            this.stopAnimation()
            if (panorama != null) {
                if (mPanorama != null) {
                    mPanorama!!.clear()
                    mPanorama!!.releaseView()
                    mPanorama = null
                }
                panorama.internalView = this
                panorama.internalCameraListener = internalCameraListener
                if (renderer != null) {
                    renderer!!.internalScene = panorama
                    renderer!!.resizeFromLayer()
                    mPanorama = panorama
                    this.startAnimation()
                } else {
                    renderer = PLRenderer(this, panorama)
                    renderer!!.internalListener = object : PLRendererListener {
                        override fun rendererFirstChanged(gl: GL10?, render: PLIRenderer?, width: Int, height: Int) {
                            glContext = gl
                            runOnUiThread { onGLContextCreated(glContext) }
                        }

                        override fun rendererDestroyed(render: PLIRenderer?) {
                        }

                        override fun rendererCreated(render: PLIRenderer?) {
                        }

                        override fun rendererChanged(render: PLIRenderer?, width: Int, height: Int) {
                            if (!isRendererCreated) {
                                isRendererCreated = true
                                startAnimation()
                            }
                        }
                    }
                    mGLSurfaceView = PLSurfaceView(this.applicationContext, renderer)
                    mPanorama = panorama
                    this.setContentView(this.onGLSurfaceViewCreated(mGLSurfaceView))
                }
            } else {
                if (mPanorama != null) {
                    mPanorama!!.clear()
                    mPanorama!!.releaseView()
                    mPanorama = null
                }
                if (renderer != null) renderer!!.internalScene = null
            }
        }
    }

    override fun getRenderingViewport(): CGRect {
        return (if (renderer != null) tempRenderingViewport!!.setValues(renderer!!.viewport) else tempRenderingViewport!!.reset())
    }

    override fun getRenderingSize(): CGSize {
        return (if (renderer != null) tempRenderingSize!!.setValues(renderer!!.size) else tempRenderingSize!!.reset())
    }

    override fun isRendererCreated(): Boolean {
        return isRendererCreated
    }

    override fun isValidForCameraAnimation(): Boolean {
        return validForCameraAnimation
    }

    protected fun setValidForCameraAnimation(isValidForCameraAnimation: Boolean) {
        validForCameraAnimation = isValidForCameraAnimation
    }

    override fun getCamera(): PLICamera? {
        return (if (mPanorama != null)
            mPanorama!!.camera else null)
    }

    override fun setCamera(camera: PLICamera) {
        if (mPanorama != null)
            mPanorama!!.camera = camera
    }

    protected var animationTimer: NSTimer?
        get() = mAnimationTimer
        protected set(timer) {
            if (mAnimationTimer != null) {
                mAnimationTimer!!.invalidate()
                mAnimationTimer = null
            }
            mAnimationTimer = timer
        }

    override fun getAnimationInterval(): Float {
        return mAnimationInterval
    }

    override fun setAnimationInterval(animationInterval: Float) {
        mAnimationInterval = animationInterval
    }

    override fun getAnimationFrameInterval(): Int {
        return mAnimationFrameInterval
    }

    override fun setAnimationFrameInterval(animationFrameInterval: Int) {
        if (animationFrameInterval >= 1) {
            mAnimationFrameInterval = animationFrameInterval
            mAnimationInterval = PLConstants.kDefaultAnimationTimerIntervalByFrame * animationFrameInterval
        }
    }

    override fun isAnimating(): Boolean {
        return isAnimating
    }

    override fun getStartPoint(): CGPoint {
        return mStartPoint!!
    }

    override fun setStartPoint(startPoint: CGPoint) {
        mStartPoint!!.setValues(startPoint)
    }

    override fun getEndPoint(): CGPoint {
        return mEndPoint!!
    }

    override fun setEndPoint(endPoint: CGPoint) {
        mEndPoint!!.setValues(endPoint)
    }

    protected var auxiliarStartPoint: CGPoint?
        get() = mAuxiliarStartPoint
        protected set(startPoint) {
            if (startPoint != null)
                mAuxiliarStartPoint!!.setValues(startPoint)
        }

    protected var auxiliarEndPoint: CGPoint?
        get() = mAuxiliarEndPoint
        protected set(endPoint) {
            if (endPoint != null)
                mAuxiliarEndPoint!!.setValues(endPoint)
        }

    override fun isValidForFov(): Boolean {
        return mIsValidForFov
    }

    protected fun setValidForFov(isValidForFov: Boolean) {
        mIsValidForFov = isValidForFov
    }

    override fun isAccelerometerEnabled(): Boolean {
        return accelerometerEnabled
    }

    override fun setAccelerometerEnabled(isAccelerometerEnabled: Boolean) {
        accelerometerEnabled = isAccelerometerEnabled
    }

    override fun isAccelerometerLeftRightEnabled(): Boolean {
        return accelerometerLeftRightEnabled
    }

    override fun setAccelerometerLeftRightEnabled(isAccelerometerLeftRightEnabled: Boolean) {
        accelerometerLeftRightEnabled = isAccelerometerLeftRightEnabled
    }

    override fun isAccelerometerUpDownEnabled(): Boolean {
        return accelerometerUpDownEnabled
    }

    override fun setAccelerometerUpDownEnabled(isAccelerometerUpDownEnabled: Boolean) {
        accelerometerUpDownEnabled = isAccelerometerUpDownEnabled
    }

    override fun getAccelerometerSensitivity(): Float {
        return mAccelerometerSensitivity
    }

    override fun setAccelerometerSensitivity(accelerometerSensitivity: Float) {
        mAccelerometerSensitivity = PLMath.valueInRange(
            accelerometerSensitivity,
            PLRangeMake(PLConstants.kAccelerometerSensitivityMinValue, PLConstants.kAccelerometerSensitivityMaxValue)
        )
    }

    override fun isValidForSensorialRotation(): Boolean {
        return mIsValidForSensorialRotation
    }

    override fun isValidForScrolling(): Boolean {
        return mIsValidForScrolling
    }

    protected fun setValidForScrolling(isValidForScrolling: Boolean) {
        mIsValidForScrolling = isValidForScrolling
    }

    override fun isScrollingEnabled(): Boolean {
        return scrollingEnabled
    }

    override fun setScrollingEnabled(isScrollingEnabled: Boolean) {
        scrollingEnabled = isScrollingEnabled
    }

    override fun getMinDistanceToEnableScrolling(): Int {
        return mMinDistanceToEnableScrolling
    }

    override fun setMinDistanceToEnableScrolling(minDistanceToEnableScrolling: Int) {
        if (minDistanceToEnableScrolling >= 0) mMinDistanceToEnableScrolling = minDistanceToEnableScrolling
    }

    override fun getMinDistanceToEnableDrawing(): Int {
        return mMinDistanceToEnableDrawing
    }

    override fun setMinDistanceToEnableDrawing(minDistanceToEnableDrawing: Int) {
        if (minDistanceToEnableDrawing > 0) mMinDistanceToEnableDrawing = minDistanceToEnableDrawing
    }

    override fun isValidForInertia(): Boolean {
        return mIsValidForInertia
    }

    protected fun setValidForInertia(isValidForInertia: Boolean) {
        mIsValidForInertia = isValidForInertia
    }

    override fun isInertiaEnabled(): Boolean {
        return mIsInertiaEnabled
    }

    override fun setInertiaEnabled(isInertiaEnabled: Boolean) {
        mIsInertiaEnabled = isInertiaEnabled
    }

    override fun getInertiaInterval(): Float {
        return mInertiaInterval
    }

    override fun setInertiaInterval(inertiaInterval: Float) {
        mInertiaInterval = inertiaInterval
    }

    override fun isResetEnabled(): Boolean {
        return mIsResetEnabled
    }

    override fun setResetEnabled(isResetEnabled: Boolean) {
        mIsResetEnabled = isResetEnabled
    }

    override fun isShakeResetEnabled(): Boolean {
        return mIsShakeResetEnabled
    }

    override fun setShakeResetEnabled(isShakeResetEnabled: Boolean) {
        mIsShakeResetEnabled = isShakeResetEnabled
    }

    override fun getNumberOfTouchesForReset(): Int {
        return mNumberOfTouchesForReset
    }

    override fun setNumberOfTouchesForReset(numberOfTouchesForReset: Int) {
        if (numberOfTouchesForReset > 2 && numberOfTouchesForReset <= kMaxTouches) mNumberOfTouchesForReset = numberOfTouchesForReset
    }

    override fun getShakeThreshold(): Float {
        return mShakeThreshold
    }

    override fun setShakeThreshold(shakeThreshold: Float) {
        if (shakeThreshold > 0.0f) mShakeThreshold = shakeThreshold
    }

    override fun isValidForTransition(): Boolean {
        return mIsValidForTransition
    }

    protected fun setValidForTransition(isValidForTransition: Boolean) {
        mIsValidForTransition = isValidForTransition
    }

    override fun getCurrentTransition(): PLITransition {
        return currentTransition!!
    }

    protected fun setCurrentTransition(transition: PLITransition?) {
        currentTransition = transition
    }

    override fun isValidForTouch(): Boolean {
        return mIsValidForTouch
    }

    protected fun setValidForTouch(isValidForTouch: Boolean) {
        mIsValidForTouch = isValidForTouch
    }

    override fun getTouchStatus(): PLTouchStatus {
        return mTouchStatus!!
    }

    protected fun setTouchStatus(touchStatus: PLTouchStatus?) {
        mTouchStatus = touchStatus
    }

    override fun getCurrentDeviceOrientation(): UIDeviceOrientation {
        return mCurrentDeviceOrientation!!
    }

    override fun getDownloadManager(): PLIFileDownloaderManager {
        return mFileDownloaderManager!!
    }

    override fun isProgressBarVisible(): Boolean {
        return (progressBar != null && progressBar!!.visibility != View.GONE)
    }

    override fun isLocked(): Boolean {
        return (mPanorama == null || mPanorama!!.isLocked)
    }

    override fun setLocked(isLocked: Boolean) {
        if (mPanorama != null) mPanorama!!.isLocked = isLocked
    }

    override fun getListener(): PLViewListener {
        return mListener!!
    }

    override fun setListener(listener: PLViewListener) {
        mListener = listener
    }

    /**
     * draw methods
     */
    protected fun drawView(): Boolean {
        if (isRendererCreated && renderer!!.isRunning && mPanorama != null) {
            if (!mIsValidForFov) mPanorama!!.camera.rotate(this, mStartPoint, mEndPoint)
            mGLSurfaceView!!.requestRender()
            return true
        }
        return false
    }

    /**
     * animation methods
     */
    override fun startAnimation(): Boolean {
        if (!isAnimating) {
            if (renderer != null) renderer!!.start()
            this.animationTimer = NSTimer.scheduledTimerWithTimeInterval(
                mAnimationInterval,
                { target, userInfo -> drawView() },
                null,
                true
            )
            isAnimating = true
            return true
        }
        return false
    }

    override fun stopAnimation(): Boolean {
        if (isAnimating) {
            this.stopInertia()
            this.animationTimer = null
            if (renderer != null) renderer!!.stop()
            if (currentTransition != null) currentTransition!!.stop()
            if (mPanorama != null) mPanorama!!.camera.stopAnimation(this)
            mIsValidForFov = false
            mIsValidForScrolling = mIsValidForFov
            mIsValidForTouch = mIsValidForScrolling
            isAnimating = mIsValidForTouch
            return true
        }
        return false
    }

    /**
     * fov methods
     */
    protected fun calculateFov(touches: List<UITouch?>?): Boolean {
        if (touches!!.size == 2 && isZoomEnabled) {
            mAuxiliarStartPoint!!.setValues(touches[0]!!.locationInView())
            mAuxiliarEndPoint!!.setValues(touches[1]!!.locationInView())

            mFovCounter++
            if (mFovCounter < PLConstants.kDefaultFovMinCounter) {
                if (mFovCounter == PLConstants.kDefaultFovMinCounter - 1) mFovDistance =
                    PLMath.distanceBetweenPoints(mAuxiliarStartPoint, mAuxiliarEndPoint)
                return false
            }

            val distance = PLMath.distanceBetweenPoints(mAuxiliarStartPoint, mAuxiliarEndPoint)
            val distanceDiff = distance - mFovDistance

            if (abs(distanceDiff.toDouble()) < mPanorama!!.camera.minDistanceToEnableFov) return false

            val isZoomIn = (distance > mFovDistance)
            var isNotCancelable = true

            if (mListener != null) isNotCancelable = mListener!!.onShouldRunZooming(this, distanceDiff, isZoomIn, !isZoomIn)

            if (isNotCancelable) {
                mFovDistance = distance
                mPanorama!!.camera.addFov(this, distanceDiff)
                if (mListener != null) mListener!!.onDidRunZooming(this, distanceDiff, isZoomIn, !isZoomIn)
                return true
            }
        }
        return false
    }

    /**
     * action methods
     */
    protected fun executeDefaultAction(touches: List<UITouch?>?, eventType: PLTouchEventType): Boolean {
        val touchCount = touches!!.size
        if (touchCount == mNumberOfTouchesForReset) {
            mIsValidForFov = false
            if (eventType == PLTouchEventType.PLTouchEventTypeBegan) this.executeResetAction(touches)
        } else if (touchCount == 2 && isZoomEnabled) {
            var isNotCancelable = true
            if (mListener != null) isNotCancelable = mListener!!.onShouldBeginZooming(this)
            if (isNotCancelable) {
                if (!mIsValidForFov) {
                    mFovCounter = 0
                    mIsValidForFov = true
                }
                if (eventType == PLTouchEventType.PLTouchEventTypeMoved) this.calculateFov(touches)
                else if (eventType == PLTouchEventType.PLTouchEventTypeBegan) {
                    mAuxiliarStartPoint!!.setValues(touches[0]!!.locationInView())
                    mAuxiliarEndPoint!!.setValues(touches[1]!!.locationInView())
                    if (mListener != null) mListener!!.onDidBeginZooming(this, mAuxiliarStartPoint, mAuxiliarEndPoint)
                }
            }
        } else if (touchCount == 1) {
            if (eventType == PLTouchEventType.PLTouchEventTypeMoved) {
                if (mIsValidForFov || (mStartPoint!!.x == 0.0f && mEndPoint!!.y == 0.0f)) mStartPoint!!.setValues(this.getLocationOfFirstTouch(touches))
            } else if (eventType == PLTouchEventType.PLTouchEventTypeEnded && mStartPoint!!.x == 0.0f && mEndPoint!!.y == 0.0f) mStartPoint!!.setValues(
                this.getLocationOfFirstTouch(touches)
            )
            mIsValidForFov = false
            return false
        }
        return true
    }

    protected fun executeResetAction(touches: List<UITouch?>?): Boolean {
        if (mIsResetEnabled && touches!!.size == mNumberOfTouchesForReset) {
            var isNotCancelable = true
            if (mListener != null) isNotCancelable = mListener!!.onShouldReset(this)
            if (isNotCancelable) {
                this.reset()
                if (mListener != null) mListener!!.onDidReset(this)
                return true
            }
        }
        return false
    }

    /**
     * touch methods
     */
    protected fun isTouchInView(touches: List<UITouch?>?): Boolean {
        var i = 0
        val touchesLength = touches!!.size
        while (i < touchesLength) {
            if (touches[i]!!.view !== mGLSurfaceView) return false
            i++
        }
        return true
    }

    protected fun getLocationOfFirstTouch(touches: List<UITouch?>?): CGPoint {
        return touches!![0]!!.locationInView()
    }

    protected fun touchesBegan(touches: List<UITouch?>?, event: MotionEvent?) {
        val listenerExists = (mListener != null)

        if (listenerExists) mListener!!.onTouchesBegan(this, touches, event)

        if (this.isLocked || validForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener!!.onShouldBeginTouching(
                this, touches, event
            ))
        ) return

        when (touches!![0]!!.tapCount) {
            1 -> {
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount
                if (mIsValidForScrolling) {
                    if (mInertiaTimer != null) this.stopInertia()
                    else {
                        mStartPoint!!.setValues(mEndPoint)
                        mIsValidForScrolling = false
                        if (listenerExists) mListener!!.onDidEndScrolling(this, mStartPoint, mEndPoint)
                    }
                }
            }

            2 -> mTouchStatus = PLTouchStatus.PLTouchStatusDoubleTapCount
        }
        mIsValidForTouch = true
        mTouchStatus = PLTouchStatus.PLTouchStatusBegan

        if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeBegan)) {
            mEndPoint!!.setValues(mStartPoint!!.setValues(this.getLocationOfFirstTouch(touches)))
            if (touches[0]!!.tapCount == 1) {
                mTouchStatus = PLTouchStatus.PLTouchStatusFirstSingleTapCount
                if (renderer != null && renderer!!.isRunning && mPanorama != null) mPanorama!!.waitingForClick = true
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount
            }
        }

        if (listenerExists) mListener!!.onDidBeginTouching(this, touches, event)
    }

    protected fun touchesMoved(touches: List<UITouch?>?, event: MotionEvent?) {
        val listenerExists = (mListener != null)

        if (listenerExists) mListener!!.onTouchesMoved(this, touches, event)

        if (this.isLocked || validForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener!!.onShouldMoveTouching(
                this, touches, event
            ))
        ) return

        mTouchStatus = PLTouchStatus.PLTouchStatusMoved

        if (!this.mIsAcceleratedTouchScrollingEnabled) {
            mStartPoint!!.setValues(mEndPoint)
        }

        if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeMoved)) mEndPoint!!.setValues(this.getLocationOfFirstTouch(touches))

        if (listenerExists) mListener!!.onDidMoveTouching(this, touches, event)
    }

    protected fun touchesEnded(touches: List<UITouch?>?, event: MotionEvent?) {
        val listenerExists = (mListener != null)

        if (listenerExists) mListener!!.onTouchesEnded(this, touches, event)

        if (this.isLocked || validForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener!!.onShouldEndTouching(
                this, touches, event
            ))
        ) {
            mIsValidForTouch = false
            return
        }

        var updateInitialSensorialRotation = mIsValidForSensorialRotation
        mTouchStatus = PLTouchStatus.PLTouchStatusEnded

        if (mIsValidForFov) {
            mIsValidForTouch = false
            mIsValidForFov = mIsValidForTouch
            mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
            if (listenerExists) mListener!!.onDidEndZooming(this)
        } else {
            if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeEnded)) {
                val endPoint = this.getLocationOfFirstTouch(touches)
                if (PLMath.distanceBetweenPoints(mStartPoint, endPoint) >= mMinDistanceToEnableDrawing) mEndPoint!!.setValues(endPoint)
                else mEndPoint!!.setValues(mStartPoint)

                var isNotCancelable = true
                var isNotValidAction = false

                if (scrollingEnabled && listenerExists) isNotCancelable = mListener!!.onShouldBeingScrolling(this, mStartPoint, mEndPoint)

                if (scrollingEnabled && isNotCancelable) {
                    val isValidForMoving = (PLMath.distanceBetweenPoints(mStartPoint, mEndPoint) >= mMinDistanceToEnableScrolling)
                    if (mIsInertiaEnabled) {
                        if (isValidForMoving) {
                            mIsValidForScrolling = true
                            isNotCancelable = true
                            if (listenerExists) {
                                mListener!!.onDidBeginScrolling(this, mStartPoint, mEndPoint)
                                isNotCancelable = mListener!!.onShouldBeginInertia(this, mStartPoint, mEndPoint)
                            }
                            if (isNotCancelable) {
                                updateInitialSensorialRotation = false
                                this.startInertia()
                            }
                        } else isNotValidAction = true
                    } else {
                        if (isValidForMoving) {
                            mIsValidForScrolling = true
                            mIsValidForTouch = false
                            if (listenerExists) mListener!!.onDidBeginScrolling(this, mStartPoint, mEndPoint)
                        } else isNotValidAction = true
                    }
                } else isNotValidAction = true
                if (isNotValidAction) {
                    mIsValidForTouch = false
                    mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
                }
            }
        }

        if (updateInitialSensorialRotation) this.updateInitialSensorialRotation()

        if (listenerExists) mListener!!.onDidEndTouching(this, touches, event)
    }

    /**
     * inertia methods
     */
    protected fun startInertia() {
        if (this.isLocked || mIsValidForInertia || mIsValidForTransition || (mListener != null && !mListener!!.onShouldRunInertia(
                this,
                mStartPoint,
                mEndPoint
            ))
        ) return

        mIsValidForInertia = true
        var interval = mInertiaInterval / PLMath.distanceBetweenPoints(mStartPoint, mEndPoint)
        if (interval < 0.01f) {
            mInertiaStepValue = 0.01f / interval
            interval = 0.01f
        } else mInertiaStepValue = 1.0f
        mInertiaTimer = NSTimer.scheduledTimerWithTimeInterval(
            interval,
            { target, userInfo -> inertia() },
            null,
            true
        )
        if (mListener != null) mListener!!.onDidBeginInertia(this, mStartPoint, mEndPoint)
    }

    protected fun inertia() {
        if (this.isLocked || validForCameraAnimation || mIsValidForTransition) return

        val m = (mEndPoint!!.y - mStartPoint!!.y) / (mEndPoint!!.x - mStartPoint!!.x)
        val b = (mStartPoint!!.y * mEndPoint!!.x - mEndPoint!!.y * mStartPoint!!.x) / (mEndPoint!!.x - mStartPoint!!.x)
        val x: Float
        val y: Float
        val add: Float

        if (abs((mEndPoint!!.x - mStartPoint!!.x).toDouble()) >= abs((mEndPoint!!.y - mStartPoint!!.y).toDouble())) {
            add = (if (mEndPoint!!.x > mStartPoint!!.x) -mInertiaStepValue else mInertiaStepValue)
            x = mEndPoint!!.x + add
            if ((add > 0.0f && x > mStartPoint!!.x) || (add <= 0.0f && x < mStartPoint!!.x)) {
                this.stopInertia()
                return
            }
            y = m * x + b
        } else {
            add = (if (mEndPoint!!.y > mStartPoint!!.y) -mInertiaStepValue else mInertiaStepValue)
            y = mEndPoint!!.y + add
            if ((add > 0.0f && y > mStartPoint!!.y) || (add <= 0.0f && y < mStartPoint!!.y)) {
                this.stopInertia()
                return
            }
            x = (y - b) / m
        }
        mEndPoint!!.setValues(x, y)
        if (mListener != null) mListener!!.onDidRunInertia(this, mStartPoint, mEndPoint)
    }

    protected fun stopInertia(): Boolean {
        if (mInertiaTimer != null) {
            mInertiaTimer!!.invalidate()
            mInertiaTimer = null
            mIsValidForInertia = false
            if (mListener != null) mListener!!.onDidEndInertia(this, mStartPoint, mEndPoint)
            this.updateInitialSensorialRotation()
            mIsValidForTouch = false
            mIsValidForScrolling = mIsValidForTouch
            if (mListener != null) mListener!!.onDidEndScrolling(this, mStartPoint, mEndPoint)
            mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
            return true
        }
        return false
    }

    /**
     * accelerometer methods
     */
    protected fun activateAccelerometer(): Boolean {
        if (sensorManager != null && sensorManager!!.registerListener(
                this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL
            )
        ) return true
        Timber.d("Accelerometer sensor is not available on the device!")
        return false
    }

    protected fun deactiveAccelerometer() {
        if (sensorManager != null) sensorManager!!.unregisterListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
    }

    protected fun accelerometer(event: SensorEvent?, acceleration: UIAcceleration) {
        if (this.isLocked || this.resetWithShake(acceleration) || mIsValidForTouch || mIsValidForScrolling || mIsValidForSensorialRotation || validForCameraAnimation || mIsValidForTransition) return

        if (accelerometerEnabled) {
            if (mListener != null && !mListener!!.onShouldAccelerate(this, acceleration, event)) return

            var x = 0.0f
            val y = (if (accelerometerUpDownEnabled) -acceleration.z else 0.0f)
            val factor =
                mAccelerometerSensitivity * (if (mPanorama!!.camera.isReverseRotation) -PLConstants.kAccelerometerMultiplyFactor else PLConstants.kAccelerometerMultiplyFactor)

            when (this.currentDeviceOrientation) {
                UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> if (accelerometerLeftRightEnabled) x =
                    acceleration.x

                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> if (accelerometerLeftRightEnabled) x = -acceleration.y
                UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> if (accelerometerLeftRightEnabled) x = acceleration.y
                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> if (accelerometerLeftRightEnabled) x = -acceleration.x
                else -> {}
            }
            val size = renderer!!.size
            mAuxiliarStartPoint!!.setValues((size.width shr 1).toFloat(), (size.height shr 1).toFloat())
            mAuxiliarEndPoint!!.setValues(mAuxiliarStartPoint!!.x + x * factor, mAuxiliarStartPoint!!.y + y * factor)
            mPanorama!!.camera.rotate(this, mAuxiliarStartPoint, mAuxiliarEndPoint)

            if (mListener != null) mListener!!.onDidAccelerate(this, acceleration, event)
        }
    }

    /**
     * gyroscope methods
     */
    protected fun activateGyroscope(): Boolean {
        return (sensorManager != null && sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            (PLConstants.kDefaultGyroscopeInterval * 1000.0f).toInt()
        ))
    }

    protected fun deactivateGyroscope() {
        if (sensorManager != null) sensorManager!!.unregisterListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_GYROSCOPE))
    }

    /**
     * magnetometer methods
     */
    protected fun activateMagnetometer(): Boolean {
        return (sensorManager != null && sensorManager!!.registerListener(
            this,
            sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            (PLConstants.kDefaultMagnetometerInterval * 1000.0f).toInt()
        ))
    }

    protected fun deactivateMagnetometer() {
        if (sensorManager != null) sensorManager!!.unregisterListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
    }

    /**
     * sensorial rotation methods
     */
    override fun startSensorialRotation(): Boolean {
        if (!mIsValidForSensorialRotation) {
            if (this.activateGyroscope()) {
                mHasFirstGyroscopePitch = false
                mGyroscopeLastTime = 0
                mGyroscopeRotationY = 0.0f
                mGyroscopeRotationX = mGyroscopeRotationY
                sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeGyroscope
                mIsValidForSensorialRotation = true
            } else {
                Timber.d("Gyroscope sensor is not available on device!")
                if (sensorManager != null && sensorManager!!.getSensorList(Sensor.TYPE_ACCELEROMETER).size > 0 && sensorManager!!.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size > 0) {
                    mSensorialRotationThresholdTimestamp = 0
                    mSensorialRotationThresholdFlag = false
                    mSensorialRotationAccelerometerData = FloatArray(3)
                    mSensorialRotationRotationMatrix = FloatArray(16)
                    mSensorialRotationOrientationData = FloatArray(3)
                    mHasFirstMagneticHeading = false
                    mHasFirstAccelerometerPitch = mHasFirstMagneticHeading
                    mAccelerometerPitch = 0.0f
                    mLastAccelerometerPitch = mAccelerometerPitch
                    mFirstAccelerometerPitch = mLastAccelerometerPitch
                    mMagneticHeading = 0.0f
                    mLastMagneticHeading = mMagneticHeading
                    mFirstMagneticHeading = mLastMagneticHeading
                    sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer
                    mIsValidForSensorialRotation = true
                    this.activateMagnetometer()
                } else Timber.d("Accelerometer or/and magnetometer sensor/s is/are not available on device!")
            }
            return mIsValidForSensorialRotation
        }
        return false
    }

    protected fun doGyroUpdate(pitch: Float, yaw: Float) {
        if (this.isLocked || mIsValidForTouch || mIsValidForScrolling || validForCameraAnimation || mIsValidForTransition || !mHasFirstGyroscopePitch) return

        mPanorama!!.camera.lookAt(this, pitch, yaw)
    }

    protected fun doSimulatedGyroUpdate() {
        if (this.isLocked || mIsValidForTouch || mIsValidForScrolling || validForCameraAnimation || mIsValidForTransition || !mHasFirstAccelerometerPitch || !mHasFirstMagneticHeading) return

        var step: Float
        var offset = abs((mLastAccelerometerPitch - mAccelerometerPitch).toDouble()).toFloat()
        if (offset < 0.25f) mAccelerometerPitch = mLastAccelerometerPitch
        else {
            step = (if (offset <= 10.0f) 0.25f else 1.0f)
            if (mLastAccelerometerPitch > mAccelerometerPitch) mAccelerometerPitch += step
            else if (mLastAccelerometerPitch < mAccelerometerPitch) mAccelerometerPitch -= step
        }
        offset = abs((mLastMagneticHeading - mMagneticHeading).toDouble()).toFloat()
        if (offset < 0.25f) mMagneticHeading = mLastMagneticHeading
        else {
            step = (if (offset <= 10.0f) 0.25f else 1.0f)
            if (mLastMagneticHeading > mMagneticHeading) mMagneticHeading += step
            else if (mLastMagneticHeading < mMagneticHeading) mMagneticHeading -= step
        }
        mPanorama!!.camera.lookAt(this, mAccelerometerPitch, mMagneticHeading)
    }

    override fun stopSensorialRotation(): Boolean {
        if (mIsValidForSensorialRotation) {
            mIsValidForSensorialRotation = false
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) this.deactivateGyroscope()
            else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                this.deactivateMagnetometer()
                mSensorialRotationOrientationData = null
                mSensorialRotationRotationMatrix = mSensorialRotationOrientationData
                mSensorialRotationAccelerometerData = mSensorialRotationRotationMatrix
            }
            sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow
            return true
        }
        return false
    }

    protected fun updateGyroscopeRotationByOrientation(currentOrientation: UIDeviceOrientation?, newOrientation: UIDeviceOrientation?) {
        val tempRotation: Float
        when (currentOrientation) {
            UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> when (newOrientation) {
                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = mGyroscopeRotationY
                    mGyroscopeRotationY = -tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = -mGyroscopeRotationY
                    mGyroscopeRotationY = tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> {
                    mGyroscopeRotationX = -mGyroscopeRotationX
                    mGyroscopeRotationY = -mGyroscopeRotationY
                }

                else -> {}
            }

            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> when (newOrientation) {
                UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = -mGyroscopeRotationY
                    mGyroscopeRotationY = tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = mGyroscopeRotationY
                    mGyroscopeRotationY = -tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> {
                    mGyroscopeRotationX = -mGyroscopeRotationX
                    mGyroscopeRotationY = -mGyroscopeRotationY
                }

                else -> {}
            }

            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> when (newOrientation) {
                UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = mGyroscopeRotationY
                    mGyroscopeRotationY = -tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = -mGyroscopeRotationY
                    mGyroscopeRotationY = tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> {
                    mGyroscopeRotationX = -mGyroscopeRotationX
                    mGyroscopeRotationY = -mGyroscopeRotationY
                }

                else -> {}
            }

            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> when (newOrientation) {
                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = -mGyroscopeRotationY
                    mGyroscopeRotationY = tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> {
                    tempRotation = mGyroscopeRotationX
                    mGyroscopeRotationX = mGyroscopeRotationY
                    mGyroscopeRotationY = -tempRotation
                }

                UIDeviceOrientation.UIDeviceOrientationPortrait -> {
                    mGyroscopeRotationX = -mGyroscopeRotationX
                    mGyroscopeRotationY = -mGyroscopeRotationY
                }

                else -> {}
            }

            else -> {}
        }
    }

    override fun updateInitialSensorialRotation(): Boolean {
        if (mIsValidForSensorialRotation) {
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) {
                mHasFirstGyroscopePitch = false
                return true
            } else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                mSensorialRotationThresholdTimestamp = 0
                mHasFirstMagneticHeading = false
                mHasFirstAccelerometerPitch = mHasFirstMagneticHeading
                mSensorialRotationThresholdFlag = mHasFirstAccelerometerPitch
                return true
            }
        }
        return false
    }

    /**
     * orientation methods
     */
    @Suppress("deprecation")
    protected fun activateOrientation(): Boolean {
        if (sensorManager != null && sensorManager!!.registerListener(
                this,
                sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME
            )
        ) return true
        Timber.d("Orientation sensor is not available on the device!")
        return false
    }

    protected fun deactiveOrientation() {
        if (sensorManager != null) sensorManager!!.unregisterListener(this, sensorManager!!.getDefaultSensor(Sensor.TYPE_ORIENTATION))
    }

    /**
     * shake methods
     */
    protected fun resetWithShake(acceleration: UIAcceleration?): Boolean {
        if (!mIsShakeResetEnabled || !mIsResetEnabled || this.isLocked || validForCameraAnimation || mIsValidForTransition) return false

        var result = false
        val currentTime = System.currentTimeMillis()

        if ((currentTime - mShakeData!!.lastTime) > PLConstants.kShakeDiffTime) {
            val diffTime = (currentTime - mShakeData!!.lastTime)
            mShakeData!!.lastTime = currentTime
            mShakeData!!.shakePosition.setValues(acceleration)

            val speed =
                (abs((mShakeData!!.shakePosition.x + mShakeData!!.shakePosition.y + mShakeData!!.shakePosition.z - mShakeData!!.shakeLastPosition.x - mShakeData!!.shakeLastPosition.y - mShakeData!!.shakeLastPosition.z).toDouble()) / diffTime * 10000).toFloat()

            if (speed > mShakeThreshold) {
                var isNotCancelable = true
                if (mListener != null) isNotCancelable = mListener!!.onShouldReset(this)
                if (isNotCancelable) {
                    this.reset()
                    if (mListener != null) mListener!!.onDidReset(this)
                    result = true
                }
            }

            mShakeData!!.shakeLastPosition.setValues(mShakeData!!.shakePosition)
        }
        return result
    }

    /**
     * transition methods
     */
    override fun startTransition(transition: PLITransition, newPanorama: PLIPanorama): Boolean {
        if (mIsValidForTransition || mPanorama == null || renderer == null) return false

        mIsValidForTransition = true

        this.stopInertia()
        mIsValidForFov = false
        mIsValidForScrolling = mIsValidForFov
        mIsValidForTouch = mIsValidForScrolling
        mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))

        currentTransition = transition
        currentTransition!!.internalListener = object : PLTransitionListener {
            override fun isRemovableListener(): Boolean {
                return true
            }

            override fun didBeginTransition(transition: PLITransition) {
                if (mListener != null) mListener!!.onDidBeginTransition(transition.view, transition)
            }

            override fun didProcessTransition(transition: PLITransition, progressPercentage: Int) {
                if (mListener != null) mListener!!.onDidProcessTransition(transition.view, transition, progressPercentage)
            }

            override fun didStopTransition(transition: PLITransition, progressPercentage: Int) {
                mIsValidForTransition = false
                currentTransition = null
                if (mListener != null) mListener!!.onDidStopTransition(transition.view, transition, progressPercentage)
            }

            override fun didEndTransition(transition: PLITransition) {
                mIsValidForTransition = false
                currentTransition = null
                panorama = transition.newPanorama
                if (mListener != null) mListener!!.onDidEndTransition(transition.view, transition)
            }
        }
        return currentTransition!!.start(this, newPanorama)
    }

    override fun stopTransition(): Boolean {
        if (mIsValidForTransition && currentTransition != null) {
            currentTransition!!.stop()
            return true
        }
        return false
    }

    /**
     * progress-bar methods
     */
    override fun showProgressBar(): Boolean {
        if (progressBar != null && progressBar!!.visibility == View.GONE) {
            progressBar!!.visibility = View.VISIBLE
            return true
        }
        return false
    }

    override fun hideProgressBar(): Boolean {
        if (progressBar != null && progressBar!!.visibility == View.VISIBLE) {
            progressBar!!.visibility = View.GONE
            return true
        }
        return false
    }

    /**
     * load methods
     */
    override fun load(loader: PLILoader) {
        this.load(loader, false, null, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue)
    }

    override fun load(loader: PLILoader, showProgressBar: Boolean) {
        this.load(loader, showProgressBar, null, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue)
    }

    override fun load(loader: PLILoader, showProgressBar: Boolean, transition: PLITransition) {
        this.load(loader, showProgressBar, transition, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue)
    }

    override fun load(loader: PLILoader, showProgressBar: Boolean, transition: PLITransition?, initialPitch: Float, initialYaw: Float) {
        mFileDownloaderManager!!.removeAll()
        loader.internalListener = object : PLLoaderListener {
            override fun didBegin(loader: PLILoader) {
                val listener: PLViewListener = listener
                listener.onDidBeginLoader(this@PLActivity, loader)
            }

            override fun didComplete(loader: PLILoader) {
                hideProgressBar()
                val listener: PLViewListener = listener
                listener.onDidCompleteLoader(this@PLActivity, loader)
            }

            override fun didStop(loader: PLILoader) {
                hideProgressBar()
                val listener: PLViewListener = listener
                listener.onDidStopLoader(this@PLActivity, loader)
            }

            override fun didError(loader: PLILoader, error: String) {
                hideProgressBar()
                val listener: PLViewListener = listener
                listener.onDidErrorLoader(this@PLActivity, loader, error)
            }
        }
        if (showProgressBar) {
            this.showProgressBar()
            Handler(Looper.getMainLooper()).postDelayed(
                { loader.load(this@PLActivity, transition, initialPitch, initialYaw) },
                300
            )
        } else loader.load(this, transition, initialPitch, initialYaw)
    }

    /**
     * clear methods
     */
    override fun clear() {
        if (mPanorama != null) {
            mFileDownloaderManager!!.removeAll()
            this.setPanorama(null)
        }
    }

    override fun isZoomEnabled(): Boolean {
        return mIsZoomEnabled
    }

    override fun setZoomEnabled(enabled: Boolean) {
        this.mIsZoomEnabled = enabled
    }

    override fun isAcceleratedTouchScrollingEnabled(): Boolean {
        return mIsAcceleratedTouchScrollingEnabled
    }

    override fun setAcceleratedTouchScrollingEnabled(enabled: Boolean) {
        this.mIsAcceleratedTouchScrollingEnabled = enabled
    }

    /**
     * dealloc methods
     */
    override fun onDestroy() {
        this.stopSensorialRotation()
        this.deactiveOrientation()
        this.deactiveAccelerometer()

        this.stopAnimation()

        mFileDownloaderManager!!.removeAll()

        if (mPanorama != null) mPanorama!!.clear()

        val releaseViewObjects: MutableList<PLIReleaseView?> = ArrayList()
        releaseViewObjects.add(mPanorama)
        releaseViewObjects.add(renderer)
        releaseViewObjects.add(internalCameraListener)
        releaseViewObjects.add(currentTransition)
        releaseViewObjects.addAll(internalTouches!!)
        releaseViewObjects.addAll(currentTouches!!)
        for (releaseViewObject in releaseViewObjects) releaseViewObject?.releaseView()
        releaseViewObjects.clear()

        super.onDestroy()
    }

    /**
     * internal classes declaration
     */
    protected inner class PLSurfaceView(context: Context?, renderer: Renderer?) : GLSurfaceView(context) {
        /**
         * init methods
         */
        init {
            this.setRenderer(renderer)
            this.renderMode = RENDERMODE_WHEN_DIRTY
        }
    }

    protected inner class PLInternalCameraListener
    /**
     * init methods
     */(
        /**
         * member variables
         */
        private var view: PLActivity?
    ) : PLCameraListener, PLIReleaseView {
        /**
         * PLCameraListener methods
         */
        override fun didBeginAnimation(sender: Any, camera: PLICamera, type: PLCameraAnimationType) {
            when (type) {
                PLCameraAnimationType.PLCameraAnimationTypeLookAt -> view!!.isValidForCameraAnimation = true
                else -> {}
            }
            val listener = view!!.listener
            listener.onDidBeginCameraAnimation(view, sender, camera, type)
        }

        override fun didEndAnimation(sender: Any, camera: PLICamera, type: PLCameraAnimationType) {
            when (type) {
                PLCameraAnimationType.PLCameraAnimationTypeLookAt -> view!!.isValidForCameraAnimation = false
                else -> {}
            }
            val listener = view!!.listener
            listener.onDidEndCameraAnimation(view, sender, camera, type)
        }

        override fun didLookAt(sender: Any, camera: PLICamera, pitch: Float, yaw: Float, animated: Boolean) {
            if (sender !== view) view!!.updateInitialSensorialRotation()
            val listener = view!!.listener
            listener.onDidLookAtCamera(view, sender, camera, pitch, yaw, animated)
        }

        override fun didRotate(sender: Any, camera: PLICamera, pitch: Float, yaw: Float, roll: Float) {
            if (sender !== view) view!!.updateInitialSensorialRotation()
            val listener = view!!.listener
            listener.onDidRotateCamera(view, sender, camera, pitch, yaw, roll)
        }

        override fun didFov(sender: Any, camera: PLICamera, fov: Float, animated: Boolean) {
            val listener = view!!.listener
            listener.onDidFovCamera(view, sender, camera, fov, animated)
        }

        override fun didReset(sender: Any, camera: PLICamera) {
            if (sender !== view) view!!.updateInitialSensorialRotation()
            val listener = view!!.listener
            listener.onDidResetCamera(view, sender, camera)
        }

        override fun releaseView() {
            view = null
        }

        @Throws(Throwable::class)
        protected fun finalize() {
            view = null
        }
    }

    /**
     * android: member variables
     */
    private var glContext: GL10? = null
    private var mGLSurfaceView: GLSurfaceView? = null
    protected var sensorManager: SensorManager? = null
        private set
    private var gestureDetector: GestureDetector? = null
    protected var contentLayout: ViewGroup? = null
        private set
    private var tempRenderingViewport: CGRect? = null
    private var tempRenderingSize: CGSize? = null
    private var tempSize: CGSize? = null
    private var tempAcceleration: UIAcceleration? = null
    private var internalTouches: MutableList<UITouch?>? = null
    private var currentTouches: MutableList<UITouch?>? = null
    private lateinit var location: IntArray

    /**
     * android: property methods
     */
    override fun getContext(): Activity {
        return this
    }

    override fun getGLContext(): GL10 {
        return glContext!!
    }

    override fun getGLSurfaceView(): GLSurfaceView {
        return mGLSurfaceView!!
    }

    override fun getSize(): CGSize {
        val displayMetrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return tempSize!!.setValues(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    protected fun getTouches(event: MotionEvent): List<UITouch?>? {
        return this.getTouches(event, 1)
    }

    protected fun getTouches(event: MotionEvent, tapCount: Int): List<UITouch?>? {
        mGLSurfaceView!!.getLocationOnScreen(location)
        val top = location[1]
        val left = location[0]
        currentTouches!!.clear()
        var i = 0
        val length = min(event.pointerCount.toDouble(), kMaxTouches.toDouble()).toInt()
        while (i < length) {
            val touch = internalTouches!![i]
            touch!!.setPosition(event.getX(i) - left, event.getY(i) - top)
            touch.tapCount = tapCount
            currentTouches!!.add(touch)
            i++
        }
        return currentTouches
    }

    /**
     * android: events methods
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            location = IntArray(2)
            gestureDetector = GestureDetector(
                this,
                object : SimpleOnGestureListener() {
                    override fun onDoubleTap(event: MotionEvent): Boolean {
                        return this@PLActivity.onDoubleTap(event)
                    }

                    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
                        return this@PLActivity.onDoubleTapEvent(event)
                    }

                    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                        return this@PLActivity.onSingleTapConfirmed(event)
                    }
                }
            )
            tempRenderingViewport = CGRect()
            tempRenderingSize = CGSize()
            tempSize = CGSize()
            tempAcceleration = UIAcceleration()
            internalTouches = ArrayList(kMaxTouches)
            currentTouches = ArrayList(kMaxTouches)
            this.initializeValues()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    /**
     * This event is fired when GLSurfaceView is created
     *
     * @param glSurfaceView current GLSurfaceView
     */
    @Suppress("deprecation")
    protected fun onGLSurfaceViewCreated(glSurfaceView: GLSurfaceView?): View {
        for (i in 0 until kMaxTouches) internalTouches!!.add(UITouch(glSurfaceView, CGPoint(0.0f, 0.0f)))
        contentLayout = RelativeLayout(this)
        contentLayout!!.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT))
        contentLayout!!.addView(
            glSurfaceView,
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT)
        )
        val progressBarLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        progressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        progressBar = ProgressBar(this)
        progressBar!!.isIndeterminate = true
        progressBar!!.visibility = View.GONE
        contentLayout!!.addView(progressBar, progressBarLayoutParams)
        return this.onContentViewCreated(contentLayout!!)
    }

    /**
     * This event is fired when root content view is created
     *
     * @param contentView current root content view
     * @return root content view that Activity will use
     */
    protected fun onContentViewCreated(contentView: View): View {
        return contentView
    }

    /**
     * This event is fired when OpenGL context is created
     *
     * @param gl current OpenGL context
     */
    protected fun onGLContextCreated(gl: GL10?) {
    }

    override fun onResume() {
        super.onResume()
        if (isRendererCreated && mPanorama != null) this.startAnimation()
        this.activateOrientation()
        this.activateAccelerometer()
        if (mIsValidForSensorialRotation) {
            this.updateInitialSensorialRotation()
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) this.activateGyroscope()
            else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                mSensorialRotationThresholdTimestamp = System.currentTimeMillis() + 1000
                this.activateMagnetometer()
            }
        }
    }

    override fun onPause() {
        this.deactiveAccelerometer()
        this.deactiveOrientation()
        if (mIsValidForSensorialRotation) {
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) this.deactivateGyroscope()
            else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) this.deactivateMagnetometer()
        }
        if (currentTransition != null) currentTransition!!.end()
        this.stopAnimation()
        super.onPause()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    @Suppress("deprecation")
    override fun onSensorChanged(event: SensorEvent) {
        val values = event.values
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> if (isRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (mSensorialRotationAccelerometerData != null) {
                    mSensorialRotationAccelerometerData!![0] = values[0]
                    mSensorialRotationAccelerometerData!![1] = values[1]
                    mSensorialRotationAccelerometerData!![2] = values[2]
                }
                this.accelerometer(event, tempAcceleration!!.setValues(values))
            }

            Sensor.TYPE_ORIENTATION -> {
                var newOrientation = mCurrentDeviceOrientation
                when (this.resources.configuration.orientation) {
                    Configuration.ORIENTATION_PORTRAIT -> when (this.windowManager.defaultDisplay.orientation) {
                        Surface.ROTATION_0, Surface.ROTATION_90 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait
                        Surface.ROTATION_180, Surface.ROTATION_270 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown
                    }

                    Configuration.ORIENTATION_LANDSCAPE -> when (this.windowManager.defaultDisplay.orientation) {
                        Surface.ROTATION_0, Surface.ROTATION_90 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeLeft
                        Surface.ROTATION_180, Surface.ROTATION_270 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeRight
                    }
                }
                if (mCurrentDeviceOrientation != newOrientation) {
                    if (mIsValidForSensorialRotation && sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) this.updateGyroscopeRotationByOrientation(
                        mCurrentDeviceOrientation,
                        newOrientation
                    )
                    mCurrentDeviceOrientation = newOrientation
                }
            }

            Sensor.TYPE_MAGNETIC_FIELD -> if (isRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (mIsValidForSensorialRotation && sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer && mSensorialRotationAccelerometerData != null) {
                    if (mSensorialRotationThresholdFlag) {
                        SensorManager.getRotationMatrix(mSensorialRotationRotationMatrix, null, mSensorialRotationAccelerometerData, values)
                        SensorManager.remapCoordinateSystem(
                            mSensorialRotationRotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            mSensorialRotationRotationMatrix
                        )
                        SensorManager.getOrientation(mSensorialRotationRotationMatrix, mSensorialRotationOrientationData)
                        var yaw = mSensorialRotationOrientationData!![0] * PLConstants.kToDegrees
                        var pitch = -mSensorialRotationOrientationData!![1] * PLConstants.kToDegrees
                        if (mHasFirstMagneticHeading) {
                            if ((pitch >= 0.0f && pitch < 50.0f) || (pitch < 0.0f && pitch > -50.0f)) {
                                yaw -= mFirstMagneticHeading
                                val diff = yaw - mLastMagneticHeading
                                if (abs(diff.toDouble()) > 100.0f) {
                                    mLastMagneticHeading = yaw
                                    mMagneticHeading += (if (diff >= 0.0f) 360.0f else -360.0f)
                                } else if ((yaw > mLastMagneticHeading && yaw - PLConstants.kSensorialRotationYawErrorMargin > mLastMagneticHeading) || (yaw < mLastMagneticHeading && yaw + PLConstants.kSensorialRotationYawErrorMargin < mLastMagneticHeading)) mLastMagneticHeading =
                                    yaw
                            }
                        } else {
                            val cameraYaw = mPanorama!!.camera.lookAtRotation!!.yaw
                            mFirstMagneticHeading = yaw - cameraYaw
                            mMagneticHeading = cameraYaw
                            mLastMagneticHeading = mMagneticHeading
                            mHasFirstMagneticHeading = true
                        }
                        if (mHasFirstAccelerometerPitch) {
                            pitch -= mFirstAccelerometerPitch
                            if ((pitch > mLastAccelerometerPitch && pitch - PLConstants.kSensorialRotationPitchErrorMargin > mLastAccelerometerPitch) || (pitch < mLastAccelerometerPitch && pitch + PLConstants.kSensorialRotationPitchErrorMargin < mLastAccelerometerPitch)) mLastAccelerometerPitch =
                                pitch
                        } else {
                            val cameraPitch = mPanorama!!.camera.lookAtRotation!!.pitch
                            mFirstAccelerometerPitch = pitch - cameraPitch
                            mAccelerometerPitch = cameraPitch
                            mLastAccelerometerPitch = mAccelerometerPitch
                            mHasFirstAccelerometerPitch = true
                        }
                        this.doSimulatedGyroUpdate()
                    } else {
                        if (mSensorialRotationThresholdTimestamp == 0L) mSensorialRotationThresholdTimestamp = System.currentTimeMillis()
                        else if ((System.currentTimeMillis() - mSensorialRotationThresholdTimestamp) >= PLConstants.kSensorialRotationThreshold) mSensorialRotationThresholdFlag =
                            true
                    }
                }
            }

            Sensor.TYPE_GYROSCOPE -> if (isRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (mHasFirstGyroscopePitch) {
                    if (mGyroscopeLastTime != 0L) {
                        var timeDiff = (event.timestamp - mGyroscopeLastTime) * PLConstants.kGyroscopeTimeConversion
                        if (timeDiff > 1.0) timeDiff = PLConstants.kGyroscopeMinTimeStep
                        mGyroscopeRotationX += values[0] * timeDiff
                        mGyroscopeRotationY += values[1] * timeDiff
                        when (mCurrentDeviceOrientation) {
                            UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> this.doGyroUpdate(
                                mGyroscopeRotationX * PLConstants.kToDegrees,
                                -mGyroscopeRotationY * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> this.doGyroUpdate(
                                -mGyroscopeRotationY * PLConstants.kToDegrees,
                                -mGyroscopeRotationX * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> this.doGyroUpdate(
                                mGyroscopeRotationY * PLConstants.kToDegrees,
                                mGyroscopeRotationX * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> this.doGyroUpdate(
                                -mGyroscopeRotationX * PLConstants.kToDegrees,
                                mGyroscopeRotationY * PLConstants.kToDegrees
                            )

                            else -> {}
                        }
                    }
                } else {
                    val cameraRotation = mPanorama!!.camera.lookAtRotation
                    when (mCurrentDeviceOrientation) {
                        UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> {
                            mGyroscopeRotationX = cameraRotation!!.pitch * PLConstants.kToRadians
                            mGyroscopeRotationY = -cameraRotation.yaw * PLConstants.kToRadians
                        }

                        UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> {
                            mGyroscopeRotationX = -cameraRotation!!.yaw * PLConstants.kToRadians
                            mGyroscopeRotationY = -cameraRotation.pitch * PLConstants.kToRadians
                        }

                        UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> {
                            mGyroscopeRotationX = cameraRotation!!.yaw * PLConstants.kToRadians
                            mGyroscopeRotationY = cameraRotation.pitch * PLConstants.kToRadians
                        }

                        UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> {
                            mGyroscopeRotationX = -cameraRotation!!.pitch * PLConstants.kToRadians
                            mGyroscopeRotationY = cameraRotation.yaw * PLConstants.kToRadians
                        }

                        else -> {}
                    }
                    mHasFirstGyroscopePitch = true
                }
                mGyroscopeLastTime = event.timestamp
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
            if (gestureDetector!!.onTouchEvent(event)) return true
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    this.touchesBegan(this.getTouches(event), event)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    this.touchesMoved(this.getTouches(event), event)
                    return true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    this.touchesEnded(this.getTouches(event), event)
                    return true
                }
            }
        }
        return false
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        this.touchesBegan(this.getTouches(event, 2), event)
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        return false
    }

    companion object {
        // ============================
        // Specific methods for Android
        // ============================
        /**
         * android: constants
         */
        private const val kMaxTouches = 10
    }
}