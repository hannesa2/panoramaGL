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

package com.panoramagl.structs;

public class PLVertex implements PLIStruct<PLVertex>
{
	/**member variables*/
	
	public float x, y, z;
	
	/**init methods*/
	
	public PLVertex()
	{
		this(0.0f, 0.0f, 0.0f);
	}
	
	public PLVertex(PLVertex vertex)
	{
		this(vertex.x, vertex.y, vertex.z);
	}
	
	public PLVertex(float x, float y, float z)
	{
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static PLVertex PLVertexMake()
	{
		return new PLVertex();
	}
	
	public static PLVertex PLVertexMake(PLVertex vertex)
	{
		return new PLVertex(vertex);
	}
	
	public static PLVertex PLVertexMake(float x, float y, float z)
	{
		return new PLVertex(x, y, z);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0.0f && y == 0.0f && z == 0.0f);
	}
	
	@Override
	public PLVertex reset()
	{
		x = y = z = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLVertex setValues(PLVertex vertex)
	{
		x = vertex.x;
		y = vertex.y;
		z = vertex.z;
		return this;
	}
	
	public PLVertex setValues(float x, float y, float z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	/**clone methods*/
	
	@Override
	public PLVertex clone()
	{
		return new PLVertex(x, y, z);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null && o instanceof PLVertex)
		{
			if(this == o)
				return true;
			PLVertex vertex = (PLVertex)o;
			return (x == vertex.x && y == vertex.y && z == vertex.z);
		}
		return false;
	}
}