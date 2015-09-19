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

import com.panoramagl.enumerations.PLSceneElementTouchStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class PLSceneElementBase extends PLRenderableElementBase implements PLISceneElement
{
	/**member variables*/
	
	private long mIdentifier;
	private boolean mIsCollisionEnabled, mIsInteractionLocked, mIsRecycledByParent;
	private PLSceneElementTouchStatus mTouchStatus;
	private List<PLITexture> mTextures;
	
	/**init methods*/
	
	public PLSceneElementBase()
	{
		super();
	}
	
	public PLSceneElementBase(PLITexture texture)
	{
		super();
		this.addTexture(texture);
	}
	
	public PLSceneElementBase(long identifier)
	{
		super();
		mIdentifier = identifier;
	}
	
	public PLSceneElementBase(long identifier, PLITexture texture)
	{
		this(identifier);
		this.addTexture(texture);
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		mIdentifier = -1;
		mIsCollisionEnabled = mIsRecycledByParent = true;
		mIsInteractionLocked = false;
		mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusOut;
		mTextures = new ArrayList<PLITexture>(5);
	}
	
	/**reset methods*/
	
	@Override
	public void reset()
	{
		super.reset();
		mIsInteractionLocked = false;
		mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusOut;
	}
	
	/**property methods*/
	
	@Override
	public long getIdentifier()
	{
		return mIdentifier;
	}
	
	@Override
	public void setIdentifier(long identifier)
	{
		mIdentifier = identifier;
	}
	
	@Override
	public boolean isCollisionEnabled()
	{
		return mIsCollisionEnabled;
	}
	
	@Override
	public void setCollisionEnabled(boolean isCollisionEnabled)
	{
		mIsCollisionEnabled = isCollisionEnabled;
	}
	
	protected void setInternalInteractionLocked(boolean isInteractionLocked)
	{
		mIsInteractionLocked = isInteractionLocked;
	}
	
	@Override
	public boolean isRecycledByParent()
	{
		return mIsRecycledByParent;
	}
	
	@Override
	public void setRecycledByParent(boolean isRecycledByParent)
	{
		mIsRecycledByParent = isRecycledByParent;
	}
	
	@Override
	public PLSceneElementTouchStatus getTouchStatus()
	{
		return mTouchStatus;
	}
	
	protected void setInternalTouchStatus(PLSceneElementTouchStatus touchStatus)
	{
		mTouchStatus = touchStatus;
	}
	
	protected List<PLITexture> getTextures()
	{
		return mTextures;
	}
	
	/**texture methods*/
	
	@Override
	public int texturesLength()
	{
		return mTextures.size();
	}
	
	@Override
	public List<PLITexture> getTextures(List<PLITexture> textures)
	{
		if(textures != null)
		{
			synchronized(mTextures)
			{
				textures.clear();
				textures.addAll(mTextures);
			}
		}
		return textures;
	}
	
	@Override
	public PLITexture getTexture(int index)
	{
		return (index >= 0 && index < mTextures.size() ? mTextures.get(index) : null);
	}
	
	@Override
	public boolean addTexture(PLITexture texture)
	{
		if(texture != null)
		{
			synchronized(mTextures)
			{
				mTextures.add(texture);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean insertTexture(PLITexture texture, int index)
	{
		if(texture != null && index >= 0 && index <= mTextures.size())
		{
			synchronized(mTextures)
			{
				mTextures.add(index, texture);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean removeTexture(PLITexture texture)
	{
		if(texture != null && mTextures.contains(texture))
		{
			synchronized(mTextures)
			{
				mTextures.remove(texture);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public PLITexture removeTextureAtIndex(int index)
	{
		if(index >= 0 && index < mTextures.size())
		{
			synchronized(mTextures)
			{
				return mTextures.remove(index);
			}
		}
		return null;
	}
	
	@Override
	public boolean removeAllTextures()
	{
		if(mTextures.size() > 0)
		{
			synchronized(mTextures)
			{
				mTextures.clear();
				return true;
			}
		}
		return false;
	}
	
	protected boolean removeAllTextures(boolean recycleTexturesByParent)
	{
		int texturesLength = mTextures.size();
		if(texturesLength > 0)
		{
			synchronized(mTextures)
			{
				if(recycleTexturesByParent)
				{
					for(int i = 0; i < texturesLength; i++)
					{
						PLITexture texture = mTextures.get(i);
						if(texture.isRecycledByParent())
							texture.recycle();
					}
				}
				mTextures.clear();
				return true;
			}
		}
		return false;
	}
	
	/**image methods*/
	
	@Override
	public boolean addImage(PLIImage image)
	{
		if(image != null)
		{
			synchronized(mTextures)
			{
				mTextures.add(new PLTexture(image));
				return true;
			}
		}
		return false;
	}
	
	/**clear methods*/
	
	@Override
	protected void internalClear()
	{
		this.removeAllTextures(true);
	}
	
	/**interaction methods*/
	
	@Override
	public boolean lockInteraction()
	{
		if(!mIsInteractionLocked)
		{
			mIsInteractionLocked = true;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean unlockInteraction()
	{
		if(mIsInteractionLocked)
		{
			mIsInteractionLocked = false;
			return true;
		}
		return false;
	}
	
	/**touch methods*/
	
	@Override
	public boolean touchOver(Object sender)
	{
		if(!mIsInteractionLocked && mTouchStatus != PLSceneElementTouchStatus.PLSceneElementTouchStatusOver)
		{
			mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusOver;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchMove(Object sender)
	{
		if(!mIsInteractionLocked)
		{
			mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusMove;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchOut(Object sender)
	{
		if(!mIsInteractionLocked && mTouchStatus != PLSceneElementTouchStatus.PLSceneElementTouchStatusOut)
		{
			mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusOut;
			return true;
		}
		return false;
	}
	
	@Override
	public boolean touchDown(Object sender)
	{
		if(!mIsInteractionLocked && mTouchStatus != PLSceneElementTouchStatus.PLSceneElementTouchStatusDown)
		{
			mTouchStatus = PLSceneElementTouchStatus.PLSceneElementTouchStatusDown;
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
			if(object instanceof PLISceneElement)
			{
				PLISceneElement element = (PLISceneElement)object;
				this.setIdentifier(element.getIdentifier());
				this.setCollisionEnabled(element.isCollisionEnabled());
				this.setRecycledByParent(element.isRecycledByParent());
				synchronized(mTextures)
				{
					element.getTextures(mTextures);
				}
			}
			return true;
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		try
		{
			this.clear();
		}
		catch(Throwable e)
		{
		}
		super.finalize();
	}
}