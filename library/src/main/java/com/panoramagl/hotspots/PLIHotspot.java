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

import com.panoramagl.PLISceneElement;
import com.panoramagl.structs.PLRect;

public interface PLIHotspot extends PLISceneElement
{
	/**property methods*/
	
	float getAtv();
	void setAtv(float atv);
	
	float getAth();
	void setAth(float ath);
	
	float getWidth();
	void setWidth(float width);
	
	float getHeight();
	void setHeight(float height);
	
	String getOnClick();
	void setOnClick(String onClick);
	
	float getOverAlpha();
	void setOverAlpha(float overAlpha);
	
	float getDefaultOverAlpha();
	void setDefaultOverAlpha(float defaultOverAlpha);
	
	PLRect getRect();
	void getRect(PLRect rect);
	
	/**layout methods*/
	
	void setSize(float width, float height);
	
	void setLayout(float pitch, float yaw, float width, float height);
}