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

import com.panoramagl.structs.PLViewParameters;

import java.util.HashMap;
import java.util.Map;

public class PLViewParameterType
{
	/**constants*/
	
	public static final int PLViewParameterTypeNone =				0;
	public static final int PLViewParameterTypeReset =				1;
	public static final int PLViewParameterTypeScrolling =			(1 << 1);
	public static final int PLViewParameterTypeInertia =			(1 << 2);
	public static final int PLViewParameterTypeAccelerometer =		(1 << 3);
	public static final int PLViewParameterTypeSensorialRotation =	(1 << 4);
	public static final int PLViewParameterTypeAll =				((1 << 5) - 1);
	
	/**check methods*/
	
	public static PLViewParameters checkViewParametersWithMask(int mask)
	{
		PLViewParameters parameters = PLViewParameters.PLViewParametersMake();
		if((mask & PLViewParameterTypeReset) == PLViewParameterTypeReset)
			parameters.reset = true;
		if((mask & PLViewParameterTypeScrolling) == PLViewParameterTypeScrolling)
			parameters.scrolling = true;
		if((mask & PLViewParameterTypeInertia) == PLViewParameterTypeInertia)
			parameters.inertia = true;
		if((mask & PLViewParameterTypeAccelerometer) == PLViewParameterTypeAccelerometer)
			parameters.accelerometer = true;
		if((mask & PLViewParameterTypeSensorialRotation) == PLViewParameterTypeSensorialRotation)
			parameters.sensorialRotation = true;
		return parameters;
	}
	
	public static PLViewParameters checkViewParametersWithStringMask(String mask)
	{
		String[] parameters = mask.split("\\|");
		int parametersMask = 0, parametersLength = parameters.length;
		if(parametersLength > 0)
		{
			Map<String, Integer> values = new HashMap<String, Integer>(7);
			values.put("none", PLViewParameterTypeNone);
			values.put("reset", PLViewParameterTypeReset);
			values.put("scrolling", PLViewParameterTypeScrolling);
			values.put("inertia", PLViewParameterTypeInertia);
			values.put("accelerometer", PLViewParameterTypeAccelerometer);
			values.put("sensorialRotation", PLViewParameterTypeSensorialRotation);
			values.put("all", PLViewParameterTypeAll);
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
    	return checkViewParametersWithMask(parametersMask);
	}
}