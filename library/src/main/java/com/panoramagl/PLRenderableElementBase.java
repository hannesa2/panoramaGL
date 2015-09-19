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

import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLRotation;
import com.panoramagl.utils.PLLog;

import javax.microedition.khronos.opengles.GL10;

public abstract class PLRenderableElementBase extends PLObject implements PLIRenderableElement
{
	/**member variables*/
	
	private boolean mIsVisible, mIsValid, mIsRendering;
	
	/**init methods*/
	
	protected void initializeValues()
	{
		super.initializeValues();
		mIsVisible = mIsValid = true;
		mIsRendering = false;
	}
	
	/**property methods*/
	
	@Override
	public boolean isVisible()
	{
		return mIsVisible;
	}
	
	@Override
	public void setVisible(boolean isVisible)
	{
		mIsVisible = isVisible;
	}
	
	@Override
	public boolean isValid()
	{
		return mIsValid;
	}
	
	protected void setValid(boolean isValid)
	{
		mIsValid = isValid;
	}
	
	@Override
	public boolean isRendering()
	{
		return mIsRendering;
	}
	
	protected void setRendering(boolean isRendering)
	{
		mIsRendering = isRendering;
	}
	
	/**translate methods*/
	
	protected void translate(GL10 gl)
	{
		boolean isYZAxisInverseRotation = this.isYZAxisInverseRotation();
		PLPosition position = this.getPosition();
		float y = (isYZAxisInverseRotation ? position.z : position.y), z = (isYZAxisInverseRotation ? position.y : position.z);
		gl.glTranslatef(this.isXAxisEnabled() ? position.x : 0.0f, this.isYAxisEnabled() ? y : 0.0f, this.isZAxisEnabled() ? z : 0.0f);
	}
	
	/**rotate methods*/
	
	protected void rotate(GL10 gl)
	{
		this.internalRotate(gl, this.getRotation());
	}
	
	protected void internalRotate(GL10 gl, PLRotation rotation)
	{
		boolean isYZAxisInverseRotation = this.isYZAxisInverseRotation(), isReverseRotation = this.isReverseRotation();
		float yDirection = (isYZAxisInverseRotation ? 1.0f : 0.0f), zDirection = (isYZAxisInverseRotation ? 0.0f : 1.0f);
		if(this.isPitchEnabled())
			gl.glRotatef(isReverseRotation ? rotation.pitch : -rotation.pitch, 1.0f, 0.0f, 0.0f);
		if(this.isYawEnabled())
			gl.glRotatef(isReverseRotation ? rotation.yaw : -rotation.yaw, 0.0f, yDirection, zDirection);
		if(this.isRollEnabled())
			gl.glRotatef(isReverseRotation ? rotation.roll : -rotation.roll, 0.0f, yDirection, zDirection);
	}
	
	/**alpha methods*/
	
	protected void beginAlpha(GL10 gl)
	{
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glColor4f(1.0f, 1.0f, 1.0f, this.getAlpha());
	}
	
	protected void endAlpha(GL10 gl)
	{
		gl.glDisable(GL10.GL_BLEND);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}
	
	/**clear methods*/
	
	@Override
	public void clear()
	{
		boolean isVisible = mIsVisible;
		if(isVisible)
			mIsVisible = false;
		while(mIsRendering);
		this.internalClear();
		if(isVisible)
			mIsVisible = true;
	}
	
	protected abstract void internalClear();
	
	/**render methods*/
	
	protected void beginRender(GL10 gl, PLIRenderer renderer)
	{
		gl.glPushMatrix();
		this.rotate(gl);
		this.translate(gl);
		this.beginAlpha(gl);
	}
	
	protected void endRender(GL10 gl, PLIRenderer renderer)
	{
		this.endAlpha(gl);
		gl.glPopMatrix();
	}
	
	@Override
	public boolean render(GL10 gl, PLIRenderer renderer)
	{
		try
		{
			if(mIsVisible && mIsValid)
			{
				mIsRendering = true;
				this.beginRender(gl, renderer);
				this.internalRender(gl, renderer);
				this.endRender(gl, renderer);
				mIsRendering = false;
				return true;
			}
		}
		catch(Throwable e)
		{
			mIsRendering = false;
			PLLog.error("PLRenderableElementBase::render", e);
		}
		return false;
	}
	
	protected abstract void internalRender(GL10 gl, PLIRenderer renderer);
}