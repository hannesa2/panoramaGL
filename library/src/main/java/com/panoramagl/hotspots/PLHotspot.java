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

package com.panoramagl.hotspots;

import com.panoramagl.PLConstants;
import com.panoramagl.PLIImage;
import com.panoramagl.PLIObject;
import com.panoramagl.PLIRenderer;
import com.panoramagl.PLIScene;
import com.panoramagl.PLITexture;
import com.panoramagl.PLIView;
import com.panoramagl.PLSceneElementBase;
import com.panoramagl.PLTexture;
import com.panoramagl.computation.PLVector3;
import com.panoramagl.enumerations.PLSceneElementTouchStatus;
import com.panoramagl.interpreters.PLCommandInterpreter;
import com.panoramagl.interpreters.PLIInterpreter;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLRect;
import com.panoramagl.utils.PLUtils;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public class PLHotspot extends PLSceneElementBase implements PLIHotspot
{
	/**member variables*/
	
	private float mWidth, mHeight;
	private float mAtv, mAth;
	private float[] mVertexs;
	private FloatBuffer mVertexsBuffer, mTextureCoordsBuffer;
	private String mOnClick;
	private float mOverAlpha, mDefaultOverAlpha;
	private boolean hasChangedCoordProperty;
	
	/**init methods*/
	
	public PLHotspot(long identifier, float atv, float ath)
	{
		super(identifier);
		this.setAtv(atv);
		this.setAth(ath);
	}
	
	public PLHotspot(long identifier, float atv, float ath, float width, float height)
	{
		super(identifier);
		this.setAtv(atv);
		this.setAth(ath);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public PLHotspot(long identifier, PLITexture texture, float atv, float ath)
	{
		super(identifier, texture);
		this.setAtv(atv);
		this.setAth(ath);
	}
	
	public PLHotspot(long identifier, PLITexture texture, float atv, float ath, float width, float height)
	{
		super(identifier, texture);
		this.setAtv(atv);
		this.setAth(ath);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	public PLHotspot(long identifier, PLIImage image, float atv, float ath)
	{
		this(identifier, new PLTexture(image), atv, ath);
	}
	
	public PLHotspot(long identifier, PLIImage image, float atv, float ath, float width, float height)
	{
		this(identifier, new PLTexture(image), atv, ath, width, height);
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		mVertexs = new float[12];
		this.setWidth(PLConstants.kDefaultHotspotSize);
		this.setHeight(PLConstants.kDefaultHotspotSize);
		mAtv = mAth = 0.0f;
		this.setYZAxisInverseRotation(true);
		this.setZ(PLConstants.kDefaultHotspotZPosition);
		mOnClick = null;
		this.setAlpha(PLConstants.kDefaultHotspotAlpha);
		this.setDefaultAlpha(PLConstants.kDefaultHotspotAlpha);
		mOverAlpha = mDefaultOverAlpha = PLConstants.kDefaultHotspotOverAlpha;
		hasChangedCoordProperty = true;
	}
	
	/**reset methods*/
	
	@Override
	public void reset()
	{
		super.reset();
		this.setOverAlpha(mDefaultOverAlpha);
	}
	
	/**property methods*/
	
	@Override
	public float getAtv()
	{
		return mAtv;
	}
	
	@Override
	public void setAtv(float atv)
	{
		if(mAtv != atv)
		{
			mAtv = atv;
			this.hasChangedCoordProperty = true;
		}
	}
	
	protected void setInternalAtv(float atv)
	{
		mAtv = atv;
	}
	
	@Override
	public float getAth()
	{
		return mAth;
	}
	
	@Override
	public void setAth(float ath)
	{
		if(mAth != ath)
		{
			mAth = ath;
			this.hasChangedCoordProperty = true;
		}
	}
	
	protected void setInternalAth(float ath)
	{
		mAth = ath;
	}
	
	@Override
	public float getWidth()
	{
		return (mWidth / (PLConstants.kPanoramaRadius * 2.0f));
	}
	
	@Override
	public void setWidth(float width)
	{
		if(width >= 0.0f && width <= 1.0f && this.getWidth() != width)
		{
			mWidth = width * PLConstants.kPanoramaRadius * 2.0f;
			hasChangedCoordProperty = true;
		}
	}
	
	protected void setInternalWidth(float width)
	{
		mWidth = width;
	}
	
	@Override
	public float getHeight()
	{
		return (mHeight / (PLConstants.kPanoramaRadius * 2.0f));
	}
	
	@Override
	public void setHeight(float height)
	{
		if(height >= 0.0f && height <= 1.0f && this.getHeight() != height)
		{
			mHeight = height * PLConstants.kPanoramaRadius * 2.0f;
			hasChangedCoordProperty = true;
		}
	}
	
	protected void setInternalHeight(float height)
	{
		mHeight = height;
	}
	
	@Override
	public String getOnClick()
	{
		return mOnClick;
	}
	
	public void setOnClick(String onClick)
	{
		mOnClick = (onClick != null ? onClick.trim() : null);
	}
	
	@Override
	public void setAlpha(float alpha)
	{
		this.setInternalAlpha(Math.min(alpha, this.getDefaultAlpha()));
	}
	
	@Override
	public float getOverAlpha()
	{
		return mOverAlpha;
	}
	
	@Override
	public void setOverAlpha(float overAlpha)
	{
		mOverAlpha = overAlpha;
	}
	
	@Override
	public float getDefaultOverAlpha()
	{
		return mDefaultOverAlpha;
	}
	
	@Override
	public void setDefaultOverAlpha(float defaultOverAlpha)
	{
		mDefaultOverAlpha = defaultOverAlpha;
	}
	
	@Override
	public PLRect getRect()
	{
		PLRect rect = PLRect.PLRectMake();
		this.getRect(rect);
		return rect;
	}
	
	@Override
	public void getRect(PLRect rect)
	{
		if(rect != null)
		{
			if(mVertexsBuffer != null)
				rect.setValues(mVertexs[0], mVertexs[1], mVertexs[2], mVertexs[9], mVertexs[10], mVertexs[11]);
			else
				rect.reset();
		}
	}
	
	@Override
	public float[] getVertexs()
	{
		return (mVertexsBuffer != null ? mVertexs : null);
	}
	
	@Override
	public void setX(float x)
	{
	}
	
	@Override
	public void setY(float y)
	{
	}
	
	@Override
	public void setZ(float z)
	{
		if(this.getZ() != z)
		{
			super.setZ(z);
			hasChangedCoordProperty = true;
		}
	}
	
	/**layout methods*/
	
	@Override
	public void setSize(float width, float height)
	{
		this.setWidth(width);
		this.setHeight(height);
	}
	
	@Override
	public void setLayout(float pitch, float yaw, float width, float height)
	{
		this.setPitch(pitch);
		this.setYaw(yaw);
		this.setWidth(width);
		this.setHeight(height);
	}
	
	/**utility methods*/
	
	protected void array(float[] result, int size, float ... args)
	{
	    for(int i = 0; i < size; i++)
	        result[i] = args[i];
	}
	
	/**calculate methods*/
	
	protected PLPosition convertPitchAndYawToPosition(float pitch, float yaw)
	{
		float r = this.getZ(), pr = (90.0f - pitch) * PLConstants.kToRadians, yr = -yaw * PLConstants.kToRadians;
		float x = r * (float)Math.sin(pr) * (float)Math.cos(yr);
		float y = r * (float)Math.sin(pr) * (float)Math.sin(yr);
		float z = r * (float)Math.cos(pr);
		return PLPosition.PLPositionMake(y, z, x);
	}
	
	protected List<PLPosition> calculatePoints(GL10 gl)
	{
		List<PLPosition> result = new ArrayList<PLPosition>(4);
		//1
		PLPosition pos = this.convertPitchAndYawToPosition(mAtv, mAth), pos1 = this.convertPitchAndYawToPosition(mAtv + 0.0001f, mAth);
		//2 and 3
		PLVector3 p1 = new PLVector3(pos.x, pos.y, pos.z),
				  p2p1 = new PLVector3(0.0f, 0.0f, 0.0f).sub(p1),
				  r = p2p1.crossProduct(new PLVector3(pos1.x, pos1.y, pos1.z).sub(p1)),
				  s = p2p1.crossProduct(r);
		//4
		r.normalize();
		s.normalize();
		//5.1
		float w = mWidth * PLConstants.kPanoramaRadius, h = mHeight * PLConstants.kPanoramaRadius;
		float radius = (float)Math.sqrt((w * w) + (h * h));
		//5.2
		float angle = (float)Math.asin(h / radius);
		//5.3
		PLVector3 n = new PLVector3(0.0f, 0.0f, 0.0f);
		for(float theta : new float[]{ PLConstants.kPI - angle, angle, PLConstants.kPI + angle, 2 * PLConstants.kPI - angle})
		{
			n.x = p1.x + (radius * (float)Math.cos(theta) * r.x) + (radius * (float)Math.sin(theta) * s.x);
			n.y = p1.y + (radius * (float)Math.cos(theta) * r.y) + (radius * (float)Math.sin(theta) * s.y);
			n.z = p1.z + (radius * (float)Math.cos(theta) * r.z) + (radius * (float)Math.sin(theta) * s.z);
			n.normalize();
			result.add(PLPosition.PLPositionMake(n.x, n.y, n.z));
		}
		return result;
	}
	
	protected void calculateCoords(GL10 gl)
	{
		if(!hasChangedCoordProperty)
			return;
		
		hasChangedCoordProperty = false;
		
		float textureCoords[] = new float[8];
		
		List<PLPosition> positions = this.calculatePoints(gl);
		PLPosition pos1 = positions.get(0);
		PLPosition pos2 = positions.get(1);
		PLPosition pos3 = positions.get(2);
		PLPosition pos4 = positions.get(3);
		
		this.array
		(
			mVertexs, 12,
			pos1.x, pos1.y, pos1.z,
			pos2.x, pos2.y, pos2.z,
			pos3.x, pos3.y, pos3.z,
			pos4.x, pos4.y, pos4.z
		);
		this.array
		(
			textureCoords, 8,
			1.0f, 1.0f,	
			0.0f, 1.0f,
			1.0f, 0.0f,
			0.0f, 0.0f
		);
		
		mVertexsBuffer = PLUtils.makeFloatBuffer(mVertexs);
		mTextureCoordsBuffer = PLUtils.makeFloatBuffer(textureCoords);
	}
	
	/**translate methods*/
	
	@Override
	protected void translate(GL10 gl)
	{
	}
	
	/**render methods*/
	
	@Override
	protected void internalRender(GL10 gl, PLIRenderer renderer)
	{
		this.calculateCoords(gl);
		
		List<PLITexture> textures = this.getTextures();
		int textureId = (textures.size() > 0 ? textures.get(0).getTextureId(gl) : 0);
		if(textureId == 0 || mVertexsBuffer == null || mTextureCoordsBuffer == null)
			return;
		
		gl.glEnable(GL10.GL_TEXTURE_2D);
		
		PLIView view = renderer.getInternalView();
		gl.glColor4f(1.0f, 1.0f, 1.0f, (view != null && view.isValidForTransition()) || this.getTouchStatus() == PLSceneElementTouchStatus.PLSceneElementTouchStatusOut ? this.getAlpha() : mOverAlpha);
		
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVertexsBuffer);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureCoordsBuffer);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glCullFace(GL10.GL_FRONT);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		
		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glDisable(GL10.GL_BLEND);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
	}
	
	/**touch methods*/
	
	@Override
	public boolean touchDown(Object sender)
	{
		if(super.touchDown(sender))
		{
			if(mOnClick != null && mOnClick.length() > 0)
			{
				PLIInterpreter commandInterpreter = new PLCommandInterpreter();
				if(sender instanceof PLIScene)
					commandInterpreter.interpret(((PLIScene)sender).getInternalView(), mOnClick);
				else if(sender instanceof PLIRenderer)
					commandInterpreter.interpret(((PLIRenderer)sender).getInternalView(), mOnClick);
			}
			return true;
		}
		return false;
	}
	
	/**clone methods*/
	
	@Override
	public boolean clonePropertiesOf(PLIObject object)
	{
		if(super.clonePropertiesOf(object))
		{
			if(object instanceof PLIHotspot)
			{
				PLIHotspot hotspot = (PLIHotspot)object;
				this.setAtv(hotspot.getAtv());
				this.setAth(hotspot.getAth());
				this.setWidth(hotspot.getWidth());
				this.setHeight(hotspot.getHeight());
				this.setOverAlpha(hotspot.getOverAlpha());
				this.setDefaultOverAlpha(hotspot.getDefaultOverAlpha());
			}
			return true;
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mVertexsBuffer = mTextureCoordsBuffer = null;
		mVertexs = null;
		super.finalize();
	}
}