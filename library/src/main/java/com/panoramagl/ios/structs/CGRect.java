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

public class CGRect implements PLIStruct<CGRect>
{
	/**member variables*/
	
	public int x, y, width, height;
	
	/**init methods*/
	
	public CGRect()
	{
		this(0, 0, 0, 0);
	}
	
	public CGRect(CGRect rect)
	{
		this(rect.x, rect.y, rect.width, rect.height);
	}
	
	public CGRect(int x, int y, int width, int height)
	{
		super();
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	public static CGRect CGRectMake()
	{
		return new CGRect();
	}
	
	public static CGRect CGRectMake(CGRect rect)
	{
		return new CGRect(rect);
	}
	
	public static CGRect CGRectMake(int x, int y, int width, int height)
	{
		return new CGRect(x, y, width, height);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0 && y == 0 && width == 0 && height == 0);
	}
	
	@Override
	public CGRect reset()
	{
		x = y = width = height = 0;
		return this;
	}
	
	/**set methods*/
	
	public CGRect setValues(int x, int y, int width, int height)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		return this;
	}
	
	@Override
	public CGRect setValues(CGRect rect)
	{
		x = rect.x;
		y = rect.y;
		width = rect.width;
		height = rect.height;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public CGRect clone()
	{
		return new CGRect(x, y, width, height);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof CGRect)
		{
			if(this == o)
				return true;
			CGRect rect = (CGRect)o;
			return (x == rect.x && y == rect.y && width == rect.width && height == rect.height);
		}
		return false;
	}
}