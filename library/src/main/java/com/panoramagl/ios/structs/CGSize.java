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

public class CGSize implements PLIStruct<CGSize>
{
	/**member variables*/
	
	public int width, height;
	
	/**init methods*/
	
	public CGSize()
	{
		this(0, 0);
	}
	
	public CGSize(CGSize size)
	{
		this(size.width, size.height);
	}
	
	public CGSize(int width, int height)
	{
		super();
		this.width = width;
		this.height = height;
	}
	
	public static CGSize CGSizeMake()
	{
		return new CGSize();
	}
	
	public static CGSize CGSizeMake(CGSize size)
	{
		return new CGSize(size);
	}
	
	public static CGSize CGSizeMake(int width, int height)
	{
		return new CGSize(width, height);
	}
	
	public static CGSize CGSizeMake(float width, float height)
	{
		return new CGSize((int)width, (int)height);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (width == 0 && height == 0);
	}
	
	@Override
	public CGSize reset()
	{
		width = height = 0;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public CGSize setValues(CGSize size)
	{
		width = size.width;
		height = size.height;
		return this;
	}
	
	public CGSize setValues(int width, int height)
	{
		this.width = width;
		this.height = height;
		return this;
	}
	
	public CGSize setValues(float width, float height)
	{
		this.width = (int)width;
		this.height = (int)height;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public CGSize clone()
	{
		return new CGSize(width, height);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof CGSize)
		{
			if(this == o)
				return true;
			CGSize size = (CGSize)o;
			return (width == size.width && height == size.height);
		}
		return false;
	}
}