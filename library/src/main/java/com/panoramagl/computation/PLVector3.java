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

/*
 * This functions are a port from C++ to Java of 
 * "Demonstration of a line mesh intersection test (Sample1-Mesh_Line_Intersection.zip)" 
 * example by Jonathan Kreuzer http://www.3dkingdoms.com/weekly.
 * See checkLineBoxWithRay.h and checkLineBoxWithRay.cpp.
 */

package com.panoramagl.computation;


import com.panoramagl.structs.PLIStruct;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLVertex;

public class PLVector3 implements PLIStruct<PLVector3>
{
	/**member variables*/
	
	public float x;
	public float y;
	public float z;
	
	/**init methods*/
	
	public PLVector3()
	{
		super();
		x = y = z = 0.0f;
	}
	
	public PLVector3(PLVertex vertex)
	{
		this(vertex.x, vertex.y, vertex.z);
	}
	
	public PLVector3(PLPosition position)
	{
		this(position.x, position.y, position.z);
	}
	
	public PLVector3(PLVector3 vector)
	{
		this(vector.x, vector.y, vector.z);
	}
	
	public PLVector3(float xValue, float yValue, float zValue)
	{
		super();
		x = xValue;
		y = yValue;
		z = zValue;
	}
	
	public static PLVector3 vector3()
	{
		return new PLVector3();
	}
	
	public static PLVector3 vector3(PLVertex vertex)
	{
		return new PLVector3(vertex);
	}
	
	public static PLVector3 vector3(PLPosition position)
	{
		return new PLVector3(position);
	}
	
	public static PLVector3 vector3(PLVector3 vector3)
	{
		return new PLVector3(vector3);
	}
	
	public static PLVector3 vector3(float x, float y, float z)
	{
		return new PLVector3(x, y, z);
	}
	
	/**property methods*/
	
	public PLPosition getPosition(PLPosition position)
	{
		return position.setValues(x, y, z);
	}
	
	/**reset methods*/
	
	@Override
	public boolean isResetted()
	{
		return (x == 0.0f && y == 0.0f && z == 0.0f);
	}
	
	@Override
	public PLVector3 reset()
	{
		x = y = z = 0.0f;
		return this;
	}
	
	/**set methods*/
	
	@Override
	public PLVector3 setValues(PLVector3 vector)
	{
		x = vector.x;
		y = vector.y;
		z = vector.z;
		return this;
	}
	
	public PLVector3 setValues(float[] values)
	{
		x = values[0];
		y = values[1];
		z = values[2];
		return this;
	}
	
	public PLVector3 setValues(float xValue, float yValue, float zValue)
	{
		x = xValue;
		y = yValue;
		z = zValue;
		return this;
	}
	
	/**vector methods*/
	
	public boolean equals(PLVector3 value)
	{
		return (x == value.x && y == value.y && z == value.z);
	}
	
	public PLVector3 add(PLVector3 value)
	{
		return new PLVector3(x + value.x, y + value.y, z + value.z);
	}
	
	public PLVector3 add(PLVector3 value, boolean newVector3)
	{
		if(newVector3)
			return this.add(value);
		x += value.x;
		y += value.y;
		z += value.z;
		return this;
	}
	
	public PLVector3 sub(PLVector3 value)
	{
		return new PLVector3(x - value.x, y - value.y, z - value.z);
	}
	
	public PLVector3 sub(PLVector3 value, boolean newVector3)
	{
		if(newVector3)
			return this.sub(value);
		x -= value.x;
		y -= value.y;
		z -= value.z;
		return this;
	}
	
	public PLVector3 minus()
	{
		return new PLVector3(-x, -y, -z);
	}
	
	public PLVector3 minus(boolean newVector3)
	{
		if(newVector3)
			return this.minus();
		x = -x;
		y = -y;
		z = -z;
		return this;
	}
	
	public PLVector3 div(PLVector3 value)
	{
		return new PLVector3(x / value.x, y / value.y, z / value.z);
	}
	
	public PLVector3 div(PLVector3 value, boolean newVector3)
	{
		if(newVector3)
			return this.div(value);
		x /= value.x;
		y /= value.y;
		z /= value.z;
		return this;
	}
	
	public PLVector3 divf(float value)
	{
		float invert = (1.0f / value);
		return new PLVector3(x * invert, y * invert, z * invert);
	}
	
	public PLVector3 divf(float value, boolean newVector3)
	{
		if(newVector3)
			return this.divf(value);
		float invert = (1.0f / value);
		x *= invert;
		y *= invert;
		z *= invert;
		return this;
	}
	
	public PLVector3 mult(PLVector3 value)
	{
		return new PLVector3(x * value.x, y * value.y, z * value.z);
	}
	
	public PLVector3 mult(PLVector3 value, boolean newVector3)
	{
		if(newVector3)
			return this.mult(value);
		x *= value.x;
		y *= value.y;
		z *= value.z;
		return this;
	}
	
	public PLVector3 multf(float value)
	{
		return new PLVector3(x * value, y * value, z * value);
	}
	
	public PLVector3 multf(float value, boolean newVector3)
	{
		if(newVector3)
			return this.multf(value);
		x *= value;
		y *= value;
		z *= value;
		return this;
	}
	
	public float dot(PLVector3 value)
	{
		return (x * value.x + y * value.y + z * value.z);
	}
	
	public PLVector3 crossProduct(PLVector3 value)
	{
		return new PLVector3
		(
			y * value.z - z * value.y,
			z * value.x - x * value.z,
			x * value.y - y * value.x
		);
	}
	
	public PLVector3 crossProduct(PLVector3 value, boolean newVector3)
	{
		if(newVector3)
			return this.crossProduct(value);
		float tempX = y * value.z - z * value.y;
		float tempY = z * value.x - x * value.z;
		z = x * value.y - y * value.x;
		x = tempX;
		y = tempY;
		return this;
	}
	
	public float magnitude()
	{
		return (float)Math.sqrt(x * x + y * y + z * z);
	}
	
	public float distance(PLVector3 value)
	{
		float tempX = x - value.x, tempY = y - value.y, tempZ = z - value.z;
		return (float)Math.sqrt(tempX * tempX + tempY * tempY + tempZ * tempZ);
	}
	
	public void normalize()
	{
		float magnitude = (x * x + y * y + z * z);
		if (magnitude == 0)
			return;
		float mult = (1.0f / (float)Math.sqrt(magnitude));
		x *= mult;
		y *= mult;
		z *= mult;
	}
	
	/**clone methods*/
	
	@Override
	public PLVector3 clone()
	{
		return new PLVector3(x, y, z);
	}
	
	/**native methods*/
	
	@Override
	public boolean equals(Object o)
	{
		if(o != null)
		{
			if(this == o)
				return true;
			if(o instanceof PLVector3)
				return this.equals((PLVector3)o);
		}
		return false;
	}
}