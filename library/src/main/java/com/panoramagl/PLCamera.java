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

package com.panoramagl;

import com.panoramagl.computation.PLMath;
import com.panoramagl.enumerations.PLCameraAnimationType;
import com.panoramagl.ios.NSTimer;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLRotation;
import com.panoramagl.utils.PLUtils;

import javax.microedition.khronos.opengles.GL10;

public class PLCamera extends PLRenderableElementBase implements PLICamera {
    /**
     * member variables
     */

    private boolean mIsNotLocked, mIsFovEnabled;
    private float mFov, mInitialFov, mFovSensitivity, mFovSensitivityByDisplayPPI;
    private PLRange mFovRange;
    private int mMinDistanceToEnableFov;
    private float mRotationSensitivity, mRotationSensitivityByDisplayPPI;
    private int mZoomLevels;
    private PLRotation mInitialLookAt, mLookAtRotation;
    private boolean mIsAnimating;
    private PLCameraAnimationType mAnimationType;
    private NSTimer mAnimationTimer;
    private PLCameraListener mInternalListener, mListener;

    /**
     * init methods
     */

    public PLCamera() {
        super();
    }

    public PLCamera(PLICamera camera) {
        super();
        this.clonePropertiesOf(camera);
    }

    @Override
    protected void initializeValues() {
        mIsNotLocked = mIsFovEnabled = true;
        mFovRange = PLRange.PLRangeMake(PLConstants.kDefaultFovMinValue, PLConstants.kDefaultFovMaxValue);
        mFov = mInitialFov = PLMath.normalizeFov(PLConstants.kDefaultFov, mFovRange);
        this.setInternalFovSensitivity(PLConstants.kDefaultFovSensitivity);
        mMinDistanceToEnableFov = PLConstants.kDefaultMinDistanceToEnableFov;
        this.setInternalRotationSensitivity(PLConstants.kDefaultRotationSensitivity);
        mZoomLevels = PLConstants.kDefaultZoomLevels;
        mInitialLookAt = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f);
        mLookAtRotation = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f);
        mIsAnimating = false;
        mAnimationType = PLCameraAnimationType.PLCameraAnimationTypeNone;
        mAnimationTimer = null;
        super.initializeValues();
        this.setReverseRotation(true);
    }

    /**
     * reset methods
     */

    @Override
    public void reset() {
        this.reset(null);
    }

    @Override
    public void reset(Object sender) {
        if (mIsNotLocked) {
            super.reset();
            this.internalStopAnimation(sender);
            this.setInternalFov(sender, mInitialFov, false, true, false);
            this.internalLookAt(sender, mInitialLookAt.pitch, mInitialLookAt.yaw, false, true, false);
            if (mInternalListener != null)
                mInternalListener.didReset(sender, this);
            if (mListener != null)
                mListener.didReset(sender, this);
        }
    }

    /**
     * property methods
     */

    @Override
    public boolean isLocked() {
        return !mIsNotLocked;
    }

    @Override
    public void setLocked(boolean isLocked) {
        mIsNotLocked = !isLocked;
    }

    @Override
    public boolean isFovEnabled() {
        return mIsFovEnabled;
    }

    @Override
    public void setFovEnabled(boolean isFovEnabled) {
        if (mIsNotLocked)
            mIsFovEnabled = isFovEnabled;
    }

    @Override
    public float getInitialFov() {
        return mInitialFov;
    }

    @Override
    public void setInitialFov(float initialFov) {
        if (mIsNotLocked)
            mInitialFov = PLMath.normalizeFov(initialFov, mFovRange);
    }

    protected void setInternalInitialFov(float initialFov) {
        mInitialFov = PLMath.normalizeFov(initialFov, mFovRange);
    }

    @Override
    public float getFov() {
        return mFov;
    }

    @Override
    public void setFov(float fov) {
        if (mIsNotLocked)
            this.setInternalFov(null, fov, false, true, false);
    }

    @Override
    public float getFovFactor() {
        return ((mFov - mFovRange.max) / (mFovRange.max - mFovRange.min) + 1.0f);
    }

    @Override
    public void setFovFactor(float fovFactor) {
        this.setFovFactor(null, fovFactor, false);
    }

    @Override
    public float getFovSensitivity() {
        return mFovSensitivity;
    }

    protected float getFovSensitivityByDisplayPPI() {
        return mFovSensitivityByDisplayPPI;
    }

    @Override
    public void setFovSensitivity(float fovSensitivity) {
        if (mIsNotLocked && fovSensitivity >= PLConstants.kFovSensitivityMinValue && fovSensitivity <= PLConstants.kFovSensitivityMaxValue)
            this.setInternalFovSensitivity(fovSensitivity);
    }

    protected void setInternalFovSensitivity(float fovSensitivity) {
        if (mFovSensitivity != fovSensitivity) {
            mFovSensitivity = fovSensitivity;
            mFovSensitivityByDisplayPPI = PLConstants.kDisplayPPIBaseline / PLUtils.getDisplayPPI() * fovSensitivity;
        }
    }

    @Override
    public PLRange getFovRange() {
        return mFovRange;
    }

    @Override
    public void setFovRange(PLRange range) {
        this.setFovRange(range.min, range.max);
    }

    @Override
    public void setFovRange(float min, float max) {
        if (mIsNotLocked && max >= min) {
            mFovRange.setValues((min < PLConstants.kFovMinValue ? PLConstants.kFovMinValue : min), (max > PLConstants.kFovMaxValue ? PLConstants.kFovMaxValue : max));
            this.setInitialFov(mInitialFov);
            this.setInternalFov(null, mFov, false, true, false);
        }
    }

    protected void setInternalFovRange(float min, float max) {
        mFovRange.setValues((min < PLConstants.kFovMinValue ? PLConstants.kFovMinValue : min), (max > PLConstants.kFovMaxValue ? PLConstants.kFovMaxValue : max));
    }

    @Override
    public float getFovMin() {
        return mFovRange.min;
    }

    @Override
    public void setFovMin(float min) {
        if (mIsNotLocked && mFovRange.max >= min) {
            mFovRange.min = (min < PLConstants.kFovMinValue ? PLConstants.kFovMinValue : min);
            this.setInitialFov(mInitialFov);
            this.setInternalFov(null, mFov, false, true, false);
        }
    }

    @Override
    public float getFovMax() {
        return mFovRange.max;
    }

    @Override
    public void setFovMax(float max) {
        if (mIsNotLocked && max >= mFovRange.min) {
            mFovRange.max = (max > PLConstants.kFovMaxValue ? PLConstants.kFovMaxValue : max);
            this.setInitialFov(mInitialFov);
            this.setInternalFov(null, mFov, false, true, false);
        }
    }

    @Override
    public int getMinDistanceToEnableFov() {
        return mMinDistanceToEnableFov;
    }

    @Override
    public void setMinDistanceToEnableFov(int distance) {
        if (mIsNotLocked && distance > 0)
            mMinDistanceToEnableFov = distance;
    }

    protected void setInternalMinDistanceToEnableFov(int distance) {
        mMinDistanceToEnableFov = distance;
    }

    @Override
    public float getRotationSensitivity() {
        return mRotationSensitivity;
    }

    protected float getRotationSensitivityByDisplayPPI() {
        return mRotationSensitivityByDisplayPPI;
    }

    @Override
    public void setRotationSensitivity(float rotationSensitivity) {
        if (mIsNotLocked && rotationSensitivity >= PLConstants.kRotationSensitivityMinValue && rotationSensitivity <= PLConstants.kRotationSensitivityMaxValue)
            this.setInternalRotationSensitivity(rotationSensitivity);
    }

    protected void setInternalRotationSensitivity(float rotationSensitivity) {
        if (mRotationSensitivity != rotationSensitivity) {
            mRotationSensitivity = rotationSensitivity;
            mRotationSensitivityByDisplayPPI = PLConstants.kDisplayPPIBaseline / PLUtils.getDisplayPPI() * rotationSensitivity;
        }
    }

    @Override
    public float getZoomFactor() {
        return (1.0f - this.getFovFactor());
    }

    @Override
    public void setZoomFactor(float zoomFactor) {
        this.setZoomFactor(null, zoomFactor, false);
    }

    @Override
    public int getZoomLevel() {
        return (mFovRange.min != mFovRange.max ? (int) Math.round((mFovRange.max - mFov) / ((mFovRange.max - mFovRange.min) / mZoomLevels)) : 0);
    }

    @Override
    public void setZoomLevel(int zoomLevel) {
        this.setZoomLevel(null, zoomLevel, false);
    }

    @Override
    public int getZoomLevels() {
        return mZoomLevels;
    }

    @Override
    public void setZoomLevels(int zoomLevels) {
        if (mIsNotLocked && zoomLevels > 0)
            mZoomLevels = zoomLevels;
    }

    protected void setInternalZoomLevels(int zoomLevels) {
        mZoomLevels = zoomLevels;
    }

    @Override
    public PLRotation getInitialLookAt() {
        return mInitialLookAt;
    }

    @Override
    public void setInitialLookAt(PLRotation rotation) {
        if (rotation != null) {
            this.setInitialPitch(rotation.pitch);
            this.setInitialYaw(rotation.yaw);
        }
    }

    @Override
    public void setInitialLookAt(float pitch, float yaw) {
        this.setInitialPitch(pitch);
        this.setInitialYaw(yaw);
    }

    @Override
    public float getInitialPitch() {
        return mInitialLookAt.pitch;
    }

    @Override
    public void setInitialPitch(float pitch) {
        if (mIsNotLocked)
            mInitialLookAt.pitch = this.getRotationAngleNormalized(pitch, this.getPitchRange());
    }

    protected void setInternalInitialPitch(float pitch) {
        mInitialLookAt.pitch = this.getRotationAngleNormalized(pitch, this.getPitchRange());
    }

    @Override
    public float getInitialYaw() {
        return mInitialLookAt.yaw;
    }

    @Override
    public void setInitialYaw(float yaw) {
        if (mIsNotLocked)
            mInitialLookAt.yaw = this.getRotationAngleNormalized(yaw, this.getYawRange());
    }

    protected void setInternalInitialYaw(float yaw) {
        mInitialLookAt.yaw = this.getRotationAngleNormalized(yaw, this.getYawRange());
    }

    @Override
    public PLRotation getLookAtRotation() {
        PLRotation rotation = this.getRotation();
        return (this.isReverseRotation() ? mLookAtRotation.setValues(rotation) : mLookAtRotation.setValues(-rotation.pitch, -rotation.yaw, -rotation.roll));
    }

    @Override
    public boolean isAnimating() {
        return mIsAnimating;
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
    public void setVisible(boolean isVisible) {
        if (mIsNotLocked)
            super.setVisible(isVisible);
    }

    protected void setInternalVisible(boolean isVisible) {
        super.setVisible(isVisible);
    }

    @Override
    public void setX(float x) {
        if (mIsNotLocked)
            this.setInternalX(x);
    }

    @Override
    public void setY(float y) {
        if (mIsNotLocked)
            this.setInternalY(y);
    }

    @Override
    public void setZ(float z) {
        if (mIsNotLocked)
            this.setInternalZ(z);
    }

    @Override
    public void setPitch(float pitch) {
        if (mIsNotLocked)
            this.setInternalPitch(pitch);
    }

    @Override
    public void setYaw(float yaw) {
        if (mIsNotLocked)
            this.setInternalYaw(yaw);
    }

    @Override
    public void setRoll(float roll) {
        if (mIsNotLocked)
            this.setInternalRoll(roll);
    }

    @Override
    public PLCameraListener getInternalListener() {
        return mInternalListener;
    }

    @Override
    public void setInternalListener(PLCameraListener listener) {
        mInternalListener = listener;
    }

    @Override
    public PLCameraListener getListener() {
        return mListener;
    }

    @Override
    public void setListener(PLCameraListener listener) {
        if (mIsNotLocked)
            mListener = listener;
    }

    /**
     * animation methods
     */

    protected boolean internalStartAnimation(Object sender, NSTimer timer, PLCameraAnimationType type) {
        if (!mIsAnimating) {
            mIsAnimating = true;
            mAnimationType = type;
            this.setAnimationTimer(timer);
            if (mInternalListener != null)
                mInternalListener.didBeginAnimation(sender, this, type);
            if (mListener != null)
                mListener.didBeginAnimation(sender, this, type);
            return true;
        }
        return false;
    }

    protected boolean internalStopAnimation(Object sender) {
        if (mIsAnimating) {
            mIsAnimating = false;
            this.setAnimationTimer(null);
            if (mInternalListener != null)
                mInternalListener.didEndAnimation(sender, this, mAnimationType);
            if (mListener != null)
                mListener.didEndAnimation(sender, this, mAnimationType);
            mAnimationType = PLCameraAnimationType.PLCameraAnimationTypeNone;
            return true;
        }
        return false;
    }

    @Override
    public boolean stopAnimation() {
        return (mIsNotLocked ? this.internalStopAnimation(null) : false);
    }

    @Override
    public boolean stopAnimation(Object sender) {
        return (mIsNotLocked ? this.internalStopAnimation(sender) : false);
    }

    /**
     * conversion methods
     */

    protected float convertFovFactorToFov(float fovFactor) {
        return (mFovRange.max - ((1.0f - fovFactor) * (mFovRange.max - mFovRange.min)));
    }

    /**
     * fov methods
     */

    @Override
    public boolean setFov(float fov, boolean animated) {
        return this.setFov(null, fov, animated);
    }

    @Override
    public boolean setFov(Object sender, float fov, boolean animated) {
        if (mIsNotLocked) {
            if (animated) {
                if (!mIsAnimating && mIsFovEnabled) {
                    float newFov = PLMath.normalizeFov(fov, mFovRange);
                    if (mFov != newFov) {
                        this.internalStartAnimation
                                (
                                        sender,
                                        NSTimer.scheduledTimerWithTimeInterval
                                                (
                                                        PLConstants.kCameraFovAnimationTimerInterval,
                                                        new NSTimer.Runnable() {
                                                            @Override
                                                            public void run(NSTimer target, Object[] userInfo) {
                                                                PLCamera camera = (PLCamera) userInfo[0];
                                                                PLFovAnimatedData data = (PLFovAnimatedData) userInfo[1];
                                                                data.currentFov += data.fovStep;
                                                                camera.setInternalFov(data.sender, data.currentFov, true, false, true);
                                                                data.currentStep++;
                                                                if (data.currentStep >= data.maxStep) {
                                                                    camera.internalStopAnimation(data.sender);
                                                                    camera.setInternalFov(data.sender, data.maxFov, true, true, true);
                                                                }
                                                            }
                                                        },
                                                        new Object[]{this, PLFovAnimatedData.PLFovAnimatedDataMake(sender, this, newFov, PLConstants.kCameraFovAnimationMaxStep)},
                                                        true
                                                ),
                                        PLCameraAnimationType.PLCameraAnimationTypeFov
                                );
                        return true;
                    }
                }
            } else
                return this.setInternalFov(sender, fov, false, true, false);
        }
        return false;
    }

    protected boolean setInternalFov(Object sender, float fov, boolean skipFovEnabled, boolean fireEvent, boolean isEventAnimated) {
        if (skipFovEnabled || mIsFovEnabled) {
            float newFov = PLMath.normalizeFov(fov, mFovRange);
            if (mFov != newFov) {
                mFov = newFov;
                if (fireEvent) {
                    if (mInternalListener != null)
                        mInternalListener.didFov(sender, this, newFov, isEventAnimated);
                    if (mListener != null)
                        mListener.didFov(sender, this, newFov, isEventAnimated);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setFovFactor(float fovFactor, boolean animated) {
        return (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f ? this.setFov(null, this.convertFovFactorToFov(fovFactor), animated) : false);
    }

    @Override
    public boolean setFovFactor(Object sender, float fovFactor, boolean animated) {
        return (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f ? this.setFov(sender, this.convertFovFactorToFov(fovFactor), animated) : false);
    }

    @Override
    public boolean addFov(float distance) {
        return this.addFov(null, distance);
    }

    @Override
    public boolean addFov(Object sender, float distance) {
        return (mIsNotLocked ? this.setInternalFov(sender, mFov - (distance / PLConstants.kDisplayPPIBaseline * mFovSensitivityByDisplayPPI), false, true, false) : false);
    }

    /**
     * zoom methods
     */

    @Override
    public boolean setZoomFactor(float zoomFactor, boolean animated) {
        return (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f ? this.setFovFactor(null, 1.0f - zoomFactor, animated) : false);
    }

    @Override
    public boolean setZoomFactor(Object sender, float zoomFactor, boolean animated) {
        return (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f ? this.setFovFactor(sender, 1.0f - zoomFactor, animated) : false);
    }

    @Override
    public boolean setZoomLevel(int zoomLevel, boolean animated) {
        return this.setZoomLevel(null, zoomLevel, animated);
    }

    @Override
    public boolean setZoomLevel(Object sender, int zoomLevel, boolean animated) {
        if (mIsNotLocked && zoomLevel >= 0 && zoomLevel <= mZoomLevels)
            return this.setFov(sender, mFovRange.max - (((mFovRange.max - mFovRange.min) / mZoomLevels) * zoomLevel), animated);
        return false;
    }

    @Override
    public boolean zoomIn(boolean animated) {
        return this.setZoomLevel(null, this.getZoomLevel() + 1, animated);
    }

    @Override
    public boolean zoomIn(Object sender, boolean animated) {
        return this.setZoomLevel(sender, this.getZoomLevel() + 1, animated);
    }

    @Override
    public boolean zoomOut(boolean animated) {
        return this.setZoomLevel(null, this.getZoomLevel() - 1, animated);
    }

    @Override
    public boolean zoomOut(Object sender, boolean animated) {
        return this.setZoomLevel(sender, this.getZoomLevel() - 1, animated);
    }

    /**
     * lookat methods
     */

    @Override
    public boolean lookAt(PLRotation rotation) {
        return (mIsNotLocked ? this.internalLookAt(null, rotation.pitch, rotation.yaw, false, true, false) : false);
    }

    @Override
    public boolean lookAt(Object sender, PLRotation rotation) {
        return (mIsNotLocked ? this.internalLookAt(sender, rotation.pitch, rotation.yaw, false, true, false) : false);
    }

    @Override
    public boolean lookAt(PLRotation rotation, boolean animated) {
        return this.lookAt(null, rotation.pitch, rotation.yaw, animated);
    }

    @Override
    public boolean lookAt(Object sender, PLRotation rotation, boolean animated) {
        return this.lookAt(sender, rotation.pitch, rotation.yaw, animated);
    }

    @Override
    public boolean lookAt(float pitch, float yaw) {
        return (mIsNotLocked ? this.internalLookAt(null, pitch, yaw, false, true, false) : false);
    }

    @Override
    public boolean lookAt(Object sender, float pitch, float yaw) {
        return (mIsNotLocked ? this.internalLookAt(sender, pitch, yaw, false, true, false) : false);
    }

    @Override
    public boolean lookAt(float pitch, float yaw, boolean animated) {
        return this.lookAt(null, pitch, yaw, animated);
    }

    @Override
    public boolean lookAt(Object sender, float pitch, float yaw, boolean animated) {
        if (mIsNotLocked) {
            if (animated) {
                if (!mIsAnimating && this.isPitchEnabled() && this.isYawEnabled()) {
                    this.internalStartAnimation
                            (
                                    sender,
                                    NSTimer.scheduledTimerWithTimeInterval
                                            (
                                                    PLConstants.kCameraLookAtAnimationTimerInterval,
                                                    new NSTimer.Runnable() {
                                                        @Override
                                                        public void run(NSTimer target, Object[] userInfo) {
                                                            PLCamera camera = (PLCamera) userInfo[0];
                                                            PLLookAtAnimatedData data = (PLLookAtAnimatedData) userInfo[1];
                                                            data.currentPitch += data.pitchStep;
                                                            data.currentYaw += data.yawStep;
                                                            camera.internalLookAt(data.sender, data.currentPitch, data.currentYaw, true, false, true);
                                                            data.currentStep++;
                                                            if (data.currentStep >= data.maxStep) {
                                                                camera.internalStopAnimation(data.sender);
                                                                camera.internalLookAt(data.sender, data.maxPitch, data.maxYaw, true, true, true);
                                                            }
                                                        }
                                                    },
                                                    new Object[]{this, PLLookAtAnimatedData.PLLookAtAnimatedDataMake(sender, this, pitch, yaw, PLConstants.kCameraLookAtAnimationMaxStep)},
                                                    true
                                            ),
                                    PLCameraAnimationType.PLCameraAnimationTypeLookAt
                            );
                    return true;
                }
            } else
                return this.internalLookAt(sender, pitch, yaw, false, true, false);
        }
        return false;
    }

    protected boolean internalLookAt(Object sender, float pitch, float yaw, boolean skipRotationEnabled, boolean fireEvent, boolean isEventAnimated) {
        if (skipRotationEnabled || (this.isPitchEnabled() && this.isYawEnabled())) {
            if (!this.isReverseRotation()) {
                pitch = -pitch;
                yaw = -yaw;
            }
            this.setInternalPitch(pitch);
            this.setInternalYaw(yaw);
            if (fireEvent) {
                pitch = this.getPitch();
                yaw = this.getYaw();
                if (mInternalListener != null)
                    mInternalListener.didLookAt(sender, this, pitch, yaw, isEventAnimated);
                if (mListener != null)
                    mListener.didLookAt(sender, this, pitch, yaw, isEventAnimated);
            }
            return true;
        }
        return false;
    }

    /**
     * lookat and fov combined methods
     **/

    @Override
    public boolean lookAtAndFov(float pitch, float yaw, float fov, boolean animated) {
        return this.lookAtAndFov(null, pitch, yaw, fov, animated);
    }

    @Override
    public boolean lookAtAndFov(Object sender, float pitch, float yaw, float fov, boolean animated) {
        if (mIsNotLocked) {
            if (animated) {
                if (!mIsAnimating && this.isPitchEnabled() && this.isYawEnabled() && mIsFovEnabled) {
                    this.internalStartAnimation
                            (
                                    sender,
                                    NSTimer.scheduledTimerWithTimeInterval
                                            (
                                                    PLConstants.kCameraLookAtAnimationTimerInterval,
                                                    new NSTimer.Runnable() {
                                                        @Override
                                                        public void run(NSTimer target, Object[] userInfo) {
                                                            PLCamera camera = (PLCamera) userInfo[0];
                                                            PLLookAtAndFovAnimatedData data = (PLLookAtAndFovAnimatedData) userInfo[1];
                                                            data.currentPitch += data.pitchStep;
                                                            data.currentYaw += data.yawStep;
                                                            data.currentFov += data.fovStep;
                                                            camera.internalLookAt(data.sender, data.currentPitch, data.currentYaw, true, false, true);
                                                            camera.setInternalFov(data.sender, data.currentFov, true, false, true);
                                                            data.currentStep++;
                                                            if (data.currentStep >= data.maxStep) {
                                                                camera.internalStopAnimation(data.sender);
                                                                camera.internalLookAt(data.sender, data.maxPitch, data.maxYaw, true, true, true);
                                                                camera.setInternalFov(data.sender, data.maxFov, true, true, true);
                                                            }
                                                        }
                                                    },
                                                    new Object[]{this, PLLookAtAndFovAnimatedData.PLLookAtAndFovAnimatedDataMake(sender, this, pitch, yaw, fov, PLConstants.kCameraLookAtAnimationMaxStep)},
                                                    true
                                            ),
                                    PLCameraAnimationType.PLCameraAnimationTypeLookAt
                            );
                    return true;
                }
            } else
                return (this.internalLookAt(sender, pitch, yaw, false, true, false) && this.setInternalFov(sender, fov, false, true, false));
        }
        return false;
    }

    @Override
    public boolean lookAtAndFovFactor(float pitch, float yaw, float fovFactor, boolean animated) {
        return (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f ? this.lookAtAndFov(null, pitch, yaw, this.convertFovFactorToFov(fovFactor), animated) : false);
    }

    @Override
    public boolean lookAtAndFovFactor(Object sender, float pitch, float yaw, float fovFactor, boolean animated) {
        return (mIsNotLocked && fovFactor >= 0.0f && fovFactor <= 1.0f ? this.lookAtAndFov(sender, pitch, yaw, this.convertFovFactorToFov(fovFactor), animated) : false);
    }

    @Override
    public boolean lookAtAndZoomFactor(float pitch, float yaw, float zoomFactor, boolean animated) {
        return (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f ? this.lookAtAndFov(null, pitch, yaw, this.convertFovFactorToFov(1.0f - zoomFactor), animated) : false);
    }

    @Override
    public boolean lookAtAndZoomFactor(Object sender, float pitch, float yaw, float zoomFactor, boolean animated) {
        return (mIsNotLocked && zoomFactor >= 0.0f && zoomFactor <= 1.0f ? this.lookAtAndFov(sender, pitch, yaw, this.convertFovFactorToFov(1.0f - zoomFactor), animated) : false);
    }

    /**
     * rotate methods
     */

    @Override
    public void rotate(float pitch, float yaw) {
        this.rotate(null, pitch, yaw);
    }

    @Override
    public void rotate(Object sender, float pitch, float yaw) {
        if (mIsNotLocked) {
            super.rotate(pitch, yaw);
            pitch = this.getPitch();
            yaw = this.getYaw();
            float roll = this.getRoll();
            if (mInternalListener != null)
                mInternalListener.didRotate(sender, this, pitch, yaw, roll);
            if (mListener != null)
                mListener.didRotate(sender, this, pitch, yaw, roll);
        }
    }

    @Override
    public void rotate(float pitch, float yaw, float roll) {
        this.rotate(null, pitch, yaw, roll);
    }

    @Override
    public void rotate(Object sender, float pitch, float yaw, float roll) {
        if (mIsNotLocked) {
            super.rotate(pitch, yaw, roll);
            pitch = this.getPitch();
            yaw = this.getYaw();
            roll = this.getRoll();
            if (mInternalListener != null)
                mInternalListener.didRotate(sender, this, pitch, yaw, roll);
            if (mListener != null)
                mListener.didRotate(sender, this, pitch, yaw, roll);
        }
    }

    @Override
    public void rotate(CGPoint startPoint, CGPoint endPoint) {
        this.rotate(null, startPoint, endPoint);
    }

    @Override
    public void rotate(Object sender, CGPoint startPoint, CGPoint endPoint) {
        if (mIsNotLocked) {
            float yOffset = endPoint.y - startPoint.y, xOffset = startPoint.x - endPoint.x;
            boolean didRotatePitch = (yOffset != 0.0f), didRotateYaw = (xOffset != 0.0f);
            if (didRotatePitch || didRotateYaw) {
                float rotationSensitivity = mFov / PLConstants.kFovBaseline * mRotationSensitivityByDisplayPPI;
                if (didRotatePitch)
                    this.setPitch(this.getPitch() + ((yOffset / PLConstants.kMaxDisplaySize * rotationSensitivity)));
                if (didRotateYaw)
                    this.setYaw(this.getYaw() + ((xOffset / PLConstants.kMaxDisplaySize * rotationSensitivity)));
                float pitch = this.getPitch(), yaw = this.getYaw(), roll = this.getRoll();
                if (mInternalListener != null)
                    mInternalListener.didRotate(sender, this, pitch, yaw, roll);
                if (mListener != null)
                    mListener.didRotate(sender, this, pitch, yaw, roll);
            }
        }
    }

    /**
     * clear methods
     */

    @Override
    protected void internalClear() {
    }

    /**
     * render methods
     */

    @Override
    protected void beginRender(GL10 gl, PLIRenderer renderer) {
        this.rotate(gl);
        this.translate(gl);
    }

    @Override
    protected void internalRender(GL10 gl, PLIRenderer renderer) {
    }

    @Override
    protected void endRender(GL10 gl, PLIRenderer renderer) {
    }

    /**
     * clone methods
     */

    @Override
    public boolean clonePropertiesOf(PLIObject object) {
        if (mIsNotLocked && super.clonePropertiesOf(object)) {
            if (object instanceof PLICamera) {
                PLICamera camera = (PLICamera) object;
                this.setFovRange(camera.getFovRange());
                this.setFovSensitivity(camera.getFovSensitivity());
                this.setMinDistanceToEnableFov(camera.getMinDistanceToEnableFov());
                this.setInitialFov(camera.getInitialFov());
                this.setFovEnabled(camera.isFovEnabled());
                this.setInternalFov(null, camera.getFov(), true, false, false);
                this.setRotationSensitivity(camera.getRotationSensitivity());
                this.setZoomLevels(camera.getZoomLevels());
                this.setInitialLookAt(camera.getInitialLookAt());
                this.setListener(camera.getListener());
            }
            return true;
        }
        return false;
    }

    @Override
    public PLICamera clone() {
        return new PLCamera(this);
    }

    /**
     * dealloc methods
     */

    @Override
    protected void finalize() throws Throwable {
        this.internalStopAnimation(null);
        mInitialLookAt = mLookAtRotation = null;
        mFovRange = null;
        mInternalListener = mListener = null;
        super.finalize();
    }

    /**
     * internal classes declaration
     */

    protected static class PLAnimatedDataBase {
        /**
         * member variables
         */

        public Object sender;
        public int currentStep, maxStep;

        /**
         * init methods
         */

        public PLAnimatedDataBase(Object senderValue) {
            super();
            sender = senderValue;
            currentStep = 0;
        }

        /**
         * dealloc methods
         */

        @Override
        protected void finalize() throws Throwable {
            sender = null;
            super.finalize();
        }
    }

    protected static class PLFovAnimatedData extends PLAnimatedDataBase {
        /**
         * member variables
         */

        public float currentFov, maxFov, fovStep;

        /**
         * init methods
         */

        public PLFovAnimatedData(Object sender, PLCamera camera, float fov, int defaultMaxStep) {
            super(sender);
            currentFov = camera.getFov();
            maxFov = PLMath.normalizeFov(fov, camera.getFovRange());
            float fovDiff = maxFov - currentFov, maxDiff = PLConstants.kFovMaxValue - Math.abs(fovDiff);
            maxStep = Math.max((int) Math.sqrt(defaultMaxStep * defaultMaxStep * Math.abs(1.0f - maxDiff * maxDiff / PLConstants.kFovMax2Value)), 1);
            fovStep = fovDiff / maxStep;
        }

        public static PLFovAnimatedData PLFovAnimatedDataMake(Object sender, PLCamera camera, float fov, int defaultMaxStep) {
            return new PLFovAnimatedData(sender, camera, fov, defaultMaxStep);
        }
    }

    protected static class PLLookAtAnimatedData extends PLAnimatedDataBase {
        /**
         * member variables
         */

        public float currentPitch, maxPitch, pitchStep;
        public float currentYaw, maxYaw, yawStep;

        /**
         * init methods
         */

        public PLLookAtAnimatedData(Object sender, PLCamera camera, float pitch, float yaw, int defaultMaxStep) {
            super(sender);
            PLRotation rotation = camera.getLookAtRotation();
            currentPitch = rotation.pitch;
            currentYaw = rotation.yaw;
            maxPitch = camera.getRotationAngleNormalized(pitch, camera.getPitchRange());
            maxYaw = camera.getRotationAngleNormalized(yaw, camera.getYawRange());
            float pitchDiff = camera.getRotationAngleNormalized(maxPitch - currentPitch, camera.getPitchRange());
            float yawDiff = camera.getRotationAngleNormalized(maxYaw - currentYaw, camera.getYawRange());
            float maxDiff = PLConstants.kRotationMaxValue - Math.abs(Math.abs(pitchDiff) > Math.abs(yawDiff) ? pitchDiff : yawDiff);
            maxStep = Math.max((int) Math.sqrt(defaultMaxStep * defaultMaxStep * Math.abs(1.0f - maxDiff * maxDiff / PLConstants.kRotationMax2Value)), 1);
            pitchStep = pitchDiff / maxStep;
            if (yawDiff > 180.0f)
                yawStep = (yawDiff - 360.0f) / maxStep;
            else if (yawDiff < -180.0f)
                yawStep = (360.0f - yawDiff) / maxStep;
            else
                yawStep = yawDiff / maxStep;
        }

        public static PLLookAtAnimatedData PLLookAtAnimatedDataMake(Object sender, PLCamera camera, float pitch, float yaw, int defaultMaxStep) {
            return new PLLookAtAnimatedData(sender, camera, pitch, yaw, defaultMaxStep);
        }
    }

    protected static class PLLookAtAndFovAnimatedData extends PLLookAtAnimatedData {
        /**
         * member variables
         */

        public float currentFov, maxFov, fovStep;

        /**
         * init methods
         */

        public PLLookAtAndFovAnimatedData(Object sender, PLCamera camera, float pitch, float yaw, float fov, int defaultMaxStep) {
            super(sender, camera, pitch, yaw, defaultMaxStep);
            currentFov = camera.getFov();
            maxFov = PLMath.normalizeFov(fov, camera.getFovRange());
            fovStep = (maxFov - currentFov) / maxStep;
        }

        public static PLLookAtAndFovAnimatedData PLLookAtAndFovAnimatedDataMake(Object sender, PLCamera camera, float pitch, float yaw, float fov, int defaultMaxStep) {
            return new PLLookAtAndFovAnimatedData(sender, camera, pitch, yaw, fov, defaultMaxStep);
        }
    }
}