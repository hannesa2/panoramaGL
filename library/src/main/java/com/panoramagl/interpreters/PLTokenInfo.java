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

package com.panoramagl.interpreters;

import com.panoramagl.PLObjectBase;

import java.util.ArrayList;
import java.util.List;

public class PLTokenInfo extends PLObjectBase implements PLITokenInfo
{
	/**member variables*/
	
	private String mName;
	private List<Object> mValues;
	
	/**init methods*/
	
	public PLTokenInfo()
	{
		super();
	}
	
	public PLTokenInfo(String name)
	{
		super();
		mName = name;
	}
	
	public PLTokenInfo(String name, List<Object> values)
	{
		super();
		mName = name;
		mValues.addAll(values);
	}
	
	@Override
	protected void initializeValues()
	{
		mName = null;
		mValues = new ArrayList<Object>(5);
	}
	
	/**property methods*/
	
	@Override
	public String getName()
	{
		return mName;
	}
	
	@Override
	public void setName(String name)
	{
		mName = name;
	}
	
	@Override
	public List<Object> getValues()
	{
		return mValues;
	}
	
	@Override
	public void setValues(List<Object> values)
	{
		if(values != null)
		{
			mValues.clear();
			mValues.addAll(values);
		}
	}
	
	/**value methods*/
	
	@Override
	public boolean hasValue(int index)
	{
		return (index >= 0 && index < mValues.size());
	}
	
	@Override
	public Object getValue(int index)
	{
		return mValues.get(index);
	}
	
	@Override
	public String getString(int index)
	{
		return mValues.get(index).toString();
	}
	
	@Override
	public boolean getBoolean(int index)
	{
		return Boolean.parseBoolean(mValues.get(index).toString());
	}
	
	@Override
	public int getInt(int index)
	{
		return Integer.parseInt(mValues.get(index).toString());
	}
	
	@Override
	public float getFloat(int index)
	{
		return Float.parseFloat(mValues.get(index).toString());
	}
	
	@Override
	public double getDouble(int index)
	{
		return Double.parseDouble(mValues.get(index).toString());
	}
	
	@Override
	public PLITokenInfo getTokenInfo(int index)
	{
		return (PLITokenInfo)mValues.get(index);
	}
	
	@Override
	public int valuesLength()
	{
		return mValues.size();
	}
	
	@Override
	public boolean addValue(Object value)
	{
		if(value != null)
		{
			mValues.add(value);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean insertValue(Object value, int index)
	{
		if(value != null && index >= 0 && index <= mValues.size())
		{
			mValues.add(index, value);
			return true;
		}
		return false;
	}
	
	@Override
	public boolean removeValue(Object value)
	{
		if(value != null && mValues.contains(value))
		{
			mValues.remove(value);
			return true;
		}
		return false;
	}
	
	@Override
	public Object removeValueAtIndex(int index)
	{
		if(index >= 0 && index < mValues.size())
			return mValues.remove(index);
		return null;
	}
	
	@Override
	public boolean removeAllValues()
	{
		if(mValues.size() > 0)
		{
			mValues.clear();
			return true;
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mName = null;
		mValues = null;
		super.finalize();
	}
}