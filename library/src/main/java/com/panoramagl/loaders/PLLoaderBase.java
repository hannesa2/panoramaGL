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

package com.panoramagl.loaders;

import com.panoramagl.PLObjectBase;

public abstract class PLLoaderBase extends PLObjectBase implements PLILoader
{
	/**member variables*/
	
	private PLLoaderListener mInternalListener, mListener;
	
	/**init methods**/
	
	public PLLoaderBase()
	{
		super();
	}
	
	@Override
	protected void initializeValues()
	{
		mInternalListener = mListener = null;
	}
	
	/**property methods*/
	
	@Override
	public PLLoaderListener getInternalListener()
	{
		return mInternalListener;
	}
	
	@Override
	public void setInternalListener(PLLoaderListener listener)
	{
		mInternalListener = listener;
	}
	
	@Override
	public PLLoaderListener getListener()
	{
		return mListener;
	}
	
	@Override
	public void setListener(PLLoaderListener listener)
	{
		mListener = listener;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mInternalListener = mListener = null;
		super.finalize();
	}
}