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

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;

import com.panoramagl.computation.PLMath;
import com.panoramagl.enumerations.PLTextureColorFormat;
import com.panoramagl.opengl.IGLWrapper;
import com.panoramagl.utils.PLLog;
import com.panoramagl.utils.PLUtils;

import javax.microedition.khronos.opengles.GL10;

public class PLTexture extends PLObjectBase implements PLITexture
{
	/**member variables*/
	
	private int[] mTextureId;
	private PLIImage mImage;
	private int mWidth, mHeight;
	private boolean mIsValid, mIsRecycled, mIsRecycledByParent;
	private PLTextureColorFormat mColorFormat;
	private IGLWrapper mGLWrapper;
	private PLTextureListener mListener;
	
	/**init methods*/
	
	public PLTexture(PLIImage image)
	{
		this(image, PLTextureColorFormat.PLTextureColorFormatUnknown, true);
	}
	
	public PLTexture(PLIImage image, boolean isRecycledByParent)
	{
		this(image, PLTextureColorFormat.PLTextureColorFormatUnknown, isRecycledByParent);
	}
	
	public PLTexture(PLIImage image, PLTextureColorFormat colorFormat)
	{
		this(image, colorFormat, true);
	}
	
	public PLTexture(PLIImage image, PLTextureColorFormat colorFormat, boolean isRecycledByParent)
	{
		super();
		mImage = image;
		mColorFormat = colorFormat;
		mIsRecycledByParent = isRecycledByParent;
	}
	
	@Override
	protected void initializeValues()
	{
		mTextureId = new int[]{ 0 };
		mImage = null;
		mWidth = mHeight = 0;
		mIsValid = false;
		mIsRecycled = true;
		mIsRecycledByParent = true;
	    mColorFormat = PLTextureColorFormat.PLTextureColorFormatUnknown;
	    mGLWrapper = null;
	    mListener = null;
	}
	
	/**property methods*/
	
	@Override
	public PLIImage getImage()
	{
		return mImage;
	}
	
	@Override
	public int getTextureId(GL10 gl)
	{
		if(mIsValid)
			return mTextureId[0];
		return (this.loadTexture(gl) ? mTextureId[0] : 0);
	}
	
	@Override
	public int getWidth()
	{
		return mWidth;
	}
	
	@Override
	public int getHeight()
	{
		return mHeight;
	}
	
	@Override
	public boolean isValid()
	{
		return mIsValid;
	}
	
	@Override
	public boolean isRecycled()
	{
		return mIsRecycled;
	}
	
	@Override
	public boolean isRecycledByParent()
	{
		return mIsRecycledByParent;
	}
	
	@Override
	public void setRecycledByParent(boolean isRecycledByParent)
	{
		mIsRecycledByParent = isRecycledByParent;
	}
	
	@Override
	public PLTextureColorFormat getColorFormat()
	{
		return mColorFormat;
	}
	
	@Override
	public void setColorFormat(PLTextureColorFormat colorFormat)
	{
		mColorFormat = colorFormat;
	}
	
	@Override
	public PLTextureListener getListener()
	{
		return mListener;
	}
	
	@Override
	public void setListener(PLTextureListener listener)
	{
		mListener = listener;
	}
	
	/**conversion methods*/
	
	protected int convertSizeToPowerOfTwo(int size)
	{
		if(size <= 4)
			return 4;
		else if(size <= 8)
			return 8;
		else if(size <= 16)
			return 16;
		else if(size <= 32)
			return 32;
		else if(size <= 64)
			return 64;
		else if(size <= 128)
			return 128;
		else if(size <= 256)
			return 256;
		else if(size <= 512)
			return 512;
		else if(size <= 1024)
			return 1024;
		else
			return PLConstants.kTextureMaxSize;
	}
	
	protected PLIImage convertImage(PLIImage image, PLTextureColorFormat colorFormat)
	{
		if(colorFormat != PLTextureColorFormat.PLTextureColorFormatUnknown)
		{
			Bitmap newBitmap = PLUtils.convertBitmap(image.getBitmap(), colorFormat);
			if(newBitmap != image.getBitmap())
				return new PLImage(newBitmap);
		}
		return image;
	}
	
	/**load methods*/
	
	protected boolean loadTexture(GL10 gl)
	{
		try
		{
			if(mImage == null || !mImage.isValid())
				return false;
			
			this.recycleTexture(gl);
			
			mWidth = mImage.getWidth();
			mHeight = mImage.getHeight();
			
			if(mWidth > PLConstants.kTextureMaxSize || mHeight > PLConstants.kTextureMaxSize)
			{
				PLLog.error("PLTexture::loadTexture", "Invalid texture size. The texture max size must be %d x %d and currently is %d x %d.", PLConstants.kTextureMaxSize, PLConstants.kTextureMaxSize, mWidth, mHeight);
				this.recycleImage();
				return false;
			}
			
			boolean isResizableImage = false;
			if(!PLMath.isPowerOfTwo(mWidth))
			{
				isResizableImage = true;
				mWidth = this.convertSizeToPowerOfTwo(mWidth);
			}
			if(!PLMath.isPowerOfTwo(mHeight))
			{
				isResizableImage = true;
				mHeight = this.convertSizeToPowerOfTwo(mHeight);
			}
			
			if(isResizableImage)
				mImage.scale(mWidth, mHeight);
			
			gl.glGenTextures(1, mTextureId, 0);
			
			int error = gl.glGetError();
			if(error != GL10.GL_NO_ERROR)
			{
				PLLog.error("PLTexture::loadTexture", "glGetError #1 = (%d) %s ...", error, GLU.gluErrorString(error));
				this.recycleImage();
				return false;
			}
			
			gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId[0]);
			
			error = gl.glGetError();
			if(error != GL10.GL_NO_ERROR)
			{
				PLLog.error("PLTexture::loadTexture", "glGetError #2 = (%d) %s ...", error, GLU.gluErrorString(error));
				this.recycleImage();
				return false;
			}
			
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR); //GLES10.GL_NEAREST || GL10.GL_LINEAR
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE); //GL10.GL_REPEAT
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
			
			gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE); //GL10.GL_REPLACE
			
			PLIImage image = this.convertImage(mImage, mColorFormat);
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image.getBitmap(), 0);
			if(image != mImage)
				image.recycle();
			
			error = gl.glGetError();
			if(error != GL10.GL_NO_ERROR)
			{
				PLLog.error("PLTexture::loadTexture", "glGetError #3 = (%d) %s ...", error, GLU.gluErrorString(error));
				this.recycleImage();
				return false;
			}
			
			this.recycleImage();
			
			mIsValid = true;
			mIsRecycled = false;
			
			if(gl instanceof IGLWrapper)
				mGLWrapper = (IGLWrapper)gl;
			
			if(mListener != null)
				mListener.didLoad(this);
			
			return true;
		}
		catch(Throwable e)
		{
			PLLog.error("PLTexture::loadTexture", e);
		}
		return false;
	}
	
	/**recycle methods*/
	
	@Override
	public void recycle()
	{
		this.recycleImage();
		this.recycleTexture(mGLWrapper);
		mIsRecycled = true;
	}
	
	protected void recycleImage()
	{
		if(mImage != null)
		{
			mImage.recycle();
			mImage = null;
		}
	}
	
	protected void recycleTexture(GL10 gl)
	{
		if(gl != null && mTextureId != null && mTextureId[0] != 0)
		{
			if(PLUtils.getAndroidVersion() < 3f)
			{
				gl.glDeleteTextures(1, mTextureId, 0);
				mTextureId[0] = 0;
				mGLWrapper = null;
				mIsValid = false;
			}
			else if(mGLWrapper != null)
			{
				GLSurfaceView glSurfaceView = mGLWrapper.getGLSurfaceView();
				if(glSurfaceView != null)
					glSurfaceView.queueEvent(new PLRecycleTextureRunnable(this));
			}
		}
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			this.recycle();
		}
		catch(Throwable e)
		{
		}
		super.finalize();
	}
	
	/**internal classes declaration*/
	
	protected class PLRecycleTextureRunnable implements Runnable
	{
		/**member variables*/
		
		private PLTexture mTexture;
		private IGLWrapper mGLWrapper;
		
		/**init methods*/
		
		public PLRecycleTextureRunnable(PLTexture texture)
		{
			super();
			mTexture = texture;
			mGLWrapper = texture.mGLWrapper;
		}
		
		/**Runnable methods*/
		
		@Override
		public void run()
		{
			mGLWrapper.glDeleteTextures(1, mTextureId, 0);
			mTexture.mTextureId[0] = 0;
			mGLWrapper = null;
			mTexture.mIsValid = false;
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mTexture = null;
			super.finalize();
		}
	}
}