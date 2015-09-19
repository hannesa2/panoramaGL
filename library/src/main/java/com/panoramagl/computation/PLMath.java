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

package com.panoramagl.computation;


import com.panoramagl.PLConstants;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLRange;

public class PLMath extends Object 
{
	/**distance methods*/
	
	public static float distanceBetweenPoints(CGPoint point1, CGPoint point2)
	{
		return (float)Math.sqrt(((point2.x - point1.x) * (point2.x - point1.x)) + ((point2.y - point1.y) * (point2.y - point1.y)));
	}
	
	public static float distanceBetweenPoints(float x1, float y1, float x2, float y2)
	{
		return (float)Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
	}
	
	/**range methods*/
	
	public static float valueInRange(float value, PLRange range)
	{
		return valueInRange(value, range.min, range.max); 
	}
	
	public static float valueInRange(float value, float min, float max)
	{
		return Math.max(min, Math.min(value, max));
	}
	
	/**normalize methods*/
	
	public static float normalizeAngle(float angle, PLRange range)
	{
		return normalizeAngle(angle, range.min, range.max);
	}
	
	public static float normalizeAngle(float angle, float min, float max)
	{
		float result = angle;
	    if(min < 0.0f)
		{
	        while(result <= -180.0f) result += 360.0f;
	        while(result > 180.0f) result -= 360.0f;
	    } 
		else 
		{
	        while(result < 0.0f) result += 360.0f;
	        while(result >= 360.0f) result -= 360.0f;
	    }
		return PLMath.valueInRange(result, min, max);
	}
	
	public static float normalizeFov(float fov, PLRange range)
	{
		return PLMath.valueInRange(fov, range);
	}
	
	public static float normalizeFov(float fov, float min, float max)
	{
		return PLMath.valueInRange(fov, min, max);
	}
	
	/**pow methods*/
	
	public static boolean isPowerOfTwo(int value)
	{
		while((value & 1) == 0)
			value = value >> 1;
		return (value == 1);
	}
	
	/**conversion methods*/
	
	public static void convertFromSphericalToCartesian(float radius, float pitch, float yaw, PLPosition result)
	{
		convertFromSphericalToCartesian(radius, pitch, yaw, 90.0f, 180.0f, result);
	}
	
	public static void convertFromSphericalToCartesian(float radius, float pitch, float yaw, float picthOffset, float yawOffset, PLPosition result)
	{
		if(result != null)
		{
			float pr = (pitch + picthOffset) * PLConstants.kToRadians, yr = (yaw + yawOffset) * PLConstants.kToRadians;
			result.setValues((radius * (float)Math.sin(pr) * (float)Math.cos(yr)), (radius * (float)Math.sin(pr) * (float)Math.sin(yr)), (radius * (float)Math.cos(pr)));
		}
	}
}