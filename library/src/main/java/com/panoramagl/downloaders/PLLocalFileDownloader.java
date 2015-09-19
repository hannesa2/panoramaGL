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

import android.content.Context;

import com.panoramagl.utils.PLLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PLLocalFileDownloader extends PLFileDownloaderBase
{
	/**member variables*/
	
	private Context mContext;
	
	/**init methods*/
	
	public PLLocalFileDownloader(Context context)
	{
		this(context, null, null);
	}
	
	public PLLocalFileDownloader(Context context, String url)
	{
		this(context, url, null);
	}
	
	public PLLocalFileDownloader(Context context, String url, PLFileDownloaderListener listener)
	{
		super(url, listener);
		mContext = context;
	}
	
	/**property methods*/
	
	protected Context getContext()
	{
		return mContext;
	}
	
	protected void setContext(Context context)
	{
		mContext = context;
	}
	
	/**download methods*/
	
	@Override
	protected byte[] downloadFile()
	{
		this.setRunning(true);
		byte[] result = null;
		InputStream is = null;
		String url = this.getURL();
		PLFileDownloaderListener listener = this.getListener();
		boolean hasListener = (listener != null);
		long startTime = System.currentTimeMillis();
		try
		{
            if(this.isRunning())
            {
            	if(hasListener)
            		listener.didBeginDownload(url, startTime);
            	if(url.startsWith("res://"))
    			{
    				int sepPos = url.lastIndexOf("/");
    				int resourceId = mContext.getResources().getIdentifier(url.substring(sepPos + 1), url.substring(6, sepPos), mContext.getPackageName());
    				is = mContext.getResources().openRawResource(resourceId);
    			}
    			else if(url.startsWith("file://"))
    			{
    				File file = new File(url.substring(7));
    				if(file.canRead())
    					is = new FileInputStream(file);
    			}
            }
            else
            	throw new PLRequestInvalidatedException(url);
			if(this.isRunning())
			{
				result = new byte[is.available()];
				is.read(result);
				if(hasListener)
				{
					listener.didProgressDownload(url, 100);
	            	listener.didEndDownload(url, result, System.currentTimeMillis() - startTime);
				}
			}
			else
				throw new PLRequestInvalidatedException(url);
    	}
        catch(Throwable e)
		{
        	if(this.isRunning())
        	{
        		PLLog.error("PLLocalFileDownloader::downloadFile", e);
    			if(hasListener)
    				listener.didErrorDownload(url, e.toString(), -1, result);
        	}
		}
		finally
		{
			if(is != null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
					PLLog.error("PLLocalFileDownloader::downloadFile", e);
				}
			}
		}
    	this.setRunning(false);
    	return result;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mContext = null;
		super.finalize();
	}
}