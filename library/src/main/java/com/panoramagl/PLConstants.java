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

import com.panoramagl.enumerations.PLCubeFaceOrientation;

public class PLConstants {
    /**
     * math constants
     */

    public static final float kPI = 3.14159265358979323846f;
    public static final float kPI8 = (kPI / 8.0f);
    public static final float kPI16 = (kPI / 16.0f);
    public static final float kToDegrees = (180.0f / kPI);
    public static final float kToRadians = (kPI / 180.0f);

    /**
     * float constants
     */

    public static final float kFloatMinValue = -1000000.0f;
    public static final float kFloatMaxValue = Float.MAX_VALUE;
    public static final float kFloatUndefinedValue = -3829713706.1158636387f;

    /**
     * display constants
     */

    public static final int kMaxDisplaySize = 4096;
    public static final float kDisplayPPIBaseline = 218.104455f;

    /**
     * object constants
     */

    public static final float kDefaultAlpha = 1.0f;

    /**
     * texture constants
     */

    public static final int kTextureMaxSize = 2048;

    /**
     * renderer constants
     */

    public static final int kViewportSize = kMaxDisplaySize;
    public static final float kViewportScale = 5.12f;
    public static final float kPerspectiveAspect = 1.0f;
    public static final float kPerspectiveZNear = 0.01f;
    public static final float kPerspectiveZFar = 100.0f;

    /**
     * panorama constants
     */

    public static final float kPanoramaRadius = 1.0f;

    /**
     * cube constants
     */

    public static final int kCubeFrontFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationFront.ordinal();
    public static final int kCubeBackFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationBack.ordinal();
    public static final int kCubeLeftFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationLeft.ordinal();
    public static final int kCubeRightFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationRight.ordinal();
    public static final int kCubeUpFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationUp.ordinal();
    public static final int kCubeDownFaceIndex = PLCubeFaceOrientation.PLCubeFaceOrientationDown.ordinal();

    /**
     * sphere constants
     */

    public static final int kDefaultSpherePreviewDivs = 50;
    public static final int kDefaultSphereDivs = 50;

    /**
     * sphere2 constants
     */

    public static final int kDefaultSphere2PreviewDivs = 30;
    public static final int kDefaultSphere2Divs = 40;

    /**
     * cylinder constants
     */

    public static final int kDefaultCylinderPreviewDivs = 60;
    public static final int kDefaultCylinderDivs = 60;
    public static final float kDefaultCylinderHeight = 3.0f;

    /**
     * animation constants
     */

    public static final float kDefaultAnimationTimerInterval = (1.0f / 45.0f);
    public static final float kDefaultAnimationTimerIntervalByFrame = (1.0f / 30.0f);
    public static final int kDefaultAnimationFrameInterval = 1;
    public static final float kCameraLookAtAnimationTimerInterval = kDefaultAnimationTimerInterval;
    public static final float kCameraFovAnimationTimerInterval = kDefaultAnimationTimerInterval;
    public static final int kCameraLookAtAnimationMaxStep = 45;
    public static final int kCameraFovAnimationMaxStep = 25;

    /**
     * rotation constants
     */

    public static final float kRotationMinValue = -180.0f;
    public static final float kRotationMaxValue = 180.0f;
    public static final float kRotationMax2Value = (kRotationMaxValue * kRotationMaxValue);

    public static final float kDefaultPitchMinRange = -90.0f;
    public static final float kDefaultPitchMaxRange = 90.0f;

    public static final float kDefaultYawMinRange = kRotationMinValue;
    public static final float kDefaultYawMaxRange = kRotationMaxValue;

    public static final float kDefaultRollMinRange = kRotationMinValue;
    public static final float kDefaultRollMaxRange = kRotationMaxValue;

    public static final float kRotationSensitivityMinValue = 1.0f;
    public static final float kRotationSensitivityMaxValue = 180.0f;
    public static final float kDefaultRotationSensitivity = 30.0f;

    /**
     * fov (field of view) constants
     */

    public static final float kFovMinValue = 0.01f;
    public static final float kFovMaxValue = 179.0f;
    public static final float kFovMax2Value = (kFovMaxValue * kFovMaxValue);

    public static final float kFovBaseline = 90.0f;
    public static final float kDefaultFov = kFovBaseline;
    public static final float kDefaultFovMinValue = 30.0f;
    public static final float kDefaultFovMaxValue = kDefaultFov;

    public static final float kFovSensitivityMinValue = 1.0f;
    public static final float kFovSensitivityMaxValue = 100.0f;
    public static final float kDefaultFovSensitivity = 30.0f;

    public static final int kDefaultMinDistanceToEnableFov = 5;

    public static final int kDefaultFovMinCounter = 3;

    /**
     * zoom constants
     */

    public static final int kDefaultZoomLevels = 2;

    /**
     * reset constants
     */

    public static final int kDefaultNumberOfTouchesForReset = 3;

    /**
     * inertia constants
     */

    public static final int kDefaultInertiaInterval = 3;

    /**
     * accelerometer constants
     */

    public static final float kDefaultAccelerometerInterval = (1.0f / 30.0f);
    public static final float kAccelerometerSensitivityMinValue = 1.0f;
    public static final float kAccelerometerSensitivityMaxValue = 10.0f;
    public static final float kDefaultAccelerometerSensitivity = kAccelerometerSensitivityMaxValue;
    public static final float kAccelerometerMultiplyFactor = 5.0f;

    /**
     * gyroscope constants
     */

    public static final float kDefaultGyroscopeInterval = (1.0f / 30.0f);
    public static final float kGyroscopeTimeConversion = (1.0f / 1000000000.0f);
    public static final float kGyroscopeMinTimeStep = (1.0f / 40.0f);

    /**
     * magnetometer constants
     */

    public static final float kDefaultMagnetometerInterval = (1.0f / 30.0f);

    /**
     * sensorial rotation constants
     */

    public static final int kSensorialRotationThreshold = 150;
    public static final int kSensorialRotationPitchErrorMargin = 5;
    public static final int kSensorialRotationYawErrorMargin = 5;

    /**
     * scrolling constants
     */

    public static final int kDefaultMinDistanceToEnableScrolling = 30;

    /**
     * drawing constants
     */

    public static final int kDefaultMinDistanceToEnableDrawing = 10;

    /**
     * shake constants
     */

    public static final int kShakeThreshold = 1300;
    public static final int kShakeDiffTime = 100;

    /**
     * transition constants
     */

    public static final float kDefaultTransitionInterval = 3.0f;
    public static final int kDefaultTransitionIterationsPerSecond = 30;

    /**
     * hotspot constants
     */

    public static final float kDefaultHotspotSize = 0.05f;
    public static final float kDefaultHotspotZPosition = kPanoramaRadius;
    public static final float kDefaultHotspotAlpha = 0.8f;
    public static final float kDefaultHotspotOverAlpha = 1.0f;
}