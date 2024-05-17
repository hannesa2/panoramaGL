package com.panoramagl

import kotlin.jvm.JvmOverloads
import com.panoramagl.enumerations.PLTextureColorFormat
import com.panoramagl.opengl.IGLWrapper
import javax.microedition.khronos.opengles.GL10
import com.panoramagl.utils.PLUtils
import com.panoramagl.computation.PLMath
import android.opengl.GLU
import android.opengl.GLUtils
import timber.log.Timber
import kotlin.Throws

@Suppress("MemberVisibilityCanBePrivate")
open class PLTexture @JvmOverloads constructor(
    private var pliImage: PLIImage,
    private var mColorFormat: PLTextureColorFormat = PLTextureColorFormat.PLTextureColorFormatUnknown,
    private var mIsRecycledByParent: Boolean = true
) : PLObjectBase(), PLITexture {
    private var textureId = intArrayOf(0)
    private var width = 0
    private var height = 0
    private var isValid = false
    private var isRecycled = false
    private var glWrapper: IGLWrapper? = null
    private var mListener: PLTextureListener? = null

    override fun initializeValues() {
        height = 0
        width = height
        isValid = false
        isRecycled = true
        mIsRecycledByParent = true
        mColorFormat = PLTextureColorFormat.PLTextureColorFormatUnknown
        glWrapper = null
        mListener = null
    }

    override fun getImage(): PLIImage {
        return pliImage
    }

    override fun getTextureId(gl: GL10): Int {
        if (isValid)
            return textureId[0]
        return if (loadTexture(gl))
            textureId[0]
        else
            0
    }

    override fun getWidth(): Int {
        return width
    }

    override fun getHeight(): Int {
        return height
    }

    override fun isValid(): Boolean {
        return isValid
    }

    override fun isRecycled(): Boolean {
        return isRecycled
    }

    override fun isRecycledByParent(): Boolean {
        return mIsRecycledByParent
    }

    override fun setRecycledByParent(isRecycledByParent: Boolean) {
        mIsRecycledByParent = isRecycledByParent
    }

    override fun getColorFormat(): PLTextureColorFormat {
        return mColorFormat
    }

    override fun setColorFormat(colorFormat: PLTextureColorFormat) {
        mColorFormat = colorFormat
    }

    override fun getListener(): PLTextureListener {
        return mListener!!
    }

    override fun setListener(listener: PLTextureListener) {
        mListener = listener
    }

    protected fun convertSizeToPowerOfTwo(size: Int): Int {
        return if (size <= 4) 4 else if (size <= 8) 8 else if (size <= 16) 16 else if (size <= 32) 32 else if (size <= 64) 64 else if (size <= 128) 128 else if (size <= 256) 256 else if (size <= 512) 512 else if (size <= 1024) 1024 else PLConstants.kTextureMaxSize
    }

    protected fun convertImage(image: PLIImage, colorFormat: PLTextureColorFormat): PLIImage {
        if (colorFormat != PLTextureColorFormat.PLTextureColorFormatUnknown) {
            val newBitmap = PLUtils.convertBitmap(image.bitmap, colorFormat)
            if (newBitmap != image.bitmap)
                return PLImage(newBitmap)
        }
        return image
    }

    protected fun loadTexture(gl: GL10): Boolean {
        try {
            if (!pliImage.isValid)
                return false
            recycleTexture(gl)
            width = pliImage.width
            height = pliImage.height
            if (width > PLConstants.kTextureMaxSize || height > PLConstants.kTextureMaxSize) {
                Timber.e(
                    "Invalid texture size. The texture max size must be %d x %d and currently is %d x %d.",
                    PLConstants.kTextureMaxSize,
                    PLConstants.kTextureMaxSize,
                    width,
                    height
                )
                recycleImage()
                return false
            }
            var isResizableImage = false
            if (!PLMath.isPowerOfTwo(width)) {
                isResizableImage = true
                width = convertSizeToPowerOfTwo(width)
            }
            if (!PLMath.isPowerOfTwo(height)) {
                isResizableImage = true
                height = convertSizeToPowerOfTwo(height)
            }
            if (isResizableImage)
                pliImage.scale(width, height)
            gl.glGenTextures(1, textureId, 0)
            var error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                Timber.e("glGetError #1 = ($error) ${GLU.gluErrorString(error)}")
                recycleImage()
                return false
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId[0])
            error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                Timber.e("glGetError #2 = ($error) ${GLU.gluErrorString(error)}")
                recycleImage()
                return false
            }
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()) //GLES10.GL_NEAREST || GL10.GL_LINEAR
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat()) //GL10.GL_REPEAT
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE.toFloat()) //GL10.GL_REPLACE
            val image = convertImage(pliImage, mColorFormat)
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image.bitmap, 0)
            error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                Timber.e("glGetError #3 = ($error) ${GLU.gluErrorString(error)}")
                recycleImage()
                return false
            }
            recycleImage()
            isValid = true
            isRecycled = false
            if (gl is IGLWrapper) glWrapper = gl
            if (mListener != null) mListener!!.didLoad(this)
            return true
        } catch (e: Throwable) {
            Timber.e(e)
        }
        return false
    }

    override fun recycle() {
        recycleImage()
        recycleTexture(glWrapper)
        isRecycled = true
    }

    protected fun recycleImage() {
        pliImage.recycle()
    }

    protected fun recycleTexture(gl: GL10?) {
        if (gl != null && textureId[0] != 0) {
            glWrapper?.glSurfaceView?.queueEvent(PLRecycleTextureRunnable(this))
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            recycle()
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    protected inner class PLRecycleTextureRunnable(texture: PLTexture) : Runnable {
        private var mTexture: PLTexture?
        private var mGLWrapper: IGLWrapper?
        override fun run() {
            mGLWrapper!!.glDeleteTextures(1, textureId, 0)
            mTexture!!.textureId[0] = 0
            mGLWrapper = null
            mTexture!!.isValid = false
        }

        @Throws(Throwable::class)
        protected fun finalize() {
            mTexture = null
        }

        init {
            mTexture = texture
            mGLWrapper = texture.glWrapper
        }
    }
}