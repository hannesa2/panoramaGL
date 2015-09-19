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

public class PLRotation implements PLIStruct<PLRotation>
{
	/**member variables*/
	
	public float pitch, yaw, roll;
	
	/**init methods*/
	
	public PLRotation()
	{
		this(0.0f, 0.0f, 0.0f);
	}
	
	public PLRotation(PLRotation rotation)
	{
		this(rotation.pitch, rotation.yaw, rotation.roll);
	}
	
	public PLRotation(float pitch, float yaw, float roll)
	{
		super();
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
	}
	
	public static PLRotation PLRotationMake()
	{
		return new PLRotation();
	}
	
	public static PLRotation PLRotationMake(PLRotation rotation)
	{
		return new PLRotation(rotation);
	}
	
	public static PLRotation PLRotationMake(float pitch, float yaw, float roll)
	{
		return new PLRotation(pitch, yaw, roll);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (pitch == 0.0f && yaw == 0.0f && roll == 0.0f);
	}
	
	@Override
	public PLRotation reset()
	{
		pitch = yaw = roll = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLRotation setValues(PLRotation rotation)
	{
		pitch = rotation.pitch;
		yaw = rotation.yaw;
		roll = rotation.roll;
		return this;
	}
	
	public PLRotation setValues(float pitch, float yaw, float roll)
	{
		this.pitch = pitch;
		this.yaw = yaw;
		this.roll = roll;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLRotation clone()
	{
		return new PLRotation(pitch, yaw, roll);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLRotation)
		{
			if(this == o)
				return true;
			PLRotation rotation = (PLRotation)o;
			return (pitch == rotation.pitch && yaw == rotation.yaw && roll == rotation.roll);
		}
		return false;
	}
}