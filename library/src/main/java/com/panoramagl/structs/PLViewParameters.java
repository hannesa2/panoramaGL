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

public class PLViewParameters implements PLIStruct<PLViewParameters>
{
	/**member variables*/
	
	public boolean reset, scrolling, inertia, accelerometer, sensorialRotation;
	
	/**init methods*/
	
	public PLViewParameters()
	{
		super();
		this.reset();
	}
	
	public PLViewParameters(PLViewParameters parameters)
	{
		super();
		this.setValues(parameters);
	}
	
	public static PLViewParameters PLViewParametersMake()
	{
		return new PLViewParameters();
	}
	
	public static PLViewParameters PLViewParametersMake(PLViewParameters parameters)
	{
		return new PLViewParameters(parameters);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (!reset && !scrolling && !inertia && !accelerometer && !sensorialRotation);
	}
	
	@Override
	public PLViewParameters reset()
	{
		reset = scrolling = inertia = accelerometer = sensorialRotation = false;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLViewParameters setValues(PLViewParameters parameters)
	{
		reset = parameters.reset;
		scrolling = parameters.scrolling;
		inertia = parameters.inertia;
		accelerometer = parameters.accelerometer;
		sensorialRotation = parameters.sensorialRotation;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLViewParameters clone()
	{
		return new PLViewParameters(this);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLViewParameters)
		{
			if(this == o)
				return true;
			PLViewParameters parameters = (PLViewParameters)o;
			return
			(
				reset == parameters.reset && 
				scrolling == parameters.scrolling && 
				inertia == parameters.inertia && 
				accelerometer == parameters.accelerometer && 
				sensorialRotation == parameters.sensorialRotation
			);
		}
		return false;
	}
}