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

import com.panoramagl.hotspots.PLIHotspot;

import java.util.List;

public interface PLIPanorama extends PLIScene
{
	/**property methods*/
	
	int getPreviewTilesNumber();
	int getTilesNumber();
	
	int[] getPreviewTilesOrder();
	
	void setPreviewImage(PLIImage image);
	
	/**texture methods*/
	
	int previewTexturesLength();
	List<PLITexture> getPreviewTextures(List<PLITexture> textures);
	PLITexture getPreviewTexture(int index);
	boolean setPreviewTexture(PLITexture texture, int index);
	boolean removePreviewTexture(PLITexture texture);
	PLITexture removePreviewTextureAtIndex(int index);
	boolean removeAllPreviewTextures();
	
	int texturesLength();
	List<PLITexture> getTextures(List<PLITexture> textures);
	PLITexture getTexture(int index);
	boolean setTexture(PLITexture texture, int index);
	boolean removeTexture(PLITexture texture);
	PLITexture removeTextureAtIndex(int index);
	boolean removeAllTextures();
	
	/**hotspot methods*/
	
	int hotspotsLength();
	List<PLIHotspot> getHotspots(List<PLIHotspot> hotspots);
	PLIHotspot getHotspot(int index);
	boolean addHotspot(PLIHotspot hotspot);
	boolean insertHotspot(PLIHotspot hotspot, int index);
	boolean removeHotspot(PLIHotspot hotspot);
	PLIHotspot removeHotspotAtIndex(int index);
	boolean removeAllHotspots();
}