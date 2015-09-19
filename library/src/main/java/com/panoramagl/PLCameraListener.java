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

import com.panoramagl.enumerations.PLCameraAnimationType;

public interface PLCameraListener
{
	/**event methods*/
	
	void didBeginAnimation(Object sender, PLICamera camera, PLCameraAnimationType type);
	void didEndAnimation(Object sender, PLICamera camera, PLCameraAnimationType type);
	
	void didLookAt(Object sender, PLICamera camera, float pitch, float yaw, boolean animated);
	void didRotate(Object sender, PLICamera camera, float pitch, float yaw, float roll);
	void didFov(Object sender, PLICamera camera, float fov, boolean animated);
	void didReset(Object sender, PLICamera camera);
}