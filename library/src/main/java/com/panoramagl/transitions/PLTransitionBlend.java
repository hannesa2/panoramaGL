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

package com.panoramagl.transitions;

import com.panoramagl.PLICamera;
import com.panoramagl.PLIPanorama;
import com.panoramagl.PLIView;
import com.panoramagl.enumerations.PLTransitionProcessingType;

public class PLTransitionBlend extends PLTransitionBase
{
	/**member variables*/
	
	private float mZoomFactor;
	private float mNewPanoramaBlendStep;
	private boolean mIsFirstTime;
	
	/**init methods*/
	
	public PLTransitionBlend(float interval)
	{
		super(interval);
	}
	
	public PLTransitionBlend(float interval, float zoomFactor)
	{
		super(interval);
		this.setZoomFactor(zoomFactor);
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		mZoomFactor = -1.0f;
		mIsFirstTime = true;
	}
	
	/**property methods*/
	
	public float getZoomFactor()
	{
		return mZoomFactor;
	}
	
	public void setZoomFactor(float zoomFactor)
	{
		if((zoomFactor >= 0.0f && zoomFactor <= 1.0f) || zoomFactor == -1.0f)
			mZoomFactor = zoomFactor;
	}
	
	/**process methods*/
	
	protected void afterStarting(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera)
	{
		mNewPanoramaBlendStep = newPanorama.getDefaultAlpha() / (this.getInterval() * this.iterationsPerSecond());
		newPanorama.setAlpha(0.0f);
		currentPanoramaCamera.clonePropertiesOf(currentPanorama.getCamera());
		newPanoramaCamera.clonePropertiesOf(newPanorama.getCamera());
		if(mZoomFactor != -1.0f && mZoomFactor > currentPanoramaCamera.getZoomFactor())
			currentPanoramaCamera.setZoomFactor(mZoomFactor, true);
	}
	
	@Override
	protected PLTransitionProcessingType internalProcess(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, PLICamera currentPanoramaCamera, PLICamera newPanoramaCamera)
	{
		if(currentPanorama.getCamera().isAnimating() || currentPanoramaCamera.isAnimating())
			return PLTransitionProcessingType.PLTransitionProcessingTypeWaiting;
		else if(mIsFirstTime)
		{
			mIsFirstTime = false;
			return PLTransitionProcessingType.PLTransitionProcessingTypeBegin;
		}
		return this.processPanorama(newPanorama, mNewPanoramaBlendStep);
	}
	
	protected PLTransitionProcessingType processPanorama(PLIPanorama panorama, float blendStep)
	{
		float alpha = panorama.getAlpha() + blendStep, defaultAlpha = panorama.getDefaultAlpha();
		panorama.setAlpha(alpha);
		this.setProgressPercentage(Math.min((int)(alpha * 100.0f / defaultAlpha), 100));
		return (alpha >= defaultAlpha ? PLTransitionProcessingType.PLTransitionProcessingTypeEnd : PLTransitionProcessingType.PLTransitionProcessingTypeRunning);
	}
	
	@Override
	protected void afterFinishing(PLIView view, PLIPanorama currentPanorama, PLIPanorama newPanorama, boolean isEnd)
	{
		if(!isEnd)
			currentPanorama.resetAlpha();
		newPanorama.resetAlpha();
	}
}