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

package com.panoramagl.downloaders;

import com.panoramagl.PLObjectBase;

public abstract class PLFileDownloaderBase extends PLObjectBase implements PLIFileDownloader
{
	/**constants*/
	
	public static final int kDefaultMaxAttempts = 1;
	
	/**member variables*/
	
	private String mURL;
	private boolean mIsRunning;
	private int mMaxAttempts;
	private Thread mThread;
	private Runnable mThreadRunnable;
	private PLFileDownloaderListener mListener;
	
	/**init methods*/
	
	public PLFileDownloaderBase()
	{
		this(null, null);
	}
	
	public PLFileDownloaderBase(String url)
	{
		this(url, null);
	}
	
	public PLFileDownloaderBase(String url, PLFileDownloaderListener listener)
	{
		super();
		mURL = url;
		mListener = listener;
	}
	
	@Override
	protected void initializeValues()
	{
		mURL = null;
		mIsRunning = false;
		mMaxAttempts = kDefaultMaxAttempts;
		mThread = null;
		mThreadRunnable = null;
		mListener = null;
	}
	
	/**property methods*/
	
	@Override
	public String getURL()
	{
		return mURL;
	}
	
	@Override
	public PLIFileDownloader setURL(String url)
	{
		if(!mIsRunning && url != null)
		{
			synchronized(this)
			{
				mURL = url.trim();
			}
		}
		return this;
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
	public int getMaxAttempts()
	{
		return mMaxAttempts;
	}
	
	@Override
	public PLIFileDownloader setMaxAttempts(int maxAttemps)
	{
		if(!mIsRunning && maxAttemps > 0)
		{
			synchronized(this)
			{
				mMaxAttempts = maxAttemps;
			}
		}
		return this;
	}
	
	protected Thread getThread()
	{
		return mThread;
	}
	
	protected void setThread(Thread thread)
	{
		mThread = thread;
	}
	
	protected Runnable getThreadRunnable()
	{
		return mThreadRunnable;
	}
	
	protected void setThreadRunnable(Runnable threadRunnable)
	{
		mThreadRunnable = threadRunnable;
	}
	
	@Override
	public PLFileDownloaderListener getListener()
	{
		return mListener;
	}
	
	@Override
	public PLIFileDownloader setListener(PLFileDownloaderListener listener)
	{
		if(!mIsRunning)
		{
			synchronized(this)
			{
				mListener = listener;
			}
		}
		return this;
	}
	
	/**download methods*/
	
	protected abstract byte[] downloadFile();
    
	@Override
	public byte[] download()
    {
		if(!mIsRunning)
		{
			synchronized(this)
			{
				return this.downloadFile();
			}
		}
		return null;
    }
	
	@Override
	public boolean downloadAsynchronously()
	{
		if(!mIsRunning)
		{
			synchronized(this)
			{
				if(mThreadRunnable == null)
				{
					mThreadRunnable = new Runnable()
					{
						public void run()
						{
							downloadFile();
						}
					};
				}
				mThread = new Thread(mThreadRunnable);
				mThread.start();
				return true;
			}
		}
		return false;
	}
	
	/**stop methods*/
    
	@Override
    public boolean stop()
    {
		if(mIsRunning)
		{
			synchronized(this)
			{
    	    	mIsRunning = false;
    	    	mThread = null;
    	    	if(mListener != null)
    	    		mListener.didStopDownload(mURL);
    	    	return true;
    		}
		}
		return false;
    }
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mURL = null;
		mThread = null;
		mThreadRunnable = null;
		mListener = null;
		super.finalize();
	}
}