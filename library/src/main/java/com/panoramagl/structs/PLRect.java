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

public class PLRect implements PLIStruct<PLRect>
{
	/**member variables*/
	
	public PLPosition leftTop;
	public PLPosition rightBottom;
	
	/**init methods*/
	
	public PLRect()
	{
		this(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
	}
	
	public PLRect(PLRect rect)
	{
		this(rect.leftTop.x, rect.leftTop.y, rect.leftTop.z, rect.rightBottom.x, rect.rightBottom.y, rect.rightBottom.z);
	}
	
	public PLRect(float left, float top, float zLeftTop, float right, float bottom, float zRightBottom)
	{
		super();
		leftTop = PLPosition.PLPositionMake(left, top, zLeftTop);
		rightBottom = PLPosition.PLPositionMake(right, bottom, zRightBottom);
	}
	
	public static PLRect PLRectMake()
	{
		return new PLRect();
	}
	
	public static PLRect PLRectMake(PLRect rect)
	{
		return new PLRect(rect);
	}
	
	public static PLRect PLRectMake(float left, float top, float zLeftTop, float right, float bottom, float zRightBottom)
	{
		return new PLRect(left, top, zLeftTop, right, bottom, zRightBottom);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (leftTop.isResetted() && rightBottom.isResetted());
	}
	
	@Override
	public PLRect reset()
	{
		leftTop.reset();
		rightBottom.reset();
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLRect setValues(PLRect rect)
	{
		leftTop.setValues(rect.leftTop);
		rightBottom.setValues(rect.rightBottom);
		return this;
	}
	
	public PLRect setValues(float left, float top, float zLeftTop, float right, float bottom, float zRightBottom)
	{
		leftTop.setValues(left, top, zLeftTop);
		rightBottom.setValues(right, bottom, zRightBottom);
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLRect clone()
	{
		return new PLRect(leftTop.x, leftTop.y, leftTop.z, rightBottom.x, rightBottom.y, rightBottom.z);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLRect)
		{
			if(this == o)
				return true;
			PLRect rect = (PLRect)o;
			return (leftTop.equals(rect.leftTop) && rightBottom.equals(rect.rightBottom));
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		leftTop = null;
		rightBottom = null;
		super.finalize();
	}
}