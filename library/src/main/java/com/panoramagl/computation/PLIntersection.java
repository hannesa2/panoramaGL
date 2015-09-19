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

public class PLIntersection
{
	/**static variables*/
	
	private static final PLVector3 sAuxVector3 = new PLVector3();
	
	/**internal check methods*/
	
	private static boolean getIntersection(float distance1, float distance2, PLVector3[] ray, PLVector3[] hitPoint)
	{
		if(distance1 * distance2 >= 0.0f || distance1 == distance2)
			return false;
		PLVector3 intersect = sAuxVector3.setValues(ray[1]).sub(ray[0], false).multf(-distance1/(distance2-distance1), false).add(ray[0], false);
		if(hitPoint[0] != null)
			hitPoint[0].setValues(intersect);
		else
			hitPoint[0] = intersect.clone();
		return true;
	}
	
	private static boolean inBox(PLVector3[] hitPoint, PLVector3 startBound, PLVector3 endBound, int axis) 
	{
		if(axis == 1 && hitPoint[0].z > startBound.z && hitPoint[0].z < endBound.z && hitPoint[0].y > startBound.y && hitPoint[0].y < endBound.y) return true;
		if(axis == 2 && hitPoint[0].z > startBound.z && hitPoint[0].z < endBound.z && hitPoint[0].x > startBound.x && hitPoint[0].x < endBound.x) return true;
		if(axis == 3 && hitPoint[0].x > startBound.x && hitPoint[0].x < endBound.x && hitPoint[0].y > startBound.y && hitPoint[0].y < endBound.y) return true;
		return false;
	}
	
	private static boolean evalSideIntersection(float distance1, float distance2, PLVector3[] ray, PLVector3[] hitPoint, PLVector3 startBound, PLVector3 endBound, int axis)
	{	
		if(PLIntersection.getIntersection(distance1, distance2, ray, hitPoint)) 
		{
			if(!PLIntersection.inBox(hitPoint, startBound, endBound, axis))
				return false;
			return true;
		}
		return false;
	}
	
	/**check methods*/
	
	public static boolean checkLineBox(PLVector3[] ray, PLVector3 startBound, PLVector3 endBound, PLVector3[] hitPoint)
	{
		//Check for a quick exit if ray is completely to one side of the box
		if(
		   (ray[1].x < startBound.x && ray[0].x < startBound.x) ||
		   (ray[1].x > endBound.x   && ray[0].x > endBound.x  ) ||
		   (ray[1].y < startBound.y && ray[0].y < startBound.y) ||
		   (ray[1].y > endBound.y   && ray[0].y > endBound.y  ) ||
		   (ray[1].z < startBound.z && ray[0].z < startBound.z) ||
		   (ray[1].z > endBound.z   && ray[0].z > endBound.z  )
		)
			return false;
		
		//Check if ray originates in the box
		if(ray[0].x > startBound.x && ray[0].x < endBound.x && ray[0].y > startBound.y && ray[0].y < endBound.y && ray[0].z > startBound.z && ray[0].z < endBound.z)
		{
			if(hitPoint[0] != null)
				hitPoint[0].setValues(ray[0]);
			else
				hitPoint[0] = ray[0].clone();
			return true;
		}
		
		//Check for a ray intersection with each side of the box
		if(
		   (PLIntersection.evalSideIntersection(ray[0].x-startBound.x, ray[1].x-startBound.x, ray, hitPoint, startBound, endBound, 1)) ||
		   (PLIntersection.evalSideIntersection(ray[0].y-startBound.y, ray[1].y-startBound.y, ray, hitPoint, startBound, endBound, 2)) ||
		   (PLIntersection.evalSideIntersection(ray[0].z-startBound.z, ray[1].z-startBound.z, ray, hitPoint, startBound, endBound, 3)) ||
		   (PLIntersection.evalSideIntersection(ray[0].x-endBound.x  , ray[1].x-endBound.x  , ray, hitPoint, startBound, endBound, 1)) ||
		   (PLIntersection.evalSideIntersection(ray[0].y-endBound.y  , ray[1].y-endBound.y  , ray, hitPoint, startBound, endBound, 2)) ||
		   (PLIntersection.evalSideIntersection(ray[0].z-endBound.z  , ray[1].z-endBound.z  , ray, hitPoint, startBound, endBound, 3)) 
		)
			return true;
		return false;
	}
	
	public static boolean checkLineBox(PLVector3[] ray, PLVector3 point1, PLVector3 point2, PLVector3 point3, PLVector3 point4, PLVector3[] hitPoint)
	{
		if(
			checkLineBox(ray, point1, point4, hitPoint) ||
			checkLineBox(ray, point4, point1, hitPoint) ||
			checkLineBox(ray, point2, point3, hitPoint) ||
			checkLineBox(ray, point3, point2, hitPoint) ||
			checkLineBox(ray, point1, point3, hitPoint) ||
			checkLineBox(ray, point3, point1, hitPoint) ||
			checkLineBox(ray, point1, point2, hitPoint) ||
			checkLineBox(ray, point2, point1, hitPoint)
		)
			return true;
		return false;
	}
	
	public static boolean checkLineTriangle(PLVector3[] ray, PLVector3 firstVertex, PLVector3 secondVertex, PLVector3 thirdVertex, PLVector3[] hitPoint)
	{		
		//Calculate triangle normal
		PLVector3 normal = secondVertex.sub(firstVertex).crossProduct(thirdVertex.sub(firstVertex));
		normal.normalize();
		
		//Find distance from ray to the plane defined by the triangle
		float distance1 = ray[0].sub(firstVertex).dot(normal);
		float distance2 = ray[1].sub(firstVertex).dot(normal);
		
		if((distance1 * distance2 >= 0.0f) ||	//Ray doesn't cross the triangle.
		   (distance1 == distance2))			//Ray and plane are parallel.
			return false;
		
		//Find point on the ray that intersects with the plane
		PLVector3 intersect = ray[0].add(ray[1].sub(ray[0]).multf(-distance1/(distance2-distance1)));
		
		//Find if the intersection point lies inside the triangle by testing it against all edges
		if(normal.crossProduct(secondVertex.sub(firstVertex)).dot(intersect.sub(firstVertex)) < 0.0f)
			return false;
		
		if(normal.crossProduct(thirdVertex.sub(secondVertex)).dot(intersect.sub(secondVertex)) < 0.0f)
			return false;
		
		if(normal.crossProduct(firstVertex.sub(thirdVertex)).dot(intersect.sub(firstVertex)) < 0.0f)
			return false;
		
		if(hitPoint[0] != null)
			hitPoint[0].setValues(intersect);
		else
			hitPoint[0] = intersect;
		return true;
	}
}