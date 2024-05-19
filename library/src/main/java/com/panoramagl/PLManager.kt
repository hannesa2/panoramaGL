package com.panoramagl

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.opengl.GLSurfaceView
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.view.*
import android.view.GestureDetector.OnDoubleTapListener
import android.view.GestureDetector.SimpleOnGestureListener
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.core.app.ActivityCompat
import com.panoramagl.computation.PLMath
import com.panoramagl.downloaders.PLFileDownloaderManager
import com.panoramagl.downloaders.PLIFileDownloaderManager
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
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLShakeData
import com.panoramagl.transitions.PLITransition
import com.panoramagl.transitions.PLTransitionListener
import timber.log.Timber
import javax.microedition.khronos.opengles.GL10

@Suppress("unused")
open class PLManager(private val context: Context) : PLIView, SensorEventListener, OnDoubleTapListener {
    private var isValidForCameraAnimation: Boolean = false
    private var viewContainer: ViewGroup? = null

    private var mGLContext: GL10? = null
    private var mGLSurfaceView: GLSurfaceView? = null
    var sensorManager: SensorManager
        private set
    var sensorDelay : Int
    private var gestureDetector: GestureDetector? = null
    var contentLayout: ViewGroup? = null
        private set
    private var mTempRenderingViewport: CGRect? = null
    private var mTempRenderingSize: CGSize? = null
    private var mTempSize: CGSize? = null
    private var mTempAcceleration: UIAcceleration? = null
    private var mInternalTouches: MutableList<UITouch?>? = null
    private var mCurrentTouches: MutableList<UITouch?>? = null
    private var mLocation: IntArray = intArrayOf()

    private var mPanorama: PLIPanorama? = null
    protected var renderer: PLIRenderer? = null
        private set
    private var mIsRendererCreated = false
    private var internalCameraListener: PLInternalCameraListener? = null
    private var mAnimationTimer: NSTimer? = null
    private var mAnimationInterval = 0f
    private var mAnimationFrameInterval = 0
    private var isAnimating = false
    private var mStartPoint: CGPoint? = null
    private var mEndPoint: CGPoint? = null
    private var auxiliarStartPoint: CGPoint? = null
    private var auxiliarEndPoint: CGPoint? = null
    private var mIsValidForFov = false
    private var mFovDistance = 0f
    private var mFovCounter = 0
    private var mIsAccelerometerEnabled = false
    private var mIsAccelerometerLeftRightEnabled = false
    private var mIsAccelerometerUpDownEnabled = false
    private var mAccelerometerSensitivity = 0f
    private var mIsValidForSensorialRotation = false
    protected var sensorialRotationType: PLSensorialRotationType? = null
        private set
    private var sensorialRotationThresholdTimestamp: Long = 0
    private var mSensorialRotationThresholdFlag = false
    private var sensorialRotationAccelerometerData: FloatArray? = null
    private var sensorialRotationRotationMatrix: FloatArray? = null
    private var sensorialRotationOrientationData: FloatArray? = null
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
    private var mIsScrollingEnabled = false
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
    private var mCurrentTransition: PLITransition? = null
    private var mIsValidForTouch = false
    private var mTouchStatus: PLTouchStatus? = null
    private var mCurrentDeviceOrientation: UIDeviceOrientation? = null
    private var mFileDownloaderManager: PLIFileDownloaderManager? = null
    protected var progressBar: ProgressBar? = null
        private set
    private var mListener: PLViewListener? = null
    private var mIsZoomEnabled = false
    private var mIsAcceleratedTouchScrollingEnabled = true

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorDelay = if (ActivityCompat.checkSelfPermission(context,android.Manifest.permission.HIGH_SAMPLING_RATE_SENSORS) == PackageManager.PERMISSION_GRANTED)
            SensorManager.SENSOR_DELAY_FASTEST
        else
            SensorManager.SENSOR_DELAY_GAME
    }

    fun onCreate() {
        try {
            gestureDetector = GestureDetector(
                context,
                object : SimpleOnGestureListener() {
                    override fun onDoubleTap(event: MotionEvent): Boolean {
                        return this@PLManager.onDoubleTap(event)
                    }

                    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
                        return this@PLManager.onDoubleTapEvent(event)
                    }

                    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                        return this@PLManager.onSingleTapConfirmed(event)
                    }
                }
            )
            mTempRenderingViewport = CGRect()
            mTempRenderingSize = CGSize()
            mTempSize = CGSize()
            mTempAcceleration = UIAcceleration()
            mInternalTouches = ArrayList(kMaxTouches)
            mCurrentTouches = ArrayList(kMaxTouches)
            mLocation = IntArray(2)
            initializeValues()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    /**
     * init methods
     */
    protected fun initializeValues() {
        mIsRendererCreated = false
        isValidForCameraAnimation = false
        internalCameraListener = PLInternalCameraListener(this)
        mAnimationInterval = PLConstants.kDefaultAnimationTimerInterval
        mAnimationFrameInterval = PLConstants.kDefaultAnimationFrameInterval
        isAnimating = false
        mStartPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        mEndPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        auxiliarStartPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        auxiliarEndPoint = CGPoint.CGPointMake(0.0f, 0.0f)
        mIsAccelerometerEnabled = false
        mIsAccelerometerUpDownEnabled = true
        mIsAccelerometerLeftRightEnabled = mIsAccelerometerUpDownEnabled
        mAccelerometerSensitivity = PLConstants.kDefaultAccelerometerSensitivity
        sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow
        mIsScrollingEnabled = false
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
            stopInertia()
            mIsValidForTouch = false
            mIsValidForInertia = mIsValidForTouch
            mIsValidForScrolling = mIsValidForInertia
            mIsValidForFov = mIsValidForScrolling
            mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
            auxiliarStartPoint!!.setValues(auxiliarEndPoint!!.setValues(0.0f, 0.0f))
            mFovCounter = 0
            mFovDistance = 0.0f
            if (resetCamera && mPanorama != null) mPanorama!!.camera.reset(this)
            updateInitialSensorialRotation()
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
            stopAnimation()
            if (panorama != null) {
                if (mPanorama != null) {
                    mPanorama!!.clear()
                    mPanorama!!.releaseView()
                    mPanorama = null
                }
                panorama.internalView = this
                panorama.internalCameraListener = internalCameraListener
                if (renderer != null) {
                    renderer?.internalScene = panorama
                    renderer?.resizeFromLayer()
                    mPanorama = panorama
                    startAnimation()
                } else {
                    renderer = PLRenderer(this, panorama)
                    renderer?.internalListener = object : PLRendererListener {
                        override fun rendererFirstChanged(gl: GL10?, render: PLIRenderer?, width: Int, height: Int) {
                            mGLContext = gl
                            Handler(context.mainLooper).post { onGLContextCreated() }
                        }

                        override fun rendererDestroyed(render: PLIRenderer?) {}
                        override fun rendererCreated(render: PLIRenderer?) {}
                        override fun rendererChanged(render: PLIRenderer?, width: Int, height: Int) {
                            if (!mIsRendererCreated) {
                                mIsRendererCreated = true
                                startAnimation()
                            }
                        }
                    }
                    mGLSurfaceView = PLSurfaceView(getContext(), renderer!!)
                    mPanorama = panorama
                    onGLSurfaceViewCreated(mGLSurfaceView)
                }
            } else {
                mPanorama?.clear()
                mPanorama?.releaseView()
                renderer?.internalScene = null
            }
        }
    }

    override fun getRenderingViewport(): CGRect {
        return if (renderer != null) mTempRenderingViewport!!.setValues(renderer!!.viewport) else mTempRenderingViewport!!.reset()
    }

    override fun getRenderingSize(): CGSize {
        return if (renderer != null) mTempRenderingSize!!.setValues(renderer!!.size) else mTempRenderingSize!!.reset()
    }

    override fun isRendererCreated(): Boolean {
        return mIsRendererCreated
    }

    override fun isValidForCameraAnimation(): Boolean {
        return isValidForCameraAnimation
    }

    override fun getCamera(): PLICamera? {
        return if (mPanorama != null)
            mPanorama!!.camera
        else
            null
    }

    override fun setCamera(camera: PLICamera) {
        mPanorama?.camera = camera
    }

    var animationTimer: NSTimer?
        get() = mAnimationTimer
        set(timer) {
            mAnimationTimer?.invalidate()
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

    protected var auxiliaryStartPoint: CGPoint?
        get() = auxiliarStartPoint
        protected set(startPoint) {
            if (startPoint != null) auxiliarStartPoint!!.setValues(startPoint)
        }
    protected var auxiliaryEndPoint: CGPoint?
        get() = auxiliarEndPoint
        protected set(endPoint) {
            if (endPoint != null) auxiliarEndPoint!!.setValues(endPoint)
        }

    override fun isValidForFov(): Boolean {
        return mIsValidForFov
    }

    protected fun setValidForFov(isValidForFov: Boolean) {
        mIsValidForFov = isValidForFov
    }

    override fun isAccelerometerEnabled(): Boolean {
        return mIsAccelerometerEnabled
    }

    override fun setAccelerometerEnabled(enabled: Boolean) {
        mIsAccelerometerEnabled = enabled
    }

    override fun isAccelerometerLeftRightEnabled(): Boolean {
        return mIsAccelerometerLeftRightEnabled
    }

    override fun setAccelerometerLeftRightEnabled(isAccelerometerLeftRightEnabled: Boolean) {
        mIsAccelerometerLeftRightEnabled = isAccelerometerLeftRightEnabled
    }

    override fun isAccelerometerUpDownEnabled(): Boolean {
        return mIsAccelerometerUpDownEnabled
    }

    override fun setAccelerometerUpDownEnabled(isAccelerometerUpDownEnabled: Boolean) {
        mIsAccelerometerUpDownEnabled = isAccelerometerUpDownEnabled
    }

    override fun getAccelerometerSensitivity(): Float {
        return mAccelerometerSensitivity
    }

    override fun setAccelerometerSensitivity(accelerometerSensitivity: Float) {
        mAccelerometerSensitivity = PLMath.valueInRange(
            accelerometerSensitivity,
            PLRange.PLRangeMake(PLConstants.kAccelerometerSensitivityMinValue, PLConstants.kAccelerometerSensitivityMaxValue)
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
        return mIsScrollingEnabled
    }

    override fun setScrollingEnabled(isScrollingEnabled: Boolean) {
        mIsScrollingEnabled = isScrollingEnabled
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
        return mCurrentTransition!!
    }

    protected fun setCurrentTransition(transition: PLITransition?) {
        mCurrentTransition = transition
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
        return progressBar != null && progressBar!!.visibility != View.GONE
    }

    override fun isLocked(): Boolean {
        return if (mPanorama != null) mPanorama!!.isLocked else true
    }

    override fun setLocked(isLocked: Boolean) {
        if (mPanorama != null) mPanorama!!.isLocked = isLocked
    }

    override fun getListener(): PLViewListener? {
        return mListener
    }

    override fun setListener(listener: PLViewListener) {
        mListener = listener
    }

    /**
     * draw methods
     */
    protected fun drawView(): Boolean {
        if (mIsRendererCreated && renderer!!.isRunning && mPanorama != null) {
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
            animationTimer = NSTimer.scheduledTimerWithTimeInterval(
                mAnimationInterval,
                { _, _ -> drawView() },
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
            stopInertia()
            animationTimer = null
            if (renderer != null) renderer!!.stop()
            if (mCurrentTransition != null) mCurrentTransition!!.stop()
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
            auxiliarStartPoint!!.setValues(touches[0]!!.locationInView())
            auxiliarEndPoint!!.setValues(touches[1]!!.locationInView())
            mFovCounter++
            if (mFovCounter < PLConstants.kDefaultFovMinCounter) {
                if (mFovCounter == PLConstants.kDefaultFovMinCounter - 1) mFovDistance =
                    PLMath.distanceBetweenPoints(auxiliarStartPoint, auxiliarEndPoint)
                return false
            }
            val distance = PLMath.distanceBetweenPoints(auxiliarStartPoint, auxiliarEndPoint)
            val distanceDiff = distance - mFovDistance
            if (Math.abs(distanceDiff) < mPanorama!!.camera.minDistanceToEnableFov) return false
            val isZoomIn = distance > mFovDistance
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
            if (eventType == PLTouchEventType.PLTouchEventTypeBegan) executeResetAction(touches)
        } else if (touchCount == 2 && isZoomEnabled) {
            var isNotCancelable = true
            if (mListener != null) isNotCancelable = mListener!!.onShouldBeginZooming(this)
            if (isNotCancelable) {
                if (!mIsValidForFov) {
                    mFovCounter = 0
                    mIsValidForFov = true
                }
                if (eventType == PLTouchEventType.PLTouchEventTypeMoved) calculateFov(touches) else if (eventType == PLTouchEventType.PLTouchEventTypeBegan) {
                    auxiliarStartPoint!!.setValues(touches[0]!!.locationInView())
                    auxiliarEndPoint!!.setValues(touches[1]!!.locationInView())
                    if (mListener != null) mListener!!.onDidBeginZooming(this, auxiliarStartPoint, auxiliarEndPoint)
                }
            }
        } else if (touchCount == 1) {
            if (eventType == PLTouchEventType.PLTouchEventTypeMoved) {
                if (mIsValidForFov || mStartPoint!!.x == 0.0f && mEndPoint!!.y == 0.0f) mStartPoint!!.setValues(getLocationOfFirstTouch(touches))
            } else if (eventType == PLTouchEventType.PLTouchEventTypeEnded && mStartPoint!!.x == 0.0f && mEndPoint!!.y == 0.0f) mStartPoint!!.setValues(
                getLocationOfFirstTouch(touches)
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
        val listenerExists = mListener != null
        if (listenerExists) mListener!!.onTouchesBegan(this, touches, event)
        if (this.isLocked || isValidForCameraAnimation || mIsValidForTransition || !isTouchInView(touches) || listenerExists && !mListener!!.onShouldBeginTouching(
                this,
                touches,
                event
            )
        ) return
        when (touches!![0]!!.tapCount) {
            1 -> {
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount
                if (mIsValidForScrolling) {
                    if (mInertiaTimer != null) stopInertia() else {
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
        if (!executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeBegan)) {
            mEndPoint!!.setValues(mStartPoint!!.setValues(getLocationOfFirstTouch(touches)))
            if (touches[0]!!.tapCount == 1) {
                mTouchStatus = PLTouchStatus.PLTouchStatusFirstSingleTapCount
                if (renderer != null && renderer!!.isRunning && mPanorama != null) mPanorama!!.waitingForClick = true
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount
            }
        }
        if (listenerExists) mListener!!.onDidBeginTouching(this, touches, event)
    }

    fun touchesMoved(touches: List<UITouch?>?, event: MotionEvent?) {
        val listenerExists = mListener != null
        if (listenerExists) mListener!!.onTouchesMoved(this, touches, event)
        if (this.isLocked || isValidForCameraAnimation || mIsValidForTransition || !isTouchInView(touches) || listenerExists && !mListener!!.onShouldMoveTouching(
                this,
                touches,
                event
            )
        ) return
        mTouchStatus = PLTouchStatus.PLTouchStatusMoved
        if (!mIsAcceleratedTouchScrollingEnabled) mStartPoint!!.setValues(mEndPoint)
        if (!executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeMoved)) mEndPoint!!.setValues(getLocationOfFirstTouch(touches))
        if (listenerExists) mListener!!.onDidMoveTouching(this, touches, event)
    }

    fun touchesEnded(touches: List<UITouch?>?, event: MotionEvent?) {
        val listenerExists = mListener != null
        if (listenerExists)
            mListener!!.onTouchesEnded(this, touches, event)
        if (this.isLocked || isValidForCameraAnimation || mIsValidForTransition || !isTouchInView(touches) || listenerExists &&
            !mListener!!.onShouldEndTouching(this, touches, event)
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
            mListener?.onDidEndZooming(this)
        } else {
            if (!executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeEnded)) {
                val endPoint = getLocationOfFirstTouch(touches)
                if (PLMath.distanceBetweenPoints(mStartPoint, endPoint) >= mMinDistanceToEnableDrawing)
                    mEndPoint!!.setValues(endPoint)
                else
                    mEndPoint!!.setValues(mStartPoint)

                var isNotCancelable = true
                var isNotValidAction = false
                if (mIsScrollingEnabled && listenerExists)
                    isNotCancelable = mListener!!.onShouldBeingScrolling(this, mStartPoint, mEndPoint)
                if (mIsScrollingEnabled && isNotCancelable) {
                    val isValidForMoving = PLMath.distanceBetweenPoints(mStartPoint, mEndPoint) >= mMinDistanceToEnableScrolling
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
                                startInertia()
                            }
                        } else
                            isNotValidAction = true
                    } else {
                        if (isValidForMoving) {
                            mIsValidForScrolling = true
                            mIsValidForTouch = false
                            mListener?.onDidBeginScrolling(this, mStartPoint, mEndPoint)
                        } else
                            isNotValidAction = true
                    }
                } else
                    isNotValidAction = true
                if (isNotValidAction) {
                    mIsValidForTouch = false
                    mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
                }
            }
        }
        if (updateInitialSensorialRotation)
            updateInitialSensorialRotation()

        mListener?.onDidEndTouching(this, touches, event)
    }

    /**
     * inertia methods
     */
    fun startInertia() {
        if (this.isLocked || mIsValidForInertia || mIsValidForTransition || mListener != null && !mListener!!.onShouldRunInertia(
                this,
                mStartPoint,
                mEndPoint
            )
        ) return
        mIsValidForInertia = true
        var interval = mInertiaInterval / PLMath.distanceBetweenPoints(mStartPoint, mEndPoint)
        if (interval < 0.01f) {
            mInertiaStepValue = 0.01f / interval
            interval = 0.01f
        } else mInertiaStepValue = 1.0f
        mInertiaTimer = NSTimer.scheduledTimerWithTimeInterval(
            interval,
            { _, _ -> inertia() },
            null,
            true
        )
        if (mListener != null) mListener!!.onDidBeginInertia(this, mStartPoint, mEndPoint)
    }

    protected fun inertia() {
        if (this.isLocked || isValidForCameraAnimation || mIsValidForTransition) return
        val dx = mEndPoint!!.x - mStartPoint!!.x
        if (dx == 0f) {
            stopInertia()
            return
        }
        val m = (mEndPoint!!.y - mStartPoint!!.y) / dx
        val b = (mStartPoint!!.y * mEndPoint!!.x - mEndPoint!!.y * mStartPoint!!.x) / dx
        val x: Float
        val y: Float
        val add: Float
        if (Math.abs(dx) >= Math.abs(mEndPoint!!.y - mStartPoint!!.y)) {
            add = if (mEndPoint!!.x > mStartPoint!!.x) -mInertiaStepValue else mInertiaStepValue
            x = mEndPoint!!.x + add
            if (add > 0.0f && x > mStartPoint!!.x || add <= 0.0f && x < mStartPoint!!.x) {
                stopInertia()
                return
            }
            y = m * x + b
        } else {
            add = if (mEndPoint!!.y > mStartPoint!!.y) -mInertiaStepValue else mInertiaStepValue
            y = mEndPoint!!.y + add
            if (add > 0.0f && y > mStartPoint!!.y || add <= 0.0f && y < mStartPoint!!.y) {
                stopInertia()
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
            updateInitialSensorialRotation()
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
        if (sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                sensorDelay
            )
        ) return true
        Timber.d("Accelerometer sensor is not available on the device!")
        return false
    }

    protected fun deactiveAccelerometer() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER))
    }

    protected fun accelerometer(event: SensorEvent?, acceleration: UIAcceleration) {
        if (this.isLocked || resetWithShake(acceleration) || mIsValidForTouch || mIsValidForScrolling || mIsValidForSensorialRotation || isValidForCameraAnimation || mIsValidForTransition) return
        if (mIsAccelerometerEnabled) {
            if (mListener != null && !mListener!!.onShouldAccelerate(this, acceleration, event)) return
            var x = 0.0f
            val y = if (mIsAccelerometerUpDownEnabled) -acceleration.z else 0.0f
            val factor =
                mAccelerometerSensitivity * if (mPanorama!!.camera.isReverseRotation) -PLConstants.kAccelerometerMultiplyFactor else PLConstants.kAccelerometerMultiplyFactor
            when (this.currentDeviceOrientation) {
                UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> if (mIsAccelerometerLeftRightEnabled) x =
                    acceleration.x

                UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> if (mIsAccelerometerLeftRightEnabled) x = -acceleration.y
                UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> if (mIsAccelerometerLeftRightEnabled) x = acceleration.y
                UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> if (mIsAccelerometerLeftRightEnabled) x = -acceleration.x
                else -> {
                }
            }
            val size = renderer!!.size
            auxiliarStartPoint!!.setValues((size.width shr 1).toFloat(), (size.height shr 1).toFloat())
            auxiliarEndPoint!!.setValues(auxiliarStartPoint!!.x + x * factor, auxiliarStartPoint!!.y + y * factor)
            mPanorama!!.camera.rotate(this, auxiliarStartPoint, auxiliarEndPoint)
            if (mListener != null) mListener!!.onDidAccelerate(this, acceleration, event)
        }
    }

    /**
     * gyroscope methods
     */
    protected fun activateGyroscope(): Boolean {
        return sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
            sensorDelay
        )
    }

    protected fun deactivateGyroscope() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE))
    }

    /**
     * magnetometer methods
     */
    protected fun activateMagnetometer(): Boolean {
        return sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
            sensorDelay
        )
    }

    protected fun deactivateMagnetometer() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD))
    }

    /**
     * sensorial rotation methods
     */
    override fun startSensorialRotation(): Boolean {
        if (!mIsValidForSensorialRotation) {
            if (activateGyroscope()) {
                mHasFirstGyroscopePitch = false
                mGyroscopeLastTime = 0
                mGyroscopeRotationY = 0.0f
                mGyroscopeRotationX = mGyroscopeRotationY
                sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeGyroscope
                mIsValidForSensorialRotation = true
            } else {
                Timber.d("Gyroscope sensor is not available on device!")
                if (sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size > 0 && sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size > 0) {
                    sensorialRotationThresholdTimestamp = 0
                    mSensorialRotationThresholdFlag = false
                    sensorialRotationAccelerometerData = FloatArray(3)
                    sensorialRotationRotationMatrix = FloatArray(16)
                    sensorialRotationOrientationData = FloatArray(3)
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
                    activateMagnetometer()
                } else
                    Timber.d("Accelerometer or/and magnetometer sensor/s is/are not available on device!")
            }
            return mIsValidForSensorialRotation
        }
        return false
    }

    protected fun doGyroUpdate(pitch: Float, yaw: Float) {
        if (this.isLocked || mIsValidForTouch || mIsValidForScrolling || isValidForCameraAnimation || mIsValidForTransition || !mHasFirstGyroscopePitch) return
        mPanorama!!.camera.lookAt(this, pitch, yaw)
    }

    protected fun doSimulatedGyroUpdate() {
        if (this.isLocked || mIsValidForTouch || mIsValidForScrolling || isValidForCameraAnimation || mIsValidForTransition || !mHasFirstAccelerometerPitch || !mHasFirstMagneticHeading) return
        var step: Float
        var offset = Math.abs(mLastAccelerometerPitch - mAccelerometerPitch)
        if (offset < 0.25f) mAccelerometerPitch = mLastAccelerometerPitch else {
            step = if (offset <= 10.0f) 0.25f else 1.0f
            if (mLastAccelerometerPitch > mAccelerometerPitch) mAccelerometerPitch += step else if (mLastAccelerometerPitch < mAccelerometerPitch) mAccelerometerPitch -= step
        }
        offset = Math.abs(mLastMagneticHeading - mMagneticHeading)
        if (offset < 0.25f) mMagneticHeading = mLastMagneticHeading else {
            step = if (offset <= 10.0f) 0.25f else 1.0f
            if (mLastMagneticHeading > mMagneticHeading) mMagneticHeading += step else if (mLastMagneticHeading < mMagneticHeading) mMagneticHeading -= step
        }
        mPanorama!!.camera.lookAt(this, mAccelerometerPitch, mMagneticHeading)
    }

    override fun stopSensorialRotation(): Boolean {
        if (mIsValidForSensorialRotation) {
            mIsValidForSensorialRotation = false
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) deactivateGyroscope() else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                deactivateMagnetometer()
                sensorialRotationOrientationData = null
                sensorialRotationRotationMatrix = sensorialRotationOrientationData
                sensorialRotationAccelerometerData = sensorialRotationRotationMatrix
            }
            sensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow
            return true
        }
        return false
    }

    fun updateGyroscopeRotationByOrientation(currentOrientation: UIDeviceOrientation?, newOrientation: UIDeviceOrientation?) {
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

                else -> Unit
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

                else -> Unit
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

                else -> Unit
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

                else -> Unit
            }

            else -> Unit
        }
    }

    override fun updateInitialSensorialRotation(): Boolean {
        if (mIsValidForSensorialRotation) {
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) {
                mHasFirstGyroscopePitch = false
                return true
            } else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                sensorialRotationThresholdTimestamp = 0
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
    fun activateOrientation(): Boolean {
        if (sensorManager.registerListener(
                this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME
            )
        ) return true
        Timber.d("Orientation sensor is not available on the device!")
        return false
    }

    fun deactiveOrientation() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION))
    }

    /**
     * shake methods
     */
    fun resetWithShake(acceleration: UIAcceleration?): Boolean {
        if (!mIsShakeResetEnabled || !mIsResetEnabled || this.isLocked || isValidForCameraAnimation || mIsValidForTransition) return false
        var result = false
        val currentTime = System.currentTimeMillis()
        if (currentTime - mShakeData!!.lastTime > PLConstants.kShakeDiffTime) {
            val diffTime = currentTime - mShakeData!!.lastTime
            mShakeData!!.lastTime = currentTime
            mShakeData!!.shakePosition.setValues(acceleration)
            val speed =
                Math.abs(mShakeData!!.shakePosition.x + mShakeData!!.shakePosition.y + mShakeData!!.shakePosition.z - mShakeData!!.shakeLastPosition.x - mShakeData!!.shakeLastPosition.y - mShakeData!!.shakeLastPosition.z) / diffTime * 10000
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
        if (mIsValidForTransition || mPanorama == null || renderer == null)
            return false
        mIsValidForTransition = true
        stopInertia()
        mIsValidForFov = false
        mIsValidForScrolling = mIsValidForFov
        mIsValidForTouch = mIsValidForScrolling
        mStartPoint!!.setValues(mEndPoint!!.setValues(0.0f, 0.0f))
        mCurrentTransition = transition
        mCurrentTransition!!.internalListener = object : PLTransitionListener {
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
                mCurrentTransition = null
                if (mListener != null) mListener!!.onDidStopTransition(transition.view, transition, progressPercentage)
            }

            override fun didEndTransition(transition: PLITransition) {
                mIsValidForTransition = false
                mCurrentTransition = null
                panorama = transition.newPanorama
                if (mListener != null) mListener!!.onDidEndTransition(transition.view, transition)
            }
        }
        return mCurrentTransition!!.start(this, newPanorama)
    }

    override fun stopTransition(): Boolean {
        if (mIsValidForTransition && mCurrentTransition != null) {
            mCurrentTransition!!.stop()
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
                listener?.onDidBeginLoader(this@PLManager, loader)
            }

            override fun didComplete(loader: PLILoader) {
                hideProgressBar()
                listener?.onDidCompleteLoader(this@PLManager, loader)
            }

            override fun didStop(loader: PLILoader) {
                hideProgressBar()
                listener?.onDidStopLoader(this@PLManager, loader)
            }

            override fun didError(loader: PLILoader, error: String) {
                hideProgressBar()
                listener?.onDidErrorLoader(this@PLManager, loader, error)
            }
        }
        if (showProgressBar) {
            showProgressBar()
            Handler(Looper.getMainLooper()).postDelayed(
                { loader.load(this@PLManager, transition, initialPitch, initialYaw) }, 300
            )
        } else
            loader.load(this, transition, initialPitch, initialYaw)
    }

    /**
     * clear methods
     */
    override fun clear() {
        if (mPanorama != null) {
            mFileDownloaderManager!!.removeAll()
            setPanorama(null)
        }
    }

    override fun isZoomEnabled(): Boolean {
        return mIsZoomEnabled
    }

    override fun setZoomEnabled(enabled: Boolean) {
        mIsZoomEnabled = enabled
    }

    override fun isAcceleratedTouchScrollingEnabled(): Boolean {
        return mIsAcceleratedTouchScrollingEnabled
    }

    override fun setAcceleratedTouchScrollingEnabled(enabled: Boolean) {
        mIsAcceleratedTouchScrollingEnabled = enabled
    }

    /**
     * dealloc methods
     */
    fun onDestroy() {
        stopSensorialRotation()
        deactiveOrientation()
        deactiveAccelerometer()
        stopAnimation()
        mFileDownloaderManager!!.removeAll()
        if (mPanorama != null) mPanorama!!.clear()
        val releaseViewObjects: MutableList<PLIReleaseView?> = ArrayList()
        releaseViewObjects.add(mPanorama)
        releaseViewObjects.add(renderer)
        releaseViewObjects.add(internalCameraListener)
        releaseViewObjects.add(mCurrentTransition)
        releaseViewObjects.addAll(mInternalTouches!!)
        releaseViewObjects.addAll(mCurrentTouches!!)
        for (releaseViewObject in releaseViewObjects) releaseViewObject?.releaseView()
        releaseViewObjects.clear()
    }

    override fun getContext(): Context {
        return context
    }

    override fun getGLContext(): GL10 {
        return mGLContext!!
    }

    override fun getGLSurfaceView(): GLSurfaceView {
        return mGLSurfaceView!!
    }

    override fun getSize(): CGSize {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return mTempSize!!.setValues(displayMetrics.widthPixels, displayMetrics.heightPixels)
    }

    /**
     * android: touch utility methods
     */
    fun getTouches(event: MotionEvent): List<UITouch?>? {
        return this.getTouches(event, 1)
    }

    fun getTouches(event: MotionEvent, tapCount: Int): List<UITouch?>? {
        mGLSurfaceView!!.getLocationOnScreen(mLocation)
        val top = mLocation[1]
        val left = mLocation[0]
        mCurrentTouches!!.clear()
        var i = 0
        val length = Math.min(event.pointerCount, kMaxTouches)
        while (i < length) {
            val touch = mInternalTouches!![i]
            touch!!.setPosition(event.getX(i) - left, event.getY(i) - top)
            touch.tapCount = tapCount
            mCurrentTouches!!.add(touch)
            i++
        }
        return mCurrentTouches
    }

    /**
     * This event is fired when GLSurfaceView is created
     *
     * @param glSurfaceView current GLSurfaceView
     */
    fun onGLSurfaceViewCreated(glSurfaceView: GLSurfaceView?): View? {
        for (i in 0 until kMaxTouches) mInternalTouches!!.add(UITouch(glSurfaceView, CGPoint(0.0f, 0.0f)))
        contentLayout = RelativeLayout(context)
        contentLayout?.layoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT)
        contentLayout?.addView(
            glSurfaceView,
            RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT)
        )
        val progressBarLayoutParams = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
        progressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT)
        progressBar = ProgressBar(context)
        progressBar!!.isIndeterminate = true
        progressBar!!.visibility = View.GONE
        contentLayout?.addView(progressBar, progressBarLayoutParams)
        return onContentViewCreated(contentLayout)
    }

    fun setContentView(viewContainer: ViewGroup?) {
        this.viewContainer = viewContainer
    }

    /**
     * This event is fired when root content view is created
     *
     * @return root content view that Activity will use
     */
    fun onContentViewCreated(contentView: View?): View? {
        //Add 360 view
        viewContainer!!.addView(contentView, 0)
        //Return root content view
        return viewContainer
    }

    /**
     * This event is fired when OpenGL context is created
     *
     * @param gl current OpenGL context
     */
    protected fun onGLContextCreated() = Unit

    fun onResume() {
        if (mIsRendererCreated && mPanorama != null)
            startAnimation()
        activateOrientation()
        if (mIsValidForSensorialRotation) {
            updateInitialSensorialRotation()
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) activateGyroscope() else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                sensorialRotationThresholdTimestamp = System.currentTimeMillis() + 1000
                activateMagnetometer()
            }
        }
    }

    fun onPause() {
        deactiveAccelerometer()
        deactiveOrientation()
        if (mIsValidForSensorialRotation) {
            if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) deactivateGyroscope() else if (sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) deactivateMagnetometer()
        }
        if (mCurrentTransition != null) mCurrentTransition!!.end()
        stopAnimation()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit

    override fun onSensorChanged(event: SensorEvent) {
        val values = event.values
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> if (mIsRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (sensorialRotationAccelerometerData != null) {
                    sensorialRotationAccelerometerData!![0] = values[0]
                    sensorialRotationAccelerometerData!![1] = values[1]
                    sensorialRotationAccelerometerData!![2] = values[2]
                }
                accelerometer(event, mTempAcceleration!!.setValues(values))
            }
//            Sensor.TYPE_ORIENTATION -> {
//                val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
//                var newOrientation = mCurrentDeviceOrientation
//                when (context.resources.configuration.orientation) {
//                    Configuration.ORIENTATION_PORTRAIT -> when (display.orientation) {
//                        Surface.ROTATION_0, Surface.ROTATION_90 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait
//                        Surface.ROTATION_180, Surface.ROTATION_270 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown
//                    }
//                    Configuration.ORIENTATION_LANDSCAPE -> when (display.orientation) {
//                        Surface.ROTATION_0, Surface.ROTATION_90 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeLeft
//                        Surface.ROTATION_180, Surface.ROTATION_270 -> newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeRight
//                    }
//                    else -> Unit
//                }
//                if (mCurrentDeviceOrientation != newOrientation) {
//                    if (mIsValidForSensorialRotation && sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) updateGyroscopeRotationByOrientation(
//                        mCurrentDeviceOrientation,
//                        newOrientation
//                    )
//                    mCurrentDeviceOrientation = newOrientation
//                }
//            }
            Sensor.TYPE_MAGNETIC_FIELD -> if (mIsRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (mIsValidForSensorialRotation && sensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer && sensorialRotationAccelerometerData != null) {
                    if (mSensorialRotationThresholdFlag) {
                        SensorManager.getRotationMatrix(sensorialRotationRotationMatrix, null, sensorialRotationAccelerometerData, values)
                        SensorManager.remapCoordinateSystem(
                            sensorialRotationRotationMatrix,
                            SensorManager.AXIS_X,
                            SensorManager.AXIS_Z,
                            sensorialRotationRotationMatrix
                        )
                        SensorManager.getOrientation(sensorialRotationRotationMatrix, sensorialRotationOrientationData)
                        var yaw = sensorialRotationOrientationData!![0] * PLConstants.kToDegrees
                        var pitch = -sensorialRotationOrientationData!![1] * PLConstants.kToDegrees
                        if (mHasFirstMagneticHeading) {
                            if (pitch >= 0.0f && pitch < 50.0f || pitch < 0.0f && pitch > -50.0f) {
                                yaw -= mFirstMagneticHeading
                                val diff = yaw - mLastMagneticHeading
                                if (Math.abs(diff) > 100.0f) {
                                    mLastMagneticHeading = yaw
                                    mMagneticHeading += if (diff >= 0.0f) 360.0f else -360.0f
                                } else if (yaw > mLastMagneticHeading && yaw - PLConstants.kSensorialRotationYawErrorMargin > mLastMagneticHeading || yaw < mLastMagneticHeading && yaw + PLConstants.kSensorialRotationYawErrorMargin < mLastMagneticHeading) mLastMagneticHeading =
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
                            if (pitch > mLastAccelerometerPitch && pitch - PLConstants.kSensorialRotationPitchErrorMargin > mLastAccelerometerPitch || pitch < mLastAccelerometerPitch && pitch + PLConstants.kSensorialRotationPitchErrorMargin < mLastAccelerometerPitch) mLastAccelerometerPitch =
                                pitch
                        } else {
                            val cameraPitch = mPanorama!!.camera.lookAtRotation!!.pitch
                            mFirstAccelerometerPitch = pitch - cameraPitch
                            mAccelerometerPitch = cameraPitch
                            mLastAccelerometerPitch = mAccelerometerPitch
                            mHasFirstAccelerometerPitch = true
                        }
                        doSimulatedGyroUpdate()
                    } else {
                        if (sensorialRotationThresholdTimestamp == 0L) sensorialRotationThresholdTimestamp =
                            System.currentTimeMillis() else if (System.currentTimeMillis() - sensorialRotationThresholdTimestamp >= PLConstants.kSensorialRotationThreshold) mSensorialRotationThresholdFlag =
                            true
                    }
                }
            }

            Sensor.TYPE_GYROSCOPE -> if (mIsRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
                if (mHasFirstGyroscopePitch) {
                    if (mGyroscopeLastTime != 0L) {
                        var timeDiff = (event.timestamp - mGyroscopeLastTime) * PLConstants.kGyroscopeTimeConversion
                        if (timeDiff > 1.0) timeDiff = PLConstants.kGyroscopeMinTimeStep
                        mGyroscopeRotationX += values[0] * timeDiff
                        mGyroscopeRotationY += values[1] * timeDiff
                        when (mCurrentDeviceOrientation) {
                            UIDeviceOrientation.UIDeviceOrientationUnknown, UIDeviceOrientation.UIDeviceOrientationPortrait -> doGyroUpdate(
                                mGyroscopeRotationX * PLConstants.kToDegrees,
                                -mGyroscopeRotationY * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationLandscapeLeft -> doGyroUpdate(
                                -mGyroscopeRotationY * PLConstants.kToDegrees,
                                -mGyroscopeRotationX * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationLandscapeRight -> doGyroUpdate(
                                mGyroscopeRotationY * PLConstants.kToDegrees,
                                mGyroscopeRotationX * PLConstants.kToDegrees
                            )

                            UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown -> doGyroUpdate(
                                -mGyroscopeRotationX * PLConstants.kToDegrees,
                                mGyroscopeRotationY * PLConstants.kToDegrees
                            )

                            else -> {
                            }
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

                        else -> {
                        }
                    }
                    mHasFirstGyroscopePitch = true
                }
                mGyroscopeLastTime = event.timestamp
            }
        }
    }

    fun onTouchEvent(event: MotionEvent): Boolean {
        if (mIsRendererCreated && renderer!!.isRunning && !mIsValidForTransition) {
            if (gestureDetector!!.onTouchEvent(event))
                return true
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    touchesBegan(this.getTouches(event), event)
                    return true
                }

                MotionEvent.ACTION_MOVE -> {
                    touchesMoved(this.getTouches(event), event)
                    return true
                }

                MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                    touchesEnded(this.getTouches(event), event)
                    return true
                }
            }
        }
        return false
    }

    override fun onDoubleTap(event: MotionEvent): Boolean {
        touchesBegan(this.getTouches(event, 2), event)
        return true
    }

    override fun onDoubleTapEvent(event: MotionEvent): Boolean {
        return false
    }

    override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
        return false
    }

    fun setValidForCameraAnimation(value: Boolean) {
        isValidForCameraAnimation = value
    }

    companion object {
        private const val kMaxTouches = 10
    }
}
