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

package com.panoramagl.structs;

public class PLCameraParameters implements PLIStruct<PLCameraParameters>
{
	/**member variables*/
	
	public boolean atvMin, atvMax, athMin, athMax, reverseRotation, rotationSensitivity, vLookAt, hLookAt;
	public boolean zoomLevels, fovMin, fovMax, fovSensitivity, fov;
	
	/**init methods*/
	
	public PLCameraParameters()
	{
		super();
		this.reset();
	}
	
	public PLCameraParameters(PLCameraParameters parameters)
	{
		super();
		this.setValues(parameters);
	}
	
	public static PLCameraParameters PLCameraParametersMake()
	{
		return new PLCameraParameters();
	}
	
	public static PLCameraParameters PLCameraParametersMake(PLCameraParameters parameters)
	{
		return new PLCameraParameters(parameters);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (!atvMin && !atvMax && !athMin && !athMax && !reverseRotation && !rotationSensitivity && !vLookAt && !hLookAt && !zoomLevels && !fovMin && !fovMax && !fovSensitivity && !fov);
	}
	
	@Override
	public PLCameraParameters reset()
	{
		atvMin = atvMax = athMin = athMax = reverseRotation = rotationSensitivity = vLookAt = hLookAt = false;
		zoomLevels = fovMin = fovMax = fovSensitivity = fov = false;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLCameraParameters setValues(PLCameraParameters parameters)
	{
		atvMin = parameters.atvMin;
		atvMax = parameters.atvMax;
		athMin = parameters.athMin;
		athMax = parameters.athMax;
		reverseRotation = parameters.reverseRotation;
		rotationSensitivity = parameters.rotationSensitivity;
		vLookAt = parameters.vLookAt;
		hLookAt = parameters.hLookAt;
		zoomLevels = parameters.zoomLevels;
		fovMin = parameters.fovMin;
		fovMax = parameters.fovMax;
		fovSensitivity = parameters.fovSensitivity;
		fov = parameters.fov;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLCameraParameters clone()
	{
		return new PLCameraParameters(this);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLCameraParameters)
		{
			if(this == o)
				return true;
			PLCameraParameters parameters = (PLCameraParameters)o;
			return
			(
				atvMin == parameters.atvMin && 
				atvMax == parameters.atvMax && 
				athMin == parameters.athMin && 
				athMax == parameters.athMax && 
				reverseRotation == parameters.reverseRotation &&
				rotationSensitivity == parameters.rotationSensitivity && 
				vLookAt == parameters.vLookAt && 
				hLookAt == parameters.hLookAt && 
				zoomLevels == parameters.zoomLevels && 
				fovMin == parameters.fovMin && 
				fovMax == parameters.fovMax && 
				fovSensitivity == parameters.fovSensitivity && 
				fov == parameters.fov
			);
		}
		return false;
	}
}