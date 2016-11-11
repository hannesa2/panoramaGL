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

import android.content.Context;
import android.opengl.GLSurfaceView;

import com.panoramagl.downloaders.PLIFileDownloaderManager;
import com.panoramagl.enumerations.PLTouchStatus;
import com.panoramagl.ios.enumerations.UIDeviceOrientation;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;
import com.panoramagl.loaders.PLILoader;
import com.panoramagl.transitions.PLITransition;

import javax.microedition.khronos.opengles.GL10;

public interface PLIView
{
	/**reset methods*/
	
	boolean reset();
	boolean reset(boolean resetCamera);
	
	/**property methods*/
	
	PLIPanorama getPanorama();
	void setPanorama(PLIPanorama panorama);
	
	CGRect getRenderingViewport();
	
	CGSize getRenderingSize();
	
	boolean isRendererCreated();
	
	boolean isValidForCameraAnimation();
	
	PLICamera getCamera();
	void setCamera(PLICamera camera);
	
	float getAnimationInterval();
	void setAnimationInterval(float animationInterval);
	
	int getAnimationFrameInterval();
	void setAnimationFrameInterval(int animationFrameInterval);
	
	boolean isAnimating();
	
	CGPoint getStartPoint();
	void setStartPoint(CGPoint startPoint);
	
	CGPoint getEndPoint();
	void setEndPoint(CGPoint endPoint);
	
	boolean isValidForFov();
	
	boolean isAccelerometerEnabled();
	void setAccelerometerEnabled(boolean isAccelerometerEnabled);
	
	boolean isAccelerometerLeftRightEnabled();
	void setAccelerometerLeftRightEnabled(boolean isAccelerometerLeftRightEnabled);
	
	boolean isAccelerometerUpDownEnabled();
	void setAccelerometerUpDownEnabled(boolean isAccelerometerUpDownEnabled);
	
	float getAccelerometerInterval();
	void setAccelerometerInterval(float accelerometerInterval);
	
	float getAccelerometerSensitivity();
	void setAccelerometerSensitivity(float accelerometerSensitivity);
	
	boolean isValidForSensorialRotation();
	
	boolean isValidForScrolling();
	
	boolean isScrollingEnabled();
	void setScrollingEnabled(boolean isScrollingEnabled);
	
	int getMinDistanceToEnableScrolling();
	void setMinDistanceToEnableScrolling(int minDistanceToEnableScrolling);
	
	int getMinDistanceToEnableDrawing();
	void setMinDistanceToEnableDrawing(int minDistanceToEnableDrawing);
	
	boolean isValidForInertia();
	
	boolean isInertiaEnabled();
	void setInertiaEnabled(boolean isInertiaEnabled);
	
	float getInertiaInterval();
	void setInertiaInterval(float inertiaInterval);
	
	boolean isResetEnabled();
	void setResetEnabled(boolean isResetEnabled);
	
	boolean isShakeResetEnabled();
	void setShakeResetEnabled(boolean isShakeResetEnabled);
	
	int getNumberOfTouchesForReset();
	void setNumberOfTouchesForReset(int numberOfTouchesForReset);
	
	float getShakeThreshold();
	void setShakeThreshold(float shakeThreshold);
	
	boolean isValidForTransition();
	
	PLITransition getCurrentTransition();
	
	boolean isValidForTouch();
	
	PLTouchStatus getTouchStatus();
	
	UIDeviceOrientation getCurrentDeviceOrientation();
	
	PLIFileDownloaderManager getDownloadManager();
	
	boolean isProgressBarVisible();
	
	boolean isLocked();
	void setLocked(boolean isLocked);
	
	PLViewListener getListener();
	void setListener(PLViewListener listener);
	
	Context getContext();
	GL10 getGLContext();
	GLSurfaceView getGLSurfaceView();
	CGSize getSize();
	
	/**animation methods*/
	
	boolean startAnimation();
	boolean stopAnimation();
	
	/**sensorial rotation methods*/
	
	boolean startSensorialRotation();
	boolean stopSensorialRotation();
	
	boolean updateInitialSensorialRotation();
	
	/**transition methods*/
	
	boolean startTransition(PLITransition transition, PLIPanorama newPanorama);
	boolean stopTransition();
	
	/**progress-bar methods*/
	
	boolean showProgressBar();
	boolean hideProgressBar();
	
	/**load methods*/
	
	void load(PLILoader loader);
	void load(PLILoader loader, boolean showProgressBar);
	void load(PLILoader loader, boolean showProgressBar, PLITransition transition);
	void load(PLILoader loader, boolean showProgressBar, PLITransition transition, float initialPitch, float initialYaw);
	
	/**clear methods*/
	
	void clear();

	/**zoom enabled**/
	boolean isZoomEnabled();
	void setZoomEnabled(boolean enabled);
}