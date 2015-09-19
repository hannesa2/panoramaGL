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

import android.opengl.GLSurfaceView;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL10Ext;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public class GLWrapper implements IGLWrapper, GL11ExtensionPack
{
	/**member variables*/
	
	private GL10 mGL;
    private GL10Ext mGL10Ext;
    private GL11 mGL11;
    private GL11Ext mGL11Ext;
    private GL11ExtensionPack mGL11ExtPack;
    private GLSurfaceView mGLSurfaceView;
	
	/**init methods*/
	
	public GLWrapper(GL gl, GLSurfaceView glSurfaceView)
	{
		mGL = (GL10)gl;
		if(gl instanceof GL10Ext)
		{
            mGL10Ext = (GL10Ext)gl;
        }
        if(gl instanceof GL11)
        {
            mGL11 = (GL11)gl;
        }
        if(gl instanceof GL11Ext)
        {
            mGL11Ext = (GL11Ext)gl;
        }
        if(gl instanceof GL11ExtensionPack)
        {
        	mGL11ExtPack = (GL11ExtensionPack)gl;
        }
        mGLSurfaceView = glSurfaceView;
	}
	
	/**property methods*/
	
	@Override
	public GLSurfaceView getGLSurfaceView()
	{
		return mGLSurfaceView;
	}
	
	/**GL10 methods*/
	
	@Override
	public void glActiveTexture(int texture)
	{
		mGL.glActiveTexture(texture);
	}
	
	@Override
	public void glAlphaFunc(int func, float ref)
	{
		mGL.glAlphaFunc(func, ref);
	}
	
	@Override
	public void glAlphaFuncx(int func, int ref)
	{
		mGL.glAlphaFuncx(func, ref);
	}
	
	@Override
	public void glBindTexture(int target, int texture)
	{
		mGL.glBindTexture(target, texture);
	}
	
	@Override
	public void glBlendFunc(int sfactor, int dfactor)
	{
		mGL.glBlendFunc(sfactor, dfactor);
	}
	
	@Override
	public void glClear(int mask)
	{
		mGL.glClear(mask);
	}
	
	@Override
	public void glClearColor(float red, float green, float blue, float alpha)
	{
		mGL.glClearColor(red, green, blue, alpha);
	}
	
	@Override
	public void glClearColorx(int red, int green, int blue, int alpha)
	{
		mGL.glClearColorx(red, green, blue, alpha);
	}
	
	@Override
	public void glClearDepthf(float depth)
	{
		mGL.glClearDepthf(depth);
	}
	
	@Override
	public void glClearDepthx(int depth)
	{
		mGL.glClearDepthx(depth);
	}
	
	@Override
	public void glClearStencil(int s)
	{
		mGL.glClearStencil(s);
	}
	
	@Override
	public void glClientActiveTexture(int texture)
	{
		mGL.glClientActiveTexture(texture);
	}
	
	@Override
	public void glColor4f(float red, float green, float blue, float alpha)
	{
		mGL.glColor4f(red, green, blue, alpha);
	}
	
	@Override
	public void glColor4x(int red, int green, int blue, int alpha)
	{
		mGL.glColor4x(red, green, blue, alpha);
	}
	
	@Override
	public void glColorMask(boolean red, boolean green, boolean blue, boolean alpha)
	{
		mGL.glColorMask(red, green, blue, alpha);
	}
	
	@Override
	public void glColorPointer(int size, int type, int stride, Buffer pointer)
	{
		mGL.glColorPointer(size, type, stride, pointer);
	}
	
	@Override
	public void glCompressedTexImage2D(int target, int level,
			int internalformat, int width, int height, int border,
			int imageSize, Buffer data)
	{
		mGL.glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
	}
	
	@Override
	public void glCompressedTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int imageSize,
			Buffer data)
	{
		mGL.glCompressedTexSubImage2D(target, level, xoffset, yoffset, width, height, format, imageSize, data);
	}
	
	@Override
	public void glCopyTexImage2D(int target, int level, int internalformat,
			int x, int y, int width, int height, int border)
	{
		mGL.glCopyTexImage2D(target, level, internalformat, x, y, width, height, border);
	}
	
	@Override
	public void glCopyTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int x, int y, int width, int height)
	{
		mGL.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}
	
	@Override
	public void glCullFace(int mode)
	{
		mGL.glCullFace(mode);
	}
	
	@Override
	public void glDeleteTextures(int n, IntBuffer textures)
	{
		mGL.glDeleteTextures(n, textures);
	}
	
	@Override
	public void glDeleteTextures(int n, int[] textures, int offset)
	{
		mGL.glDeleteTextures(n, textures, offset);
	}
	
	@Override
	public void glDepthFunc(int func)
	{
		mGL.glDepthFunc(func);
	}
	
	@Override
	public void glDepthMask(boolean flag)
	{
		mGL.glDepthMask(flag);
	}
	
	@Override
	public void glDepthRangef(float zNear, float zFar)
	{
		mGL.glDepthRangef(zNear, zFar);
	}
	
	@Override
	public void glDepthRangex(int zNear, int zFar)
	{
		mGL.glDepthRangex(zNear, zFar);
	}
	
	@Override
	public void glDisable(int cap)
	{
		mGL.glDisable(cap);
	}
	
	@Override
	public void glDisableClientState(int array)
	{
		mGL.glDisableClientState(array);
	}
	
	@Override
	public void glDrawArrays(int mode, int first, int count)
	{
		mGL.glDrawArrays(mode, first, count);
	}
	
	@Override
	public void glDrawElements(int mode, int count, int type, Buffer indices)
	{
		mGL.glDrawElements(mode, count, type, indices);
	}
	
	@Override
	public void glEnable(int cap)
	{
		mGL.glEnable(cap);
	}
	
	@Override
	public void glEnableClientState(int array)
	{
		mGL.glEnableClientState(array);
	}
	
	@Override
	public void glFinish()
	{
		mGL.glFinish();
	}
	
	@Override
	public void glFlush()
	{
		mGL.glFlush();
	}
	
	@Override
	public void glFogf(int pname, float param)
	{
		mGL.glFogf(pname, param);
	}
	
	@Override
	public void glFogfv(int pname, FloatBuffer params)
	{
		mGL.glFogfv(pname, params);
	}
	
	@Override
	public void glFogfv(int pname, float[] params, int offset)
	{
		mGL.glFogfv(pname, params, offset);
	}
	
	@Override
	public void glFogx(int pname, int param)
	{
		mGL.glFogx(pname, param);
	}
	
	@Override
	public void glFogxv(int pname, IntBuffer params)
	{
		mGL.glFogxv(pname, params);
	}
	
	@Override
	public void glFogxv(int pname, int[] params, int offset)
	{
		mGL.glFogxv(pname, params, offset);
	}
	
	@Override
	public void glFrontFace(int mode)
	{
		mGL.glFrontFace(mode);
	}
	
	@Override
	public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar)
	{
		mGL.glFrustumf(left, right, bottom, top, zNear, zFar);
	}
	
	@Override
	public void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar)
	{
		mGL.glFrustumx(left, right, bottom, top, zNear, zFar);
	}
	
	@Override
	public void glGenTextures(int n, IntBuffer textures)
	{
		mGL.glGenTextures(n, textures);
	}
	
	@Override
	public void glGenTextures(int n, int[] textures, int offset)
	{
		mGL.glGenTextures(n, textures, offset);
	}
	
	@Override
	public int glGetError()
	{
		return mGL.glGetError();
	}
	
	@Override
	public void glGetIntegerv(int pname, IntBuffer params)
	{
		mGL.glGetIntegerv(pname, params);
	}
	
	@Override
	public void glGetIntegerv(int pname, int[] params, int offset)
	{
		mGL.glGetIntegerv(pname, params, offset);
	}
	
	@Override
	public String glGetString(int name)
	{
		return mGL.glGetString(name);
	}
	
	@Override
	public void glHint(int target, int mode)
	{
		mGL.glHint(target, mode);
	}
	
	@Override
	public void glLightModelf(int pname, float param)
	{
		mGL.glLightModelf(pname, param);
	}
	
	@Override
	public void glLightModelfv(int pname, FloatBuffer params)
	{
		mGL.glLightModelfv(pname, params);
	}
	
	@Override
	public void glLightModelfv(int pname, float[] params, int offset)
	{
		mGL.glLightModelfv(pname, params, offset);
	}
	
	@Override
	public void glLightModelx(int pname, int param)
	{
		mGL.glLightModelx(pname, param);
	}
	
	@Override
	public void glLightModelxv(int pname, IntBuffer params)
	{
		mGL.glLightModelxv(pname, params);
	}
	
	@Override
	public void glLightModelxv(int pname, int[] params, int offset)
	{
		mGL.glLightModelxv(pname, params, offset);
	}
	
	@Override
	public void glLightf(int light, int pname, float param)
	{
		mGL.glLightf(light, pname, param);
	}
	
	@Override
	public void glLightfv(int light, int pname, FloatBuffer params)
	{
		mGL.glLightfv(light, pname, params);
	}
	
	@Override
	public void glLightfv(int light, int pname, float[] params, int offset)
	{
		mGL.glLightfv(light, pname, params, offset);
	}
	
	@Override
	public void glLightx(int light, int pname, int param)
	{
		mGL.glLightx(light, pname, param);
	}
	
	@Override
	public void glLightxv(int light, int pname, IntBuffer params)
	{
		mGL.glLightxv(light, pname, params);
	}
	
	@Override
	public void glLightxv(int light, int pname, int[] params, int offset)
	{
		mGL.glLightxv(light, pname, params, offset);
	}
	
	@Override
	public void glLineWidth(float width)
	{
		mGL.glLineWidth(width);
	}
	
	@Override
	public void glLineWidthx(int width)
	{
		mGL.glLineWidthx(width);
	}
	
	@Override
	public void glLoadIdentity()
	{
		mGL.glLoadIdentity();
	}
	
	@Override
	public void glLoadMatrixf(FloatBuffer m)
	{
		mGL.glLoadMatrixf(m);
	}
	
	@Override
	public void glLoadMatrixf(float[] m, int offset)
	{
		mGL.glLoadMatrixf(m, offset);
	}
	
	@Override
	public void glLoadMatrixx(IntBuffer m)
	{
		mGL.glLoadMatrixx(m);
	}
	
	@Override
	public void glLoadMatrixx(int[] m, int offset)
	{
		mGL.glLoadMatrixx(m, offset);
	}
	
	@Override
	public void glLogicOp(int opcode)
	{
		mGL.glLogicOp(opcode);
	}
	
	@Override
	public void glMaterialf(int face, int pname, float param)
	{
		mGL.glMaterialf(face, pname, param);
	}
	
	@Override
	public void glMaterialfv(int face, int pname, FloatBuffer params)
	{
		mGL.glMaterialfv(face, pname, params);
	}
	
	@Override
	public void glMaterialfv(int face, int pname, float[] params, int offset)
	{
		mGL.glMaterialfv(face, pname, params, offset);
	}
	
	@Override
	public void glMaterialx(int face, int pname, int param)
	{
		mGL.glMaterialx(face, pname, param);
	}
	
	@Override
	public void glMaterialxv(int face, int pname, IntBuffer params)
	{
		mGL.glMaterialxv(face, pname, params);
	}
	
	@Override
	public void glMaterialxv(int face, int pname, int[] params, int offset)
	{
		mGL.glMaterialxv(face, pname, params, offset);
	}
	
	@Override
	public void glMatrixMode(int mode)
	{
		mGL.glMatrixMode(mode);
	}
	
	@Override
	public void glMultMatrixf(FloatBuffer m)
	{
		mGL.glMultMatrixf(m);
	}
	
	@Override
	public void glMultMatrixf(float[] m, int offset)
	{
		mGL.glMultMatrixf(m, offset);
	}
	
	@Override
	public void glMultMatrixx(IntBuffer m)
	{
		mGL.glMultMatrixx(m);
	}
	
	@Override
	public void glMultMatrixx(int[] m, int offset)
	{
		mGL.glMultMatrixx(m, offset);
	}
	
	@Override
	public void glMultiTexCoord4f(int target, float s, float t, float r, float q)
	{
		mGL.glMultiTexCoord4f(target, s, t, r, q);
	}
	
	@Override
	public void glMultiTexCoord4x(int target, int s, int t, int r, int q)
	{
		mGL.glMultiTexCoord4x(target, s, t, r, q);
	}
	
	@Override
	public void glNormal3f(float nx, float ny, float nz)
	{
		mGL.glNormal3f(nx, ny, nz);
	}
	
	@Override
	public void glNormal3x(int nx, int ny, int nz)
	{
		mGL.glNormal3x(nx, ny, nz);
	}
	
	@Override
	public void glNormalPointer(int type, int stride, Buffer pointer)
	{
		mGL.glNormalPointer(type, stride, pointer);
	}
	
	@Override
	public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar)
	{
		mGL.glOrthof(left, right, bottom, top, zNear, zFar);
	}
	
	@Override
	public void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar)
	{
		mGL.glOrthox(left, right, bottom, top, zNear, zFar);
	}
	
	@Override
	public void glPixelStorei(int pname, int param)
	{
		mGL.glPixelStorei(pname, param);
	}
	
	@Override
	public void glPointSize(float size)
	{
		mGL.glPointSize(size);
	}
	
	@Override
	public void glPointSizex(int size)
	{
		mGL.glPointSizex(size);
	}
	
	@Override
	public void glPolygonOffset(float factor, float units)
	{
		mGL.glPolygonOffset(factor, units);
	}
	
	@Override
	public void glPolygonOffsetx(int factor, int units)
	{
		mGL.glPolygonOffsetx(factor, units);
	}
	
	@Override
	public void glPopMatrix()
	{
		mGL.glPopMatrix();
	}
	
	@Override
	public void glPushMatrix()
	{
		mGL.glPushMatrix();
	}
	
	@Override
	public void glReadPixels(int x, int y, int width, int height, int format, int type, Buffer pixels)
	{
		mGL.glReadPixels(x, y, width, height, format, type, pixels);
	}
	
	@Override
	public void glRotatef(float angle, float x, float y, float z)
	{
		mGL.glRotatef(angle, x, y, z);
	}
	
	@Override
	public void glRotatex(int angle, int x, int y, int z)
	{
		mGL.glRotatex(angle, x, y, z);
	}
	
	@Override
	public void glSampleCoverage(float value, boolean invert)
	{
		mGL.glSampleCoverage(value, invert);
	}
	
	@Override
	public void glSampleCoveragex(int value, boolean invert)
	{
		mGL.glSampleCoveragex(value, invert);
	}
	
	@Override
	public void glScalef(float x, float y, float z)
	{
		mGL.glScalef(x, y, z);
	}
	
	@Override
	public void glScalex(int x, int y, int z)
	{
		mGL.glScalex(x, y, z);
	}
	
	@Override
	public void glScissor(int x, int y, int width, int height)
	{
		mGL.glScissor(x, y, width, height);
	}
	
	@Override
	public void glShadeModel(int mode)
	{
		mGL.glShadeModel(mode);
	}
	
	@Override
	public void glStencilFunc(int func, int ref, int mask)
	{
		mGL.glStencilFunc(func, ref, mask);
	}
	
	@Override
	public void glStencilMask(int mask)
	{
		mGL.glStencilMask(mask);
	}
	
	@Override
	public void glStencilOp(int fail, int zfail, int zpass)
	{
		mGL.glStencilOp(fail, zfail, zpass);
	}
	
	@Override
	public void glTexCoordPointer(int size, int type, int stride, Buffer pointer)
	{
		mGL.glTexCoordPointer(size, type, stride, pointer);
	}
	
	@Override
	public void glTexEnvf(int target, int pname, float param)
	{
		mGL.glTexEnvf(target, pname, param);
	}
	
	@Override
	public void glTexEnvfv(int target, int pname, FloatBuffer params)
	{
		mGL.glTexEnvfv(target, pname, params);
	}
	
	@Override
	public void glTexEnvfv(int target, int pname, float[] params, int offset)
	{
		mGL.glTexEnvfv(target, pname, params, offset);
	}
	
	@Override
	public void glTexEnvx(int target, int pname, int param)
	{
		mGL.glTexEnvx(target, pname, param);
	}
	
	@Override
	public void glTexEnvxv(int target, int pname, IntBuffer params)
	{
		mGL.glTexEnvxv(target, pname, params);
	}
	
	@Override
	public void glTexEnvxv(int target, int pname, int[] params, int offset)
	{
		mGL.glTexEnvxv(target, pname, params, offset);
	}
	
	@Override
	public void glTexImage2D(int target, int level, int internalformat,
			int width, int height, int border, int format, int type,
			Buffer pixels)
	{
		mGL.glTexImage2D(target, level, internalformat, width, height, border, format, type, pixels);
	}
	
	@Override
	public void glTexParameterf(int target, int pname, float param)
	{
		mGL.glTexParameterf(target, pname, param);
	}
	
	@Override
	public void glTexParameterx(int target, int pname, int param)
	{
		mGL.glTexParameterx(target, pname, param);
	}
	
	@Override
	public void glTexSubImage2D(int target, int level, int xoffset,
			int yoffset, int width, int height, int format, int type,
			Buffer pixels)
	{
		mGL.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}
	
	@Override
	public void glTranslatef(float x, float y, float z)
	{
		mGL.glTranslatef(x, y, z);
	}
	
	@Override
	public void glTranslatex(int x, int y, int z)
	{
		mGL.glTranslatex(x, y, z);
	}
	
	@Override
	public void glVertexPointer(int size, int type, int stride, Buffer pointer)
	{
		mGL.glVertexPointer(size, type, stride, pointer);
	}
	
	@Override
	public void glViewport(int x, int y, int width, int height)
	{
		mGL.glViewport(x, y, width, height);
	}
	
	/**GL10Ext methods*/
	
	@Override
	public int glQueryMatrixxOES(IntBuffer mantissa, IntBuffer exponent)
	{
		return mGL10Ext.glQueryMatrixxOES(mantissa, exponent);
	}
	
	@Override
	public int glQueryMatrixxOES(int[] mantissa, int mantissaOffset, int[] exponent, int exponentOffset)
	{
		return mGL10Ext.glQueryMatrixxOES(mantissa, mantissaOffset, exponent, exponentOffset);
	}
	
	/**GL11 methods*/
	
	@Override
	public void glBindBuffer(int target, int buffer)
	{
		mGL11.glBindBuffer(target, buffer);
	}
	
	@Override
	public void glBufferData(int target, int size, Buffer data, int usage)
	{
		mGL11.glBufferData(target, size, data, usage);
	}
	
	@Override
	public void glBufferSubData(int target, int offset, int size, Buffer data)
	{
		mGL11.glBufferSubData(target, offset, size, data);
	}
	
	@Override
	public void glClipPlanef(int plane, FloatBuffer equation)
	{
		mGL11.glClipPlanef(plane, equation);
	}
	
	@Override
	public void glClipPlanef(int plane, float[] equation, int offset)
	{
		mGL11.glClipPlanef(plane, equation, offset);
	}
	
	@Override
	public void glClipPlanex(int plane, IntBuffer equation)
	{
		mGL11.glClipPlanex(plane, equation);
	}
	
	@Override
	public void glClipPlanex(int plane, int[] equation, int offset)
	{
		mGL11.glClipPlanex(plane, equation, offset);
	}
	
	@Override
	public void glColor4ub(byte red, byte green, byte blue, byte alpha)
	{
		mGL11.glColor4ub(red, green, blue, alpha);
	}
	
	@Override
	public void glColorPointer(int size, int type, int stride, int offset)
	{
		mGL11.glColorPointer(size, type, stride, offset);
	}
	
	@Override
	public void glDeleteBuffers(int n, IntBuffer buffers)
	{
		mGL11.glDeleteBuffers(n, buffers);
	}
	
	@Override
	public void glDeleteBuffers(int n, int[] buffers, int offset)
	{
		mGL11.glDeleteBuffers(n, buffers, offset);
	}
	
	@Override
	public void glDrawElements(int mode, int count, int type, int offset)
	{
		mGL11.glDrawElements(mode, count, type, offset);
	}
	
	@Override
	public void glGenBuffers(int n, IntBuffer buffers)
	{
		mGL11.glGenBuffers(n, buffers);
	}
	
	@Override
	public void glGenBuffers(int n, int[] buffers, int offset)
	{
		mGL11.glGenBuffers(n, buffers, offset);
	}
	
	@Override
	public void glGetBooleanv(int pname, IntBuffer params)
	{
		mGL11.glGetBooleanv(pname, params);
	}
	
	@Override
	public void glGetBooleanv(int pname, boolean[] params, int offset)
	{
		mGL11.glGetBooleanv(pname, params, offset);
	}
	
	@Override
	public void glGetBufferParameteriv(int target, int pname, IntBuffer params)
	{
		mGL11.glGetBufferParameteriv(target, pname, params);
	}
	
	@Override
	public void glGetBufferParameteriv(int target, int pname, int[] params, int offset)
	{
		mGL11.glGetBufferParameteriv(target, pname, params, offset);
	}
	
	@Override
	public void glGetClipPlanef(int pname, FloatBuffer eqn)
	{
		mGL11.glGetClipPlanef(pname, eqn);
	}
	
	@Override
	public void glGetClipPlanef(int pname, float[] eqn, int offset)
	{
		mGL11.glGetClipPlanef(pname, eqn, offset);
	}
	
	@Override
	public void glGetClipPlanex(int pname, IntBuffer eqn)
	{
		mGL11.glGetClipPlanex(pname, eqn);
	}
	
	@Override
	public void glGetClipPlanex(int pname, int[] eqn, int offset)
	{
		mGL11.glGetClipPlanex(pname, eqn, offset);
	}
	
	@Override
	public void glGetFixedv(int pname, IntBuffer params)
	{
		mGL11.glGetFixedv(pname, params);
	}
	
	@Override
	public void glGetFixedv(int pname, int[] params, int offset)
	{
		mGL11.glGetFixedv(pname, params, offset);
	}
	
	@Override
	public void glGetFloatv(int pname, FloatBuffer params)
	{
		mGL11.glGetFloatv(pname, params);
	}
	
	@Override
	public void glGetFloatv(int pname, float[] params, int offset)
	{
		mGL11.glGetFloatv(pname, params, offset);
	}
	
	@Override
	public void glGetLightfv(int light, int pname, FloatBuffer params)
	{
		mGL11.glGetLightfv(light, pname, params);
	}
	
	@Override
	public void glGetLightfv(int light, int pname, float[] params, int offset)
	{
		mGL11.glGetLightfv(light, pname, params, offset);
	}
	
	@Override
	public void glGetLightxv(int light, int pname, IntBuffer params)
	{
		mGL11.glGetLightxv(light, pname, params);
	}
	
	@Override
	public void glGetLightxv(int light, int pname, int[] params, int offset)
	{
		mGL11.glGetLightxv(light, pname, params, offset);
	}
	
	@Override
	public void glGetMaterialfv(int face, int pname, FloatBuffer params)
	{
		mGL11.glGetMaterialfv(face, pname, params);
	}
	
	@Override
	public void glGetMaterialfv(int face, int pname, float[] params, int offset)
	{
		mGL11.glGetMaterialfv(face, pname, params, offset);
	}
	
	@Override
	public void glGetMaterialxv(int face, int pname, IntBuffer params)
	{
		mGL11.glGetMaterialxv(face, pname, params);
	}
	
	@Override
	public void glGetMaterialxv(int face, int pname, int[] params, int offset)
	{
		mGL11.glGetMaterialxv(face, pname, params, offset);
	}
	
	@Override
	public void glGetPointerv(int pname, Buffer[] params)
	{
		mGL11.glGetPointerv(pname, params);
	}
	
	@Override
	public void glGetTexEnviv(int env, int pname, IntBuffer params)
	{
		mGL11.glGetTexEnviv(env, pname, params);
	}
	
	@Override
	public void glGetTexEnviv(int env, int pname, int[] params, int offset)
	{
		mGL11.glGetTexEnviv(env, pname, params, offset);
	}
	
	@Override
	public void glGetTexEnvxv(int env, int pname, IntBuffer params)
	{
		mGL11.glGetTexEnvxv(env, pname, params);
	}
	
	@Override
	public void glGetTexEnvxv(int env, int pname, int[] params, int offset)
	{
		mGL11.glGetTexEnvxv(env, pname, params, offset);
	}
	
	@Override
	public void glGetTexParameterfv(int target, int pname, FloatBuffer params)
	{
		mGL11.glGetTexParameterfv(target, pname, params);
	}
	
	@Override
	public void glGetTexParameterfv(int target, int pname, float[] params, int offset)
	{
		mGL11.glGetTexParameterfv(target, pname, params, offset);
	}
	
	@Override
	public void glGetTexParameteriv(int target, int pname, IntBuffer params)
	{
		mGL11.glGetTexParameteriv(target, pname, params);
	}
	
	@Override
	public void glGetTexParameteriv(int target, int pname, int[] params, int offset)
	{
		mGL11.glGetTexParameteriv(target, pname, params, offset);
	}
	
	@Override
	public void glGetTexParameterxv(int target, int pname, IntBuffer params)
	{
		mGL11.glGetTexParameterxv(target, pname, params);
	}
	
	@Override
	public void glGetTexParameterxv(int target, int pname, int[] params, int offset)
	{
		mGL11.glGetTexParameterxv(target, pname, params, offset);
	}
	
	@Override
	public boolean glIsBuffer(int buffer)
	{
		return mGL11.glIsBuffer(buffer);
	}
	
	@Override
	public boolean glIsEnabled(int cap)
	{
		return mGL11.glIsEnabled(cap);
	}
	
	@Override
	public boolean glIsTexture(int texture)
	{
		return mGL11.glIsTexture(texture);
	}
	
	@Override
	public void glNormalPointer(int type, int stride, int offset)
	{
		mGL11.glNormalPointer(type, stride, offset);
	}
	
	@Override
	public void glPointParameterf(int pname, float param)
	{
		mGL11.glPointParameterf(pname, param);
	}
	
	@Override
	public void glPointParameterfv(int pname, FloatBuffer params)
	{
		mGL11.glPointParameterfv(pname, params);
	}
	
	@Override
	public void glPointParameterfv(int pname, float[] params, int offset)
	{
		mGL11.glPointParameterfv(pname, params, offset);
	}
	
	@Override
	public void glPointParameterx(int pname, int param)
	{
		mGL11.glPointParameterx(pname, param);
	}
	
	@Override
	public void glPointParameterxv(int pname, IntBuffer params)
	{
		mGL11.glPointParameterxv(pname, params);
	}
	
	@Override
	public void glPointParameterxv(int pname, int[] params, int offset)
	{
		mGL11.glPointParameterxv(pname, params, offset);
	}
	
	@Override
	public void glPointSizePointerOES(int type, int stride, Buffer pointer)
	{
		mGL11.glPointSizePointerOES(type, stride, pointer);
	}
	
	@Override
	public void glTexCoordPointer(int size, int type, int stride, int offset)
	{
		mGL11.glTexCoordPointer(size, type, stride, offset);
	}
	
	@Override
	public void glTexEnvi(int target, int pname, int param)
	{
		mGL11.glTexEnvi(target, pname, param);
	}
	
	@Override
	public void glTexEnviv(int target, int pname, IntBuffer params)
	{
		mGL11.glTexEnviv(target, pname, params);
	}
	
	@Override
	public void glTexEnviv(int target, int pname, int[] params, int offset)
	{
		mGL11.glTexEnviv(target, pname, params, offset);
	}
	
	@Override
	public void glTexParameterfv(int target, int pname, FloatBuffer params)
	{
		mGL11.glTexParameterfv(target, pname, params);
	}
	
	@Override
	public void glTexParameterfv(int target, int pname, float[] params, int offset)
	{
		mGL11.glTexParameterfv(target, pname, params, offset);
	}
	
	@Override
	public void glTexParameteri(int target, int pname, int param)
	{
		mGL11.glTexParameteri(target, pname, param);
	}
	
	@Override
	public void glTexParameteriv(int target, int pname, IntBuffer params)
	{
		mGL11.glTexParameteriv(target, pname, params);
	}
	
	@Override
	public void glTexParameteriv(int target, int pname, int[] params, int offset)
	{
		mGL11.glTexParameteriv(target, pname, params, offset);
	}
	
	@Override
	public void glTexParameterxv(int target, int pname, IntBuffer params)
	{
		mGL11.glTexParameterxv(target, pname, params);
	}
	
	@Override
	public void glTexParameterxv(int target, int pname, int[] params, int offset)
	{
		mGL11.glTexParameterxv(target, pname, params, offset);
	}
	
	@Override
	public void glVertexPointer(int size, int type, int stride, int offset)
	{
		mGL11.glVertexPointer(size, type, stride, offset);
	}
	
	/**GL11Ext methods*/
	
	@Override
	public void glCurrentPaletteMatrixOES(int matrixpaletteindex)
	{
		mGL11Ext.glCurrentPaletteMatrixOES(matrixpaletteindex);
	}
	
	@Override
	public void glDrawTexfOES(float x, float y, float z, float width, float height)
	{
		mGL11Ext.glDrawTexfOES(x, y, z, width, height);
	}
	
	@Override
	public void glDrawTexfvOES(FloatBuffer coords)
	{
		mGL11Ext.glDrawTexfvOES(coords);
	}
	
	@Override
	public void glDrawTexfvOES(float[] coords, int offset)
	{
		mGL11Ext.glDrawTexfvOES(coords, offset);
	}
	
	@Override
	public void glDrawTexiOES(int x, int y, int z, int width, int height)
	{
		mGL11Ext.glDrawTexiOES(x, y, z, width, height);
	}
	
	@Override
	public void glDrawTexivOES(IntBuffer coords)
	{
		mGL11Ext.glDrawTexivOES(coords);
	}
	
	@Override
	public void glDrawTexivOES(int[] coords, int offset)
	{
		mGL11Ext.glDrawTexivOES(coords, offset);
	}
	
	@Override
	public void glDrawTexsOES(short x, short y, short z, short width, short height)
	{
		mGL11Ext.glDrawTexsOES(x, y, z, width, height);
	}
	
	@Override
	public void glDrawTexsvOES(ShortBuffer coords)
	{
		mGL11Ext.glDrawTexsvOES(coords);
	}
	
	@Override
	public void glDrawTexsvOES(short[] coords, int offset)
	{
		mGL11Ext.glDrawTexsvOES(coords, offset);
	}
	
	@Override
	public void glDrawTexxOES(int x, int y, int z, int width, int height)
	{
		mGL11Ext.glDrawTexxOES(x, y, z, width, height);
	}
	
	@Override
	public void glDrawTexxvOES(IntBuffer coords)
	{
		mGL11Ext.glDrawTexxvOES(coords);
	}
	
	@Override
	public void glDrawTexxvOES(int[] coords, int offset)
	{
		mGL11Ext.glDrawTexxvOES(coords, offset);
	}
	
	@Override
	public void glLoadPaletteFromModelViewMatrixOES()
	{
		mGL11Ext.glLoadPaletteFromModelViewMatrixOES();
	}
	
	@Override
	public void glMatrixIndexPointerOES(int size, int type, int stride, Buffer pointer)
	{
		mGL11Ext.glMatrixIndexPointerOES(size, type, stride, pointer);
	}
	
	@Override
	public void glMatrixIndexPointerOES(int size, int type, int stride, int offset)
	{
		mGL11Ext.glMatrixIndexPointerOES(size, type, stride, offset);
	}
	
	@Override
	public void glWeightPointerOES(int size, int type, int stride, Buffer pointer)
	{
		mGL11Ext.glWeightPointerOES(size, type, stride, pointer);
	}
	
	@Override
	public void glWeightPointerOES(int size, int type, int stride, int offset)
	{
		mGL11Ext.glWeightPointerOES(size, type, stride, offset);
	}
	
	/**GL11ExtensionPack methods*/
	
	@Override
	public void glBindFramebufferOES(int target, int framebuffer)
	{
		mGL11ExtPack.glBindFramebufferOES(target, framebuffer);
	}
	
	@Override
	public void glBindRenderbufferOES(int target, int renderbuffer)
	{
		mGL11ExtPack.glBindRenderbufferOES(target, renderbuffer);
	}
	
	@Override
	public void glBlendEquation(int mode)
	{
		mGL11ExtPack.glBlendEquation(mode);
	}
	
	@Override
	public void glBlendEquationSeparate(int modeRGB, int modeAlpha)
	{
		mGL11ExtPack.glBlendEquationSeparate(modeRGB, modeAlpha);
	}
	
	@Override
	public void glBlendFuncSeparate(int srcRGB, int dstRGB, int srcAlpha, int dstAlpha)
	{
		mGL11ExtPack.glBlendFuncSeparate(srcRGB, dstRGB, srcAlpha, dstAlpha);
	}
	
	@Override
	public int glCheckFramebufferStatusOES(int target)
	{
		return mGL11ExtPack.glCheckFramebufferStatusOES(target);
	}
	
	@Override
	public void glDeleteFramebuffersOES(int n, IntBuffer framebuffers)
	{
		mGL11ExtPack.glDeleteFramebuffersOES(n, framebuffers);
	}
	
	@Override
	public void glDeleteFramebuffersOES(int n, int[] framebuffers, int offset)
	{
		mGL11ExtPack.glDeleteFramebuffersOES(n, framebuffers, offset);
	}
	
	@Override
	public void glDeleteRenderbuffersOES(int n, IntBuffer renderbuffers)
	{
		mGL11ExtPack.glDeleteRenderbuffersOES(n, renderbuffers);
	}
	
	@Override
	public void glDeleteRenderbuffersOES(int n, int[] renderbuffers, int offset)
	{
		mGL11ExtPack.glDeleteRenderbuffersOES(n, renderbuffers, offset);
	}
	
	@Override
	public void glFramebufferRenderbufferOES(int target, int attachment, int renderbuffertarget, int renderbuffer)
	{
		mGL11ExtPack.glFramebufferRenderbufferOES(target, attachment, renderbuffertarget, renderbuffer);
	}
	
	@Override
	public void glFramebufferTexture2DOES(int target, int attachment, int textarget, int texture, int level)
	{
		mGL11ExtPack.glFramebufferTexture2DOES(target, attachment, textarget, texture, level);
	}
	
	@Override
	public void glGenFramebuffersOES(int n, IntBuffer framebuffers)
	{
		mGL11ExtPack.glGenFramebuffersOES(n, framebuffers);
	}
	
	@Override
	public void glGenFramebuffersOES(int n, int[] framebuffers, int offset)
	{
		mGL11ExtPack.glGenFramebuffersOES(n, framebuffers, offset);
	}
	
	@Override
	public void glGenRenderbuffersOES(int n, IntBuffer renderbuffers)
	{
		mGL11ExtPack.glGenRenderbuffersOES(n, renderbuffers);
	}
	
	@Override
	public void glGenRenderbuffersOES(int n, int[] renderbuffers, int offset)
	{
		mGL11ExtPack.glGenRenderbuffersOES(n, renderbuffers, offset);
	}
	
	@Override
	public void glGenerateMipmapOES(int target)
	{
		mGL11ExtPack.glGenerateMipmapOES(target);
	}
	
	@Override
	public void glGetFramebufferAttachmentParameterivOES(int target, int attachment, int pname, IntBuffer params)
	{
		mGL11ExtPack.glGetFramebufferAttachmentParameterivOES(target, attachment, pname, params);
	}
	
	@Override
	public void glGetFramebufferAttachmentParameterivOES(int target, int attachment, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glGetFramebufferAttachmentParameterivOES(target, attachment, pname, params, offset);
	}
	
	@Override
	public void glGetRenderbufferParameterivOES(int target, int pname, IntBuffer params)
	{
		mGL11ExtPack.glGetRenderbufferParameterivOES(target, pname, params);
	}
	
	@Override
	public void glGetRenderbufferParameterivOES(int target, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glGetRenderbufferParameterivOES(target, pname, params, offset);
	}
	
	@Override
	public void glGetTexGenfv(int coord, int pname, FloatBuffer params)
	{
		mGL11ExtPack.glGetTexGenfv(coord, pname, params);
	}
	
	@Override
	public void glGetTexGenfv(int coord, int pname, float[] params, int offset)
	{
		mGL11ExtPack.glGetTexGenfv(coord, pname, params, offset);
	}
	
	@Override
	public void glGetTexGeniv(int coord, int pname, IntBuffer params)
	{
		mGL11ExtPack.glGetTexGeniv(coord, pname, params);
	}
	
	@Override
	public void glGetTexGeniv(int coord, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glGetTexGeniv(coord, pname, params, offset);
	}
	
	@Override
	public void glGetTexGenxv(int coord, int pname, IntBuffer params)
	{
		mGL11ExtPack.glGetTexGenxv(coord, pname, params);
	}
	
	@Override
	public void glGetTexGenxv(int coord, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glGetTexGenxv(coord, pname, params, offset);
	}
	
	@Override
	public boolean glIsFramebufferOES(int framebuffer)
	{
		return mGL11ExtPack.glIsFramebufferOES(framebuffer);
	}
	
	@Override
	public boolean glIsRenderbufferOES(int renderbuffer)
	{
		return mGL11ExtPack.glIsRenderbufferOES(renderbuffer);
	}
	
	@Override
	public void glRenderbufferStorageOES(int target, int internalformat, int width, int height)
	{
		mGL11ExtPack.glRenderbufferStorageOES(target, internalformat, width, height);
	}
	
	@Override
	public void glTexGenf(int coord, int pname, float param)
	{
		mGL11ExtPack.glTexGenf(coord, pname, param);
	}
	
	@Override
	public void glTexGenfv(int coord, int pname, FloatBuffer params)
	{
		mGL11ExtPack.glTexGenfv(coord, pname, params);
	}
	
	@Override
	public void glTexGenfv(int coord, int pname, float[] params, int offset)
	{
		mGL11ExtPack.glTexGenfv(coord, pname, params, offset);
	}
	
	@Override
	public void glTexGeni(int coord, int pname, int param)
	{
		mGL11ExtPack.glTexGeni(coord, pname, param);
	}
	
	@Override
	public void glTexGeniv(int coord, int pname, IntBuffer params)
	{
		mGL11ExtPack.glTexGeniv(coord, pname, params);
	}
	
	@Override
	public void glTexGeniv(int coord, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glTexGeniv(coord, pname, params, offset);
	}
	
	@Override
	public void glTexGenx(int coord, int pname, int param)
	{
		mGL11ExtPack.glTexGenx(coord, pname, param);
	}
	
	@Override
	public void glTexGenxv(int coord, int pname, IntBuffer params)
	{
		mGL11ExtPack.glTexGenxv(coord, pname, params);
	}
	
	@Override
	public void glTexGenxv(int coord, int pname, int[] params, int offset)
	{
		mGL11ExtPack.glTexGenxv(coord, pname, params, offset);
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mGLSurfaceView = null;
		mGL = null;
		mGL10Ext = null;
		mGL11 = null;
		mGL11Ext = null;
		mGL11ExtPack = null;
		super.finalize();
	}
}