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
package com.panoramagl

import android.graphics.*
import com.panoramagl.ios.structs.CGRect
import com.panoramagl.ios.structs.CGSize
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

class PLImage : PLIImage {

    override var bitmap: Bitmap? = null
        private set
    override var width = 0
        private set
    override var height = 0
        private set
    override var isRecycled = false
        private set
    override var isLoaded = false
        private set

    constructor() : super() {
        bitmap = null
        height = 0
        width = height
        isLoaded = false
        isRecycled = isLoaded
    }

    @JvmOverloads
    constructor(bitmap: Bitmap, copy: Boolean = true) : super() {
        createWithBitmap(bitmap, copy)
    }

    constructor(buffer: ByteArray?) : super() {
        createWithBuffer(buffer)
    }

    protected fun createWithBitmap(bitmap: Bitmap, copy: Boolean) {
        width = bitmap.width
        height = bitmap.height
        this.bitmap = if (copy) Bitmap.createBitmap(bitmap) else bitmap
        isRecycled = false
        isLoaded = true
    }

    protected fun createWithBuffer(buffer: ByteArray?) {
        if (buffer != null) {
            bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.size)
            width = bitmap?.width ?: 0
            height = bitmap?.height ?: 0
            isRecycled = false
            isLoaded = true
        }
    }

    override val size: CGSize
        get() = CGSize.CGSizeMake(width, height)
    override val rect: CGRect
        get() = CGRect.CGRectMake(0, 0, width, height)
    override val count: Int
        get() = width * height * 4
    override val bits: ByteBuffer?
        get() {
            if (isValid) return null
            val buffer = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, buffer)
            val byteBuffer = ByteBuffer.wrap(buffer.toByteArray())
            byteBuffer.order(ByteOrder.BIG_ENDIAN)
            return byteBuffer
        }
    override val isValid: Boolean
        get() = bitmap != null && !bitmap!!.isRecycled

    override fun equals(image: PLIImage): Boolean {
        if (image.bitmap == bitmap) return true
        if (image.bitmap == null || bitmap == null || image.height != height || image.width != width) return false
        val bits = image.bits
        val _bits = this.bits
        for (i in 0 until count) {
            if (bits?.get() != _bits!!.get()) return false
        }
        return true
    }

    override fun assign(bitmap: Bitmap, copy: Boolean): PLIImage {
        deleteImage()
        createWithBitmap(bitmap, copy)
        return this
    }

    override fun assign(image: PLIImage, copy: Boolean): PLIImage {
        deleteImage()
        createWithBitmap(image.bitmap!!, copy)
        return this
    }

    override fun assign(buffer: ByteArray, copy: Boolean): PLIImage {
        deleteImage()
        createWithBuffer(buffer)
        return this
    }

    override fun crop(rect: CGRect): PLIImage {
        return this.crop(rect.x, rect.y, rect.width, rect.height)
    }

    override fun crop(x: Int, y: Int, width: Int, height: Int): PLIImage {
        val croppedBitmap = Bitmap.createBitmap(width, height, bitmap!!.config)
        val canvas = Canvas(croppedBitmap)
        canvas.drawBitmap(bitmap!!, Rect(x, y, x + width, y + height), Rect(0, 0, width, height), null)
        deleteImage()
        createWithBitmap(croppedBitmap, false)
        return this
    }

    override fun scale(size: CGSize): PLIImage {
        return this.scale(size.width, size.height)
    }

    override fun scale(width: Int, height: Int): PLIImage {
        if (width < 0 || height < 0 || width == 0 && height == 0 || width == this.width && height == this.height) return this
        val image = Bitmap.createScaledBitmap(bitmap!!, width, height, true)
        deleteImage()
        createWithBitmap(image, false)
        return this
    }

    override fun rotate(angle: Int): PLIImage {
        if (angle % 90 != 0) return this
        val matrix = Matrix()
        matrix.preRotate(angle.toFloat())
        val image = Bitmap.createBitmap(bitmap!!, 0, 0, width, height, matrix, true)
        deleteImage()
        createWithBitmap(image, false)
        return this
    }

    override fun rotate(degrees: Float, px: Float, py: Float): PLIImage {
        val matrix = Matrix()
        matrix.preRotate(degrees, px, py)
        val image = Bitmap.createBitmap(bitmap!!, 0, 0, width, height, matrix, true)
        deleteImage()
        createWithBitmap(image, false)
        return this
    }

    override fun mirrorHorizontally(): PLIImage {
        return mirror(true, false)
    }

    override fun mirrorVertically(): PLIImage {
        return mirror(false, true)
    }

    override fun mirror(horizontally: Boolean, vertically: Boolean): PLIImage {
        //-1,1 Horizontal, 1, -1 Vertical, Both = -1,-1
        val matrix = Matrix()
        matrix.preScale(if (horizontally) -1.0f else 1.0f, if (vertically) -1.0f else 1.0f)
        val image = Bitmap.createBitmap(bitmap!!, 0, 0, width, height, matrix, false)
        deleteImage()
        createWithBitmap(image, false)
        return this
    }

    override fun getSubImage(rect: CGRect): Bitmap {
        return this.getSubImage(rect.x, rect.y, rect.width, rect.height)
    }

    override fun getSubImage(x: Int, y: Int, width: Int, height: Int): Bitmap {
        val pixels = IntArray(width * height)
        bitmap!!.getPixels(pixels, 0, width, x, y, width, height)
        return Bitmap.createBitmap(pixels, 0, width, width, height, bitmap!!.config)
    }

    override fun recycle() {
        if (!isRecycled)
            deleteImage()
    }

    protected fun deleteImage() {
        bitmap?.let {
            isRecycled = true
            isLoaded = false
            bitmap = null
        }
    }

    override fun cloneBitmap(): Bitmap {
        return Bitmap.createBitmap(bitmap!!)
    }

    override fun clone(): PLIImage {
        return PLImage(bitmap!!, true)
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        deleteImage()
    }

    companion object {
        @JvmStatic
        fun crop(image: PLIImage, x: Int, y: Int, width: Int, height: Int): PLIImage {
            image.bitmap!!.let {
                val dest = Bitmap.createBitmap(width, height, it.config)
                val canvas = Canvas(dest)
                canvas.drawBitmap(it, Rect(x, y, x + width, y + height), Rect(0, 0, width, height), null)
                return PLImage(dest, false)
            }
        }

        @JvmStatic
        fun joinImagesHorizontally(leftImage: PLIImage?, rightImage: PLIImage?): PLIImage? {
            if (leftImage != null && leftImage.isValid && rightImage != null && rightImage.isValid) {
                val bitmap = Bitmap.createBitmap(
                    leftImage.width + rightImage.width,
                    if (leftImage.height > rightImage.height) leftImage.height else rightImage.height,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap)
                leftImage.bitmap?.let {
                    canvas.drawBitmap(it, 0.0f, 0.0f, null)
                }
                rightImage.bitmap?.let {
                    canvas.drawBitmap(it, leftImage.width.toFloat(), 0.0f, null)
                }
                canvas.save()
                return PLImage(bitmap, false)
            }
            return null
        }
    }
}
