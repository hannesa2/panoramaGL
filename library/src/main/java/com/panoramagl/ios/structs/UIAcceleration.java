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

package com.panoramagl.ios.structs;

import com.panoramagl.structs.PLIStruct;

public class UIAcceleration implements PLIStruct<UIAcceleration> 
{
	/**member variables*/
	
	public float x, y, z;
	
	/**init methods*/
	
	public UIAcceleration()
	{
		super();
		x = y = z = 0.0f;
	}
	
	public UIAcceleration(UIAcceleration acceleration)
	{
		super();
		x = acceleration.x;
		y = acceleration.y;
		z = acceleration.z;
	}
	
	public UIAcceleration(float[] values)
	{
		this(values[0], values[1], values[2]);
	}
	
	public UIAcceleration(float x, float y, float z)
	{
		super();
		this.x = -x;
		this.y = -y;
		this.z = -z;
	}
	
	public static UIAcceleration UIAccelerationMake()
	{
		return new UIAcceleration();
	}
	
	public static UIAcceleration UIAccelerationMake(UIAcceleration acceleration)
	{
		return new UIAcceleration(acceleration.x, acceleration.y, acceleration.z);
	}
	
	public static UIAcceleration UIAccelerationMake(float[] values)
	{
		return new UIAcceleration(values);
	}
	
	public static UIAcceleration UIAccelerationMake(float x, float y, float z)
	{
		return new UIAcceleration(x, y, z);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0.0f && y == 0.0f && z == 0.0f);
	}
	
	@Override
	public UIAcceleration reset()
	{
		x = y = z = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public UIAcceleration setValues(UIAcceleration acceleration)
	{
		x = acceleration.x;
		y = acceleration.y;
		z = acceleration.z;
		return this;
	}
	
	public UIAcceleration setValues(float[] values)
	{
		x = -values[0];
		y = -values[1];
		z = -values[2];
		return this;
	}
	
	public UIAcceleration setValues(float x, float y, float z)
	{
		this.x = -x;
		this.y = -y;
		this.z = -z;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public UIAcceleration clone()
	{
		return new UIAcceleration(this);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof UIAcceleration)
		{
			if(this == o)
				return true;
			UIAcceleration acceleration = (UIAcceleration)o;
			return (x == acceleration.x && y == acceleration.y && z == acceleration.z);
		}
		return false;
	}
}