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

import com.panoramagl.downloaders.ssl.EasySSLSocketFactory;
import com.panoramagl.utils.PLLog;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class PLHTTPFileDownloader extends PLFileDownloaderBase
{
	/**static code*/
	
	static
	{
		Protocol.registerProtocol("https", new Protocol("https", new EasySSLSocketFactory(), 443));
	}
	
	/**init methods*/
	
	public PLHTTPFileDownloader()
	{
		super();
	}
	
	public PLHTTPFileDownloader(String url)
	{
		super(url);
	}
	
	public PLHTTPFileDownloader(String url, PLFileDownloaderListener listener)
	{
		super(url, listener);
	}
	
	/**download methods*/
	
	@Override
	protected byte[] downloadFile()
	{
		this.setRunning(true);
		byte[] result = null;
		InputStream is = null;
		ByteArrayOutputStream bas = null;
		String url = this.getURL();
		PLFileDownloaderListener listener = this.getListener();
		boolean hasListener = (listener != null);
		int responseCode = -1;
		long startTime = System.currentTimeMillis();
		// HttpClient instance
		HttpClient client = new HttpClient();
		// Method instance
        HttpMethod method = new GetMethod(url);
        // Method parameters
        HttpMethodParams methodParams = method.getParams();
        methodParams.setParameter(HttpMethodParams.USER_AGENT, "PanoramaGL Android");
        methodParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        methodParams.setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(this.getMaxAttempts(), false));
		try
		{
			// Execute the method
	        responseCode = client.executeMethod(method);
	        if(responseCode != HttpStatus.SC_OK)
	        	throw new IOException(method.getStatusText());
	        // Get content length
        	Header header = method.getRequestHeader("Content-Length");
        	long contentLength = (header != null ? Long.parseLong(header.getValue()) : 1);
            if(this.isRunning())
            {
            	if(hasListener)
            		listener.didBeginDownload(url, startTime);
            }
            else
            	throw new PLRequestInvalidatedException(url);
            // Get response body as stream
        	is = method.getResponseBodyAsStream();
            bas = new ByteArrayOutputStream();
            byte[] buffer = new byte[256];
            int length = 0, total = 0;
            // Read stream
            while((length = is.read(buffer)) != -1)
            {
            	if(this.isRunning())
            	{
            		bas.write(buffer, 0, length);
                	total += length;
            		if(hasListener)
	            		listener.didProgressDownload(url, (int)(((float)total / (float)contentLength) * 100.0f));
            	}
            	else
            		throw new PLRequestInvalidatedException(url);
            }
            if(total == 0)
            	throw new IOException("Request data has invalid size (0)");
            // Get data
            if(this.isRunning())
            {
            	result = bas.toByteArray();
            	if(hasListener)
            		listener.didEndDownload(url, result, System.currentTimeMillis() - startTime);
            }
            else
            	throw new PLRequestInvalidatedException(url);
    	}
        catch(Throwable e)
		{
        	if(this.isRunning())
			{
        		PLLog.error("PLHTTPFileDownloader::downloadFile", e);
        		if(hasListener)
        			listener.didErrorDownload(url, e.toString(), responseCode, result);
			}
		}
		finally
		{
			if(bas != null)
	    	{
	    		try
	    		{
					bas.close();
				}
	    		catch(IOException e)
	    		{
	    			PLLog.error("PLHTTPFileDownloader::downloadFile", e);
				}
	    	}
	    	if(is != null)
			{
				try
				{
					is.close();
				}
				catch(IOException e)
				{
					PLLog.error("PLHTTPFileDownloader::downloadFile", e);
				}
			}
			// Release the connection
			method.releaseConnection();
		}
    	this.setRunning(false);
    	return result;
	}
}