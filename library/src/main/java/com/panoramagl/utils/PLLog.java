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

package com.panoramagl.utils;

import android.util.Log;

public class PLLog
{
	/**constants*/
	
	private static final String kPanoramaGLTag = "PanoramaGL - ";
	
	/**log methods*/
	
	public static void debug(String tag, String msg)
	{
		Log.d(kPanoramaGLTag + tag, msg);
	}
	
	public static void debug(String tag, Throwable e)
	{
		Log.d(kPanoramaGLTag + tag, "", e);
	}
	
	public static void debug(String tag, String format, Object ... args)
	{
		Log.d(kPanoramaGLTag + tag, String.format(format, args));
	}
	
	public static void error(String tag, String msg)
	{
		Log.e(kPanoramaGLTag + tag, msg);
	}
	
	public static void error(String tag, Throwable e)
	{
		Log.e(kPanoramaGLTag + tag, "", e);
	}
	
	public static void error(String tag, String format, Object ... args)
	{
		Log.e(kPanoramaGLTag + tag, String.format(format, args));
	}
}