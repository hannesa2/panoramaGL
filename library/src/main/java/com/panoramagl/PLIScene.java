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

import com.panoramagl.ios.structs.CGPoint;
import com.panoramagl.structs.PLPosition;

import java.util.List;

import javax.microedition.khronos.opengles.GL10;

public interface PLIScene extends PLIRenderableElement, PLIReleaseView
{
	/**reset methods*/
	
	void resetAlpha();
	
	/**property methods*/
	
	PLICamera getCamera();
	void setCamera(PLICamera camera);
	
	PLCameraListener getInternalCameraListener();
	void setInternalCameraListener(PLCameraListener listener);
	
	PLIView getInternalView();
	void setInternalView(PLIView view);
	
	boolean isLocked();
	void setLocked(boolean isLocked);
	
	boolean getWaitingForClick();
	void setWaitingForClick(boolean isWaitingForClick);
	
	/**element methods*/
	
	int elementsLength();
	List<PLISceneElement> getElements(List<PLISceneElement> elements);
	PLISceneElement getElement(int index);
	boolean addElement(PLISceneElement element);
	boolean insertElement(PLISceneElement element, int index);
	boolean removeElement(PLISceneElement element);
	PLISceneElement removeElementAtIndex(int index);
	boolean removeAllElements();
	
	/**conversion methods*/
	
	PLPosition convertPointTo3DPoint(GL10 gl, CGPoint point, float z);
	void convertPointTo3DPoint(GL10 gl, CGPoint point, float z, PLPosition result);
}