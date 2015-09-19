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
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;

import com.panoramagl.ios.structs.CGRect;
import com.panoramagl.ios.structs.CGSize;
import com.panoramagl.utils.PLUtils;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PLImage implements PLIImage
{
	/**member variables*/
	
	private Bitmap mBitmap;
	private int mWidth, mHeight;
	private boolean mIsRecycled, mIsLoaded;
	
	/**init methods*/
	
	public PLImage()
	{
		super();
		mBitmap = null;
		mWidth = mHeight = 0;
		mIsRecycled = mIsLoaded = false;
	}
	
	public PLImage(Bitmap bitmap)
	{
		this(bitmap, true);
	}
	
	public PLImage(Bitmap bitmap, boolean copy)
	{
		super();
		this.createWithBitmap(bitmap, copy);
	}
	
	public PLImage(CGSize size)
	{
		this(size.width, size.height);
	}
	
	public PLImage(int width, int height)
	{
		super();
		this.createWithSize(width, height);	
	}
	
	public PLImage(String path)
	{
		super();
		this.createWithPath(path, Bitmap.Config.ARGB_8888);
	}
	
	public PLImage(String path, Bitmap.Config config)
	{
		super();
		this.createWithPath(path, config);
	}
	
	public PLImage(byte[] buffer)
	{
		super();
		this.createWithBuffer(buffer);
	}
	
	/**create methods*/
	
	protected void createWithPath(String path, Bitmap.Config config)
	{
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inDither = true;
		options.inPreferredConfig = config;
		mBitmap = BitmapFactory.decodeFile(path, options);
		mWidth = mBitmap.getWidth();
		mHeight = mBitmap.getHeight();
		mIsRecycled = false;
		mIsLoaded = true;
	}
	
	protected void createWithBitmap(Bitmap bitmap, boolean copy)
	{
		mWidth = bitmap.getWidth();
		mHeight = bitmap.getHeight();
		mBitmap = (copy ? Bitmap.createBitmap(bitmap) : bitmap);
		mIsRecycled = false;
		mIsLoaded = true;
	}
	
	protected void createWithSize(int width, int height)
	{
		this.deleteImage();
		this.createWithBitmap(Bitmap.createBitmap(null, 0, 0, width, height), false);
	}
	
	protected void createWithBuffer(byte[] buffer)
	{
		if(buffer != null)
		{
			mBitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
			mWidth = mBitmap.getWidth();
			mHeight = mBitmap.getHeight();
			mIsRecycled = false;
			mIsLoaded = true;
		}
	}
	
	/**property methods*/
	
	@Override
	public int getWidth()
	{
		return mWidth;
	}
	
	@Override
	public int getHeight()
	{
		return mHeight;
	}
	
	@Override
	public CGSize getSize()
	{
		return CGSize.CGSizeMake(mWidth, mHeight);
	}
	
	@Override
	public CGRect getRect()
	{
		return CGRect.CGRectMake(0, 0, mWidth, mHeight);
	}
	
	@Override
	public int getCount()
	{
		return (mWidth * mHeight * 4);
	}
	
	@Override
	public Bitmap getBitmap()
	{
		return mBitmap;
	}
	
	@Override
	public ByteBuffer getBits()
	{
		if(this.isValid())
			return null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		mBitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);
		ByteBuffer byteBuffer = ByteBuffer.wrap(buffer.toByteArray());
		byteBuffer.order(ByteOrder.BIG_ENDIAN);
		return byteBuffer;
	}
	
	@Override
	public boolean isValid()
	{
		return (mBitmap != null && !mBitmap.isRecycled());
	}
	
	@Override
	public boolean isRecycled()
	{
		return mIsRecycled;
	}
	
	@Override
	public boolean isLoaded()
	{
		return mIsLoaded;
	}
	
	/**operation methods*/
	
	@Override
	public boolean equals(PLIImage image)
	{	
		if(image.getBitmap() == mBitmap)
			return true;
		if(image.getBitmap() == null || mBitmap == null || image.getHeight() != mHeight || image.getWidth() != mWidth)
			return false;
		ByteBuffer bits = image.getBits();
		ByteBuffer _bits = this.getBits();
		for(int i = 0; i < this.getCount(); i++)
		{
			if(bits.get() != _bits.get())
				return false;
		}
		return true;
	}
	
	@Override
	public PLIImage assign(Bitmap bitmap, boolean copy)
	{
		this.deleteImage();
		this.createWithBitmap(bitmap, copy);
		return this;
	}
	
	@Override
	public PLIImage assign(PLIImage image, boolean copy)
	{
		this.deleteImage();
		this.createWithBitmap(image.getBitmap(), copy);
		return this;
	}
	
	@Override
	public PLIImage assign(byte[] buffer, boolean copy)
	{
		this.deleteImage();
		this.createWithBuffer(buffer);
		return this;
	}
	
	/**crop methods*/
	
	@Override
	public PLIImage crop(CGRect rect)
	{
		return this.crop(rect.x, rect.y, rect.width, rect.height);
	}
	
	@Override
	public PLIImage crop(int x, int y, int width, int height)
	{
		Bitmap croppedBitmap = Bitmap.createBitmap(width, height, mBitmap.getConfig());
		Canvas canvas = new Canvas(croppedBitmap);
		canvas.drawBitmap(mBitmap, new Rect(x, y, x + width, y + height), new Rect(0, 0, width, height), null);
		this.deleteImage();
		this.createWithBitmap(croppedBitmap, false);
		return this;
	}
	
	public static PLIImage crop(PLIImage image, int x, int y, int width, int height)
	{
		Bitmap source = image.getBitmap();
		Bitmap dest = Bitmap.createBitmap(width, height, source.getConfig());
		Canvas canvas = new Canvas(dest);
		canvas.drawBitmap(source, new Rect(x, y, x + width, y + height), new Rect(0, 0, width, height), null);
		return new PLImage(dest, false);
	}
	
	/**scale methods*/
	
	@Override
	public PLIImage scale(CGSize size)
	{
		return this.scale(size.width, size.height);
	}
	
	@Override
	public PLIImage scale(int width, int height)
	{
		if((width < 0 || height < 0) || (width == 0 && height == 0) || (width == mWidth && height == mHeight))
			return this;
		Bitmap image = Bitmap.createScaledBitmap(mBitmap, width, height, true);
	    this.deleteImage();
	    this.createWithBitmap(image, false);
		return this;
	}
	
	/**rotate methods*/
	
	@Override
	public PLIImage rotate(int angle)
	{
		if((angle % 90) != 0)
			return this;
		Matrix matrix = new Matrix();
		matrix.preRotate(angle);
    	Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, mWidth, mHeight, matrix, true);
    	this.deleteImage();
    	this.createWithBitmap(image, false);
    	return this;
	}
	
	@Override
	public PLIImage rotate(float degrees, float px, float py)
	{
		Matrix matrix = new Matrix();
		matrix.preRotate(degrees, px, py);
    	Bitmap image = Bitmap.createBitmap(mBitmap, 0, 0, mWidth, mHeight, matrix, true);
    	this.deleteImage();
    	this.createWithBitmap(image, false);
    	return this;
	}
	
	/**mirror methods*/
	
	@Override
	public PLIImage mirrorHorizontally()
	{
		return this.mirror(true, false);
	}
	
	@Override
	public PLIImage mirrorVertically()
	{
		return this.mirror(false,true);
	}
	
	@Override
	public PLIImage mirror(boolean horizontally, boolean vertically)
	{
		//-1,1 Horizontal, 1, -1 Vertical, Both = -1,-1
		Matrix matrix = new Matrix();
		matrix.preScale(horizontally ? -1.0f : 1.0f, vertically ? -1.0f : 1.0f);
		Bitmap image = Bitmap.createBitmap(mBitmap , 0, 0, mWidth, mHeight, matrix, false);
		this.deleteImage();
		this.createWithBitmap(image, false);
		return this;
	}
	
	/**sub-image methods*/
	
	@Override
	public Bitmap getSubImage(CGRect rect)
	{
		return this.getSubImage(rect.x, rect.y, rect.width, rect.height);
	}
	
	@Override
	public Bitmap getSubImage(int x, int y, int width, int height)
	{
		int pixels[] = new int[width * height];
		mBitmap.getPixels(pixels, 0, width, x, y, width, height);
		return Bitmap.createBitmap(pixels, 0, width, width, height, mBitmap.getConfig());
	}
	
	public static PLIImage joinImagesHorizontally(PLIImage leftImage, PLIImage rightImage)
	{
	    if(leftImage != null && leftImage.isValid() && rightImage != null && rightImage.isValid())
	    {
	    	Bitmap bitmap = Bitmap.createBitmap(leftImage.getWidth() + rightImage.getWidth(), (leftImage.getHeight() > rightImage.getHeight() ? leftImage.getHeight() : rightImage.getHeight()), Bitmap.Config.ARGB_8888);
	    	Canvas canvas = new Canvas(bitmap);
	    	canvas.drawBitmap(leftImage.getBitmap(), 0.0f, 0.0f, null);
	    	canvas.drawBitmap(rightImage.getBitmap(), leftImage.getWidth(), 0.0f, null);
	    	canvas.save();
	    	return new PLImage(bitmap, false);
	    }
	    return null;
	}
	
	/**recycle methods*/
	
	@Override
	public void recycle()
	{
		if(!mIsRecycled)
			this.deleteImage();
	}
	
	/**delete methods*/
	
	protected void deleteImage()
	{
		if(mBitmap != null)
		{
			if(PLUtils.getAndroidVersion() < 3.0f && !mBitmap.isRecycled())
				mBitmap.recycle();
			mBitmap = null;
			mIsRecycled = true;
			mIsLoaded = false;
		}
	}
	
	/**clone methods*/
	
	@Override
	public Bitmap cloneBitmap()
	{
		return Bitmap.createBitmap(mBitmap);
	}
	
	@Override
	public PLIImage clone()
	{
		return new PLImage(mBitmap, true);
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		this.deleteImage();
		super.finalize();
	}
}