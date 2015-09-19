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

import com.panoramagl.enumerations.PLTokenType;

public class PLToken implements PLIToken
{
	/**member variables*/
	
	private PLTokenType mType;
	private String mSequence;
	
	/**init methods*/
	
	public PLToken(PLTokenType type, String sequence)
	{
		super();
		mType = type;
		mSequence = sequence;
	}
	
	/**property methods*/
	
	@Override
	public PLTokenType getType()
	{
		return mType;
	}
	
	protected void setType(PLTokenType type)
	{
		mType = type;
	}
	
	@Override
	public String getSequence()
	{
		return mSequence;
	}
	
	protected void setSequence(String sequence)
	{
		mSequence = sequence;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mType = null;
		mSequence = null;
		super.finalize();
	}
}