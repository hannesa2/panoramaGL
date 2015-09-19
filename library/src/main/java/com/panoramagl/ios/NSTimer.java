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

import android.os.SystemClock;

import com.panoramagl.utils.PLLog;

import java.util.Date;

public class NSTimer extends Object
{
	/**member variables*/
	
	private boolean mIsRunning;
	
	private long mInterval;
	private Runnable mTarget;
	private Object[] mUserInfo;
	private boolean mRepeats;
	
	private Thread mThread;
	private long mLastTime, mTime;
	
	/**init methods*/
	
	public NSTimer(Date date, float interval, Runnable target, Object[] userInfo, boolean repeats)
	{
		super();
		mIsRunning = true;
		mInterval = (long)(interval * 1000.0f);
		mTarget = target;
		mUserInfo = userInfo;
		mRepeats = repeats;
		mLastTime = date.getTime();
		mThread = new Thread(new java.lang.Runnable()
		{
			@Override
			public void run()
			{
				while(mIsRunning)
				{
					mTime = SystemClock.uptimeMillis();
					if(mTime - mLastTime >= mInterval)
					{
						try
						{	
							mTarget.run(NSTimer.this, mUserInfo);
						} 
						catch(Throwable e)
						{
							PLLog.debug("NSTimer::run", e);
						}
						if(!mRepeats)
							invalidate();
					}
					mLastTime = mTime;
					try
					{
						Thread.sleep(mInterval);
					}
					catch(Throwable e)
					{
					}
				}
			}
		});
		mThread.start();
	}
	
	public static NSTimer scheduledTimerWithTimeInterval(float interval, Runnable target, Object[] userInfo, boolean repeats)
	{
		return new NSTimer(new Date(SystemClock.uptimeMillis()), interval, target, userInfo, repeats);
	}
	
	public void invalidate()
	{
		mIsRunning = false;
		mThread = null;
		mTarget = null;
		mUserInfo = null;
	}
	
	public boolean isValid()
	{
		return mIsRunning;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			this.invalidate();
		}
		catch(Throwable e)
		{
		}
		super.finalize();
	}
	
	/**sub-interfaces declaration*/
	
	public interface Runnable
	{
		public void run(NSTimer target, Object[] userInfo);
	}
}