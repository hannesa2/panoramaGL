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
import com.panoramagl.enumerations.PLSpherical2FaceOrientation;
import com.panoramagl.opengl.GLUES;
import com.panoramagl.opengl.GLUquadric;

import javax.microedition.khronos.opengles.GL10;

public class PLSpherical2Panorama extends PLQuadricPanoramaBase
{
	/**init methods*/
	
	public PLSpherical2Panorama()
	{
	    super();
	}
	
	@Override
	protected void initializeValues()
	{
	    super.initializeValues();
	    this.setPreviewDivs(PLConstants.kDefaultSphere2PreviewDivs);
		this.setDivs(PLConstants.kDefaultSphere2Divs);
	}
	
	/**property methods*/
	
	@Override
	public int getTilesNumber()
	{
		return 4;
	}
	
	@Override
	public void setImage(PLIImage image)
	{
	    if(image != null)
	    {
	    	int w = image.getWidth(), h = image.getHeight();
	    	if(w >= 128 && w <= 2048 && h >= 64 && h <= 1024 && PLMath.isPowerOfTwo(w) && PLMath.isPowerOfTwo(h) && w % h == 0)
	    	{
	    		int w2 = w >> 1, w32 = w2 >> 4;
		    	PLIImage frontImage = PLImage.crop(image, w2 - w32, 0, w32 << 1, h);
		    	PLIImage backImage = PLImage.joinImagesHorizontally(PLImage.crop(image, w - w32, 0, w32, h), PLImage.crop(image, 0, 0, w32, h));
		    	PLIImage leftImage = PLImage.crop(image, 0, 0, w2, h);
		    	PLIImage rightImage = PLImage.crop(image, w2, 0, w2, h);
		        this.setTexture(new PLTexture(frontImage), PLSpherical2FaceOrientation.PLSpherical2FaceOrientationFront.ordinal());
		        this.setTexture(new PLTexture(backImage), PLSpherical2FaceOrientation.PLSpherical2FaceOrientationBack.ordinal());
		        this.setTexture(new PLTexture(leftImage), PLSpherical2FaceOrientation.PLSpherical2FaceOrientationLeft.ordinal());
		        this.setTexture(new PLTexture(rightImage), PLSpherical2FaceOrientation.PLSpherical2FaceOrientationRight.ordinal());
	    	}
	    }
	}
	
	/**render methods*/
	
	@Override
	protected void internalRender(GL10 gl, PLIRenderer renderer)
	{
		PLITexture previewTexture = this.getPreviewTextures()[0];
		PLITexture[] textures = this.getTextures();
		PLITexture frontTexture = textures[PLSpherical2FaceOrientation.PLSpherical2FaceOrientationFront.ordinal()];
		PLITexture backTexture = textures[PLSpherical2FaceOrientation.PLSpherical2FaceOrientationBack.ordinal()];
		PLITexture leftTexture = textures[PLSpherical2FaceOrientation.PLSpherical2FaceOrientationLeft.ordinal()];
		PLITexture rightTexture = textures[PLSpherical2FaceOrientation.PLSpherical2FaceOrientationRight.ordinal()];
		
	    boolean frontTextureIsValid = (frontTexture != null && frontTexture.getTextureId(gl) != 0);
	    boolean backTextureIsValid = (backTexture != null && backTexture.getTextureId(gl) != 0);
	    boolean leftTextureIsValid = (leftTexture != null && leftTexture.getTextureId(gl) != 0);
	    boolean rightTextureIsValid = (rightTexture != null && rightTexture.getTextureId(gl) != 0);
	    
	    if(frontTextureIsValid || backTextureIsValid || leftTextureIsValid || rightTextureIsValid || (previewTexture != null && previewTexture.getTextureId(gl) != 0))
	    {
	    	gl.glEnable(GL10.GL_TEXTURE_2D);
	    	
	    	GLUquadric quadratic = this.getQuadric();
		    float radius = PLConstants.kPanoramaRadius;
		    int halfDivs = this.getDivs() / 2, quarterDivs = halfDivs / 2;
		    
		    if(previewTexture != null)
		    {
		        if(frontTextureIsValid && backTextureIsValid && leftTextureIsValid && rightTextureIsValid)
		            this.removePreviewTextureAtIndex(0, true);
		        else
		        {
		        	int previewDivs = this.getPreviewDivs();
		            gl.glBindTexture(GL10.GL_TEXTURE_2D, previewTexture.getTextureId(gl));
		            GLUES.gluSphere(gl, quadratic, radius, previewDivs, previewDivs);
		        }
		    }
		    
		    // Front Face
		    if(frontTextureIsValid)
		    {
		        gl.glBindTexture(GL10.GL_TEXTURE_2D, frontTexture.getTextureId(gl));
		        GLUES.glu3DArc(gl, quadratic, PLConstants.kPI8, -PLConstants.kPI16, false, radius, quarterDivs, quarterDivs);
		    }
		    
		    // Back Face
		    if(backTextureIsValid)
		    {
		        gl.glBindTexture(GL10.GL_TEXTURE_2D, backTexture.getTextureId(gl));
		        GLUES.glu3DArc(gl, quadratic, PLConstants.kPI8, -PLConstants.kPI16, true, radius, quarterDivs, quarterDivs);
		    }
		    
		    // Left Face
		    if(leftTextureIsValid)
		    {
		        gl.glBindTexture(GL10.GL_TEXTURE_2D, leftTexture.getTextureId(gl));
		        GLUES.gluHemisphere(gl, quadratic, false, radius, halfDivs, halfDivs);
		    }
		    
		    //Right Face
		    if(rightTextureIsValid)
		    {
		        gl.glBindTexture(GL10.GL_TEXTURE_2D, rightTexture.getTextureId(gl));
		        GLUES.gluHemisphere(gl, quadratic, true, radius, halfDivs, halfDivs);
		    }
		    
			gl.glDisable(GL10.GL_TEXTURE_2D);
	    }
	}
}