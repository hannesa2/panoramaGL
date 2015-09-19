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

import java.util.ArrayList;
import java.util.List;

public class PLFileDownloaderManager implements PLIFileDownloaderManager
{
	/**member variables*/
	
	private boolean mIsRunning;
	private List<PLIFileDownloader> mDownloaders;
	private Thread mThread;
	private Runnable mThreadRunnable;
	
	/**init methods*/
	
	public PLFileDownloaderManager()
	{
		super();
		mIsRunning = false;
		mDownloaders = new ArrayList<PLIFileDownloader>();
		mThread = null;
		mThreadRunnable = new Runnable()
		{
			public void run()
			{
				while(isRunning())
				{
					if(getDownloaders().size() > 0)
					{
						try
						{
							getDownloaders().get(0).download();
							getDownloaders().remove(0);
						}
						catch(Throwable e)
						{
						}
					}
					else
						stop();
				}
			}
		};
	}
	
	/**property methods*/
	
	@Override
	public boolean isRunning()
	{
		synchronized(mDownloaders)
		{
			return mIsRunning;
		}
	}
	
	protected void setRunning(boolean isRunning)
	{
		mIsRunning = isRunning;
	}
	
	protected List<PLIFileDownloader> getDownloaders()
	{
	    synchronized(mDownloaders)
	    {
	        return mDownloaders;
	    }
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
	
	/**file downloaders methods*/
	
	@Override
	public void add(PLIFileDownloader fileDownloader)
	{
		if(fileDownloader != null)
		{
			synchronized(mDownloaders)
			{
				mDownloaders.add(fileDownloader);
			}
		}
	}
	
	@Override
	public boolean remove(PLIFileDownloader fileDownloader)
	{
		if(fileDownloader != null && mDownloaders.contains(fileDownloader))
		{
			synchronized(mDownloaders)
			{
				mDownloaders.remove(fileDownloader);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeAll()
	{
		if(mIsRunning)
			return this.stop();
		else if(mDownloaders.size() > 0)
		{
			synchronized(mDownloaders)
			{
				mDownloaders.clear();
				return true;
			}
		}
		return false;
	}
	
	/**control methods*/
	
	@Override
	public void download(PLIFileDownloader fileDownloader)
	{
		if(fileDownloader != null)
		{
			synchronized(mDownloaders)
			{
				mDownloaders.add(fileDownloader);
				this.start();
			}
		}
	}
	
	@Override
	public boolean start()
	{
		if(!mIsRunning)
		{
			synchronized(this)
			{
				mIsRunning = true;
				mThread = new Thread(mThreadRunnable);
				mThread.start();
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
				mThread = null;
				for(int i = 0, downloadersLength = mDownloaders.size(); i < downloadersLength; i++)
				{
					try
					{
						mDownloaders.get(i).stop();
					}
					catch(Throwable e)
					{
					}
				}
				mDownloaders.clear();
				return true;
			}
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mThread = null;
		mThreadRunnable = null;
		mDownloaders = null;
		super.finalize();
	}
}