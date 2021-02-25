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

package com.panoramagl;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.panoramagl.computation.PLMath;
import com.panoramagl.downloaders.PLFileDownloaderManager;
import com.panoramagl.downloaders.PLIFileDownloaderManager;
import com.panoramagl.enumerations.PLCameraAnimationType;
import com.panoramagl.enumerations.PLSensorialRotationType;
import com.panoramagl.enumerations.PLTouchEventType;
import com.panoramagl.enumerations.PLTouchStatus;
import com.panoramagl.ios.NSTimer;
import com.panoramagl.ios.UITouch;
import com.panoramagl.ios.enumerations.UIDeviceOrientation;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;
import com.panoramagl.ios.structs.UIAcceleration;
import com.panoramagl.loaders.PLILoader;
import com.panoramagl.loaders.PLLoaderListener;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLRotation;
import com.panoramagl.structs.PLShakeData;
import com.panoramagl.transitions.PLITransition;
import com.panoramagl.transitions.PLTransitionListener;
import com.panoramagl.utils.PLLog;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class PLActivity extends Activity implements PLIView, SensorEventListener, OnDoubleTapListener {
    /**
     * member variables
     */

    private PLIPanorama mPanorama;
    private PLIRenderer mRenderer;

    private boolean mIsRendererCreated;

    private boolean mIsValidForCameraAnimation;
    private PLInternalCameraListener mInternalCameraListener;

    private NSTimer mAnimationTimer;
    private float mAnimationInterval;

    private int mAnimationFrameInterval;
    private boolean mIsAnimating;

    private CGPoint mStartPoint, mEndPoint;
    private CGPoint mAuxiliarStartPoint, mAuxiliarEndPoint;

    private boolean mIsValidForFov;
    private float mFovDistance;
    private int mFovCounter;

    private boolean mIsAccelerometerEnabled, mIsAccelerometerLeftRightEnabled, mIsAccelerometerUpDownEnabled;
    private float mAccelerometerInterval, mAccelerometerSensitivity;

    private boolean mIsValidForSensorialRotation;
    private PLSensorialRotationType mSensorialRotationType;
    private long mSensorialRotationThresholdTimestamp;
    private boolean mSensorialRotationThresholdFlag;
    private float[] mSensorialRotationAccelerometerData;
    private float[] mSensorialRotationRotationMatrix;
    private float[] mSensorialRotationOrientationData;
    private boolean mHasFirstGyroscopePitch, mHasFirstAccelerometerPitch, mHasFirstMagneticHeading;
    private float mFirstAccelerometerPitch, mLastAccelerometerPitch, mAccelerometerPitch;
    private float mFirstMagneticHeading, mLastMagneticHeading, mMagneticHeading;
    private long mGyroscopeLastTime;
    private float mGyroscopeRotationX, mGyroscopeRotationY;

    private boolean mIsValidForScrolling, mIsScrollingEnabled;
    private int mMinDistanceToEnableScrolling;

    private int mMinDistanceToEnableDrawing;

    private boolean mIsValidForInertia, mIsInertiaEnabled;
    private NSTimer mInertiaTimer;
    private float mInertiaInterval;
    private float mInertiaStepValue;

    private boolean mIsResetEnabled, mIsShakeResetEnabled;
    private int mNumberOfTouchesForReset;

    private PLShakeData mShakeData;
    private float mShakeThreshold;

    private boolean mIsValidForTransition;
    private PLITransition mCurrentTransition;

    private boolean mIsValidForTouch;
    private PLTouchStatus mTouchStatus;

    private UIDeviceOrientation mCurrentDeviceOrientation;

    private PLIFileDownloaderManager mFileDownloaderManager;

    private ProgressBar mProgressBar;

    private PLViewListener mListener;
    private boolean mIsZoomEnabled;

    /**
     * init methods
     */

    protected void initializeValues() {
        mIsRendererCreated = false;

        mIsValidForCameraAnimation = false;
        mInternalCameraListener = new PLInternalCameraListener(this);

        mAnimationInterval = PLConstants.kDefaultAnimationTimerInterval;
        mAnimationFrameInterval = PLConstants.kDefaultAnimationFrameInterval;
        mIsAnimating = false;

        mStartPoint = CGPoint.CGPointMake(0.0f, 0.0f);
        mEndPoint = CGPoint.CGPointMake(0.0f, 0.0f);
        mAuxiliarStartPoint = CGPoint.CGPointMake(0.0f, 0.0f);
        mAuxiliarEndPoint = CGPoint.CGPointMake(0.0f, 0.0f);

        mIsAccelerometerEnabled = false;
        mIsAccelerometerLeftRightEnabled = mIsAccelerometerUpDownEnabled = true;
        mAccelerometerInterval = PLConstants.kDefaultAccelerometerInterval;
        mAccelerometerSensitivity = PLConstants.kDefaultAccelerometerSensitivity;

        mSensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow;

        mIsScrollingEnabled = false;
        mMinDistanceToEnableScrolling = PLConstants.kDefaultMinDistanceToEnableScrolling;

        mMinDistanceToEnableDrawing = PLConstants.kDefaultMinDistanceToEnableDrawing;

        mIsInertiaEnabled = false;
        mInertiaInterval = PLConstants.kDefaultInertiaInterval;

        mIsResetEnabled = true;
        mIsShakeResetEnabled = false;
        mNumberOfTouchesForReset = PLConstants.kDefaultNumberOfTouchesForReset;

        mShakeData = PLShakeData.PLShakeDataMake(0);
        mShakeThreshold = PLConstants.kShakeThreshold;

        mIsValidForTransition = false;

        mTouchStatus = PLTouchStatus.PLTouchStatusNone;

        mCurrentDeviceOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait;

        mFileDownloaderManager = new PLFileDownloaderManager();

        mIsZoomEnabled = true;

        this.reset();

        this.setPanorama(new PLBlankPanorama());
    }

    /**
     * reset methods
     */

    @Override
    public boolean reset() {
        return this.reset(true);
    }

    @Override
    public boolean reset(boolean resetCamera) {
        if (!mIsValidForTransition) {
            this.stopInertia();
            mIsValidForFov = mIsValidForScrolling = mIsValidForInertia = mIsValidForTouch = false;
            mStartPoint.setValues(mEndPoint.setValues(0.0f, 0.0f));
            mAuxiliarStartPoint.setValues(mAuxiliarEndPoint.setValues(0.0f, 0.0f));
            mFovCounter = 0;
            mFovDistance = 0.0f;
            if (resetCamera && mPanorama != null)
                mPanorama.getCamera().reset(this);
            this.updateInitialSensorialRotation();
            return true;
        }
        return false;
    }

    /**
     * property methods
     */

    @Override
    public PLIPanorama getPanorama() {
        return mPanorama;
    }

    @Override
    public void setPanorama(PLIPanorama panorama) {
        if (!mIsValidForTransition) {
            this.stopAnimation();
            if (panorama != null) {
                if (mPanorama != null) {
                    mPanorama.clear();
                    mPanorama.releaseView();
                    mPanorama = null;
                }
                panorama.setInternalView(this);
                panorama.setInternalCameraListener(mInternalCameraListener);
                if (mRenderer != null) {
                    mRenderer.setInternalScene(panorama);
                    mRenderer.resizeFromLayer();
                    mPanorama = panorama;
                    this.startAnimation();
                } else {
                    mRenderer = new PLRenderer(this, panorama);
                    mRenderer.setInternalListener(new PLRendererListener() {
                        @Override
                        public void rendererFirstChanged(GL10 gl, PLIRenderer render, int width, int height) {
                            mGLContext = gl;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    onGLContextCreated(mGLContext);
                                }
                            });
                        }

                        @Override
                        public void rendererDestroyed(PLIRenderer render) {
                        }

                        @Override
                        public void rendererCreated(PLIRenderer render) {
                        }

                        @Override
                        public void rendererChanged(PLIRenderer render, int width, int height) {
                            if (!mIsRendererCreated) {
                                mIsRendererCreated = true;
                                startAnimation();
                            }
                        }
                    });
                    mGLSurfaceView = new PLSurfaceView(this.getApplicationContext(), mRenderer);
                    mPanorama = panorama;
                    this.setContentView(this.onGLSurfaceViewCreated(mGLSurfaceView));
                }
            } else {
                if (mPanorama != null) {
                    mPanorama.clear();
                    mPanorama.releaseView();
                    mPanorama = null;
                }
                if (mRenderer != null)
                    mRenderer.setInternalScene(null);
            }
        }
    }

    protected PLIRenderer getRenderer() {
        return mRenderer;
    }

    @Override
    public CGRect getRenderingViewport() {
        return (mRenderer != null ? mTempRenderingViewport.setValues(mRenderer.getViewport()) : mTempRenderingViewport.reset());
    }

    @Override
    public CGSize getRenderingSize() {
        return (mRenderer != null ? mTempRenderingSize.setValues(mRenderer.getSize()) : mTempRenderingSize.reset());
    }

    @Override
    public boolean isRendererCreated() {
        return mIsRendererCreated;
    }

    @Override
    public boolean isValidForCameraAnimation() {
        return mIsValidForCameraAnimation;
    }

    protected void setValidForCameraAnimation(boolean isValidForCameraAnimation) {
        mIsValidForCameraAnimation = isValidForCameraAnimation;
    }

    @Override
    public PLICamera getCamera() {
        return (mPanorama != null ? mPanorama.getCamera() : null);
    }

    @Override
    public void setCamera(PLICamera camera) {
        if (mPanorama != null && camera != null)
            mPanorama.setCamera(camera);
    }

    protected NSTimer getAnimationTimer() {
        return mAnimationTimer;
    }

    protected void setAnimationTimer(NSTimer timer) {
        if (mAnimationTimer != null) {
            mAnimationTimer.invalidate();
            mAnimationTimer = null;
        }
        mAnimationTimer = timer;
    }

    @Override
    public float getAnimationInterval() {
        return mAnimationInterval;
    }

    @Override
    public void setAnimationInterval(float animationInterval) {
        mAnimationInterval = animationInterval;
    }

    @Override
    public int getAnimationFrameInterval() {
        return mAnimationFrameInterval;
    }

    @Override
    public void setAnimationFrameInterval(int animationFrameInterval) {
        if (animationFrameInterval >= 1) {
            mAnimationFrameInterval = animationFrameInterval;
            mAnimationInterval = PLConstants.kDefaultAnimationTimerIntervalByFrame * animationFrameInterval;
        }
    }

    @Override
    public boolean isAnimating() {
        return mIsAnimating;
    }

    @Override
    public CGPoint getStartPoint() {
        return mStartPoint;
    }

    @Override
    public void setStartPoint(CGPoint startPoint) {
        if (startPoint != null)
            mStartPoint.setValues(startPoint);
    }

    @Override
    public CGPoint getEndPoint() {
        return mEndPoint;
    }

    @Override
    public void setEndPoint(CGPoint endPoint) {
        if (endPoint != null)
            mEndPoint.setValues(endPoint);
    }

    protected CGPoint getAuxiliarStartPoint() {
        return mAuxiliarStartPoint;
    }

    protected void setAuxiliarStartPoint(CGPoint startPoint) {
        if (startPoint != null)
            mAuxiliarStartPoint.setValues(startPoint);
    }

    protected CGPoint getAuxiliarEndPoint() {
        return mAuxiliarEndPoint;
    }

    protected void setAuxiliarEndPoint(CGPoint endPoint) {
        if (endPoint != null)
            mAuxiliarEndPoint.setValues(endPoint);
    }

    @Override
    public boolean isValidForFov() {
        return mIsValidForFov;
    }

    protected void setValidForFov(boolean isValidForFov) {
        mIsValidForFov = isValidForFov;
    }

    @Override
    public boolean isAccelerometerEnabled() {
        return mIsAccelerometerEnabled;
    }

    @Override
    public void setAccelerometerEnabled(boolean isAccelerometerEnabled) {
        mIsAccelerometerEnabled = isAccelerometerEnabled;
    }

    @Override
    public boolean isAccelerometerLeftRightEnabled() {
        return mIsAccelerometerLeftRightEnabled;
    }

    @Override
    public void setAccelerometerLeftRightEnabled(boolean isAccelerometerLeftRightEnabled) {
        mIsAccelerometerLeftRightEnabled = isAccelerometerLeftRightEnabled;
    }

    @Override
    public boolean isAccelerometerUpDownEnabled() {
        return mIsAccelerometerUpDownEnabled;
    }

    @Override
    public void setAccelerometerUpDownEnabled(boolean isAccelerometerUpDownEnabled) {
        mIsAccelerometerUpDownEnabled = isAccelerometerUpDownEnabled;
    }

    @Override
    public float getAccelerometerInterval() {
        return mAccelerometerInterval;
    }

    @Override
    public void setAccelerometerInterval(float accelerometerInterval) {
        if (accelerometerInterval > 0.0f && mAccelerometerInterval != accelerometerInterval) {
            mAccelerometerInterval = accelerometerInterval;
            this.deactiveAccelerometer();
            this.activateAccelerometer();
        }
    }

    @Override
    public float getAccelerometerSensitivity() {
        return mAccelerometerSensitivity;
    }

    @Override
    public void setAccelerometerSensitivity(float accelerometerSensitivity) {
        mAccelerometerSensitivity = PLMath.valueInRange(accelerometerSensitivity, PLRange.PLRangeMake(PLConstants.kAccelerometerSensitivityMinValue, PLConstants.kAccelerometerSensitivityMaxValue));
    }

    @Override
    public boolean isValidForSensorialRotation() {
        return mIsValidForSensorialRotation;
    }

    protected PLSensorialRotationType getSensorialRotationType() {
        return mSensorialRotationType;
    }

    @Override
    public boolean isValidForScrolling() {
        return mIsValidForScrolling;
    }

    protected void setValidForScrolling(boolean isValidForScrolling) {
        mIsValidForScrolling = isValidForScrolling;
    }

    @Override
    public boolean isScrollingEnabled() {
        return mIsScrollingEnabled;
    }

    @Override
    public void setScrollingEnabled(boolean isScrollingEnabled) {
        mIsScrollingEnabled = isScrollingEnabled;
    }

    @Override
    public int getMinDistanceToEnableScrolling() {
        return mMinDistanceToEnableScrolling;
    }

    @Override
    public void setMinDistanceToEnableScrolling(int minDistanceToEnableScrolling) {
        if (minDistanceToEnableScrolling >= 0)
            mMinDistanceToEnableScrolling = minDistanceToEnableScrolling;
    }

    @Override
    public int getMinDistanceToEnableDrawing() {
        return mMinDistanceToEnableDrawing;
    }

    @Override
    public void setMinDistanceToEnableDrawing(int minDistanceToEnableDrawing) {
        if (minDistanceToEnableDrawing > 0)
            mMinDistanceToEnableDrawing = minDistanceToEnableDrawing;
    }

    @Override
    public boolean isValidForInertia() {
        return mIsValidForInertia;
    }

    protected void setValidForInertia(boolean isValidForInertia) {
        mIsValidForInertia = isValidForInertia;
    }

    @Override
    public boolean isInertiaEnabled() {
        return mIsInertiaEnabled;
    }

    @Override
    public void setInertiaEnabled(boolean isInertiaEnabled) {
        mIsInertiaEnabled = isInertiaEnabled;
    }

    @Override
    public float getInertiaInterval() {
        return mInertiaInterval;
    }

    @Override
    public void setInertiaInterval(float inertiaInterval) {
        mInertiaInterval = inertiaInterval;
    }

    @Override
    public boolean isResetEnabled() {
        return mIsResetEnabled;
    }

    @Override
    public void setResetEnabled(boolean isResetEnabled) {
        mIsResetEnabled = isResetEnabled;
    }

    @Override
    public boolean isShakeResetEnabled() {
        return mIsShakeResetEnabled;
    }

    @Override
    public void setShakeResetEnabled(boolean isShakeResetEnabled) {
        mIsShakeResetEnabled = isShakeResetEnabled;
    }

    @Override
    public int getNumberOfTouchesForReset() {
        return mNumberOfTouchesForReset;
    }

    @Override
    public void setNumberOfTouchesForReset(int numberOfTouchesForReset) {
        if (numberOfTouchesForReset > 2 && numberOfTouchesForReset <= kMaxTouches)
            mNumberOfTouchesForReset = numberOfTouchesForReset;
    }

    @Override
    public float getShakeThreshold() {
        return mShakeThreshold;
    }

    @Override
    public void setShakeThreshold(float shakeThreshold) {
        if (shakeThreshold > 0.0f)
            mShakeThreshold = shakeThreshold;
    }

    @Override
    public boolean isValidForTransition() {
        return mIsValidForTransition;
    }

    protected void setValidForTransition(boolean isValidForTransition) {
        mIsValidForTransition = isValidForTransition;
    }

    @Override
    public PLITransition getCurrentTransition() {
        return mCurrentTransition;
    }

    protected void setCurrentTransition(PLITransition transition) {
        mCurrentTransition = transition;
    }

    @Override
    public boolean isValidForTouch() {
        return mIsValidForTouch;
    }

    protected void setValidForTouch(boolean isValidForTouch) {
        mIsValidForTouch = isValidForTouch;
    }

    @Override
    public PLTouchStatus getTouchStatus() {
        return mTouchStatus;
    }

    protected void setTouchStatus(PLTouchStatus touchStatus) {
        mTouchStatus = touchStatus;
    }

    @Override
    public UIDeviceOrientation getCurrentDeviceOrientation() {
        return mCurrentDeviceOrientation;
    }

    @Override
    public PLIFileDownloaderManager getDownloadManager() {
        return mFileDownloaderManager;
    }

    protected ProgressBar getProgressBar() {
        return mProgressBar;
    }

    @Override
    public boolean isProgressBarVisible() {
        return (mProgressBar != null && mProgressBar.getVisibility() != View.GONE);
    }

    @Override
    public boolean isLocked() {
        return (mPanorama != null ? mPanorama.isLocked() : true);
    }

    @Override
    public void setLocked(boolean isLocked) {
        if (mPanorama != null)
            mPanorama.setLocked(isLocked);
    }

    @Override
    public PLViewListener getListener() {
        return mListener;
    }

    @Override
    public void setListener(PLViewListener listener) {
        mListener = listener;
    }

    /**
     * draw methods
     */

    protected boolean drawView() {
        if (mIsRendererCreated && mRenderer.isRunning() && mPanorama != null) {
            if (!mIsValidForFov)
                mPanorama.getCamera().rotate(this, mStartPoint, mEndPoint);
            mGLSurfaceView.requestRender();
            return true;
        }
        return false;
    }

    /**
     * animation methods
     */

    @Override
    public boolean startAnimation() {
        if (!mIsAnimating) {
            if (mRenderer != null)
                mRenderer.start();
            this.setAnimationTimer
                    (
                            NSTimer.scheduledTimerWithTimeInterval
                                    (
                                            mAnimationInterval,
                                            new NSTimer.Runnable() {
                                                @Override
                                                public void run(NSTimer target, Object[] userInfo) {
                                                    drawView();
                                                }
                                            },
                                            null,
                                            true
                                    )
                    );
            mIsAnimating = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean stopAnimation() {
        if (mIsAnimating) {
            this.stopInertia();
            this.setAnimationTimer(null);
            if (mRenderer != null)
                mRenderer.stop();
            if (mCurrentTransition != null)
                mCurrentTransition.stop();
            if (mPanorama != null)
                mPanorama.getCamera().stopAnimation(this);
            mIsAnimating = mIsValidForTouch = mIsValidForScrolling = mIsValidForFov = false;
            return true;
        }
        return false;
    }

    /**
     * fov methods
     */

    protected boolean calculateFov(List<UITouch> touches) {
        if (touches.size() == 2 && isZoomEnabled()) {
            mAuxiliarStartPoint.setValues(touches.get(0).locationInView(mGLSurfaceView));
            mAuxiliarEndPoint.setValues(touches.get(1).locationInView(mGLSurfaceView));

            mFovCounter++;
            if (mFovCounter < PLConstants.kDefaultFovMinCounter) {
                if (mFovCounter == PLConstants.kDefaultFovMinCounter - 1)
                    mFovDistance = PLMath.distanceBetweenPoints(mAuxiliarStartPoint, mAuxiliarEndPoint);
                return false;
            }

            float distance = PLMath.distanceBetweenPoints(mAuxiliarStartPoint, mAuxiliarEndPoint), distanceDiff = distance - mFovDistance;

            if (Math.abs(distanceDiff) < mPanorama.getCamera().getMinDistanceToEnableFov())
                return false;

            boolean isZoomIn = (distance > mFovDistance), isNotCancelable = true;

            if (mListener != null)
                isNotCancelable = mListener.onShouldRunZooming(this, distanceDiff, isZoomIn, !isZoomIn);

            if (isNotCancelable) {
                mFovDistance = distance;
                mPanorama.getCamera().addFov(this, distanceDiff);
                if (mListener != null)
                    mListener.onDidRunZooming(this, distanceDiff, isZoomIn, !isZoomIn);
                return true;
            }
        }
        return false;
    }

    /**
     * action methods
     */

    protected boolean executeDefaultAction(List<UITouch> touches, PLTouchEventType eventType) {
        int touchCount = touches.size();
        if (touchCount == mNumberOfTouchesForReset) {
            mIsValidForFov = false;
            if (eventType == PLTouchEventType.PLTouchEventTypeBegan)
                this.executeResetAction(touches);
        } else if (touchCount == 2 && isZoomEnabled()) {
            boolean isNotCancelable = true;
            if (mListener != null)
                isNotCancelable = mListener.onShouldBeginZooming(this);
            if (isNotCancelable) {
                if (!mIsValidForFov) {
                    mFovCounter = 0;
                    mIsValidForFov = true;
                }
                if (eventType == PLTouchEventType.PLTouchEventTypeMoved)
                    this.calculateFov(touches);
                else if (eventType == PLTouchEventType.PLTouchEventTypeBegan) {
                    mAuxiliarStartPoint.setValues(touches.get(0).locationInView(mGLSurfaceView));
                    mAuxiliarEndPoint.setValues(touches.get(1).locationInView(mGLSurfaceView));
                    if (mListener != null)
                        mListener.onDidBeginZooming(this, mAuxiliarStartPoint, mAuxiliarEndPoint);
                }
            }
        } else if (touchCount == 1) {
            if (eventType == PLTouchEventType.PLTouchEventTypeMoved) {
                if (mIsValidForFov || (mStartPoint.x == 0.0f && mEndPoint.y == 0.0f))
                    mStartPoint.setValues(this.getLocationOfFirstTouch(touches));
            } else if (eventType == PLTouchEventType.PLTouchEventTypeEnded && mStartPoint.x == 0.0f && mEndPoint.y == 0.0f)
                mStartPoint.setValues(this.getLocationOfFirstTouch(touches));
            mIsValidForFov = false;
            return false;
        }
        return true;
    }

    protected boolean executeResetAction(List<UITouch> touches) {
        if (mIsResetEnabled && touches.size() == mNumberOfTouchesForReset) {
            boolean isNotCancelable = true;
            if (mListener != null)
                isNotCancelable = mListener.onShouldReset(this);
            if (isNotCancelable) {
                this.reset();
                if (mListener != null)
                    mListener.onDidReset(this);
                return true;
            }
        }
        return false;
    }

    /**
     * touch methods
     */

    protected boolean isTouchInView(List<UITouch> touches) {
        for (int i = 0, touchesLength = touches.size(); i < touchesLength; i++)
            if (touches.get(i).getView() != mGLSurfaceView)
                return false;
        return true;
    }

    protected CGPoint getLocationOfFirstTouch(List<UITouch> touches) {
        return touches.get(0).locationInView(mGLSurfaceView);
    }

    protected void touchesBegan(List<UITouch> touches, MotionEvent event) {
        boolean listenerExists = (mListener != null);

        if (listenerExists)
            mListener.onTouchesBegan(this, touches, event);

        if (this.isLocked() || mIsValidForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener.onShouldBeginTouching(this, touches, event)))
            return;

        switch (touches.get(0).getTapCount()) {
            case 1:
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount;
                if (mIsValidForScrolling) {
                    if (mInertiaTimer != null)
                        this.stopInertia();
                    else {
                        mStartPoint.setValues(mEndPoint);
                        mIsValidForScrolling = false;
                        if (listenerExists)
                            mListener.onDidEndScrolling(this, mStartPoint, mEndPoint);
                    }
                }
                break;
            case 2:
                mTouchStatus = PLTouchStatus.PLTouchStatusDoubleTapCount;
                break;
        }

        mIsValidForTouch = true;
        mTouchStatus = PLTouchStatus.PLTouchStatusBegan;

        if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeBegan)) {
            mEndPoint.setValues(mStartPoint.setValues(this.getLocationOfFirstTouch(touches)));
            if (touches.get(0).getTapCount() == 1) {
                mTouchStatus = PLTouchStatus.PLTouchStatusFirstSingleTapCount;
                if (mRenderer != null && mRenderer.isRunning() && mPanorama != null)
                    mPanorama.setWaitingForClick(true);
                mTouchStatus = PLTouchStatus.PLTouchStatusSingleTapCount;
            }
        }

        if (listenerExists)
            mListener.onDidBeginTouching(this, touches, event);
    }

    protected void touchesMoved(List<UITouch> touches, MotionEvent event) {
        boolean listenerExists = (mListener != null);

        if (listenerExists)
            mListener.onTouchesMoved(this, touches, event);

        if (this.isLocked() || mIsValidForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener.onShouldMoveTouching(this, touches, event)))
            return;

        mTouchStatus = PLTouchStatus.PLTouchStatusMoved;

        if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeMoved))
            mEndPoint.setValues(this.getLocationOfFirstTouch(touches));

        if (listenerExists)
            mListener.onDidMoveTouching(this, touches, event);
    }

    protected void touchesEnded(List<UITouch> touches, MotionEvent event) {
        boolean listenerExists = (mListener != null);

        if (listenerExists)
            mListener.onTouchesEnded(this, touches, event);

        if (this.isLocked() || mIsValidForCameraAnimation || mIsValidForTransition || !this.isTouchInView(touches) || (listenerExists && !mListener.onShouldEndTouching(this, touches, event))) {
            mIsValidForTouch = false;
            return;
        }

        boolean updateInitialSensorialRotation = mIsValidForSensorialRotation;
        mTouchStatus = PLTouchStatus.PLTouchStatusEnded;

        if (mIsValidForFov) {
            mIsValidForFov = mIsValidForTouch = false;
            mStartPoint.setValues(mEndPoint.setValues(0.0f, 0.0f));
            if (listenerExists)
                mListener.onDidEndZooming(this);
        } else {
            if (!this.executeDefaultAction(touches, PLTouchEventType.PLTouchEventTypeEnded)) {
                CGPoint endPoint = this.getLocationOfFirstTouch(touches);
                if (PLMath.distanceBetweenPoints(mStartPoint, endPoint) >= mMinDistanceToEnableDrawing)
                    mEndPoint.setValues(endPoint);
                else
                    mEndPoint.setValues(mStartPoint);

                boolean isNotCancelable = true, isNotValidAction = false;

                if (mIsScrollingEnabled && listenerExists)
                    isNotCancelable = mListener.onShouldBeingScrolling(this, mStartPoint, mEndPoint);

                if (mIsScrollingEnabled && isNotCancelable) {
                    boolean isValidForMoving = (PLMath.distanceBetweenPoints(mStartPoint, mEndPoint) >= mMinDistanceToEnableScrolling);
                    if (mIsInertiaEnabled) {
                        if (isValidForMoving) {
                            mIsValidForScrolling = true;
                            isNotCancelable = true;
                            if (listenerExists) {
                                mListener.onDidBeginScrolling(this, mStartPoint, mEndPoint);
                                isNotCancelable = mListener.onShouldBeginInertia(this, mStartPoint, mEndPoint);
                            }
                            if (isNotCancelable) {
                                updateInitialSensorialRotation = false;
                                this.startInertia();
                            }
                        } else
                            isNotValidAction = true;
                    } else {
                        if (isValidForMoving) {
                            mIsValidForScrolling = true;
                            mIsValidForTouch = false;
                            if (listenerExists)
                                mListener.onDidBeginScrolling(this, mStartPoint, mEndPoint);
                        } else
                            isNotValidAction = true;
                    }
                } else
                    isNotValidAction = true;
                if (isNotValidAction) {
                    mIsValidForTouch = false;
                    mStartPoint.setValues(mEndPoint.setValues(0.0f, 0.0f));
                }
            }
        }

        if (updateInitialSensorialRotation)
            this.updateInitialSensorialRotation();

        if (listenerExists)
            mListener.onDidEndTouching(this, touches, event);
    }

    /**
     * inertia methods
     */

    protected void startInertia() {
        if (this.isLocked() || mIsValidForInertia || mIsValidForTransition || (mListener != null && !mListener.onShouldRunInertia(this, mStartPoint, mEndPoint)))
            return;

        mIsValidForInertia = true;
        float interval = mInertiaInterval / PLMath.distanceBetweenPoints(mStartPoint, mEndPoint);
        if (interval < 0.01f) {
            mInertiaStepValue = 0.01f / interval;
            interval = 0.01f;
        } else
            mInertiaStepValue = 1.0f;
        mInertiaTimer = NSTimer.scheduledTimerWithTimeInterval
                (
                        interval,
                        new NSTimer.Runnable() {
                            @Override
                            public void run(NSTimer target, Object[] userInfo) {
                                inertia();
                            }
                        },
                        null,
                        true
                );
        if (mListener != null)
            mListener.onDidBeginInertia(this, mStartPoint, mEndPoint);
    }

    protected void inertia() {
        if (this.isLocked() || mIsValidForCameraAnimation || mIsValidForTransition)
            return;

        float m = (mEndPoint.y - mStartPoint.y) / (mEndPoint.x - mStartPoint.x);
        float b = (mStartPoint.y * mEndPoint.x - mEndPoint.y * mStartPoint.x) / (mEndPoint.x - mStartPoint.x);
        float x, y, add;

        if (Math.abs(mEndPoint.x - mStartPoint.x) >= Math.abs(mEndPoint.y - mStartPoint.y)) {
            add = (mEndPoint.x > mStartPoint.x ? -mInertiaStepValue : mInertiaStepValue);
            x = mEndPoint.x + add;
            if ((add > 0.0f && x > mStartPoint.x) || (add <= 0.0f && x < mStartPoint.x)) {
                this.stopInertia();
                return;
            }
            y = m * x + b;
        } else {
            add = (mEndPoint.y > mStartPoint.y ? -mInertiaStepValue : mInertiaStepValue);
            y = mEndPoint.y + add;
            if ((add > 0.0f && y > mStartPoint.y) || (add <= 0.0f && y < mStartPoint.y)) {
                this.stopInertia();
                return;
            }
            x = (y - b) / m;
        }
        mEndPoint.setValues(x, y);
        if (mListener != null)
            mListener.onDidRunInertia(this, mStartPoint, mEndPoint);
    }

    protected boolean stopInertia() {
        if (mInertiaTimer != null) {
            mInertiaTimer.invalidate();
            mInertiaTimer = null;
            mIsValidForInertia = false;
            if (mListener != null)
                mListener.onDidEndInertia(this, mStartPoint, mEndPoint);
            this.updateInitialSensorialRotation();
            mIsValidForScrolling = mIsValidForTouch = false;
            if (mListener != null)
                mListener.onDidEndScrolling(this, mStartPoint, mEndPoint);
            mStartPoint.setValues(mEndPoint.setValues(0.0f, 0.0f));
            return true;
        }
        return false;
    }

    /**
     * accelerometer methods
     */

    protected boolean activateAccelerometer() {
        if (mSensorManager != null && mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), (int) (mAccelerometerInterval * 1000.0f)))
            return true;
        PLLog.debug("PLView::activateAccelerometer", "Accelerometer sensor is not available on the device!");
        return false;
    }

    protected void deactiveAccelerometer() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    protected void accelerometer(SensorEvent event, UIAcceleration acceleration) {
        if (this.isLocked() || this.resetWithShake(acceleration) || mIsValidForTouch || mIsValidForScrolling || mIsValidForSensorialRotation || mIsValidForCameraAnimation || mIsValidForTransition)
            return;

        if (mIsAccelerometerEnabled) {
            if (mListener != null && !mListener.onShouldAccelerate(this, acceleration, event))
                return;

            float x = 0.0f, y = (mIsAccelerometerUpDownEnabled ? -acceleration.z : 0.0f), factor = mAccelerometerSensitivity * (mPanorama.getCamera().isReverseRotation() ? -PLConstants.kAccelerometerMultiplyFactor : PLConstants.kAccelerometerMultiplyFactor);

            switch (this.getCurrentDeviceOrientation()) {
                case UIDeviceOrientationUnknown:
                case UIDeviceOrientationPortrait:
                    if (mIsAccelerometerLeftRightEnabled)
                        x = acceleration.x;
                    break;
                case UIDeviceOrientationLandscapeLeft:
                    if (mIsAccelerometerLeftRightEnabled)
                        x = -acceleration.y;
                    break;
                case UIDeviceOrientationLandscapeRight:
                    if (mIsAccelerometerLeftRightEnabled)
                        x = acceleration.y;
                    break;
                case UIDeviceOrientationPortraitUpsideDown:
                    if (mIsAccelerometerLeftRightEnabled)
                        x = -acceleration.x;
                    break;
                default:
                    break;
            }

            CGSize size = mRenderer.getSize();
            mAuxiliarStartPoint.setValues(size.width >> 1, size.height >> 1);
            mAuxiliarEndPoint.setValues(mAuxiliarStartPoint.x + x * factor, mAuxiliarStartPoint.y + y * factor);
            mPanorama.getCamera().rotate(this, mAuxiliarStartPoint, mAuxiliarEndPoint);

            if (mListener != null)
                mListener.onDidAccelerate(this, acceleration, event);
        }
    }

    /**
     * gyroscope methods
     */

    protected boolean activateGyroscope() {
        return (mSensorManager != null && mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), (int) (PLConstants.kDefaultGyroscopeInterval * 1000.0f)));
    }

    protected void deactivateGyroscope() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }

    /**
     * magnetometer methods
     */

    protected boolean activateMagnetometer() {
        return (mSensorManager != null && mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), (int) (PLConstants.kDefaultMagnetometerInterval * 1000.0f)));
    }

    protected void deactivateMagnetometer() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
    }

    /**
     * sensorial rotation methods
     */

    @Override
    public boolean startSensorialRotation() {
        if (!mIsValidForSensorialRotation) {
            if (this.activateGyroscope()) {
                mHasFirstGyroscopePitch = false;
                mGyroscopeLastTime = 0;
                mGyroscopeRotationX = mGyroscopeRotationY = 0.0f;
                mSensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeGyroscope;
                mIsValidForSensorialRotation = true;
            } else {
                PLLog.debug("PLView::startSensorialRotation", "Gyroscope sensor is not available on device!");
                if (mSensorManager != null && mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).size() > 0 && mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).size() > 0) {
                    mSensorialRotationThresholdTimestamp = 0;
                    mSensorialRotationThresholdFlag = false;
                    mSensorialRotationAccelerometerData = new float[3];
                    mSensorialRotationRotationMatrix = new float[16];
                    mSensorialRotationOrientationData = new float[3];
                    mHasFirstAccelerometerPitch = mHasFirstMagneticHeading = false;
                    mFirstAccelerometerPitch = mLastAccelerometerPitch = mAccelerometerPitch = 0.0f;
                    mFirstMagneticHeading = mLastMagneticHeading = mMagneticHeading = 0.0f;
                    mSensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer;
                    mIsValidForSensorialRotation = true;
                    this.activateMagnetometer();
                } else
                    PLLog.debug("PLView::startSensorialRotation", "Accelerometer or/and magnetometer sensor/s is/are not available on device!");
            }
            return mIsValidForSensorialRotation;
        }
        return false;
    }

    protected void doGyroUpdate(float pitch, float yaw) {
        if (this.isLocked() || mIsValidForTouch || mIsValidForScrolling || mIsValidForCameraAnimation || mIsValidForTransition || !mHasFirstGyroscopePitch)
            return;

        mPanorama.getCamera().lookAt(this, pitch, yaw);
    }

    protected void doSimulatedGyroUpdate() {
        if (this.isLocked() || mIsValidForTouch || mIsValidForScrolling || mIsValidForCameraAnimation || mIsValidForTransition || !mHasFirstAccelerometerPitch || !mHasFirstMagneticHeading)
            return;

        float step, offset = Math.abs(mLastAccelerometerPitch - mAccelerometerPitch);
        if (offset < 0.25f)
            mAccelerometerPitch = mLastAccelerometerPitch;
        else {
            step = (offset <= 10.0f ? 0.25f : 1.0f);
            if (mLastAccelerometerPitch > mAccelerometerPitch)
                mAccelerometerPitch += step;
            else if (mLastAccelerometerPitch < mAccelerometerPitch)
                mAccelerometerPitch -= step;
        }
        offset = Math.abs(mLastMagneticHeading - mMagneticHeading);
        if (offset < 0.25f)
            mMagneticHeading = mLastMagneticHeading;
        else {
            step = (offset <= 10.0f ? 0.25f : 1.0f);
            if (mLastMagneticHeading > mMagneticHeading)
                mMagneticHeading += step;
            else if (mLastMagneticHeading < mMagneticHeading)
                mMagneticHeading -= step;
        }
        mPanorama.getCamera().lookAt(this, mAccelerometerPitch, mMagneticHeading);
    }

    @Override
    public boolean stopSensorialRotation() {
        if (mIsValidForSensorialRotation) {
            mIsValidForSensorialRotation = false;
            if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope)
                this.deactivateGyroscope();
            else if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                this.deactivateMagnetometer();
                mSensorialRotationAccelerometerData = mSensorialRotationRotationMatrix = mSensorialRotationOrientationData = null;
            }
            mSensorialRotationType = PLSensorialRotationType.PLSensorialRotationTypeUnknow;
            return true;
        }
        return false;
    }

    protected void updateGyroscopeRotationByOrientation(UIDeviceOrientation currentOrientation, UIDeviceOrientation newOrientation) {
        float tempRotation;
        switch (currentOrientation) {
            case UIDeviceOrientationUnknown:
            case UIDeviceOrientationPortrait:
                switch (newOrientation) {
                    case UIDeviceOrientationLandscapeLeft:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = mGyroscopeRotationY;
                        mGyroscopeRotationY = -tempRotation;
                        break;
                    case UIDeviceOrientationLandscapeRight:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = -mGyroscopeRotationY;
                        mGyroscopeRotationY = tempRotation;
                        break;
                    case UIDeviceOrientationPortraitUpsideDown:
                        mGyroscopeRotationX = -mGyroscopeRotationX;
                        mGyroscopeRotationY = -mGyroscopeRotationY;
                        break;
                    default:
                        break;
                }
                break;
            case UIDeviceOrientationLandscapeLeft:
                switch (newOrientation) {
                    case UIDeviceOrientationUnknown:
                    case UIDeviceOrientationPortrait:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = -mGyroscopeRotationY;
                        mGyroscopeRotationY = tempRotation;
                        break;
                    case UIDeviceOrientationPortraitUpsideDown:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = mGyroscopeRotationY;
                        mGyroscopeRotationY = -tempRotation;
                        break;
                    case UIDeviceOrientationLandscapeRight:
                        mGyroscopeRotationX = -mGyroscopeRotationX;
                        mGyroscopeRotationY = -mGyroscopeRotationY;
                        break;
                    default:
                        break;
                }
                break;
            case UIDeviceOrientationLandscapeRight:
                switch (newOrientation) {
                    case UIDeviceOrientationUnknown:
                    case UIDeviceOrientationPortrait:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = mGyroscopeRotationY;
                        mGyroscopeRotationY = -tempRotation;
                        break;
                    case UIDeviceOrientationPortraitUpsideDown:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = -mGyroscopeRotationY;
                        mGyroscopeRotationY = tempRotation;
                        break;
                    case UIDeviceOrientationLandscapeLeft:
                        mGyroscopeRotationX = -mGyroscopeRotationX;
                        mGyroscopeRotationY = -mGyroscopeRotationY;
                        break;
                    default:
                        break;
                }
                break;
            case UIDeviceOrientationPortraitUpsideDown:
                switch (newOrientation) {
                    case UIDeviceOrientationLandscapeLeft:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = -mGyroscopeRotationY;
                        mGyroscopeRotationY = tempRotation;
                        break;
                    case UIDeviceOrientationLandscapeRight:
                        tempRotation = mGyroscopeRotationX;
                        mGyroscopeRotationX = mGyroscopeRotationY;
                        mGyroscopeRotationY = -tempRotation;
                        break;
                    case UIDeviceOrientationPortrait:
                        mGyroscopeRotationX = -mGyroscopeRotationX;
                        mGyroscopeRotationY = -mGyroscopeRotationY;
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public boolean updateInitialSensorialRotation() {
        if (mIsValidForSensorialRotation) {
            if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope) {
                mHasFirstGyroscopePitch = false;
                return true;
            } else if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                mSensorialRotationThresholdTimestamp = 0;
                mSensorialRotationThresholdFlag = mHasFirstAccelerometerPitch = mHasFirstMagneticHeading = false;
                return true;
            }
        }
        return false;
    }

    /**
     * orientation methods
     */

    @SuppressWarnings("deprecation")
    protected boolean activateOrientation() {
        if (mSensorManager != null && mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_GAME))
            return true;
        PLLog.debug("PLView::activateOrientation", "Orientation sensor is not available on the device!");
        return false;
    }

    @SuppressWarnings("deprecation")
    protected void deactiveOrientation() {
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
    }

    /**
     * shake methods
     */

    protected boolean resetWithShake(UIAcceleration acceleration) {
        if (!mIsShakeResetEnabled || !mIsResetEnabled || this.isLocked() || mIsValidForCameraAnimation || mIsValidForTransition)
            return false;

        boolean result = false;
        long currentTime = System.currentTimeMillis();

        if ((currentTime - mShakeData.lastTime) > PLConstants.kShakeDiffTime) {
            long diffTime = (currentTime - mShakeData.lastTime);
            mShakeData.lastTime = currentTime;
            mShakeData.shakePosition.setValues(acceleration);

            float speed = Math.abs(mShakeData.shakePosition.x + mShakeData.shakePosition.y + mShakeData.shakePosition.z - mShakeData.shakeLastPosition.x - mShakeData.shakeLastPosition.y - mShakeData.shakeLastPosition.z) / diffTime * 10000;

            if (speed > mShakeThreshold) {
                boolean isNotCancelable = true;
                if (mListener != null)
                    isNotCancelable = mListener.onShouldReset(this);
                if (isNotCancelable) {
                    this.reset();
                    if (mListener != null)
                        mListener.onDidReset(this);
                    result = true;
                }
            }

            mShakeData.shakeLastPosition.setValues(mShakeData.shakePosition);
        }
        return result;
    }

    /**
     * transition methods
     */

    @Override
    public boolean startTransition(PLITransition transition, PLIPanorama newPanorama) {
        if (mIsValidForTransition || mPanorama == null || mRenderer == null || transition == null)
            return false;

        mIsValidForTransition = true;

        this.stopInertia();
        mIsValidForTouch = mIsValidForScrolling = mIsValidForFov = false;
        mStartPoint.setValues(mEndPoint.setValues(0.0f, 0.0f));

        mCurrentTransition = transition;
        mCurrentTransition.setInternalListener
                (
                        new PLTransitionListener() {
                            @Override
                            public boolean isRemovableListener() {
                                return true;
                            }

                            @Override
                            public void didBeginTransition(PLITransition transition) {
                                if (mListener != null)
                                    mListener.onDidBeginTransition(transition.getView(), transition);
                            }

                            @Override
                            public void didProcessTransition(PLITransition transition, int progressPercentage) {
                                if (mListener != null)
                                    mListener.onDidProcessTransition(transition.getView(), transition, progressPercentage);
                            }

                            @Override
                            public void didStopTransition(PLITransition transition, int progressPercentage) {
                                mIsValidForTransition = false;
                                mCurrentTransition = null;
                                if (mListener != null)
                                    mListener.onDidStopTransition(transition.getView(), transition, progressPercentage);
                            }

                            @Override
                            public void didEndTransition(PLITransition transition) {
                                mIsValidForTransition = false;
                                mCurrentTransition = null;
                                setPanorama(transition.getNewPanorama());
                                if (mListener != null)
                                    mListener.onDidEndTransition(transition.getView(), transition);
                            }
                        }
                );
        return mCurrentTransition.start(this, newPanorama);
    }

    @Override
    public boolean stopTransition() {
        if (mIsValidForTransition && mCurrentTransition != null) {
            mCurrentTransition.stop();
            return true;
        }
        return false;
    }

    /**
     * progress-bar methods
     */

    @Override
    public boolean showProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.GONE) {
            mProgressBar.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    @Override
    public boolean hideProgressBar() {
        if (mProgressBar != null && mProgressBar.getVisibility() == View.VISIBLE) {
            mProgressBar.setVisibility(View.GONE);
            return true;
        }
        return false;
    }

    /**
     * load methods
     */

    @Override
    public void load(PLILoader loader) {
        this.load(loader, false, null, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue);
    }

    @Override
    public void load(PLILoader loader, boolean showProgressBar) {
        this.load(loader, showProgressBar, null, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue);
    }

    @Override
    public void load(PLILoader loader, boolean showProgressBar, PLITransition transition) {
        this.load(loader, showProgressBar, transition, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue);
    }

    @Override
    public void load(final PLILoader loader, boolean showProgressBar, final PLITransition transition, final float initialPitch, final float initialYaw) {
        if (loader != null) {
            mFileDownloaderManager.removeAll();
            loader.setInternalListener
                    (
                            new PLLoaderListener() {
                                @Override
                                public void didBegin(PLILoader loader) {
                                    PLViewListener listener = getListener();
                                    if (listener != null)
                                        listener.onDidBeginLoader(PLActivity.this, loader);
                                }

                                @Override
                                public void didComplete(PLILoader loader) {
                                    hideProgressBar();
                                    PLViewListener listener = getListener();
                                    if (listener != null)
                                        listener.onDidCompleteLoader(PLActivity.this, loader);
                                }

                                @Override
                                public void didStop(PLILoader loader) {
                                    hideProgressBar();
                                    PLViewListener listener = getListener();
                                    if (listener != null)
                                        listener.onDidStopLoader(PLActivity.this, loader);
                                }

                                @Override
                                public void didError(PLILoader loader, String error) {
                                    hideProgressBar();
                                    PLViewListener listener = getListener();
                                    if (listener != null)
                                        listener.onDidErrorLoader(PLActivity.this, loader, error);
                                }
                            }
                    );
            if (showProgressBar) {
                this.showProgressBar();
                Handler handler = new Handler();
                handler.postDelayed
                        (
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        loader.load(PLActivity.this, transition, initialPitch, initialYaw);
                                    }
                                },
                                300
                        );
            } else
                loader.load(this, transition, initialPitch, initialYaw);
        }
    }

    /**
     * clear methods
     */

    @Override
    public void clear() {
        if (mPanorama != null) {
            mFileDownloaderManager.removeAll();
            this.setPanorama(null);
        }
    }

    public boolean isZoomEnabled() {
        return mIsZoomEnabled;
    }

    public void setZoomEnabled(boolean enabled) {
        this.mIsZoomEnabled = enabled;
    }

    /**
     * dealloc methods
     */

    @Override
    protected void onDestroy() {
        this.stopSensorialRotation();
        this.deactiveOrientation();
        this.deactiveAccelerometer();

        this.stopAnimation();

        mFileDownloaderManager.removeAll();

        if (mPanorama != null)
            mPanorama.clear();

        List<PLIReleaseView> releaseViewObjects = new ArrayList<PLIReleaseView>();
        releaseViewObjects.add(mPanorama);
        releaseViewObjects.add(mRenderer);
        releaseViewObjects.add(mInternalCameraListener);
        releaseViewObjects.add(mCurrentTransition);
        releaseViewObjects.addAll(mInternalTouches);
        releaseViewObjects.addAll(mCurrentTouches);
        for (PLIReleaseView releaseViewObject : releaseViewObjects)
            if (releaseViewObject != null)
                releaseViewObject.releaseView();
        releaseViewObjects.clear();

        super.onDestroy();
    }

    /**
     * internal classes declaration
     */

    protected class PLSurfaceView extends GLSurfaceView {
        /**
         * init methods
         */

        public PLSurfaceView(Context context, Renderer renderer) {
            super(context);
            this.setRenderer(renderer);
            this.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        }
    }

    protected class PLInternalCameraListener implements PLCameraListener, PLIReleaseView {
        /**
         * member variables
         */

        private PLActivity mView;

        /**
         * init methods
         */

        public PLInternalCameraListener(PLActivity view) {
            super();
            mView = view;
        }

        /**
         * PLCameraListener methods
         */

        @Override
        public void didBeginAnimation(Object sender, PLICamera camera, PLCameraAnimationType type) {
            switch (type) {
                case PLCameraAnimationTypeLookAt:
                    mView.setValidForCameraAnimation(true);
                    break;
                default:
                    break;
            }
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidBeginCameraAnimation(mView, sender, camera, type);
        }

        @Override
        public void didEndAnimation(Object sender, PLICamera camera, PLCameraAnimationType type) {
            switch (type) {
                case PLCameraAnimationTypeLookAt:
                    mView.setValidForCameraAnimation(false);
                    break;
                default:
                    break;
            }
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidEndCameraAnimation(mView, sender, camera, type);
        }

        @Override
        public void didLookAt(Object sender, PLICamera camera, float pitch, float yaw, boolean animated) {
            if (sender != mView)
                mView.updateInitialSensorialRotation();
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidLookAtCamera(mView, sender, camera, pitch, yaw, animated);
        }

        @Override
        public void didRotate(Object sender, PLICamera camera, float pitch, float yaw, float roll) {
            if (sender != mView)
                mView.updateInitialSensorialRotation();
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidRotateCamera(mView, sender, camera, pitch, yaw, roll);
        }

        @Override
        public void didFov(Object sender, PLICamera camera, float fov, boolean animated) {
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidFovCamera(mView, sender, camera, fov, animated);
        }

        @Override
        public void didReset(Object sender, PLICamera camera) {
            if (sender != mView)
                mView.updateInitialSensorialRotation();
            PLViewListener listener = mView.getListener();
            if (listener != null)
                listener.onDidResetCamera(mView, sender, camera);
        }

        /**
         * PLIReleaseView methods
         */

        @Override
        public void releaseView() {
            mView = null;
        }

        /**
         * dealloc methods
         */

        @Override
        protected void finalize() throws Throwable {
            mView = null;
            super.finalize();
        }
    }

    // ============================
    // Specific methods for Android
    // ============================

    /**
     * android: constants
     */

    private static final int kMaxTouches = 10;

    /**
     * android: member variables
     */

    private GL10 mGLContext;
    private GLSurfaceView mGLSurfaceView;
    private SensorManager mSensorManager;
    private GestureDetector mGestureDetector;
    private ViewGroup mContentLayout;
    private CGRect mTempRenderingViewport;
    private CGSize mTempRenderingSize, mTempSize;
    private UIAcceleration mTempAcceleration;
    private List<UITouch> mInternalTouches, mCurrentTouches;
    private int[] mLocation;

    /**
     * android: property methods
     */

    public Activity getContext() {
        return this;
    }

    @Override
    public GL10 getGLContext() {
        return mGLContext;
    }

    @Override
    public GLSurfaceView getGLSurfaceView() {
        return mGLSurfaceView;
    }

    protected SensorManager getSensorManager() {
        return mSensorManager;
    }

    protected ViewGroup getContentLayout() {
        return mContentLayout;
    }

    @Override
    public CGSize getSize() {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return mTempSize.setValues(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    /**
     * android: touch utility methods
     */

    protected List<UITouch> getTouches(MotionEvent event) {
        return this.getTouches(event, 1);
    }

    protected List<UITouch> getTouches(MotionEvent event, int tapCount) {
        mGLSurfaceView.getLocationOnScreen(mLocation);
        int top = mLocation[1], left = mLocation[0];
        mCurrentTouches.clear();
        for (int i = 0, length = Math.min(event.getPointerCount(), kMaxTouches); i < length; i++) {
            UITouch touch = mInternalTouches.get(i);
            touch.setPosition(event.getX(i) - left, event.getY(i) - top);
            touch.setTapCount(tapCount);
            mCurrentTouches.add(touch);
        }
        return mCurrentTouches;
    }

    /**
     * android: events methods
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mGestureDetector = new GestureDetector
                    (
                            this,
                            new SimpleOnGestureListener() {
                                @Override
                                public boolean onDoubleTap(MotionEvent event) {
                                    return PLActivity.this.onDoubleTap(event);
                                }

                                @Override
                                public boolean onDoubleTapEvent(MotionEvent event) {
                                    return PLActivity.this.onDoubleTapEvent(event);
                                }

                                @Override
                                public boolean onSingleTapConfirmed(MotionEvent event) {
                                    return PLActivity.this.onSingleTapConfirmed(event);
                                }
                            }
                    );
            mTempRenderingViewport = new CGRect();
            mTempRenderingSize = new CGSize();
            mTempSize = new CGSize();
            mTempAcceleration = new UIAcceleration();
            mInternalTouches = new ArrayList<UITouch>(kMaxTouches);
            mCurrentTouches = new ArrayList<UITouch>(kMaxTouches);
            mLocation = new int[2];
            this.initializeValues();
        } catch (Throwable e) {
            PLLog.error("PLView::onCreate", e);
        }
    }

    /**
     * This event is fired when GLSurfaceView is created
     *
     * @param glSurfaceView current GLSurfaceView
     */
    @SuppressWarnings("deprecation")
    protected View onGLSurfaceViewCreated(GLSurfaceView glSurfaceView) {
        for (int i = 0; i < kMaxTouches; i++)
            mInternalTouches.add(new UITouch(glSurfaceView, new CGPoint(0.0f, 0.0f)));
        mContentLayout = new RelativeLayout(this);
        mContentLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        mContentLayout.addView(glSurfaceView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        LayoutParams progressBarLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        progressBarLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        mProgressBar = new ProgressBar(this);
        mProgressBar.setIndeterminate(true);
        mProgressBar.setVisibility(View.GONE);
        mContentLayout.addView(mProgressBar, progressBarLayoutParams);
        return this.onContentViewCreated(mContentLayout);
    }

    /**
     * This event is fired when root content view is created
     *
     * @param contentView current root content view
     * @return root content view that Activity will use
     */
    protected View onContentViewCreated(View contentView) {
        return contentView;
    }

    /**
     * This event is fired when OpenGL context is created
     *
     * @param gl current OpenGL context
     */
    protected void onGLContextCreated(GL10 gl) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mIsRendererCreated && mPanorama != null)
            this.startAnimation();
        this.activateOrientation();
        this.activateAccelerometer();
        if (mIsValidForSensorialRotation) {
            this.updateInitialSensorialRotation();
            if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope)
                this.activateGyroscope();
            else if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer) {
                mSensorialRotationThresholdTimestamp = System.currentTimeMillis() + 1000;
                this.activateMagnetometer();
            }
        }
    }

    @Override
    protected void onPause() {
        this.deactiveAccelerometer();
        this.deactiveOrientation();
        if (mIsValidForSensorialRotation) {
            if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope)
                this.deactivateGyroscope();
            else if (mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer)
                this.deactivateMagnetometer();
        }
        if (mCurrentTransition != null)
            mCurrentTransition.end();
        this.stopAnimation();
        super.onPause();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                if (mIsRendererCreated && mRenderer.isRunning() && !mIsValidForTransition) {
                    if (mSensorialRotationAccelerometerData != null) {
                        mSensorialRotationAccelerometerData[0] = values[0];
                        mSensorialRotationAccelerometerData[1] = values[1];
                        mSensorialRotationAccelerometerData[2] = values[2];
                    }
                    this.accelerometer(event, mTempAcceleration.setValues(values));
                }
                break;
            case Sensor.TYPE_ORIENTATION:
                UIDeviceOrientation newOrientation = mCurrentDeviceOrientation;
                switch (this.getResources().getConfiguration().orientation) {
                    case Configuration.ORIENTATION_PORTRAIT:
                        switch (this.getWindowManager().getDefaultDisplay().getOrientation()) {
                            case Surface.ROTATION_0:
                            case Surface.ROTATION_90:
                                newOrientation = UIDeviceOrientation.UIDeviceOrientationPortrait;
                                break;
                            case Surface.ROTATION_180:
                            case Surface.ROTATION_270:
                                newOrientation = UIDeviceOrientation.UIDeviceOrientationPortraitUpsideDown;
                                break;
                        }
                        break;
                    case Configuration.ORIENTATION_LANDSCAPE:
                        switch (this.getWindowManager().getDefaultDisplay().getOrientation()) {
                            case Surface.ROTATION_0:
                            case Surface.ROTATION_90:
                                newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeLeft;
                                break;
                            case Surface.ROTATION_180:
                            case Surface.ROTATION_270:
                                newOrientation = UIDeviceOrientation.UIDeviceOrientationLandscapeRight;
                                break;
                        }
                        break;
                }
                if (mCurrentDeviceOrientation != newOrientation) {
                    if (mIsValidForSensorialRotation && mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeGyroscope)
                        this.updateGyroscopeRotationByOrientation(mCurrentDeviceOrientation, newOrientation);
                    mCurrentDeviceOrientation = newOrientation;
                }
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                if (mIsRendererCreated && mRenderer.isRunning() && !mIsValidForTransition) {
                    if (mIsValidForSensorialRotation && mSensorialRotationType == PLSensorialRotationType.PLSensorialRotationTypeAccelerometerAndMagnetometer && mSensorialRotationAccelerometerData != null) {
                        if (mSensorialRotationThresholdFlag) {
                            SensorManager.getRotationMatrix(mSensorialRotationRotationMatrix, null, mSensorialRotationAccelerometerData, values);
                            SensorManager.remapCoordinateSystem(mSensorialRotationRotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, mSensorialRotationRotationMatrix);
                            SensorManager.getOrientation(mSensorialRotationRotationMatrix, mSensorialRotationOrientationData);
                            float yaw = mSensorialRotationOrientationData[0] * PLConstants.kToDegrees;
                            float pitch = -mSensorialRotationOrientationData[1] * PLConstants.kToDegrees;
                            if (mHasFirstMagneticHeading) {
                                if ((pitch >= 0.0f && pitch < 50.0f) || (pitch < 0.0f && pitch > -50.0f)) {
                                    yaw -= mFirstMagneticHeading;
                                    float diff = yaw - mLastMagneticHeading;
                                    if (Math.abs(diff) > 100.0f) {
                                        mLastMagneticHeading = yaw;
                                        mMagneticHeading += (diff >= 0.0f ? 360.0f : -360.0f);
                                    } else if ((yaw > mLastMagneticHeading && yaw - PLConstants.kSensorialRotationYawErrorMargin > mLastMagneticHeading) || (yaw < mLastMagneticHeading && yaw + PLConstants.kSensorialRotationYawErrorMargin < mLastMagneticHeading))
                                        mLastMagneticHeading = yaw;
                                }
                            } else {
                                float cameraYaw = mPanorama.getCamera().getLookAtRotation().yaw;
                                mFirstMagneticHeading = yaw - cameraYaw;
                                mLastMagneticHeading = mMagneticHeading = cameraYaw;
                                mHasFirstMagneticHeading = true;
                            }
                            if (mHasFirstAccelerometerPitch) {
                                pitch -= mFirstAccelerometerPitch;
                                if ((pitch > mLastAccelerometerPitch && pitch - PLConstants.kSensorialRotationPitchErrorMargin > mLastAccelerometerPitch) || (pitch < mLastAccelerometerPitch && pitch + PLConstants.kSensorialRotationPitchErrorMargin < mLastAccelerometerPitch))
                                    mLastAccelerometerPitch = pitch;
                            } else {
                                float cameraPitch = mPanorama.getCamera().getLookAtRotation().pitch;
                                mFirstAccelerometerPitch = pitch - cameraPitch;
                                mLastAccelerometerPitch = mAccelerometerPitch = cameraPitch;
                                mHasFirstAccelerometerPitch = true;
                            }
                            this.doSimulatedGyroUpdate();
                        } else {
                            if (mSensorialRotationThresholdTimestamp == 0)
                                mSensorialRotationThresholdTimestamp = System.currentTimeMillis();
                            else if ((System.currentTimeMillis() - mSensorialRotationThresholdTimestamp) >= PLConstants.kSensorialRotationThreshold)
                                mSensorialRotationThresholdFlag = true;
                        }
                    }
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                if (mIsRendererCreated && mRenderer.isRunning() && !mIsValidForTransition) {
                    if (mHasFirstGyroscopePitch) {
                        if (mGyroscopeLastTime != 0) {
                            float timeDiff = (event.timestamp - mGyroscopeLastTime) * PLConstants.kGyroscopeTimeConversion;
                            if (timeDiff > 1.0)
                                timeDiff = PLConstants.kGyroscopeMinTimeStep;
                            mGyroscopeRotationX += values[0] * timeDiff;
                            mGyroscopeRotationY += values[1] * timeDiff;
                            switch (mCurrentDeviceOrientation) {
                                case UIDeviceOrientationUnknown:
                                case UIDeviceOrientationPortrait:
                                    this.doGyroUpdate(mGyroscopeRotationX * PLConstants.kToDegrees, -mGyroscopeRotationY * PLConstants.kToDegrees);
                                    break;
                                case UIDeviceOrientationLandscapeLeft:
                                    this.doGyroUpdate(-mGyroscopeRotationY * PLConstants.kToDegrees, -mGyroscopeRotationX * PLConstants.kToDegrees);
                                    break;
                                case UIDeviceOrientationLandscapeRight:
                                    this.doGyroUpdate(mGyroscopeRotationY * PLConstants.kToDegrees, mGyroscopeRotationX * PLConstants.kToDegrees);
                                    break;
                                case UIDeviceOrientationPortraitUpsideDown:
                                    this.doGyroUpdate(-mGyroscopeRotationX * PLConstants.kToDegrees, mGyroscopeRotationY * PLConstants.kToDegrees);
                                    break;
                                default:
                                    break;
                            }
                        }
                    } else {
                        PLRotation cameraRotation = mPanorama.getCamera().getLookAtRotation();
                        switch (mCurrentDeviceOrientation) {
                            case UIDeviceOrientationUnknown:
                            case UIDeviceOrientationPortrait:
                                mGyroscopeRotationX = cameraRotation.pitch * PLConstants.kToRadians;
                                mGyroscopeRotationY = -cameraRotation.yaw * PLConstants.kToRadians;
                                break;
                            case UIDeviceOrientationLandscapeLeft:
                                mGyroscopeRotationX = -cameraRotation.yaw * PLConstants.kToRadians;
                                mGyroscopeRotationY = -cameraRotation.pitch * PLConstants.kToRadians;
                                break;
                            case UIDeviceOrientationLandscapeRight:
                                mGyroscopeRotationX = cameraRotation.yaw * PLConstants.kToRadians;
                                mGyroscopeRotationY = cameraRotation.pitch * PLConstants.kToRadians;
                                break;
                            case UIDeviceOrientationPortraitUpsideDown:
                                mGyroscopeRotationX = -cameraRotation.pitch * PLConstants.kToRadians;
                                mGyroscopeRotationY = cameraRotation.yaw * PLConstants.kToRadians;
                                break;
                            default:
                                break;
                        }
                        mHasFirstGyroscopePitch = true;
                    }
                    mGyroscopeLastTime = event.timestamp;
                }
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mIsRendererCreated && mRenderer.isRunning() && !mIsValidForTransition) {
            if (mGestureDetector.onTouchEvent(event))
                return true;
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN:
                    this.touchesBegan(this.getTouches(event), event);
                    return true;
                case MotionEvent.ACTION_MOVE:
                    this.touchesMoved(this.getTouches(event), event);
                    return true;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP:
                    this.touchesEnded(this.getTouches(event), event);
                    return true;
            }
        }
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        this.touchesBegan(this.getTouches(event, 2), event);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        return false;
    }
}