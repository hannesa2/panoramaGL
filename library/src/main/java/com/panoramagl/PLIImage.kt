package com.panoramagl

import android.graphics.Bitmap
import com.panoramagl.ios.structs.CGRect
import com.panoramagl.ios.structs.CGSize
import java.nio.ByteBuffer

interface PLIImage {
    val width: Int
    val height: Int
    val size: CGSize
    val rect: CGRect
    val count: Int
    val bitmap: Bitmap?
    val bits: ByteBuffer?
    val isValid: Boolean
    val isRecycled: Boolean
    val isLoaded: Boolean

    fun equals(image: PLIImage): Boolean
    fun assign(bitmap: Bitmap, copy: Boolean): PLIImage
    fun assign(image: PLIImage, copy: Boolean): PLIImage
    fun assign(buffer: ByteArray, copy: Boolean): PLIImage

    fun crop(rect: CGRect): PLIImage
    fun crop(x: Int, y: Int, width: Int, height: Int): PLIImage

    fun scale(size: CGSize): PLIImage
    fun scale(width: Int, height: Int): PLIImage

    fun rotate(angle: Int): PLIImage
    fun rotate(degrees: Float, px: Float, py: Float): PLIImage

    fun mirrorHorizontally(): PLIImage
    fun mirrorVertically(): PLIImage
    fun mirror(horizontally: Boolean, vertically: Boolean): PLIImage

    fun getSubImage(rect: CGRect): Bitmap
    fun getSubImage(x: Int, y: Int, width: Int, height: Int): Bitmap

    fun recycle()

    fun cloneBitmap(): Bitmap
    fun clone(): PLIImage
}
