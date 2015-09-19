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

public class PLShakeData implements PLIStruct<PLShakeData>
{
	/**member variables*/
	
	public long lastTime;
	public PLPosition shakePosition;
	public PLPosition shakeLastPosition;
	
	/**init methods*/
	
	public PLShakeData()
	{
		this(0);
	}
	
	public PLShakeData(long lastTime)
	{
		super();
		this.lastTime = lastTime;
		shakePosition = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f);
		shakeLastPosition = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f);
	}
	
	public PLShakeData(PLShakeData shakeData)
	{
		this(shakeData.lastTime);
		shakePosition.setValues(shakeData.shakePosition);
		shakeLastPosition.setValues(shakeData.shakeLastPosition);
	}
	
	public static PLShakeData PLShakeDataMake()
	{
		return new PLShakeData();
	}
	
	public static PLShakeData PLShakeDataMake(long lastTime)
	{
		return new PLShakeData(lastTime);
	}
	
	public static PLShakeData PLShakeDataMake(PLShakeData shakeData)
	{
		return new PLShakeData(shakeData);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (lastTime == 0 && shakePosition.isResetted() && shakeLastPosition.isResetted());
	}
	
	@Override
	public PLShakeData reset()
	{
		lastTime = 0;
		shakePosition.reset();
		shakeLastPosition.reset();
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLShakeData setValues(PLShakeData shakeData)
	{
		lastTime = shakeData.lastTime;
		shakePosition.setValues(shakeData.shakePosition);
		shakeLastPosition.setValues(shakeData.shakeLastPosition);
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLShakeData clone()
	{
		return new PLShakeData(this);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLShakeData)
		{
			if(this == o)
				return true;
			PLShakeData data = (PLShakeData)o;
			return (lastTime == data.lastTime && shakePosition.equals(data.shakePosition) && shakeLastPosition.equals(data.shakeLastPosition));
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		shakePosition = null;
		shakeLastPosition = null;
		super.finalize();
	}
}