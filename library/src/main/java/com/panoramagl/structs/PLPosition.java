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

import com.panoramagl.ios.structs.UIAcceleration;

public class PLPosition implements PLIStruct<PLPosition>
{
	/**member variables*/
	
	public float x, y, z;
	
	/**init methods*/
	
	public PLPosition()
	{
		this(0.0f, 0.0f, 0.0f);
	}
	
	public PLPosition(PLPosition position)
	{
		this(position.x, position.y, position.z);
	}
	
	public PLPosition(float x, float y, float z)
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static PLPosition PLPositionMake()
	{
		return new PLPosition();
	}
	
	public static PLPosition PLPositionMake(PLPosition position)
	{
		return new PLPosition(position);
	}
	
	public static PLPosition PLPositionMake(float x, float y, float z)
	{
		return new PLPosition(x, y, z);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0.0f && y == 0.0f && z == 0.0f);
	}
	
	@Override
	public PLPosition reset()
	{
		x = y = z = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLPosition setValues(PLPosition position)
	{
		x = position.x;
		y = position.y;
		z = position.z;
		return this;
	}
	
	public PLPosition setValues(UIAcceleration acceleration)
	{
		x = acceleration.x;
		y = acceleration.y;
		z = acceleration.z;
		return this;
	}
	
	public PLPosition setValues(float[] values)
	{
		x = values[0];
		y = values[1];
		z = values[2];
		return this;
	}
	
	public PLPosition setValues(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLPosition clone()
	{
		return new PLPosition(x, y, z);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLPosition)
		{
			if(this == o)
				return true;
			PLPosition position = (PLPosition)o;
			return (x == position.x && y == position.y && z == position.z);
		}
		return false;
	}
}