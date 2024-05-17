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

import com.panoramagl.enumerations.PLCubeFaceOrientation

object PLConstants {
    /**
     * math constants
     */
    const val kPI: Float = 3.14159265358979323846f
    const val kPI8: Float = (kPI / 8.0f)
    const val kPI16: Float = (kPI / 16.0f)
    const val kToDegrees: Float = (180.0f / kPI)
    const val kToRadians: Float = (kPI / 180.0f)

    /**
     * float constants
     */
    const val kFloatMinValue: Float = -1000000.0f
    const val kFloatMaxValue: Float = Float.MAX_VALUE
    const val kFloatUndefinedValue: Float = -3829713706.1158636387f

    /**
     * display constants
     */
    const val kMaxDisplaySize: Int = 4096
    const val kDisplayPPIBaseline: Float = 218.104455f

    /**
     * object constants
     */
    const val kDefaultAlpha: Float = 1.0f

    /**
     * texture constants
     */
    const val kTextureMaxSize: Int = 8192

    /**
     * renderer constants
     */
    const val kViewportSize: Int = kMaxDisplaySize
    const val kViewportScale: Float = 5.12f
    const val kPerspectiveAspect: Float = 1.0f
    const val kPerspectiveZNear: Float = 0.01f
    const val kPerspectiveZFar: Float = 100.0f

    /**
     * panorama constants
     */
    const val kPanoramaRadius: Float = 1.0f

    /**
     * cube constants
     */
    @JvmField
    val kCubeFrontFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationFront.ordinal
    @JvmField
    val kCubeBackFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationBack.ordinal
    @JvmField
    val kCubeLeftFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationLeft.ordinal
    @JvmField
    val kCubeRightFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationRight.ordinal
    @JvmField
    val kCubeUpFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationUp.ordinal
    @JvmField
    val kCubeDownFaceIndex: Int = PLCubeFaceOrientation.PLCubeFaceOrientationDown.ordinal

    /**
     * sphere constants
     */
    const val kDefaultSpherePreviewDivs: Int = 50
    const val kDefaultSphereDivs: Int = 50

    /**
     * sphere2 constants
     */
    const val kDefaultSphere2PreviewDivs: Int = 30
    const val kDefaultSphere2Divs: Int = 40

    /**
     * cylinder constants
     */
    const val kDefaultCylinderPreviewDivs: Int = 60
    const val kDefaultCylinderDivs: Int = 60
    const val kDefaultCylinderHeight: Float = 3.0f

    /**
     * animation constants
     */
    const val kDefaultAnimationTimerInterval: Float = (1.0f / 120.0f)
    const val kDefaultAnimationTimerIntervalByFrame: Float = (1.0f / 120.0f)
    const val kDefaultAnimationFrameInterval: Int = 1
    const val kCameraLookAtAnimationTimerInterval: Float = kDefaultAnimationTimerInterval
    const val kCameraFovAnimationTimerInterval: Float = kDefaultAnimationTimerInterval
    const val kCameraLookAtAnimationMaxStep: Int = 20
    const val kCameraFovAnimationMaxStep: Int = 10

    /**
     * rotation constants
     */
    const val kRotationMinValue: Float = -180.0f
    const val kRotationMaxValue: Float = 180.0f
    const val kRotationMax2Value: Float = (kRotationMaxValue * kRotationMaxValue)

    const val kDefaultPitchMinRange: Float = -90.0f
    const val kDefaultPitchMaxRange: Float = 90.0f

    const val kDefaultYawMinRange: Float = kRotationMinValue
    const val kDefaultYawMaxRange: Float = kRotationMaxValue

    const val kDefaultRollMinRange: Float = kRotationMinValue
    const val kDefaultRollMaxRange: Float = kRotationMaxValue

    const val kRotationSensitivityMinValue: Float = 1.0f
    const val kRotationSensitivityMaxValue: Float = 270.0f
    const val kDefaultRotationSensitivity: Float = 45.0f

    /**
     * fov (field of view) constants
     */
    const val kFovMinValue: Float = 0.01f
    const val kFovMaxValue: Float = 179.0f
    const val kFovMax2Value: Float = (kFovMaxValue * kFovMaxValue)

    const val kFovBaseline: Float = 90.0f
    const val kDefaultFov: Float = kFovBaseline
    const val kDefaultFovMinValue: Float = 30.0f
    const val kDefaultFovMaxValue: Float = kDefaultFov

    const val kFovSensitivityMinValue: Float = 1.0f
    const val kFovSensitivityMaxValue: Float = 100.0f
    const val kDefaultFovSensitivity: Float = 30.0f

    const val kDefaultMinDistanceToEnableFov: Int = 5

    const val kDefaultFovMinCounter: Int = 3

    /**
     * zoom constants
     */
    const val kDefaultZoomLevels: Int = 2

    /**
     * reset constants
     */
    const val kDefaultNumberOfTouchesForReset: Int = 3

    /**
     * inertia constants
     */
    const val kDefaultInertiaInterval: Int = 3

    /**
     * accelerometer constants
     */
    const val kAccelerometerSensitivityMinValue: Float = 1.0f
    const val kAccelerometerSensitivityMaxValue: Float = 10.0f
    const val kDefaultAccelerometerSensitivity: Float = kAccelerometerSensitivityMaxValue
    const val kAccelerometerMultiplyFactor: Float = 5.0f

    /**
     * gyroscope constants
     */
    const val kDefaultGyroscopeInterval: Float = (1.0f / 30.0f)
    const val kGyroscopeTimeConversion: Float = (1.0f / 1000000000.0f)
    const val kGyroscopeMinTimeStep: Float = (1.0f / 40.0f)

    /**
     * magnetometer constants
     */
    const val kDefaultMagnetometerInterval: Float = (1.0f / 30.0f)

    /**
     * sensorial rotation constants
     */
    const val kSensorialRotationThreshold: Int = 150
    const val kSensorialRotationPitchErrorMargin: Int = 5
    const val kSensorialRotationYawErrorMargin: Int = 5

    /**
     * scrolling constants
     */
    const val kDefaultMinDistanceToEnableScrolling: Int = 30

    /**
     * drawing constants
     */
    const val kDefaultMinDistanceToEnableDrawing: Int = 10

    /**
     * shake constants
     */
    const val kShakeThreshold: Int = 1300
    const val kShakeDiffTime: Int = 100

    /**
     * transition constants
     */
    const val kDefaultTransitionInterval: Float = 3.0f
    const val kDefaultTransitionIterationsPerSecond: Int = 30

    /**
     * hotspot constants
     */
    const val kDefaultHotspotSize: Float = 0.05f
    const val kDefaultHotspotZPosition: Float = kPanoramaRadius
    const val kDefaultHotspotAlpha: Float = 0.8f
    const val kDefaultHotspotOverAlpha: Float = 1.0f
}