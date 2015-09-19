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

import java.util.List;

public interface PLISceneElement extends PLIRenderableElement
{
	/**property methods*/
	
	long getIdentifier();
	void setIdentifier(long identifier);
	
	boolean isCollisionEnabled();
	void setCollisionEnabled(boolean isCollisionEnabled);
	
	boolean isRecycledByParent();
	void setRecycledByParent(boolean isRecycledByParent);
	
	float[] getVertexs();
	
	PLSceneElementTouchStatus getTouchStatus();
	
	/**texture methods*/
	
	int texturesLength();
	List<PLITexture> getTextures(List<PLITexture> textures);
	PLITexture getTexture(int index);
	boolean addTexture(PLITexture texture);
	boolean insertTexture(PLITexture texture, int index);
	boolean removeTexture(PLITexture texture);
	PLITexture removeTextureAtIndex(int index);
	boolean removeAllTextures();
	
	/**image methods*/
	
	boolean addImage(PLIImage image);
	
	/**interaction methods*/
	
	boolean lockInteraction();
	boolean unlockInteraction();
	
	/**touch methods*/
	
	boolean touchOver(Object sender);
	boolean touchMove(Object sender);
	boolean touchOut(Object sender);
	boolean touchDown(Object sender);
}