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

import android.graphics.Bitmap;

import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;

import java.nio.ByteBuffer;

public interface PLIImage
{
	/**property methods*/
	
	int getWidth();
	
	int getHeight();
	
	CGSize getSize();
	
	CGRect getRect();
	
	int getCount();
	
	Bitmap getBitmap();
	
	ByteBuffer getBits();
	
	boolean isValid();
	
	boolean isRecycled();
	
	boolean isLoaded();
	
	/**operation methods*/
	
	boolean equals(PLIImage image);
	
	PLIImage assign(Bitmap bitmap, boolean copy);
	PLIImage assign(PLIImage image, boolean copy);
	PLIImage assign(byte[] buffer, boolean copy);
	
	/**crop methods*/
	
	PLIImage crop(CGRect rect);
	PLIImage crop(int x, int y, int width, int height);
	
	/**scale methods*/
	
	PLIImage scale(CGSize size);
	PLIImage scale(int width, int height);
	
	/**rotate methods*/
	
	PLIImage rotate(int angle);
	PLIImage rotate(float degrees, float px, float py);
	
	/**mirror methods*/
	
	PLIImage mirrorHorizontally();
	PLIImage mirrorVertically();
	PLIImage mirror(boolean horizontally, boolean vertically);
	
	/**sub-image methods*/
	
	Bitmap getSubImage(CGRect rect);
	Bitmap getSubImage(int x, int y, int width, int height);
	
	/**recycle methods*/
	
	void recycle();
	
	/**clone methods*/
	
	Bitmap cloneBitmap();
	PLIImage clone();
}