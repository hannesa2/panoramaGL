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

package com.panoramagl.enumerations;

import com.panoramagl.structs.PLCameraParameters;

import java.util.HashMap;
import java.util.Map;

public class PLCameraParameterType
{
	/**constants*/
	
	public static final int PLCameraParameterTypeNone =					0;
	public static final int PLCameraParameterTypeAtvMin =				1;
	public static final int PLCameraParameterTypeAtvMax =				(1 << 1);
	public static final int PLCameraParameterTypeAtvRange =				(PLCameraParameterTypeAtvMin | PLCameraParameterTypeAtvMax);
	public static final int PLCameraParameterTypeAthMin =				(1 << 2);
	public static final int PLCameraParameterTypeAthMax =				(1 << 3);
	public static final int PLCameraParameterTypeAthRange =				(PLCameraParameterTypeAthMin | PLCameraParameterTypeAthMax);
	public static final int PLCameraParameterTypeReverseRotation =		(1 << 4);
	public static final int PLCameraParameterTypeRotationSensitivity =	(1 << 5);
	public static final int PLCameraParameterTypeVLookAt =				(1 << 6);
	public static final int PLCameraParameterTypeHLookAt =				(1 << 7);
	public static final int PLCameraParameterTypeRotation =				(PLCameraParameterTypeVLookAt | PLCameraParameterTypeHLookAt);
	public static final int PLCameraParameterTypeZoomLevels =			(1 << 8);
	public static final int PLCameraParameterTypeFovMin =				(1 << 9);
	public static final int PLCameraParameterTypeFovMax =				(1 << 10);
	public static final int PLCameraParameterTypeFovRange =				(PLCameraParameterTypeFovMin | PLCameraParameterTypeFovMax);
	public static final int PLCameraParameterTypeFovSensitivity =		(1 << 11);
	public static final int PLCameraParameterTypeFov =					(1 << 12);
	public static final int PLCameraParameterTypeAllRotation =			(PLCameraParameterTypeAtvRange | PLCameraParameterTypeAthRange | PLCameraParameterTypeReverseRotation | PLCameraParameterTypeRotationSensitivity | PLCameraParameterTypeRotation);
	public static final int PLCameraParameterTypeAllZoom =				(PLCameraParameterTypeZoomLevels | PLCameraParameterTypeFovRange | PLCameraParameterTypeFovSensitivity | PLCameraParameterTypeFov);
	public static final int PLCameraParameterTypeAll =					((1 << 13) - 1);
	
	/**check methods*/
	
	public static PLCameraParameters checkCameraParametersWithMask(int mask)
	{
		PLCameraParameters parameters = PLCameraParameters.PLCameraParametersMake();
		if((mask & PLCameraParameterTypeAtvMin) == PLCameraParameterTypeAtvMin)
			parameters.atvMin = true;
		if((mask & PLCameraParameterTypeAtvMax) == PLCameraParameterTypeAtvMax)
			parameters.atvMax = true;
		if((mask & PLCameraParameterTypeAthMin) == PLCameraParameterTypeAthMin)
			parameters.athMin = true;
		if((mask & PLCameraParameterTypeAthMax) == PLCameraParameterTypeAthMax)
			parameters.athMax = true;
		if((mask & PLCameraParameterTypeReverseRotation) == PLCameraParameterTypeReverseRotation)
			parameters.reverseRotation = true;
		if((mask & PLCameraParameterTypeRotationSensitivity) == PLCameraParameterTypeRotationSensitivity)
			parameters.rotationSensitivity = true;
		if((mask & PLCameraParameterTypeVLookAt) == PLCameraParameterTypeVLookAt)
			parameters.vLookAt = true;
		if((mask & PLCameraParameterTypeHLookAt) == PLCameraParameterTypeHLookAt)
			parameters.hLookAt = true;
		if((mask & PLCameraParameterTypeZoomLevels) == PLCameraParameterTypeZoomLevels)
			parameters.zoomLevels = true;
		if((mask & PLCameraParameterTypeFovMin) == PLCameraParameterTypeFovMin)
			parameters.fovMin = true;
		if((mask & PLCameraParameterTypeFovMax) == PLCameraParameterTypeFovMax)
			parameters.fovMax = true;
		if((mask & PLCameraParameterTypeFovSensitivity) == PLCameraParameterTypeFovSensitivity)
			parameters.fovSensitivity = true;
		if((mask & PLCameraParameterTypeFov) == PLCameraParameterTypeFov)
			parameters.fov = true;
		return parameters;
	}
	
	public static PLCameraParameters checkCameraParametersWithStringMask(String mask)
	{
		String[] parameters = mask.split("\\|");
		int parametersMask = 0, parametersLength = parameters.length;
		if(parametersLength > 0)
		{
			Map<String, Integer> values = new HashMap<String, Integer>(21);
			values.put("none", PLCameraParameterTypeNone);
			values.put("atvMin", PLCameraParameterTypeAtvMin);
			values.put("atvMax", PLCameraParameterTypeAtvMax);
			values.put("atvRange", PLCameraParameterTypeAtvRange);
			values.put("athMin", PLCameraParameterTypeAthMin);
			values.put("athMax", PLCameraParameterTypeAthMax);
			values.put("athRange", PLCameraParameterTypeAthRange);
			values.put("reverseRotation", PLCameraParameterTypeReverseRotation);
			values.put("rotationSensitivity", PLCameraParameterTypeRotationSensitivity);
			values.put("vLookAt", PLCameraParameterTypeVLookAt);
			values.put("hLookAt", PLCameraParameterTypeHLookAt);
			values.put("rotation", PLCameraParameterTypeRotation);
			values.put("zoomLevels", PLCameraParameterTypeZoomLevels);
			values.put("fovMin", PLCameraParameterTypeFovMin);
			values.put("fovMax", PLCameraParameterTypeFovMax);
			values.put("fovRange", PLCameraParameterTypeFovRange);
			values.put("fovSensitivity", PLCameraParameterTypeFovSensitivity);
			values.put("fov", PLCameraParameterTypeFov);
			values.put("allRotation", PLCameraParameterTypeAllRotation);
			values.put("allZoom", PLCameraParameterTypeAllZoom);
			values.put("all", PLCameraParameterTypeAll);
	    	for(int i = 0; i < parametersLength; i++)
	    	{
	    		String parameter = parameters[i].trim();
	    		if(parameter.length() > 0)
	    		{
		    		boolean isNegation = (parameter.charAt(0) == '~');
		    		if(isNegation)
		    			parameter = parameter.substring(1).trim();
		    		if(values.containsKey(parameter))
		    		{
		    			if(isNegation)
		    				parametersMask &= ~values.get(parameter);
		    			else
		    				parametersMask |= values.get(parameter);
		    		}
	    		}
	    	}
		}
    	return checkCameraParametersWithMask(parametersMask);
	}
}