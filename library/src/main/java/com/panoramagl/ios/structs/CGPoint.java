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

public class CGPoint implements PLIStruct<CGPoint>
{
	/**member variables*/
	
	public float x, y;
	
	/**init methods*/
	
	public CGPoint()
	{
		this(0.0f, 0.0f);
	}
	
	public CGPoint(CGPoint point)
	{
		this(point.x, point.y);
	}
	
	public CGPoint(float x, float y)
	{
		super();
		this.x = x;
		this.y = y;
	}
	
	public static CGPoint CGPointMake()
	{
		return new CGPoint();
	}
	
	public static CGPoint CGPointMake(CGPoint point)
	{
		return new CGPoint(point);
	}
	
	public static CGPoint CGPointMake(float x, float y)
	{
		return new CGPoint(x, y);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0.0f && y == 0.0f);
	}
	
	@Override
	public CGPoint reset()
	{
		x = y = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public CGPoint setValues(CGPoint point)
	{
		x = point.x;
		y = point.y;
		return this;
	}
	
	public CGPoint setValues(float x, float y)
	{
		this.x = x;
		this.y = y;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public CGPoint clone()
	{
		return new CGPoint(x, y);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof CGPoint)
		{
			if(this == o)
				return true;
			CGPoint point = (CGPoint)o;
			return (x == point.x && y == point.y);
		}
		return false;
	}
}