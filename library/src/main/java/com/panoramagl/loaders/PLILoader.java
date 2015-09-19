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

package com.panoramagl.loaders;

import com.panoramagl.PLIView;
import com.panoramagl.transitions.PLITransition;

public interface PLILoader
{
	/**property methods*/
	
	PLLoaderListener getInternalListener();
	void setInternalListener(PLLoaderListener listener);
	
	PLLoaderListener getListener();
	void setListener(PLLoaderListener listener);
	
	/**load methods*/
	
	void load(PLIView view);
	void load(PLIView view, PLITransition transition);
	void load(PLIView view, PLITransition transition, float initialPitch, float initialYaw);
}