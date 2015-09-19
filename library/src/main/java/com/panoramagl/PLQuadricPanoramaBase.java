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

import com.panoramagl.opengl.GLUES;
import com.panoramagl.opengl.GLUquadric;

import javax.microedition.khronos.opengles.GL10;

public abstract class PLQuadricPanoramaBase extends PLPanoramaBase implements PLIQuadricPanorama
{
	/**static variables*/
	
	protected static final int[] sPreviewTilesOrder = { 0 };
	
	/**member variables*/
	
	private GLUquadric mQuadric;
	private int mPreviewDivs, mDivs;
	
	/**init methods*/
	
	@Override
	protected void initializeValues()
	{
	    super.initializeValues();
	    mQuadric = GLUES.gluNewQuadric();
		GLUES.gluQuadricNormals(mQuadric, GLUES.GLU_SMOOTH);
		GLUES.gluQuadricTexture(mQuadric, true);
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
	
	protected GLUquadric getQuadric()
	{
		return mQuadric;
	}
	
	protected void setInternalQuadric(GLUquadric quadric)
	{
		mQuadric = quadric;
	}
	
	@Override
	public int getPreviewDivs()
	{
		return mPreviewDivs;
	}
	
	@Override
	public void setPreviewDivs(int previewDivs)
	{
		if(previewDivs > 15)
			mPreviewDivs = previewDivs;
	}
	
	@Override
	public int getDivs()
	{
		return mDivs;
	}
	
	@Override
	public void setDivs(int divs)
	{
		if(divs > 15)
			mDivs = divs;
	}
	
	/**render methods*/
	
	@Override
	protected void beginRender(GL10 gl, PLIRenderer renderer)
	{
		super.beginRender(gl, renderer);
		gl.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f);
		gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
	}
	
	@Override
	protected void endRender(GL10 gl, PLIRenderer renderer)
	{
		gl.glRotatef(-180.0f, 0.0f, 1.0f, 0.0f);
		gl.glRotatef(90.0f, 1.0f, 0.0f, 0.0f);
		super.endRender(gl, renderer);
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		if(mQuadric != null)
		{
			GLUES.gluDeleteQuadric(mQuadric);
			mQuadric = null;
		}
		super.finalize();
	}
}