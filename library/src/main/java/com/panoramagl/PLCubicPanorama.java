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

import com.panoramagl.enumerations.PLCubeFaceOrientation;
import com.panoramagl.utils.PLLog;
import com.panoramagl.utils.PLUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class PLCubicPanorama extends PLPanoramaBase 
{
	/**constants*/
	
	protected static final float kR = PLConstants.kPanoramaRadius;
	
	/**static variables*/
	
	protected static final FloatBuffer sCubeBuffer = PLUtils.makeFloatBuffer
	(
		new float[]
		{
			// Front Face
			-kR, -kR,  kR,
			 kR, -kR,  kR,
			-kR,  kR,  kR,
			 kR,  kR,  kR,
			// Back Face
			-kR, -kR, -kR,
			-kR,  kR, -kR,
			 kR, -kR, -kR,
			 kR,  kR, -kR,
			// Left Face
			 kR, -kR, -kR,
			 kR,  kR, -kR,
			 kR, -kR,  kR,
			 kR,  kR,  kR,
			// Right Face
			-kR, -kR,  kR,
			-kR,  kR,  kR,
			-kR, -kR, -kR,
			-kR,  kR, -kR,
			// Top Face
			-kR,  kR,  kR,
			 kR,  kR,  kR,
			-kR,  kR, -kR,
			 kR,  kR, -kR,
			// Bottom Face
			-kR, -kR,  kR,
			-kR, -kR, -kR,
			 kR, -kR,  kR,
			 kR, -kR, -kR,
		}
	);
	
	protected static final FloatBuffer sTextureCoordsBuffer = PLUtils.makeFloatBuffer
	(
		new float[]
		{
			// Front Face
			1.0f, 1.0f,
			0.0f, 1.0f,	
			1.0f, 0.0f,
			0.0f, 0.0f,
			// Back Face
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			// Left Face
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			// Right Face
			0.0f, 1.0f,
			0.0f, 0.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
			// Top Face
			1.0f, 1.0f,
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f,
			// Bottom Face
			1.0f, 0.0f,
			1.0f, 1.0f,
			0.0f, 0.0f,
			0.0f, 1.0f,
		}
	);
	
	protected static final int[] sPreviewTilesOrder = { 1, 3, 0, 2, 4, 5 };
	
	/**init methods*/
	
	public PLCubicPanorama()
	{
		super();
	}
	
	/**property methods*/
	
	@Override
	public int getPreviewTilesNumber()
	{
		return 6;
	}
	
	@Override
	public int getTilesNumber()
	{
		return 6;
	}
	
	@Override
	public int[] getPreviewTilesOrder()
	{
		return sPreviewTilesOrder;
	}
	
	public void setImage(PLIImage image)
	{
		if(image != null)
		{
			int width = image.getWidth(), height = image.getHeight();
			if(width <= PLConstants.kTextureMaxSize && height % width == 0 && height / width == 6)
			{
				this.setTexture(new PLTexture(PLImage.crop(image, 0, 0, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationLeft.ordinal());
				this.setTexture(new PLTexture(PLImage.crop(image, 0, width, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationFront.ordinal());
				this.setTexture(new PLTexture(PLImage.crop(image, 0, width * 2, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationRight.ordinal());
				this.setTexture(new PLTexture(PLImage.crop(image, 0, width * 3, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationBack.ordinal());
				this.setTexture(new PLTexture(PLImage.crop(image, 0, width * 4, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationUp.ordinal());
				this.setTexture(new PLTexture(PLImage.crop(image, 0, width * 5, width, width)), PLCubeFaceOrientation.PLCubeFaceOrientationDown.ordinal());
			}
		}
	}
	
	/**image methods*/
	
	public boolean setImage(PLIImage image, int index)
	{
		return (image != null ? this.setTexture(new PLTexture(image), index) : false);
	}
	
	public boolean setImage(PLIImage image, PLCubeFaceOrientation face)
	{
	    return this.setImage(image, face.ordinal());
	}
	
	/**texture methods*/
	
	public boolean setTexture(PLITexture texture, PLCubeFaceOrientation face)
	{
		return this.setTexture(texture, face.ordinal());
	}
	
	protected boolean bindTextureByIndex(GL10 gl, int index)
	{
		boolean result = false;
		try
		{
			PLITexture texture = this.getTextures()[index];
			if(texture != null && texture.getTextureId(gl) != 0)
			{
				gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureId(gl));
				result = true;
	            if(this.getPreviewTextures()[index] != null)
	            	this.removePreviewTextureAtIndex(index, true);
			}
			else
			{
				texture = this.getPreviewTextures()[index];
				if(texture != null && texture.getTextureId(gl) != 0)
				{
					gl.glBindTexture(GL10.GL_TEXTURE_2D, texture.getTextureId(gl));
					result = true;
				}
			}
		}
		catch(Throwable e)
		{
			PLLog.error("PLCubicPanorama::bindTextureByIndex", e);
		}
		return result;
	}
	
	/**render methods*/
	
	@Override
	protected void internalRender(GL10 gl, PLIRenderer renderer)
	{
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_FRONT);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, sCubeBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, sTextureCoordsBuffer);
		
		// Front Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeFrontFaceIndex))
		{
			gl.glNormal3f(0.0f, 0.0f, 1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		}
		
		// Back Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeBackFaceIndex))
		{
			gl.glNormal3f(0.0f, 0.0f, -1.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 4, 4);
		}
		
		// Left Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeLeftFaceIndex))
		{
			gl.glNormal3f(1.0f, 0.0f, 0.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 8, 4);
		}
		
		// Right Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeRightFaceIndex))
		{
			gl.glNormal3f(-1.0f, 0.0f, 0.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 12, 4);
		}
		
		// Up Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeUpFaceIndex))
		{
			gl.glNormal3f(0.0f, 1.0f, 0.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 16, 4);
		}
		
		// Down Face
		if(this.bindTextureByIndex(gl, PLConstants.kCubeDownFaceIndex))
		{
			gl.glNormal3f(0.0f, -1.0f, 0.0f);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 20, 4);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);	
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glDisable(GL10.GL_TEXTURE_2D);
	}
}