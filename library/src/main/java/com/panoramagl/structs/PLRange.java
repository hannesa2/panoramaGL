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

public class PLRange implements PLIStruct<PLRange>
{
	/**member variables*/
	
	public float min, max;
	
	/**init methods*/
	
	public PLRange()
	{
		this(0.0f, 0.0f);
	}
	
	public PLRange(PLRange range)
	{
		this(range.min, range.max);
	}
	
	public PLRange(float min, float max)
	{
		super();
		this.min = min;
		this.max = max;
	}
	
	public static PLRange PLRangeMake()
	{
		return new PLRange();
	}
	
	public static PLRange PLRangeMake(PLRange range)
	{
		return new PLRange(range.min, range.max);
	}
	
	public static PLRange PLRangeMake(float min, float max)
	{
		return new PLRange(min, max);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (min == 0.0f && max == 0.0f);
	}
	
	@Override
	public PLRange reset()
	{
		min = max = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLRange setValues(PLRange range)
	{
		min = range.min;
		max = range.max;
		return this;
	}
	
	public PLRange setValues(float min, float max)
	{
		this.min = min;
		this.max = max;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLRange clone()
	{
		return new PLRange(min, max);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLRange)
		{
			if(this == o)
				return true;
			PLRange range = (PLRange)o;
			return (min == range.min && max == range.max);
		}
		return false;
	}
}