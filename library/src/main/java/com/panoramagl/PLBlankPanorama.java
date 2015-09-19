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

package com.panoramagl;

import com.panoramagl.structs.PLRGBA;

import javax.microedition.khronos.opengles.GL10;

public class PLBlankPanorama extends PLPanoramaBase
{
	/**static variables*/
	
	protected static final int[] sPreviewTilesOrder = { 0 };
	
	/**member variables*/
	
	private PLRGBA mColor;
	
	/**init methods*/
	
	public PLBlankPanorama()
	{
		super();
		super.setLocked(true);
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		mColor = PLRGBA.PLRGBAMake(0.0f, 0.0f, 0.0f, 1.0f);
	}
	
	/**property methods*/
	
	@Override
	public int getPreviewTilesNumber()
	{
		return 1;
	}
	
	@Override
	public int getTilesNumber()
	{
		return 1;
	}
	
	@Override
	public int[] getPreviewTilesOrder()
	{
		return sPreviewTilesOrder;
	}
	
	public PLRGBA getColor()
	{
		return mColor;
	}
	
	public void setColor(PLRGBA color)
	{
		if(color != null)
			mColor.setValues(color);
	}
	
	public void setColor(float red, float green, float blue)
	{
		mColor.setValues(red, green, blue, 1.0f);
	}
	
	@Override
	public void setLocked(boolean isLocked)
	{
	}
	
	/**render methods*/
	
	@Override
	protected void internalRender(GL10 gl, PLIRenderer renderer)
	{
		gl.glClearColor(mColor.red, mColor.green, mColor.blue, 1.0f);
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mColor = null;
		super.finalize();
	}
}