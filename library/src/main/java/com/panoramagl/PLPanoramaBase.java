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

package com.panoramagl;

import com.panoramagl.computation.PLMath;
import com.panoramagl.computation.PLVector3;
import com.panoramagl.hotspots.PLIHotspot;
import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.utils.PLLog;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public abstract class PLPanoramaBase extends PLSceneBase implements PLIPanorama
{
	/**static variables*/
	
	protected static final PLITexture sNullTexture = new PLNullTexture(); 
	
	/**member variables*/
	
	private PLITexture[] mPreviewTextures, mTextures;
	private List<PLIHotspot> mHotspots;
	
	/**init methods*/
	
	@Override
	protected void initializeValues()
	{
		int previewTiles = this.getPreviewTilesNumber(), tiles = this.getTilesNumber();
		mPreviewTextures = new PLITexture[previewTiles];
		mTextures = new PLITexture[tiles];
		for(int i = 0; i < previewTiles; i++)
			mPreviewTextures[i] = null;
		for(int i = 0; i < tiles; i++)
			mTextures[i] = null;
		mHotspots = new ArrayList<PLIHotspot>();
		super.initializeValues();
	}
	
	/**reset methods*/
	
	@Override
	public void reset()
	{
		if(!this.isLocked())
		{
			super.reset();
			for(int i = 0; i < mHotspots.size(); i++)
				mHotspots.get(i).reset();
		}
	}
	
	@Override
	public void resetAlpha()
	{
		super.resetAlpha();
		this.resetObjectsAlpha(mHotspots);
	}
	
	/**property methods*/
	
	protected PLITexture[] getPreviewTextures()
	{
		return mPreviewTextures;
	}
	
	protected PLITexture[] getTextures()
	{
		return mTextures;
	}
	
	@Override
	public void setPreviewImage(PLIImage image)
	{
		if(image != null)
		{
			synchronized(mPreviewTextures)
			{
				this.removeAllPreviewTextures(true);
				int width = image.getWidth();
				int height = image.getHeight();
				if(PLMath.isPowerOfTwo(width) && (height % width == 0 || width % height == 0))
				{
					int[] previewTilesOrder = this.getPreviewTilesOrder();
					int w, h, tilesLength = this.getPreviewTilesNumber();
					if(tilesLength == 1)
					{
						w = width;
						h = height;
					}
					else
						w = h = (width > height ? height : width);
					for(int i = 0; i < tilesLength; i++)
					{
						try
						{
							PLIImage subImage = new PLImage(image.getSubImage(0, previewTilesOrder[i] * w, w, h));
							mPreviewTextures[i] = new PLTexture(subImage);
						}
						catch(Throwable e)
						{
							this.removeAllPreviewTextures(true);
							PLLog.error("PLPanoramaBase::setPreviewTexture", "setPreviewTexture fails: %s", e);
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public void setAlpha(float alpha)
	{
		super.setAlpha(alpha);
		this.setObjectsAlpha(mHotspots, alpha);
	}
	
	/**texture methods*/
	
	@Override
	public int previewTexturesLength()
	{
		return this.getPreviewTilesNumber();
	}
	
	@Override
	public List<PLITexture> getPreviewTextures(List<PLITexture> textures)
	{
		return this.getTextures(mPreviewTextures, this.getPreviewTilesNumber(), textures);
	}
	
	@Override
	public PLITexture getPreviewTexture(int index)
	{
		return (index >= 0 && index < this.getPreviewTilesNumber() ? mPreviewTextures[index] : null);
	}
	
	@Override
	public boolean setPreviewTexture(PLITexture texture, int index)
	{
		return this.setTexture(mPreviewTextures, this.getPreviewTilesNumber(), texture, index, true);
	}
	
	protected boolean setPreviewTexture(PLITexture texture, int index, boolean recycleTextureByParent)
	{
		return this.setTexture(mPreviewTextures, this.getPreviewTilesNumber(), texture, index, recycleTextureByParent);
	}
	
	@Override
	public boolean removePreviewTexture(PLITexture texture)
	{
		return this.removeTexture(mPreviewTextures, this.getPreviewTilesNumber(), texture, false);
	}
	
	protected boolean removePreviewTexture(PLITexture texture, boolean recycleTextureByParent)
	{
		return this.removeTexture(mPreviewTextures, this.getPreviewTilesNumber(), texture, recycleTextureByParent);
	}
	
	@Override
	public PLITexture removePreviewTextureAtIndex(int index)
	{
		return this.removeTextureAtIndex(mPreviewTextures, this.getPreviewTilesNumber(), index, false);
	}
	
	protected PLITexture removePreviewTextureAtIndex(int index, boolean recycleTextureByParent)
	{
		return this.removeTextureAtIndex(mPreviewTextures, this.getPreviewTilesNumber(), index, recycleTextureByParent);
	}
	
	@Override
	public boolean removeAllPreviewTextures()
	{
		return this.removeTextures(mPreviewTextures, this.getPreviewTilesNumber(), false);
	}
	
	protected boolean removeAllPreviewTextures(boolean recycleTexturesByParent)
	{
		return this.removeTextures(mPreviewTextures, this.getPreviewTilesNumber(), recycleTexturesByParent);
	}
	
	@Override
	public int texturesLength()
	{
		return this.getTilesNumber();
	}
	
	@Override
	public List<PLITexture> getTextures(List<PLITexture> textures)
	{
		return this.getTextures(mTextures, this.getTilesNumber(), textures);
	}
	
	@Override
	public PLITexture getTexture(int index)
	{
		return (index >= 0 && index < this.getTilesNumber() ? mTextures[index] : null);
	}
	
	@Override
	public boolean setTexture(PLITexture texture, int index)
	{
		return this.setTexture(mTextures, this.getTilesNumber(), texture, index, true);
	}
	
	protected boolean setTexture(PLITexture texture, int index, boolean recycleTextureByParent)
	{
		return this.setTexture(mTextures, this.getTilesNumber(), texture, index, recycleTextureByParent);
	}
	
	@Override
	public boolean removeTexture(PLITexture texture)
	{
		return this.removeTexture(mTextures, this.getTilesNumber(), texture, false);
	}
	
	protected boolean removeTexture(PLITexture texture, boolean recycleTextureByParent)
	{
		return this.removeTexture(mTextures, this.getTilesNumber(), texture, recycleTextureByParent);
	}
	
	@Override
	public PLITexture removeTextureAtIndex(int index)
	{
		return this.removeTextureAtIndex(mTextures, this.getTilesNumber(), index, false);
	}
	
	protected PLITexture removeTextureAtIndex(int index, boolean recycleTextureByParent)
	{
		return this.removeTextureAtIndex(mTextures, this.getTilesNumber(), index, recycleTextureByParent);
	}
	
	@Override
	public boolean removeAllTextures()
	{
		return this.removeTextures(mTextures, this.getTilesNumber(), false);
	}
	
	protected boolean removeAllTextures(boolean recycleTexturesByParent)
	{
		return this.removeTextures(mTextures, this.getTilesNumber(), recycleTexturesByParent);
	}
	
	protected List<PLITexture> getTextures(PLITexture[] textures, int texturesLength, List<PLITexture> result)
	{
		if(result != null)
		{
			synchronized(textures)
			{
				result.clear();
				for(int i = 0; i < texturesLength; i++)
				{
					PLITexture texture = textures[i];
					result.add(texture != null ? texture : sNullTexture);
				}
			}
		}
		return result;
	}
	
	protected boolean setTexture(PLITexture[] textures, int texturesLength, PLITexture texture, int index, boolean recycleTextureByParent)
	{
		if(texture != null && index >= 0 && index < texturesLength)
		{
	        synchronized(textures)
	        {
	        	PLITexture currentTexture = textures[index];
				if(recycleTextureByParent && currentTexture != null && currentTexture.isRecycledByParent())
					currentTexture.recycle();
				textures[index] = texture;
				return true;
			}
		}
		return false;
	}
	
	protected boolean removeTexture(PLITexture[] textures, int texturesLength, PLITexture texture, boolean recycleTextureByParent)
	{
		if(texture != null)
		{
			for(int i = 0; i < texturesLength; i++)
			{
				if(textures[i] == texture)
				{
					synchronized(textures)
					{
						if(recycleTextureByParent && texture.isRecycledByParent())
							texture.recycle();
						textures[i] = null;
						return true;
					}
				}
			}
		}
		return false;
	}
	
	protected PLITexture removeTextureAtIndex(PLITexture[] textures, int texturesLength, int index, boolean recycleTextureByParent)
	{
		if(index >= 0 && index < texturesLength)
		{
			PLITexture texture = textures[index];
			if(texture != null)
			{
				synchronized(textures)
				{
					if(recycleTextureByParent && texture.isRecycledByParent())
						texture.recycle();
					textures[index] = null;
					return texture;
				}
			}
		}
		return null;
	}
	
	protected boolean removeTextures(PLITexture[] textures, int texturesLength, boolean recycleTexturesByParent)
	{
		synchronized(textures)
		{
			boolean result = false;
			for(int i = 0; i < texturesLength; i++)
			{
				PLITexture texture = textures[i];
				if(texture != null)
				{
					if(recycleTexturesByParent && texture.isRecycledByParent())
						texture.recycle();
					textures[i] = null;
					result = true;
				}
			}
			return result;
		}
	}
	
	/**hotspot methods*/
	
	@Override
	public int hotspotsLength()
	{
		return mHotspots.size();
	}
	
	@Override
	public List<PLIHotspot> getHotspots(List<PLIHotspot> hotspots)
	{
		if(hotspots != null && mHotspots.size() > 0)
		{
			synchronized(mHotspots)
			{
				hotspots.clear();
				hotspots.addAll(mHotspots);
			}
		}
		return hotspots;
	}
	
	@Override
	public PLIHotspot getHotspot(int index)
	{
		return (index >= 0 && index < mHotspots.size() ? mHotspots.get(index) : null);
	}
	
	@Override
	public boolean addHotspot(PLIHotspot hotspot)
	{
		if(hotspot != null)
		{
			synchronized(mHotspots)
			{
				mHotspots.add(hotspot);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean insertHotspot(PLIHotspot hotspot, int index)
	{
		if(hotspot != null && index >= 0 && index <= mHotspots.size())
		{
			synchronized(mHotspots)
			{
				mHotspots.add(index, hotspot);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeHotspot(PLIHotspot hotspot)
	{
		if(hotspot != null && mHotspots.contains(hotspot))
		{
			synchronized(mHotspots)
			{
				mHotspots.remove(hotspot);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public PLIHotspot removeHotspotAtIndex(int index)
	{
		if(index >= 0 && index < mHotspots.size())
		{
			synchronized(mHotspots)
			{
				return mHotspots.remove(index);
			}
		}
		return null;
	}
	
	@Override
	public boolean removeAllHotspots()
	{
		if(mHotspots.size() > 0)
		{
			synchronized(mHotspots)
			{
				mHotspots.clear();
				return true;
			}
		}
		return false;
	}
	
	protected boolean removeAllHotspots(boolean recycleHotspotsByParent)
	{
		int hotspotsLength = mHotspots.size();
		if(hotspotsLength > 0)
		{
			synchronized(mHotspots)
			{
				if(recycleHotspotsByParent)
				{
					for(int i = 0; i < hotspotsLength; i++)
					{
						PLIHotspot hotspot = mHotspots.get(i);
						if(hotspot.isRecycledByParent())
							hotspot.clear();
					}
				}
				mHotspots.clear();
				return true;
			}
		}
		return false;
	}
	
	/**clear methods*/
	
	@Override
	protected void internalClear()
	{
	    this.removeAllPreviewTextures(true);
	    this.removeAllTextures(true);
	    this.removeAllHotspots(true);
	    super.internalClear();
	}
	
	/**render methods*/
	
	@Override
	protected void renderElements(GL10 gl, PLIRenderer renderer)
	{
		super.renderElements(gl, renderer);
		this.renderRenderableElements(gl, renderer, mHotspots);
	}
	
	/**collision methods*/
	
	@Override
	protected int checkCollisionsWithRay(GL10 gl, PLVector3[] ray, CGPoint screenPoint, boolean isMoving)
	{
		return (super.checkCollisionsWithRay(gl, ray, screenPoint, isMoving) + this.checkSceneElementsCollisionWithRay(gl, mHotspots, ray, screenPoint, isMoving));
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		super.finalize();
		mPreviewTextures = mTextures = null;
		mHotspots = null;
	}
}