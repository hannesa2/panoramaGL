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

package com.panoramagl.transitions;

import com.panoramagl.PLCamera;
import com.panoramagl.PLConstants;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIPanorama;
import com.panoramagl.PLIView;
import com.panoramagl.PLObjectBase;
import com.panoramagl.enumerations.PLTransitionProcessingType;
import com.panoramagl.ios.NSTimer;

public abstract class PLTransitionBase extends PLObjectBase implements PLITransition
{
	/**member variables*/
	
	private NSTimer mTimer;
	private float mInterval;
	private int mProgressPercentage;
	
	private PLIView mView;
	private PLIPanorama mCurrentPanorama, mNewPanorama;
	private PLICamera mCurrentPanoramaCamera, mNewPanoramaCamera;
	
	private boolean mIsPanoramaLocked, mIsRunning, mIsValid;
	
	private PLTransitionListener mInternalListener;
	private PLITransitionListenerManager mListeners;
	
	/**init methods*/
	
	public PLTransitionBase()
	{
		super();
	}
	
	public PLTransitionBase(float interval)
	{
		super();
		this.setInterval(interval);
	}
	
	@Override
	protected void initializeValues()
	{
		mTimer = null;
		mInterval = PLConstants.kDefaultTransitionInterval;
		mProgressPercentage = 0;
		mView = null;
		mCurrentPanorama = mNewPanorama = null;
		mCurrentPanoramaCamera = mNewPanoramaCamera = null;
		mIsPanoramaLocked = mIsRunning = mIsValid = false;
		mInternalListener = null;
		mListeners = new PLTransitionListenerManager();
	}
	
	/**property methods*/
	
	@Override
	public PLIView getView()
	{
		return mView;
	}
	
	@Override
	public PLIPanorama getCurrentPanorama()
	{
		return mCurrentPanorama;
	}
	
	@Override
	public PLICamera getCurrentPanoramaCamera()
	{
		return mCurrentPanoramaCamera;
	}
	
	@Override
	public PLIPanorama getNewPanorama()
	{
		return mNewPanorama;
	}
	
	@Override
	public PLICamera getNewPanoramaCamera()
	{
		return mNewPanoramaCamera;
	}
	
	@Override
	public float getInterval()
	{
		return mInterval;
	}
	
	@Override
	public void setInterval(float interval)
	{
		if(!mIsRunning && interval > 0.0f)
			mInterval = interval;
	}
	
	@Override
	public int getProgressPercentage()
	{
		return mProgressPercentage;
	}
	
	protected void setProgressPercentage(int percentage)
	{
		mProgressPercentage = percentage;
	}
	
	@Override
	public boolean isRunning()
	{
		return mIsRunning;
	}
	
	protected void setRunning(boolean isRunning)
	{
		mIsRunning = isRunning;
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
	
	protected NSTimer getTimer()
	{
		return mTimer;
	}
	
	protected void setTimer(NSTimer newTimer)
	{
		if(mTimer != null)
		{
			mTimer.invalidate();
			mTimer = null;
		}
		mTimer = newTimer;
	}
	
	@Override
	public PLTransitionListener getInternalListener()
	{
		return mInternalListener;
	}
	
	@Override
	public void setInternalListener(PLTransitionListener listener)
	{
		if(!mIsRunning && listener != null)
			mInternalListener = listener;
	}
	
	@Override
	public PLITransitionListenerManager getListeners()
	{
		return mListeners;
	}
	
	/**process methods*/
	
	protected void beforeStarting(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera)
	{
	}
	
	protected void afterStarting(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera)
	{
	}
	
	protected void process()
	{
		if(mView != null && mIsRunning)
		{
			switch(this.internalProcess(mView, mCurrentPanorama, mNewPanorama, mCurrentPanoramaCamera, mNewPanoramaCamera))
			{
				case PLTransitionProcessingTypeRunning:
					if(mInternalListener != null)
						mInternalListener.didProcessTransition(this, mProgressPercentage);
					if(mListeners.length() > 0)
						mListeners.didProcessTransition(this, mProgressPercentage);
					break;
				case PLTransitionProcessingTypeWaiting:
					break;
				case PLTransitionProcessingTypeBegin:
					if(mInternalListener != null)
						mInternalListener.didBeginTransition(this);
					if(mListeners.length() > 0)
						mListeners.didBeginTransition(this);
					mIsValid = true;
					this.afterStarting(mView, mCurrentPanorama, mNewPanorama, mCurrentPanoramaCamera, mNewPanoramaCamera);
					break;
				case PLTransitionProcessingTypeEnd:
					this.end();
					break;
			}	
		}
	}
	
	protected abstract PLTransitionProcessingType internalProcess(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera);
	
	protected void beforeFinishing(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera)
	{
	}
	
	protected void afterFinishing(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, boolean isEnd)
	{
	}
	
	/**control methods*/
	
	protected int iterationsPerSecond()
	{
		return PLConstants.kDefaultTransitionIterationsPerSecond;
	}
	
	@Override
	public boolean start(PLIView view, PLIPanorama newPanorama)
	{	
		if(mIsRunning || view == null || view.getPanorama() == null || newPanorama == null)
			return false;
		mIsRunning = true;
		mView = view;
		mCurrentPanorama = view.getPanorama();
		mCurrentPanoramaCamera = new PLCamera(mCurrentPanorama.getCamera());
		mIsPanoramaLocked = mCurrentPanorama.isLocked();
		mNewPanorama = newPanorama;
		mNewPanoramaCamera = new PLCamera(mNewPanorama.getCamera());
		mProgressPercentage = 0;
		if(!mIsPanoramaLocked)
			mCurrentPanorama.setLocked(true);
		this.beforeStarting(mView, mCurrentPanorama, mNewPanorama, mCurrentPanoramaCamera, mNewPanoramaCamera);
		this.setTimer
		(
			NSTimer.scheduledTimerWithTimeInterval
			(
				1.0f / this.iterationsPerSecond(),
				new NSTimer.Runnable()
				{
					@Override
					public void run(NSTimer target, Object[] userInfo)
					{
						process();
					}
				},
				null,
				true
			)
		);
		return true;
	}
	
	@Override
	public boolean stop()
	{
		return this.stop(false);
	}
	
	protected boolean stop(boolean isEnd)
	{
		if(mIsRunning)
		{
			synchronized(this)
			{
				mIsRunning = mIsValid = false;
				this.setTimer(null);
				if(!mIsPanoramaLocked)
					mCurrentPanorama.setLocked(false);
				if(isEnd)
				{
					if(mInternalListener != null)
						mInternalListener.didEndTransition(this);
					if(mListeners.length() > 0)
						mListeners.didEndTransition(this);
				}
				else
				{
					if(mInternalListener != null)
						mInternalListener.didStopTransition(this, mProgressPercentage);
					if(mListeners.length() > 0)
						mListeners.didStopTransition(this, mProgressPercentage);
				}
				this.afterFinishing(mView, mCurrentPanorama, mNewPanorama, isEnd);
				mView = null;
				mCurrentPanorama = mNewPanorama = null;
				mCurrentPanoramaCamera = mNewPanoramaCamera = null;
				if(mInternalListener != null && mInternalListener.isRemovableListener())
					mInternalListener = null;
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean end()
	{
		if(mIsRunning)
		{
			this.beforeFinishing(mView, mCurrentPanorama, mNewPanorama, mCurrentPanoramaCamera, mNewPanoramaCamera);
			return this.stop(true);
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
			mCurrentPanorama = mNewPanorama = null;
		}
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		this.setTimer(null);
		mView = null;
		mCurrentPanorama = mNewPanorama = null;
		mCurrentPanoramaCamera = mNewPanoramaCamera = null;
		mInternalListener = null;
		mListeners = null;
		super.finalize();
	}
}