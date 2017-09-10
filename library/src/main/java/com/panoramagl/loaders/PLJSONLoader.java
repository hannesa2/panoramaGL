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

import android.graphics.Bitmap;
import android.os.Handler;

import com.panoramagl.PLBlankPanorama;
import com.panoramagl.PLConstants;
import com.panoramagl.PLCubicPanorama;
import com.panoramagl.PLCylindricalPanorama;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIImage;
import com.panoramagl.PLIPanorama;
import com.panoramagl.PLIQuadricPanorama;
import com.panoramagl.PLITexture;
import com.panoramagl.PLIView;
import com.panoramagl.PLImage;
import com.panoramagl.PLSpherical2Panorama;
import com.panoramagl.PLSphericalPanorama;
import com.panoramagl.PLTexture;
import com.panoramagl.downloaders.PLFileDownloaderListener;
import com.panoramagl.downloaders.PLHTTPFileDownloader;
import com.panoramagl.downloaders.PLLocalFileDownloader;
import com.panoramagl.enumerations.PLCameraParameterType;
import com.panoramagl.enumerations.PLCubeFaceOrientation;
import com.panoramagl.enumerations.PLPanoramaType;
import com.panoramagl.enumerations.PLTextureColorFormat;
import com.panoramagl.enumerations.PLViewParameterType;
import com.panoramagl.hotspots.PLHotspot;
import com.panoramagl.hotspots.PLIHotspot;
import com.panoramagl.structs.PLCameraParameters;
import com.panoramagl.structs.PLViewParameters;
import com.panoramagl.transitions.PLITransition;
import com.panoramagl.transitions.PLTransitionListener;
import com.panoramagl.utils.PLLog;
import com.panoramagl.utils.PLUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PLJSONLoader extends PLLoaderBase
{
	/**member variables*/
	
	private PLIView mView;
	private PLITransition mTransition;
	private float mInitialPitch, mInitialYaw;
	private byte[] mJSONData;
	private String mURL;
	private JSONObject mJSON;
	private boolean mIsPreloadingImages;
	private PLViewParameters mKeepParameters;
	private Map<String, PLITexture> mHotspotTextures;
	
	/**init methods*/
	
	public PLJSONLoader(byte[] jsonData)
	{
		super();
		mJSONData = jsonData;
	}
	
	public PLJSONLoader(String url)
	{
		super();
		mURL = url.trim();
	}
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		mView = null;
		mTransition = null;
		mInitialPitch = mInitialYaw = PLConstants.kFloatUndefinedValue;
		mJSONData = null;
		mURL = null;
		mJSON = null;
		mIsPreloadingImages = true;
		mKeepParameters = null;
		mHotspotTextures = new HashMap<String, PLITexture>();
	}
	
	/**property methods*/
	
	protected PLIView getView()
	{
		return mView;
	}
	
	protected void setView(PLIView view)
	{
		mView = view;
	}
	
	protected PLITransition getTransition()
	{
		return mTransition;
	}
	
	protected void setTransition(PLITransition transition)
	{
		mTransition = transition;
	}
	
	protected byte[] getJSONData()
	{
		return mJSONData;
	}
	
	protected void setJSONData(byte[] jsonData)
	{
		mJSONData = jsonData;
	}
	
	protected String getURL()
	{
		return mURL;
	}
	
	protected void setURL(String url)
	{
		mURL = url;
	}
	
	protected JSONObject getJSON()
	{
		return mJSON;
	}
	
	protected void setJSON(JSONObject json)
	{
		mJSON = json;
	}
	
	protected boolean isPreloadingImages()
	{
		return mIsPreloadingImages;
	}
	
	protected void setPreloadingImages(boolean isPreloadingImages)
	{
		mIsPreloadingImages = isPreloadingImages;
	}
	
	protected Map<String, PLITexture> getHotspotTextures()
	{
		return mHotspotTextures;
	}
	
	/**utility methods*/
	
	protected String buildURL(String url, String urlBase)
	{
		url = url.trim();
		if(url.indexOf("://") == -1)
			url = urlBase + (urlBase.endsWith("/") || url.startsWith("/") ? url : "/" + url);
		return url;
	}
	
	protected boolean isHTTPURL(String url)
	{
		return (url.startsWith("http://") || url.startsWith("https://"));
	}
	
	protected PLIImage getLocalImage(String url, PLTextureColorFormat colorFormat)
	{
		Bitmap bitmap = PLUtils.getBitmap(mView.getContext().getApplicationContext(), url, colorFormat);
		return (bitmap != null ? new PLImage(bitmap, false) : null);
	}
	
	protected PLIImage getLocalImageAsynchronously(String url, PLTextureColorFormat colorFormat)
	{
		PLIImage result = new PLImage();
		mView.getDownloadManager().add(new PLLocalFileDownloader(mView.getContext().getApplicationContext(), url, new PLImageFileDownloaderListener(result, colorFormat)));
		return result;
	}
	
	/**json methods*/
	
	protected void requestJSON(PLFileDownloaderListener listener)
	{
		try
		{
			if(mURL != null)
			{
				if(this.isHTTPURL(mURL))
					new PLHTTPFileDownloader(mURL, listener).downloadAsynchronously();
				else
					new PLLocalFileDownloader(mView.getContext().getApplicationContext(), mURL, listener).downloadAsynchronously();
			}
			else if(mJSONData != null)
				new Thread(new PLDataRunnable(listener, mURL, mJSONData, System.currentTimeMillis())).start();
		    else
		    	listener.didErrorDownload(mURL, "JSON string is empty", -1, null);
		}
		catch(Throwable e)
		{
			PLLog.error("PLJSONLoader::requestJSON", e);
			listener.didErrorDownload(mURL, e.getMessage(), -1, null);
		}
	}
	
	protected void parseJSON(byte[] jsonData)
	{
		try
		{
			mJSON = new JSONObject(new String(jsonData, "utf-8"));
	        String urlBase = mJSON.getString("urlBase").trim();
	        if(urlBase == null)
	        	throw new RuntimeException("urlBase property not exists");
	        else if(!this.isHTTPURL(urlBase) && !urlBase.startsWith("res://") && !urlBase.startsWith("file://"))
	        	throw new RuntimeException("urlBase property is wrong");
	        String type = mJSON.getString("type").trim();
	        final PLIPanorama panorama;
	        PLPanoramaType panoramaType = PLPanoramaType.PLPanoramaTypeUnknow;
	        if(type != null)
	        {
	        	if(type.equals("spherical"))
	            {
	                panoramaType = PLPanoramaType.PLPanoramaTypeSpherical;
	                panorama = new PLSphericalPanorama();
	            }
	            else if(type.equals("spherical2"))
	            {
	                panoramaType = PLPanoramaType.PLPanoramaTypeSpherical2;
	                panorama = new PLSpherical2Panorama();
	            }
	            else if(type.equals("cubic"))
	            {
	                panoramaType = PLPanoramaType.PLPanoramaTypeCubic;
	                panorama = new PLCubicPanorama();
	            }
	            else if(type.equals("cylindrical"))
	            {
	            	panoramaType = PLPanoramaType.PLPanoramaTypeCylindrical;
	            	panorama = new PLCylindricalPanorama();
	            }
	            else
	            	throw new RuntimeException("Panorama type is wrong");
	        }
	        else
	        	throw new RuntimeException("type property not exists");
	        PLTextureColorFormat colorFormat = PLTextureColorFormat.PLTextureColorFormatRGBA8888;
	        if(mJSON.has("imageColorFormat"))
	        {
	        	String imageColorFormat = mJSON.getString("imageColorFormat").trim().toUpperCase(Locale.US);
	        	if(imageColorFormat.equals("RGB565"))
	        		colorFormat = PLTextureColorFormat.PLTextureColorFormatRGB565;
	        	else if(imageColorFormat.equals("RGBA4444"))
	        		colorFormat = PLTextureColorFormat.PLTextureColorFormatRGBA4444;
	        }
	        if(panoramaType == PLPanoramaType.PLPanoramaTypeCylindrical && mJSON.has("height"))
        		((PLCylindricalPanorama)panorama).setHeight((float)mJSON.getDouble("height"));
	        if(mJSON.has("divisions") && panorama instanceof PLIQuadricPanorama)
	        {
	        	JSONObject divisions = mJSON.getJSONObject("divisions");
	        	if(divisions != null)
	        	{
	        		PLIQuadricPanorama quadricPanorama = (PLIQuadricPanorama)panorama;
	        		if(divisions.has("preview"))
	        			quadricPanorama.setPreviewDivs(divisions.getInt("preview"));
	        		if(divisions.has("panorama"))
	        			quadricPanorama.setDivs(divisions.getInt("panorama"));
	        	}
	        }
	        PLIPanorama oldPanorama = mView.getPanorama();
	        mKeepParameters = (oldPanorama != null && !(oldPanorama instanceof PLBlankPanorama) && mJSON.has("keep") ? PLViewParameterType.checkViewParametersWithStringMask(mJSON.getString("keep")) : PLViewParameterType.checkViewParametersWithMask(PLViewParameterType.PLViewParameterTypeNone));
	        if(!mKeepParameters.reset && mJSON.has("reset"))
	        {
	        	JSONObject reset = mJSON.getJSONObject("reset");
	        	if(reset != null)
	        	{
	        		if(reset.has("enabled"))
	        			mView.setResetEnabled(reset.getBoolean("enabled"));
	        		if(reset.has("numberOfTouches"))
	        			mView.setNumberOfTouchesForReset(reset.getInt("numberOfTouches"));
	        		if(reset.has("shake"))
			        {
			        	JSONObject shake = reset.getJSONObject("shake");
			        	if(shake != null)
			        	{
				        	if(shake.has("enabled"))
				        		mView.setShakeResetEnabled(shake.getBoolean("enabled"));
				        	if(shake.has("threshold"))
				        		mView.setShakeThreshold((float)shake.getDouble("threshold"));
			        	}
			        }
	        	}
	        }
	        if(!mKeepParameters.scrolling && mJSON.has("scrolling"))
	        {
	        	JSONObject scrolling = mJSON.getJSONObject("scrolling");
	        	if(scrolling != null)
	        	{
		        	if(scrolling.has("enabled"))
		        		mView.setScrollingEnabled(scrolling.getBoolean("enabled"));
		        	if(scrolling.has("minDistanceToEnableScrolling"))
		        		mView.setMinDistanceToEnableScrolling(scrolling.getInt("minDistanceToEnableScrolling"));
	        	}
	        }
	        if(!mKeepParameters.inertia && mJSON.has("inertia"))
	        {
	        	JSONObject inertia = mJSON.getJSONObject("inertia");
	        	if(inertia != null)
	        	{
		        	if(inertia.has("enabled"))
		        		mView.setInertiaEnabled(inertia.getBoolean("enabled"));
		        	if(inertia.has("interval"))
		        		mView.setInertiaInterval((float)inertia.getDouble("interval"));
	        	}
	        }
	        if(!mKeepParameters.accelerometer && mJSON.has("accelerometer"))
	        {
	        	JSONObject accelerometer = mJSON.getJSONObject("accelerometer");
	        	if(accelerometer != null)
	        	{
		        	if(accelerometer.has("enabled"))
		        		mView.setAccelerometerEnabled(accelerometer.getBoolean("enabled"));
		        	if(accelerometer.has("interval"))
		        		mView.setAccelerometerInterval((float)accelerometer.getDouble("interval"));
		        	if(accelerometer.has("sensitivity"))
		        		mView.setAccelerometerSensitivity((float)accelerometer.getDouble("sensitivity"));
		        	if(accelerometer.has("leftRightEnabled"))
		        		mView.setAccelerometerLeftRightEnabled(accelerometer.getBoolean("leftRightEnabled"));
		        	if(accelerometer.has("upDownEnabled"))
		        		mView.setAccelerometerUpDownEnabled(accelerometer.getBoolean("upDownEnabled"));
	        	}
	        }
	        boolean hasPreviewImage = false;
	        JSONObject images = mJSON.getJSONObject("images");
	        if(images != null)
	        {
	        	if(images.has("preview"))
	        	{
	        		String previewURL = this.buildURL(images.getString("preview"), urlBase);
	        		if(this.isHTTPURL(previewURL))
	        		{
	        			byte[] previewData = new PLHTTPFileDownloader(previewURL).download();
	        			if(previewData != null)
	        			{
	        				panorama.setPreviewImage(new PLImage(previewData));
	        				hasPreviewImage = true;
	        			}
	        		}
	        		else
	        		{
	        			PLIImage previewImage = this.getLocalImage(previewURL, colorFormat);
	        			if(previewImage != null)
	        			{
	        				panorama.setPreviewImage(previewImage);
	        				hasPreviewImage = true;
	        			}
	        		}
	        	}
	        	if(mHotspotTextures.size() > 0)
		        	mHotspotTextures.clear();
		        JSONArray hotspots = mJSON.getJSONArray("hotspots");
		        if(hotspots != null)
		        {
		            for(int i = 0, hotspotsCount = hotspots.length(); i < hotspotsCount; i++)
		            {
		                JSONObject hotspot = hotspots.getJSONObject(i);
		                if(hotspot != null)
		                {
		                    if(hotspot.has("image"))
		                    {
		                    	long identifier = (hotspot.has("id") ? hotspot.getLong("id") : -1);
		                        float atv = (hotspot.has("atv") ? (float)hotspot.getDouble("atv") : 0.0f);
		                        float ath = (hotspot.has("ath") ? (float)hotspot.getDouble("ath") : 0.0f);
		                        float width = (hotspot.has("width") ? (float)hotspot.getDouble("width") : PLConstants.kDefaultHotspotSize);
		                        float height = (hotspot.has("height") ? (float)hotspot.getDouble("height") : PLConstants.kDefaultHotspotSize);
		                    	PLIHotspot currentHotspot = new PLHotspot(identifier, atv, ath, width, height);
		                    	if(hotspot.has("alpha"))
		                    	{
		                    		currentHotspot.setDefaultAlpha((float)hotspot.getDouble("alpha"));
		                    		currentHotspot.setAlpha(currentHotspot.getDefaultAlpha());
		                    	}
		                    	if(hotspot.has("overAlpha"))
		                    	{
		                    		currentHotspot.setDefaultOverAlpha((float)hotspot.getDouble("overAlpha"));
		                    		currentHotspot.setOverAlpha(currentHotspot.getDefaultOverAlpha());
		                    	}
		                    	if(hotspot.has("onClick"))
		                    		currentHotspot.setOnClick(hotspot.getString("onClick"));
		                    	this.loadHotspotTexture(currentHotspot, hotspot.getString("image"), urlBase, colorFormat);
		            			panorama.addHotspot(currentHotspot);
		                    }
		                }
		            }
		            mHotspotTextures.clear();
		        }
	        	if(panoramaType == PLPanoramaType.PLPanoramaTypeCubic)
	        	{
	            	PLCubicPanorama cubicPanorama = (PLCubicPanorama)panorama;
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationFront, images, "front", urlBase, hasPreviewImage, colorFormat);
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationBack, images, "back", urlBase, hasPreviewImage, colorFormat);
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationLeft, images, "left", urlBase, hasPreviewImage, colorFormat);
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationRight, images, "right", urlBase, hasPreviewImage, colorFormat);
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationUp, images, "up", urlBase, hasPreviewImage, colorFormat);
	            	this.loadCubicPanoramaImage(cubicPanorama, PLCubeFaceOrientation.PLCubeFaceOrientationDown, images, "down", urlBase, hasPreviewImage, colorFormat);
	        	}
	        	else
	        	{
	        		if(images.has("image"))
	        		{
	        			String imageURL = this.buildURL(images.getString("image"), urlBase);
	        			if(this.isHTTPURL(imageURL))
	        				mView.getDownloadManager().add(new PLHTTPFileDownloader(imageURL, new PLPanoramaImageFileDownloaderListener(panorama, colorFormat)));
	        			else if(panoramaType == PLPanoramaType.PLPanoramaTypeSpherical2)
	        				((PLSpherical2Panorama)panorama).setImage(this.getLocalImage(imageURL, colorFormat));
	        			else if(panorama instanceof PLIQuadricPanorama)
	        				((PLIQuadricPanorama)panorama).setImage(this.getLocalImageAsynchronously(imageURL, colorFormat));
	        		}
	        		else if(!hasPreviewImage)
	        			throw new RuntimeException("images.image and images.preview properties not exist");
	        	}
	        	if(images.has("preload"))
	        		mIsPreloadingImages = images.getBoolean("preload");
	        }
	        else
	        	throw new RuntimeException("images property not exists");
	        if(mIsPreloadingImages)
	        	mView.getDownloadManager().start();
	        new Handler(mView.getContext().getMainLooper()).post
	        (
	        	new Runnable()
	        	{
					@Override
					public void run()
					{
						mView.reset(false);
						if(mTransition != null && mView.getPanorama() != null)
						{
							mTransition.getListeners().add
							(
								new PLTransitionListener()
								{
									@Override
									public boolean isRemovableListener()
									{
										return true;
									}
									
									@Override
									public void didBeginTransition(PLITransition transition)
									{
										synchronized(transition)
										{
											parseCameraJSON(transition.getNewPanorama());
										}
									}
									
									@Override
									public void didProcessTransition(PLITransition transition, int progressPercentage)
									{
									}
								
									@Override
									public void didStopTransition(PLITransition transition, int progressPercentage)
									{
										if(parseSensorialRotationJSON())
										{
											mView.getDownloadManager().removeAll();
											didStop(true);
										}
									}
									
									@Override
									public void didEndTransition(PLITransition transition)
									{
										if(parseSensorialRotationJSON())
										{
											if(!mIsPreloadingImages)
												mView.getDownloadManager().start();
											didComplete(true);
										}
									}
								}
							);
							mView.startTransition(mTransition, panorama);
						}
						else
						{
							if(parseCameraJSON(panorama))
							{
								mView.setPanorama(panorama);
								if(parseSensorialRotationJSON())
								{
									if(!mIsPreloadingImages)
										mView.getDownloadManager().start();
									didComplete(false);
								}
							}
						}
					}
	        	}
	        );
		}
		catch(Throwable e)
		{
			this.didError(e);
		}
	}
	
	protected boolean parseCameraJSON(PLIPanorama panorama)
	{
		try
		{
			JSONObject camera = mJSON.getJSONObject("camera");
	        if(camera != null)
	        {
	        	PLIPanorama oldPanorama = mView.getPanorama();
	        	PLICamera oldCamera = (oldPanorama != null && !(oldPanorama instanceof PLBlankPanorama) ? oldPanorama.getCamera() : null);
	        	PLICamera currentCamera = panorama.getCamera();
	        	PLCameraParameters keep = (oldCamera != null && camera.has("keep") ? PLCameraParameterType.checkCameraParametersWithStringMask(camera.getString("keep")) : PLCameraParameterType.checkCameraParametersWithMask(PLCameraParameterType.PLCameraParameterTypeNone));
	        	float pitch = currentCamera.getInitialPitch(), yaw = currentCamera.getInitialYaw();
        		if(keep.atvMin)
        			currentCamera.setPitchMin(oldCamera.getPitchMin());
        		else if(camera.has("atvMin"))
	        		currentCamera.setPitchMin((float)camera.getDouble("atvMin"));
        		if(keep.atvMax)	
        			currentCamera.setPitchMax(oldCamera.getPitchMax());
        		else if(camera.has("atvMax"))
		        	currentCamera.setPitchMax((float)camera.getDouble("atvMax"));
        		if(keep.athMin)
        			currentCamera.setYawMin(oldCamera.getYawMin());
        		else if(camera.has("athMin"))
	        		currentCamera.setYawMin((float)camera.getDouble("athMin"));
        		if(keep.athMax)
        			currentCamera.setYawMax(oldCamera.getYawMax());
        		else if(camera.has("athMax"))
		        	currentCamera.setYawMax((float)camera.getDouble("athMax"));
        		if(keep.reverseRotation)
        			currentCamera.setReverseRotation(oldCamera.isReverseRotation());
        		else if(camera.has("reverseRotation"))
	        		currentCamera.setReverseRotation(camera.getBoolean("reverseRotation"));
        		if(keep.rotationSensitivity)
        			currentCamera.setRotationSensitivity(oldCamera.getRotationSensitivity());
        		else if(camera.has("rotationSensitivity"))
	        		currentCamera.setRotationSensitivity((float)camera.getDouble("rotationSensitivity"));
        		if(mInitialPitch != PLConstants.kFloatUndefinedValue)
        			pitch = mInitialPitch;
        		else if(keep.vLookAt)
        			pitch = oldCamera.getLookAtRotation().pitch;
        		else if(camera.has("vLookAt"))
        			pitch = (float)camera.getDouble("vLookAt");
        		if(mInitialYaw != PLConstants.kFloatUndefinedValue)
        			yaw = mInitialYaw;
        		else if(keep.hLookAt)
        			yaw = oldCamera.getLookAtRotation().yaw;
        		else if(camera.has("hLookAt"))
	        		yaw = (float)camera.getDouble("hLookAt");
        		currentCamera.setInitialLookAt(pitch, yaw);
        		currentCamera.lookAt(pitch, yaw);
        		if(keep.zoomLevels)
        			currentCamera.setZoomLevels(oldCamera.getZoomLevels());
        		else if(camera.has("zoomLevels"))
	        		currentCamera.setZoomLevels(camera.getInt("zoomLevels"));
        		if(keep.fovMin)
        			currentCamera.setFovMin(oldCamera.getFovMin());
        		else if(camera.has("fovMin"))
	        		currentCamera.setFovMin((float)camera.getDouble("fovMin"));
        		if(keep.fovMax)
        			currentCamera.setFovMax(oldCamera.getFovMax());
        		else if(camera.has("fovMax"))
		        	currentCamera.setFovMax((float)camera.getDouble("fovMax"));
        		if(keep.fovSensitivity)
        			currentCamera.setFovSensitivity(oldCamera.getFovSensitivity());
        		else if(camera.has("fovSensitivity"))
	        		currentCamera.setFovSensitivity((float)camera.getDouble("fovSensitivity"));
        		if(keep.fov)
        			currentCamera.setFov(oldCamera.getFov());
        		else if(camera.has("fov"))
	        		currentCamera.setFov((float)camera.getDouble("fov"));
        		else if(camera.has("fovFactor"))
        			currentCamera.setFovFactor((float)camera.getDouble("fovFactor"));
        		else if(camera.has("zoomFactor"))
        			currentCamera.setZoomFactor((float)camera.getDouble("zoomFactor"));
        		else if(camera.has("zoomLevel"))
        			currentCamera.setZoomLevel(camera.getInt("zoomLevel"));
	        }
		}
		catch(Throwable e)
		{
			this.didError(e);
			return false;
		}
		return true;
	}
	
	protected boolean parseSensorialRotationJSON()
	{
		try
		{
			if(!mKeepParameters.sensorialRotation && mJSON.has("sensorialRotation"))
	        {
	        	if(mJSON.getBoolean("sensorialRotation"))
	        	{
	        		if(!mView.startSensorialRotation())
	        			mView.updateInitialSensorialRotation();
	        	}
	        	else
	        		mView.stopSensorialRotation();
	        }
			else
				mView.updateInitialSensorialRotation();
		}
		catch(Throwable e)
		{
			this.didError(e);
			return false;
		}
		return true;
	}
	
	/**load methods*/
	
	protected void loadCubicPanoramaImage(PLCubicPanorama panorama, PLCubeFaceOrientation face, JSONObject images, String property, String urlBase, boolean hasPreviewImage, PLTextureColorFormat colorFormat) throws Exception
	{
		if(images.has(property))
		{
			String url = this.buildURL(images.getString(property), urlBase);
			if(this.isHTTPURL(url))
				mView.getDownloadManager().add(new PLHTTPFileDownloader(url, new PLPanoramaImageFileDownloaderListener(panorama, colorFormat, face.ordinal())));
			else
				panorama.setImage(this.getLocalImageAsynchronously(url, colorFormat), face);
		}
		else if(!hasPreviewImage)
			throw new RuntimeException(String.format("images.%s property not exists", property));
	}
	
	protected void loadHotspotTexture(PLIHotspot hotspot, String filename, String urlBase, PLTextureColorFormat colorFormat)
	{
	    if(filename != null)
	    {
	    	String url = this.buildURL(filename, urlBase);
	    	if(mHotspotTextures.containsKey(url))
	    		hotspot.addTexture(mHotspotTextures.get(url));
	    	else
	    	{
	    		boolean isHTTPURL = this.isHTTPURL(url);
	    		PLITexture texture = new PLTexture(isHTTPURL ? new PLImage() : this.getLocalImageAsynchronously(url, colorFormat));
	    		mHotspotTextures.put(url, texture);
	    		hotspot.addTexture(texture);
	    		if(isHTTPURL)
	    			mView.getDownloadManager().add(new PLHTTPFileDownloader(url, new PLImageFileDownloaderListener(texture.getImage(), colorFormat)));
	    	}
	    }
	}
	
	@Override
	public void load(PLIView view)
	{
		this.load(view, null, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue);
	}
	
	@Override
	public void load(PLIView view, PLITransition transition)
	{
		this.load(view, transition, PLConstants.kFloatUndefinedValue, PLConstants.kFloatUndefinedValue);
	}
	
	@Override
	public void load(PLIView view, PLITransition transition, float initialPitch, float initialYaw)
	{
	    if(view != null && (mURL != null || mJSONData != null))
	    {
	    	mView = view;
	    	mTransition = transition;
	    	mInitialPitch = initialPitch;
	    	mInitialYaw = initialYaw;
	    	mIsPreloadingImages = true;
	    	mKeepParameters = null;
    		view.setLocked(true);
    		PLLoaderListener internalListener = this.getInternalListener(), listener = this.getListener();
    		if(internalListener != null)
    			internalListener.didBegin(this);
            if(listener != null)
            	listener.didBegin(this);
    		this.requestJSON(new PLFileDownloaderListener()
    		{
				@Override
				public void didBeginDownload(String url, long startTime)
				{
				}

				@Override
				public void didProgressDownload(String url, int progress)
				{
				}

				@Override
				public void didStopDownload(String url)
				{
				}
				
				@Override
				public void didEndDownload(String url, byte[] data, long elapsedTime)
				{
					parseJSON(data);
				}
    			
				@Override
				public void didErrorDownload(String url, String error, int responseCode, byte[] data)
				{
					didError(new Exception(error));
				}
			});
	    }
	}
	
	/**event methods*/
	
	protected void didComplete(boolean runOnUiThread)
	{
		if(runOnUiThread)
		{
			new Handler(mView.getContext().getMainLooper()).post
			(
				new Runnable()
				{
					@Override
					public void run()
					{
						didComplete();
					}
				}
			);
		}
		else
			this.didComplete();
	}
	
	protected void didComplete()
	{
		PLLoaderListener internalListener = this.getInternalListener(), listener = this.getListener();
		if(internalListener != null)
			internalListener.didComplete(this);
        if(listener != null)
        	listener.didComplete(this);
        this.didEnd();
	}
	
	protected void didStop(boolean runOnUiThread)
	{
		if(runOnUiThread)
		{
			new Handler(mView.getContext().getMainLooper()).post
			(
				new Runnable()
				{
					@Override
					public void run()
					{
						didStop();
					}
				}
			);
		}
		else
			this.didStop();
	}
	
	protected void didStop()
	{
		PLLoaderListener internalListener = this.getInternalListener(), listener = this.getListener();
		if(internalListener != null)
			internalListener.didStop(this);
        if(listener != null)
        	listener.didStop(this);
        this.didEnd();
	}
	
	protected void didError(final Throwable e)
	{
		new Handler(mView.getContext().getMainLooper()).post
		(
			new Runnable()
			{
				@Override
				public void run()
				{
					didError(e.toString());
			        PLLog.error("PLJSONLoader", e);
				}
			}
		);
	}
	
	protected void didError(String error)
	{
		if(mTransition != null)
		{
			mTransition.getListeners().removeAll();
			mTransition.stop();
		}
		mView.getDownloadManager().removeAll();
		PLLoaderListener internalListener = this.getInternalListener(), listener = this.getListener();
		if(internalListener != null)
			internalListener.didError(this, error);
        if(listener != null)
        	listener.didError(this, error);
        this.didEnd();
	}
	
	protected void didEnd()
	{
		if(mView != null)
		{
			mView.setLocked(false);
			mView = null;
		}
		mTransition = null;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mView = null;
		mTransition = null;
		mJSON = null;
		mKeepParameters = null;
		mHotspotTextures.clear();
		mHotspotTextures = null;
		super.finalize();
	}
	
	/**internal classes declaration*/
	
	protected class PLDataRunnable implements Runnable
	{
		/**member variables*/
		
		private PLFileDownloaderListener mListener;
		private String mURL;
		private byte[] mData;
		private long mStartTime;
		
		/**init methods*/
		
		public PLDataRunnable(PLFileDownloaderListener listener, String url, byte[] data, long startTime)
		{
			super();
			mListener = listener;
			mURL = url;
			mData = data;
			mStartTime = startTime;
		}
		
		/**Runnable methods*/
		
		@Override
		public void run()
		{
			mListener.didEndDownload(mURL, mData, System.currentTimeMillis() - mStartTime);
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mListener = null;
			mURL = null;
			mData = null;
			super.finalize();
		}
	}
	
	protected class PLPanoramaImageFileDownloaderListener implements PLFileDownloaderListener
	{
		/**member variables*/
		
		private PLIPanorama mPanorama;
		private PLTextureColorFormat mColorFormat;
		private int mIndex;
		
		/**init methods*/
		
		public PLPanoramaImageFileDownloaderListener(PLIPanorama panorama, PLTextureColorFormat colorFormat)
		{
			this(panorama, colorFormat, 0);
		}
		
		public PLPanoramaImageFileDownloaderListener(PLIPanorama panorama, PLTextureColorFormat colorFormat, int index)
		{
			super();
			mPanorama = panorama;
			mColorFormat = colorFormat;
			mIndex = index;
		}
		
		/**PLFileDownloaderListener methods*/
		
		@Override
		public void didBeginDownload(String url, long startTime)
		{
		}
		
		@Override
		public void didProgressDownload(String url, int progress)
		{
		}
		
		@Override
		public void didStopDownload(String url)
		{
		}
		
		@Override
		public void didEndDownload(String url, byte[] data, long elapsedTime)
		{
			PLIImage image = new PLImage(PLUtils.getBitmap(data, mColorFormat), false);
			if(mPanorama instanceof PLCubicPanorama)
				((PLCubicPanorama)mPanorama).setImage(image, mIndex);
			else if(mPanorama instanceof PLIQuadricPanorama)
				((PLIQuadricPanorama)mPanorama).setImage(image);
		}
		
		@Override
		public void didErrorDownload(String url, String error, int responseCode, byte[] data)
		{
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mPanorama = null;
			super.finalize();
		}
	}
	
	protected class PLImageFileDownloaderListener implements PLFileDownloaderListener
	{
		/**member variables*/
		
		private PLIImage mImage;
		private PLTextureColorFormat mColorFormat;
		
		/**init methods*/
		
		public PLImageFileDownloaderListener(PLIImage image, PLTextureColorFormat colorFormat)
		{
			super();
			mImage = image;
			mColorFormat = colorFormat;
		}
		
		/**PLFileDownloaderListener methods*/
		
		@Override
		public void didBeginDownload(String url, long startTime)
		{
		}
		
		@Override
		public void didProgressDownload(String url, int progress)
		{
		}
		
		@Override
		public void didStopDownload(String url)
		{
		}
		
		@Override
		public void didEndDownload(String url, byte[] data, long elapsedTime)
		{
			mImage.assign(PLUtils.getBitmap(data, mColorFormat), false);
		}
		
		@Override
		public void didErrorDownload(String url, String error, int responseCode, byte[] data)
		{
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mImage = null;
			super.finalize();
		}
	}
}