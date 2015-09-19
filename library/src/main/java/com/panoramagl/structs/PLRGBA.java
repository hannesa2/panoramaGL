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

public class PLRGBA implements PLIStruct<PLRGBA>
{
	/**member variables*/
	
	public float red, green, blue, alpha;
	
	/**init methods*/
	
	public PLRGBA()
	{
		this(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	public PLRGBA(PLRGBA rgba)
	{
		this(rgba.red, rgba.green, rgba.blue, rgba.alpha);
	}
	
	public PLRGBA(float red, float green, float blue, float alpha)
	{
		super();
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}
	
	public static PLRGBA PLRGBAMake()
	{
		return new PLRGBA();
	}
	
	public static PLRGBA PLRGBAMake(PLRGBA rgba)
	{
		return new PLRGBA(rgba);
	}
	
	public static PLRGBA PLRGBAMake(float red, float green, float blue, float alpha)
	{
		return new PLRGBA(red, green, blue, alpha);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (red == 0.0f && green == 0.0f && blue == 0.0f && alpha == 1.0f);
	}
	
	@Override
	public PLRGBA reset()
	{
		red = green = blue = 0.0f;
		alpha = 1.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLRGBA setValues(PLRGBA rgba)
	{
		red = rgba.red;
		green = rgba.green;
		blue = rgba.blue;
		alpha = rgba.alpha;
		return this;
	}
	
	public PLRGBA setValues(float red, float green, float blue, float alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLRGBA clone()
	{
		return new PLRGBA(red, green, blue, alpha);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLRGBA)
		{
			if(this == o)
				return true;
			PLRGBA rgba = (PLRGBA)o;
			return (red == rgba.red && green == rgba.green && blue == rgba.blue && alpha == rgba.alpha);
		}
		return false;
	}
}