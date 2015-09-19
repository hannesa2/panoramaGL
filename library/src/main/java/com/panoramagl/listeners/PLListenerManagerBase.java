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

package com.panoramagl.listeners;

import com.panoramagl.PLObjectBase;

import java.util.ArrayList;
import java.util.List;

public class PLListenerManagerBase<T> extends PLObjectBase implements PLIListenerManager<T>
{
	/**member variables*/
	
	private List<T> mListeners;
	
	/**init methods*/
	
	public PLListenerManagerBase()
	{
		super();
	}
	
	@Override
	protected void initializeValues()
	{
		mListeners = new ArrayList<T>(3);
	}
	
	/**property methods*/
	
	protected List<T> getListeners()
	{
		return mListeners;
	}
	
	/**listener methods*/
	
	@Override
	public int length()
	{
		return mListeners.size();
	}
	
	@Override
	public boolean add(T listener)
	{
		if(listener != null)
		{
			synchronized(mListeners)
			{
				mListeners.add(listener);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean insert(T listener, int index)
	{
		if(listener != null && index >= 0 && index <= mListeners.size())
		{
			mListeners.add(index, listener);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean remove(T listener)
	{
		if(listener != null && mListeners.contains(listener))
		{
			synchronized(mListeners)
			{
				mListeners.remove(listener);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeAll()
	{
		if(mListeners.size() > 0)
		{
			synchronized(mListeners)
			{
				mListeners.clear();
				return true;
			}
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mListeners.clear();
		mListeners = null;
		super.finalize();
	}
}