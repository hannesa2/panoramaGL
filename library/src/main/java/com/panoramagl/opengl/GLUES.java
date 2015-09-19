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

package com.panoramagl.opengl;

import java.lang.reflect.Method;

import javax.microedition.khronos.opengles.GL10;

public class GLUES extends GLUconstants
{
	/**load native library*/
	
	static
	{
	    System.loadLibrary("glues");
	}
	
	/**quadric methods*/
	
	public static GLUquadric gluNewQuadric()
	{
		return new GLUquadric(GLU_SMOOTH, GL10.GL_FALSE, GLU_OUTSIDE, GLU_FILL, null);
	}
	
	public static void gluDeleteQuadric(GLUquadric state)
	{
		state = null;
	}
	
	public static void gluQuadricError(GLUquadric qobj, int which)
	{
		if(qobj != null && qobj.errorCallback != null)
		{
			try 
			{
				qobj.errorCallback.invoke(qobj, which);
			}
			catch(Throwable e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public static void gluQuadricCallback(GLUquadric qobj, int which, Method fn)
	{
	   switch (which)
	   {
	      case GLU_ERROR:
	    	  qobj.errorCallback = fn;
	    	  break;
	      default:
	    	  gluQuadricError(qobj, GLU_INVALID_ENUM);
	    	  return;
	   }
	}
	
	public static void gluQuadricNormals(GLUquadric qobj, int normals)
	{
		switch (normals)
		   {
		      case GLU_SMOOTH:
		      case GLU_FLAT:
		      case GLU_NONE:
		           break;
		      default:
		           gluQuadricError(qobj, GLU_INVALID_ENUM);
		           return;
		   }
		   qobj.normals = normals;
	}
	
	public static void gluQuadricTexture(GLUquadric qobj, boolean textureCoords)
	{
		qobj.textureCoords = (textureCoords ? GL10.GL_TRUE : GL10.GL_FALSE);
	}
	
	public static void gluQuadricTexture(GLUquadric qobj, int textureCoords)
	{
		qobj.textureCoords = textureCoords;
	}
	
	public static void gluQuadricOrientation(GLUquadric qobj, int orientation)
	{
	   switch(orientation)
	   {
	      case GLU_OUTSIDE:
	      case GLU_INSIDE:
	           break;
	      default:
	           gluQuadricError(qobj, GLU_INVALID_ENUM);
	           return;
	   }
	   qobj.orientation = orientation;
	}
	
	public static void gluQuadricDrawStyle(GLUquadric qobj, int drawStyle)
	{
	   switch(drawStyle)
	   {
	      case GLU_POINT:
	      case GLU_LINE:
	      case GLU_FILL:
	      case GLU_SILHOUETTE:
	           break;
	      default:
	           gluQuadricError(qobj, GLU_INVALID_ENUM);
	           return;
	   }
	   qobj.drawStyle = drawStyle;
	}
	
	/**glu methods*/
	
	public static void gluCylinder(GL10 gl, GLUquadric qobj, float baseRadius, float topRadius, float height, int slices, int stacks)
	{
		gluCylinderAndroid(gl, qobj, baseRadius, topRadius, height, slices, stacks, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	public static void gluDisk(GL10 gl, GLUquadric qobj, float innerRadius, float outerRadius, int slices, int loops)
	{
		gluDiskAndroid(gl, qobj, innerRadius, outerRadius, slices, loops, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	public static void gluPartialDisk(GL10 gl, GLUquadric qobj, float innerRadius, float outerRadius, int slices, int loops, float startAngle, float sweepAngle)
	{
		gluPartialDiskAndroid(gl, qobj, innerRadius, outerRadius, slices, loops, startAngle, sweepAngle, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	public static void gluSphere(GL10 gl, GLUquadric qobj, float radius, int slices, int stacks)
	{
		gluSphereAndroid(gl, qobj, radius, slices, stacks, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	public static void gluHemisphere(GL10 gl, GLUquadric qobj, boolean positive, float radius, int slices, int stacks)
	{
		gluHemisphereAndroid(gl, qobj, positive, radius, slices, stacks, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	public static void glu3DArc(GL10 gl, GLUquadric qobj, float angleWidth, float offsetAngle, boolean positive, float radius, int slices, int stacks)
	{
		glu3DArcAndroid(gl, qobj, angleWidth, offsetAngle, positive, radius, slices, stacks, qobj.normals, qobj.textureCoords, qobj.orientation, qobj.drawStyle, qobj.errorCallback != null);
	}
	
	/**glu jni methods*/
	
	private static native void gluCylinderAndroid(GL10 gl, GLUquadric qobj, float baseRadius, float topRadius, float height, int slices, int stacks, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	private static native void gluDiskAndroid(GL10 gl, GLUquadric qobj, float innerRadius, float outerRadius, int slices, int loops, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	private static native void gluPartialDiskAndroid(GL10 gl, GLUquadric qobj, float innerRadius, float outerRadius, int slices, int loops, float startAngle, float sweepAngle, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	private static native void gluSphereAndroid(GL10 gl, GLUquadric qobj, float radius, int slices, int stacks, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	private static native void gluHemisphereAndroid(GL10 gl, GLUquadric qobj, boolean positive, float radius, int slices, int stacks, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	private static native void glu3DArcAndroid(GL10 gl, GLUquadric qobj, float angleWidth, float offsetAngle, boolean positive, float radius, int slices, int stacks, int qnormals, int qtextureCoords, int qorientation, int qdrawStyle, boolean qhasCallback);
	
	/**project methods*/
	
	private static final float PI_OVER_180 = 0.017453292519943295769236907684886f;
	
	//public static native void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar);
	public static void gluPerspective(GL10 gl, float fovy, float aspect, float zNear, float zFar)
	{	
		float halfHeight = zNear * (float)Math.tan(fovy * 0.5f * PI_OVER_180);
		float halfWidth = halfHeight * aspect;
	    gl.glFrustumf(-halfWidth, halfWidth, -halfHeight, halfHeight, zNear, zFar);
	}
	
	private static final float[] gluUnProjectData = new float[40];
	private static final int offsetModel = 	0;
	private static final int offsetA = 		16;
	private static final int offsetIn = 	32;
	private static final int offsetOut = 	36;
	
	public static int gluUnProject(float winx, float winy, float winz,
	                float model[], int offsetM,
	                float proj[], int offsetP,
	                int viewport[], int offsetV,
	                float[] xyz, int offset)
	{
		//return GLU.gluUnProject(winx, winy, winz, model, offsetM, proj, offsetP, viewport, offsetV, xyz, offset);
		/*Transformation matrices*/
		//float[] m = new float[16], A = new float[16];
		//float[] in = new float[4], out = new float[4];

		/*Normalize between -1 and 1*/
		gluUnProjectData[offsetIn]   = (winx - viewport[offsetV]) * 2.0f / viewport[offsetV+2] - 1.0f;
		gluUnProjectData[offsetIn+1] = (winy - viewport[offsetV+1]) * 2.0f / viewport[offsetV+3] - 1.0f;
		gluUnProjectData[offsetIn+2] = 2.0f * winz - 1.0f;
		gluUnProjectData[offsetIn+3] = 1.0f;

		android.opengl.Matrix.multiplyMM(gluUnProjectData, offsetA, proj, offsetP, model, offsetM);
		com.panoramagl.opengl.matrix.Matrix.invertM(gluUnProjectData, offsetModel, gluUnProjectData, offsetA);

		android.opengl.Matrix.multiplyMV(gluUnProjectData, offsetOut, gluUnProjectData, offsetModel, gluUnProjectData, offsetIn);
		if(gluUnProjectData[offsetOut+3] == 0.0)
			return GL10.GL_FALSE;
		
		xyz[offset]   = gluUnProjectData[offsetOut  ] / gluUnProjectData[offsetOut+3];
		xyz[offset+1] = gluUnProjectData[offsetOut+1] / gluUnProjectData[offsetOut+3];
		xyz[offset+2] = gluUnProjectData[offsetOut+2] / gluUnProjectData[offsetOut+3];
		return GL10.GL_TRUE;
	}
}