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

package com.panoramagl.ios;

import android.view.View;

import com.panoramagl.PLIReleaseView;
import com.panoramagl.ios.structs.CGPoint;

public class UITouch implements PLIReleaseView
{
	/**member variables*/
	
	private int mTapCount;
	private View mView;
	private CGPoint mPosition;
	
	/**property methods*/
	
	public int getTapCount()
	{
		return mTapCount;
	}
	
	public void setTapCount(int tapCount)
	{
		if(tapCount > 0)
			mTapCount = tapCount;
	}
	
	public View getView()
	{
		return mView;
	}
	
	public void setView(View view)
	{
		mView = view;
	}
	
	public CGPoint getPosition()
	{
		return mPosition;
	}
	
	public void setPosition(CGPoint point)
	{
		if(point != null)
			mPosition.setValues(point);
	}
	
	public void setPosition(float x, float y)
	{
		mPosition.x = x;
		mPosition.y = y;
	}
	
	/**init methods*/
	
	public UITouch(View view)
	{
		this(view, CGPoint.CGPointMake(0.0f, 0.0f), 1);
	}
	
	public UITouch(View view, CGPoint position)
	{
		this(view, position, 1);
	}
	
	public UITouch(View view, CGPoint position, int tapCount)
	{
		super();
		mView = view;
		mPosition = CGPoint.CGPointMake(position);
		mTapCount = tapCount;
	}
	
	/**location methods*/
	
	public CGPoint locationInView(View view)
	{
		return mPosition;
	}
	
	/**PLIReleaseView methods*/
	
	@Override
	public void releaseView()
	{
		mView = null;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mView = null;
		mPosition = null;
		super.finalize();
	}
}