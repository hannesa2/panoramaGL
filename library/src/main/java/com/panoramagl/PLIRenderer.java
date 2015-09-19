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

import android.opengl.GLSurfaceView;

import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public interface PLIRenderer extends GLSurfaceView.Renderer, PLIReleaseView
{
	/**property methods*/
	
	int getBackingWidth();
	
	int getBackingHeight();
	
	PLIView getInternalView();
	void setInternalView(PLIView view);
	
	PLIScene getInternalScene();
	void setInternalScene(PLIScene scene);
	
	boolean isRunning();
	
	boolean isRendering();
	
	CGRect getViewport();
	
	CGSize getSize();
	
	PLRendererListener getInternalListener();
	void setInternalListener(PLRendererListener listener);
	
	GL10 getGLContext();
	
	/**buffer methods*/
	
	boolean resizeFromLayer();
	boolean resizeFromLayer(GL11ExtensionPack gl11ep);
	
	/**render methods*/
	
	void render(GL10 gl);
	void renderNTimes(GL10 gl, int times);
	
	/**control methods*/
	
	boolean start();
	boolean stop();
}