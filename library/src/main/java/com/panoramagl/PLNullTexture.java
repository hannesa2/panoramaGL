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

import com.panoramagl.enumerations.PLTextureColorFormat;

import javax.microedition.khronos.opengles.GL10;

public class PLNullTexture implements PLITexture
{
	/**init methods*/
	
	public PLNullTexture()
	{
		super();
	}
	
	/**property methods*/
	
	@Override
	public PLIImage getImage()
	{
		return null;
	}
	
	@Override
	public int getTextureId(GL10 gl)
	{
		return 0;
	}
	
	@Override
	public int getWidth()
	{
		return 0;
	}
	
	@Override
	public int getHeight()
	{
		return 0;
	}
	
	@Override
	public boolean isValid()
	{
		return false;
	}
	
	@Override
	public boolean isRecycled()
	{
		return true;
	}
	
	@Override
	public boolean isRecycledByParent()
	{
		return true;
	}
	
	@Override
	public void setRecycledByParent(boolean isRecycledByParent)
	{
	}
	
	@Override
	public PLTextureColorFormat getColorFormat()
	{
		return PLTextureColorFormat.PLTextureColorFormatUnknown;
	}
	
	@Override
	public void setColorFormat(PLTextureColorFormat colorFormat)
	{
	}
	
	@Override
	public PLTextureListener getListener()
	{
		return null;
	}
	
	@Override
	public void setListener(PLTextureListener listener)
	{
	}
	
	/**recycle methods*/
	
	@Override
	public void recycle()
	{
	}
}