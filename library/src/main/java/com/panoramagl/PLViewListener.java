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

import android.hardware.SensorEvent;
import android.view.MotionEvent;

import com.panoramagl.enumerations.PLCameraAnimationType;
import com.panoramagl.hotspots.PLIHotspot;
import com.panoramagl.ios.UITouch;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.ios.structs.UIAcceleration;
import com.panoramagl.loaders.PLILoader;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.transitions.PLITransition;

import java.util.List;

public abstract class PLViewListener
{
	/**touch methods*/
	
	public void onTouchesBegan(PLIView view, List<UITouch> touches, MotionEvent event)
	{	
	}
	
	public void onTouchesMoved(PLIView view, List<UITouch> touches, MotionEvent event)
	{
	}
	
	public void onTouchesEnded(PLIView view, List<UITouch> touches, MotionEvent event)
	{
	}
	
	public boolean onShouldBeginTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
		return true;
	}
	
	public void onDidBeginTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
	}
	
	public boolean onShouldMoveTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
		return true;
	}
	
	public void onDidMoveTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
	}
	
	public boolean onShouldEndTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
		return true;
	}
	
	public void onDidEndTouching(PLIView view, List<UITouch> touches, MotionEvent event)
	{
	}
	
	/**accelerate methods*/
	
	public boolean onShouldAccelerate(PLIView view, UIAcceleration acceleration, SensorEvent event)
	{
		return true;
	}
	
	public void onDidAccelerate(PLIView view, UIAcceleration acceleration, SensorEvent event)
	{
	}
	
	/**inertia methods*/
	
	public boolean onShouldBeginInertia(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
		return true;
	}
	
	public void onDidBeginInertia(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	public boolean onShouldRunInertia(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
		return true;
	}
	
	public void onDidRunInertia(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	public void onDidEndInertia(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	/**scrolling methods*/
	
	public boolean onShouldBeingScrolling(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
		return true;
	}
	
	public void onDidBeginScrolling(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	public void onDidEndScrolling(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	/**zooming methods*/
	
	public boolean onShouldBeginZooming(PLIView view)
	{
		return true;
	}
	
	public void onDidBeginZooming(PLIView view, CGPoint startPoint, CGPoint endPoint)
	{
	}
	
	public boolean onShouldRunZooming(PLIView view, float distance, boolean isZoomIn, boolean isZoomOut)
	{
		return true;
	}
	
	public void onDidRunZooming(PLIView view, float distance, boolean isZoomIn, boolean isZoomOut)
	{
	}
	
	public void onDidEndZooming(PLIView view)
	{
	}
	
	/**reset methods*/
	
	public boolean onShouldReset(PLIView view)
	{
		return true;
	}
	
	public void onDidReset(PLIView view)
	{
	}
	
	/**camera methods*/
	
	public void onDidBeginCameraAnimation(PLIView view, Object sender, PLICamera camera, PLCameraAnimationType type)
	{
	}
	
	public void onDidEndCameraAnimation(PLIView view, Object sender, PLICamera camera, PLCameraAnimationType type)
	{	
	}
	
	public void onDidResetCamera(PLIView view, Object sender, PLICamera camera)
	{
	}
	
	public void onDidLookAtCamera(PLIView view, Object sender, PLICamera camera, float pitch, float yaw, boolean animated)
	{
	}
	
	public void onDidRotateCamera(PLIView view, Object sender, PLICamera camera, float pitch, float yaw, float roll)
	{
	}
	
	public void onDidFovCamera(PLIView view, Object sender, PLICamera camera, float fov, boolean animated)
	{
	}
	
	/**scene element methods*/
	
	public void onDidOverElement(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{	
	}
	
	public void onDidClickElement(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{
	}
	
	public void onDidOutElement(PLIView view, PLISceneElement element, CGPoint screenPoint, PLPosition scene3DPoint)
	{
	}
	
	/**hotspot methods*/
	
	public void onDidOverHotspot(PLIView view, PLIHotspot hotspot, CGPoint screenPoint, PLPosition scene3DPoint)
	{
	}
	
	public void onDidClickHotspot(PLIView view, PLIHotspot hotspot, CGPoint screenPoint, PLPosition scene3DPoint)
	{
	}
	
	public void onDidOutHotspot(PLIView view, PLIHotspot hotspot, CGPoint screenPoint, PLPosition scene3DPoint)
	{
	}
	
	/**transition methods*/
	
	public void onDidBeginTransition(PLIView view, PLITransition transition)
	{
	}
	
	public void onDidProcessTransition(PLIView view, PLITransition transition, int progressPercentage)
	{
	}
	
	public void onDidStopTransition(PLIView view, PLITransition transition, int progressPercentage)
	{
	}
	
	public void onDidEndTransition(PLIView view, PLITransition transition)
	{
	}
	
	/**loader methods*/
	
	public void onDidBeginLoader(PLIView view, PLILoader loader)
	{
	}
	
	public void onDidCompleteLoader(PLIView view, PLILoader loader)
	{
	}
	
	public void onDidStopLoader(PLIView view, PLILoader loader)
	{
	}
	
	public void onDidErrorLoader(PLIView view, PLILoader loader, String error)
	{
	}
}