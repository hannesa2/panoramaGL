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

import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLRotation;

public interface PLICamera extends PLIRenderableElement {
    /**
     * reset methods
     */

    void reset(Object sender);

    /**
     * property methods
     */

    boolean isLocked();

    void setLocked(boolean isLocked);

    boolean isFovEnabled();

    void setFovEnabled(boolean isFovEnabled);

    float getInitialFov();

    void setInitialFov(float initialFov);

    float getFov();

    void setFov(float fov);

    float getFovFactor();

    void setFovFactor(float fovFactor);

    float getFovSensitivity();

    void setFovSensitivity(float fovSensitivity);

    PLRange getFovRange();

    void setFovRange(PLRange range);

    void setFovRange(float min, float max);

    float getFovMin();

    void setFovMin(float min);

    float getFovMax();

    void setFovMax(float max);

    int getMinDistanceToEnableFov();

    void setMinDistanceToEnableFov(int distance);

    float getRotationSensitivity();

    void setRotationSensitivity(float rotationSensitivity);

    float getZoomFactor();

    void setZoomFactor(float zoomFactor);

    int getZoomLevel();

    void setZoomLevel(int zoomLevel);

    int getZoomLevels();

    void setZoomLevels(int zoomLevels);

    PLRotation getInitialLookAt();

    void setInitialLookAt(PLRotation rotation);

    void setInitialLookAt(float pitch, float yaw);

    float getInitialPitch();

    void setInitialPitch(float pitch);

    float getInitialYaw();

    void setInitialYaw(float yaw);

    PLRotation getLookAtRotation();

    boolean isAnimating();

    PLCameraListener getInternalListener();

    void setInternalListener(PLCameraListener listener);

    PLCameraListener getListener();

    void setListener(PLCameraListener listener);

    /**
     * animation methods
     */

    boolean stopAnimation();

    boolean stopAnimation(Object sender);

    /**
     * fov methods
     */

    boolean setFov(float fov, boolean animated);

    boolean setFov(Object sender, float fov, boolean animated);

    boolean setFovFactor(float fovFactor, boolean animated);

    boolean setFovFactor(Object sender, float fovFactor, boolean animated);

    boolean addFov(float distance);

    boolean addFov(Object sender, float distance);

    /**
     * zoom methods
     */

    boolean setZoomFactor(float zoomFactor, boolean animated);

    boolean setZoomFactor(Object sender, float zoomFactor, boolean animated);

    boolean setZoomLevel(int zoomLevel, boolean animated);

    boolean setZoomLevel(Object sender, int zoomLevel, boolean animated);

    boolean zoomIn(boolean animated);

    boolean zoomIn(Object sender, boolean animated);

    boolean zoomOut(boolean animated);

    boolean zoomOut(Object sender, boolean animated);

    /**
     * lookat methods
     */

    boolean lookAt(PLRotation rotation);

    boolean lookAt(Object sender, PLRotation rotation);

    boolean lookAt(PLRotation rotation, boolean animated);

    boolean lookAt(Object sender, PLRotation rotation, boolean animated);

    boolean lookAt(float pitch, float yaw);

    boolean lookAt(Object sender, float pitch, float yaw);

    boolean lookAt(float pitch, float yaw, boolean animated);

    boolean lookAt(Object sender, float pitch, float yaw, boolean animated);

    /**
     * lookat and fov combined methods
     **/

    boolean lookAtAndFov(float pitch, float yaw, float fov, boolean animated);

    boolean lookAtAndFov(Object sender, float pitch, float yaw, float fov, boolean animated);

    boolean lookAtAndFovFactor(float pitch, float yaw, float fovFactor, boolean animated);

    boolean lookAtAndFovFactor(Object sender, float pitch, float yaw, float fovFactor, boolean animated);

    boolean lookAtAndZoomFactor(float pitch, float yaw, float zoomFactor, boolean animated);

    boolean lookAtAndZoomFactor(Object sender, float pitch, float yaw, float zoomFactor, boolean animated);

    /**
     * rotate methods
     */

    void rotate(Object sender, float pitch, float yaw);

    void rotate(Object sender, float pitch, float yaw, float roll);

    void rotate(CGPoint startPoint, CGPoint endPoint);

    void rotate(Object sender, CGPoint startPoint, CGPoint endPoint);

    /**
     * clone methods
     */

    PLICamera clone();
}