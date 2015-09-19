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

import android.opengl.GLU;

import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;
import com.panoramagl.opengl.GLWrapper;
import com.panoramagl.opengl.IGLWrapper;
import com.panoramagl.opengl.matrix.MatrixTrackingGL;
import com.panoramagl.transitions.PLITransition;
import com.panoramagl.utils.PLLog;
import com.panoramagl.utils.PLOpenGLSupport;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public class PLRenderer extends PLObjectBase implements PLIRenderer
{
	/**member variables*/
	
	private int[] mBackingWidth, mBackingHeight;
	
	private int[] mDefaultFramebuffer, mColorRenderbuffer;
	
	private PLIView mView;
	private PLIScene mScene;
	
	private boolean mIsRunning, mIsRendering;
	
	private CGRect mViewport, mTempViewport;
	
	private CGSize mSize, mTempSize;
	
	private boolean mContextSupportsFrameBufferObject;
	
	private PLRendererListener mListener;
	
	/**init methods*/
	
	public PLRenderer(PLIView view, PLIScene scene)
	{
		super();
		this.setInternalView(view);
		this.setInternalScene(scene);
	}
	
	@Override
	protected void initializeValues()
	{
		mBackingWidth = new int[1];
		mBackingHeight = new int[1];
		
		mDefaultFramebuffer = new int[1];
		mColorRenderbuffer = new int[1];
		
		mIsRunning = mIsRendering = false;
		
		mViewport = CGRect.CGRectMake(mTempViewport = CGRect.CGRectMake(0, 0, PLConstants.kViewportSize, PLConstants.kViewportSize));
		
		mSize = CGSize.CGSizeMake(mTempSize = CGSize.CGSizeMake(0.0f, 0.0f));
		
		mContextSupportsFrameBufferObject = false;
	}
	
	/**property methods*/
	
	@Override
	public int getBackingWidth()
	{
		return mBackingWidth[0];
	}
	
	@Override
	public int getBackingHeight()
	{
		return mBackingHeight[0];
	}
	
	@Override
	public PLIView getInternalView()
	{
		return mView;
	}
	
	@Override
	public void setInternalView(PLIView view)
	{
		mView = view;
	}
	
	@Override
	public PLIScene getInternalScene()
	{
		return mScene;
	}
	
	@Override
	public void setInternalScene(PLIScene scene)
	{
		mScene = scene;
	}
	
	@Override
	public boolean isRunning()
	{
		return mIsRunning;
	}
	
	@Override
	public boolean isRendering()
	{
		return mIsRendering;
	}
	
	@Override
	public CGRect getViewport()
	{
		return mTempViewport.setValues(mViewport);
	}
	
	@Override
	public CGSize getSize()
	{
		return mTempSize.setValues(mSize);
	}
	
	protected boolean getContextSupportsFrameBufferObject()
	{
		return mContextSupportsFrameBufferObject;
	}
	
	@Override
	public PLRendererListener getInternalListener()
	{
		return mListener;
	}
	
	@Override
	public void setInternalListener(PLRendererListener listener)
	{
		mListener = listener;
	}
	
	/**buffer methods*/
	
	protected void createFrameBuffer(GL11ExtensionPack gl11ep)
	{
		if(mContextSupportsFrameBufferObject)
		{
	        gl11ep.glGenFramebuffersOES(1, mDefaultFramebuffer, 0);
	        if(mDefaultFramebuffer[0] <= 0)
	        	PLLog.error("PLRenderer::createFrameBuffer", "Invalid framebuffer id returned!");
	        gl11ep.glGenRenderbuffersOES(1, mColorRenderbuffer, 0);
	        if(mColorRenderbuffer[0] <= 0)
	        	PLLog.error("PLRenderer::createFrameBuffer", "Invalid renderbuffer id returned!");
	        gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mDefaultFramebuffer[0]);
	        gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0]);
		}
	}
	
	protected void destroyFramebuffer(GL11ExtensionPack gl11ep)
	{
		if(mContextSupportsFrameBufferObject)
		{
			if(mDefaultFramebuffer[0] != 0)
		    {
				gl11ep.glDeleteFramebuffersOES(1, mDefaultFramebuffer, 0);
				mDefaultFramebuffer[0] = 0;
		    }
		    if(mColorRenderbuffer[0] != 0)
		    {
		    	gl11ep.glDeleteRenderbuffersOES(1, mColorRenderbuffer, 0);
		    	mColorRenderbuffer[0] = 0;
		    }
		}
	}
	
	/**resize methods*/
	
	@Override
	public boolean resizeFromLayer()
	{
		return this.resizeFromLayer(null);
	}
	
	@Override
	public boolean resizeFromLayer(GL11ExtensionPack gl11ep)
	{	
		if(mContextSupportsFrameBufferObject && gl11ep != null)
		{
			synchronized(this)
			{
				if(mBackingWidth[0] != mSize.width || mBackingHeight[0] != mSize.height)
				{
					boolean isRunning = mIsRunning;
					if(isRunning)
						mIsRunning = false;
					
					this.destroyFramebuffer(gl11ep);
					this.createFrameBuffer(gl11ep);
					
		            gl11ep.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES,
		                    GL11ExtensionPack.GL_RGBA8, mSize.width, mSize.height);
		            gl11ep.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES,
		                    GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
		                    GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0]);
		            
		            gl11ep.glGetRenderbufferParameterivOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_RENDERBUFFER_WIDTH_OES, mBackingWidth, 0);
			        gl11ep.glGetRenderbufferParameterivOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_RENDERBUFFER_HEIGHT_OES, mBackingHeight, 0);
			        
			        mViewport.x = -(mViewport.width / 2 - mSize.width / 2);
					mViewport.y = -(mViewport.height / 2 - mSize.height / 2);
					
					if(gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES) != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES)
					{
						PLLog.error("PLRenderer::resizeFromLayer", "Failed to make complete framebuffer object %x", gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES));
						return false;
					}
					
					if(isRunning)
						mIsRunning = true;
					return true;
				}
			}
		}
		else
		{
			synchronized(this)
			{
				mViewport.x = -(mViewport.width / 2 - mSize.width / 2);
				mViewport.y = -(mViewport.height / 2 - mSize.height / 2);
			}
		}
		return false;
	}
	
	/**render methods*/
	
	protected void renderScene(GL10 gl, PLIScene scene, PLICamera camera)
	{
		if(scene != null && camera != null)
		{
			gl.glMatrixMode(GL10.GL_PROJECTION);
			gl.glLoadIdentity();
			GLU.gluPerspective(gl, camera.getFov(), PLConstants.kPerspectiveAspect, PLConstants.kPerspectiveZNear, PLConstants.kPerspectiveZFar);
			gl.glMatrixMode(GL10.GL_MODELVIEW);
			scene.render(gl, this);
		}
	}
	
	@Override
	public void render(GL10 gl)
	{
		try
		{
			if(gl != null && mIsRunning)
			{
				mIsRendering = true;
				
				if(mContextSupportsFrameBufferObject)
				{
					GL11ExtensionPack gl11ep = (GL11ExtensionPack)gl;
					gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mDefaultFramebuffer[0]);
				}
				
				gl.glViewport(mViewport.x, mViewport.y, mViewport.width, mViewport.height);
				
				gl.glMatrixMode(GL10.GL_MODELVIEW);
				gl.glLoadIdentity();
				
				gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
				gl.glClearDepthf(1.0f);
				
				gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
				gl.glClear(GL10.GL_DEPTH_BUFFER_BIT | GL10.GL_COLOR_BUFFER_BIT);
				
				gl.glEnable(GL10.GL_DEPTH_TEST);
				gl.glDepthFunc(GL10.GL_ALWAYS);
				gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
				
				gl.glScalef(1.0f, 1.0f, PLConstants.kViewportScale);
				gl.glTranslatef(0.0f, 0.0f, 0.0f);
				gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
				
				if(mView != null && mView.isValidForTransition())
				{
					PLITransition currentTransition = mView.getCurrentTransition();
					if(currentTransition != null && currentTransition.isValid())
					{
						this.renderScene(gl, currentTransition.getCurrentPanorama(), currentTransition.getCurrentPanoramaCamera());
						this.renderScene(gl, currentTransition.getNewPanorama(), currentTransition.getNewPanoramaCamera());
					}
					else
						this.renderScene(gl, mScene, mScene.getCamera());
				}
				else
					this.renderScene(gl, mScene, mScene.getCamera());
				
				if(mContextSupportsFrameBufferObject)
				{
					GL11ExtensionPack gl11ep = (GL11ExtensionPack)gl;
					gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0]);
				}
				
				mIsRendering = false;
			}
		}
		catch(Throwable e)
		{
			mIsRendering = false;
			PLLog.debug("PLRenderer::render", e);
		}
	}
	
	@Override
	public void renderNTimes(GL10 gl, int times)
	{
		for(int i = 0; i < times; i++)
			this.render(gl);
	}
	
	/**control methods*/
	
	@Override
	public boolean start()
	{
		if(!mIsRunning)
	    {
	        synchronized(this)
	        {
	        	mIsRunning = true;
	        	return true;
	        }
	    }
		return false;
	}
	
	@Override
	public boolean stop()
	{
		if(mIsRunning)
	    {
	        synchronized(this)
	        {
	        	mIsRunning = false;
	        	return true;
	        }
	    }
		return false;
	}
	
	/**PLIReleaseView methods*/
	
	@Override
	public void releaseView()
	{
		if(!mIsRunning)
		{
			mView = null;
			mScene = null;
			mListener = null;
		}
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			this.stop();
			if(mContextSupportsFrameBufferObject)
		    	this.destroyFramebuffer((GL11ExtensionPack)mGLWrapper);
		}
		catch(Throwable e)
		{
		}
		mBackingWidth = mBackingHeight = null;
		mDefaultFramebuffer = mColorRenderbuffer = null;
		mView = null;
	    mScene = null;
	    mViewport = mTempViewport = null;
		mSize = mTempSize = null;
		mListener = null;
		mGLWrapper = null;
		super.finalize();
	}
	
	// ============================
	// Specific methods for Android
	// ============================
	
	/**android: member variables*/
	
	private boolean mIsGLContextCreated;
	
	private IGLWrapper mGLWrapper;
	
	/**android: property methods*/
	
	protected boolean isGLContextCreated()
	{
		return mIsGLContextCreated;
	}
	
	protected IGLWrapper getGLWrapper()
	{
		return mGLWrapper;
	}
	
	@Override
	public GL10 getGLContext()
	{
		return mGLWrapper;
	}
	
	/**android: GLSurfaceView.Renderer methods*/
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		try
		{
			mIsGLContextCreated = false;
			mGLWrapper = (PLOpenGLSupport.isHigherThanOpenGL1(gl) ? new GLWrapper(gl, mView.getGLSurfaceView()) : new MatrixTrackingGL(gl, mView.getGLSurfaceView()));
			//mContextSupportsFrameBufferObject = PLOpenGLSupport.checkIfContextSupportsFrameBufferObject(gl);
			this.start();
			if(mListener != null)
				mListener.rendererCreated(this);
		}
		catch(Throwable e)
		{
			PLLog.error("PLRenderer::onSurfaceCreated", e);
		}
	}
	
	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height)
	{
		mSize.setValues(width, height);
		this.resizeFromLayer(mContextSupportsFrameBufferObject ? (GL11ExtensionPack)mGLWrapper : null);
		if(!mIsGLContextCreated)
		{
			if(mListener != null)
				mListener.rendererFirstChanged(mGLWrapper, this, width, height);
			mIsGLContextCreated = true;
		}
		if(mListener != null)
			mListener.rendererChanged(this, width, height);
	}
	
	@Override
	public void onDrawFrame(GL10 gl)
	{
		if(mIsGLContextCreated && mView != null)
			this.render(mGLWrapper);
	}
}