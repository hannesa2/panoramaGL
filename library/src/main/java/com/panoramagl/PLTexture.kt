package com.panoramagl

import kotlin.jvm.JvmOverloads
import com.panoramagl.enumerations.PLTextureColorFormat
import com.panoramagl.opengl.IGLWrapper
import javax.microedition.khronos.opengles.GL10
import com.panoramagl.utils.PLUtils
import com.panoramagl.utils.PLLog
import com.panoramagl.computation.PLMath
import android.opengl.GLU
import android.opengl.GLUtils
import com.panoramagl.utils.getAndroidVersion
import kotlin.Throws

class PLTexture @JvmOverloads constructor(
    private var mImage: PLIImage,
    private var mColorFormat: PLTextureColorFormat = PLTextureColorFormat.PLTextureColorFormatUnknown,
    private var mIsRecycledByParent: Boolean = true
) : PLObjectBase(), PLITexture {
    private var mTextureId: IntArray = intArrayOf(0)
    private var mWidth = 0
    private var mHeight = 0
    private var mIsValid = false
    private var mIsRecycled = false
    private var mGLWrapper: IGLWrapper? = null
    private var mListener: PLTextureListener? = null

    override fun initializeValues() {
        mHeight = 0
        mWidth = mHeight
        mIsValid = false
        mIsRecycled = true
        mIsRecycledByParent = true
        mColorFormat = PLTextureColorFormat.PLTextureColorFormatUnknown
        mGLWrapper = null
        mListener = null
    }

    override fun getImage(): PLIImage {
        return mImage
    }

    override fun getTextureId(gl: GL10): Int {
        if (mIsValid) return mTextureId[0]
        return if (loadTexture(gl)) mTextureId[0] else 0
    }

    override fun getWidth(): Int {
        return mWidth
    }

    override fun getHeight(): Int {
        return mHeight
    }

    override fun isValid(): Boolean {
        return mIsValid
    }

    override fun isRecycled(): Boolean {
        return mIsRecycled
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
            if (newBitmap != image.bitmap) return PLImage(newBitmap)
        }
        return image
    }

    protected fun loadTexture(gl: GL10): Boolean {
        try {
            if (!mImage.isValid) return false
            recycleTexture(gl)
            mWidth = mImage.width
            mHeight = mImage.height
            if (mWidth > PLConstants.kTextureMaxSize || mHeight > PLConstants.kTextureMaxSize) {
                PLLog.error(
                    "PLTexture::loadTexture",
                    "Invalid texture size. The texture max size must be %d x %d and currently is %d x %d.",
                    PLConstants.kTextureMaxSize,
                    PLConstants.kTextureMaxSize,
                    mWidth,
                    mHeight
                )
                recycleImage()
                return false
            }
            var isResizableImage = false
            if (!PLMath.isPowerOfTwo(mWidth)) {
                isResizableImage = true
                mWidth = convertSizeToPowerOfTwo(mWidth)
            }
            if (!PLMath.isPowerOfTwo(mHeight)) {
                isResizableImage = true
                mHeight = convertSizeToPowerOfTwo(mHeight)
            }
            if (isResizableImage) mImage.scale(mWidth, mHeight)
            gl.glGenTextures(1, mTextureId, 0)
            var error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                PLLog.error("PLTexture::loadTexture", "glGetError #1 = (%d) %s ...", error, GLU.gluErrorString(error))
                recycleImage()
                return false
            }
            gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId[0])
            error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                PLLog.error("PLTexture::loadTexture", "glGetError #2 = (%d) %s ...", error, GLU.gluErrorString(error))
                recycleImage()
                return false
            }
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR.toFloat()) //GLES10.GL_NEAREST || GL10.GL_LINEAR
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR.toFloat())
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE.toFloat()) //GL10.GL_REPEAT
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE.toFloat())
            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_MODULATE.toFloat()) //GL10.GL_REPLACE
            val image = convertImage(mImage, mColorFormat)
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, image.bitmap, 0)
            if (image !== mImage) image.recycle()
            error = gl.glGetError()
            if (error != GL10.GL_NO_ERROR) {
                PLLog.error("PLTexture::loadTexture", "glGetError #3 = (%d) %s ...", error, GLU.gluErrorString(error))
                recycleImage()
                return false
            }
            recycleImage()
            mIsValid = true
            mIsRecycled = false
            if (gl is IGLWrapper) mGLWrapper = gl
            if (mListener != null) mListener!!.didLoad(this)
            return true
        } catch (e: Throwable) {
            PLLog.error("PLTexture::loadTexture", e)
        }
        return false
    }

    override fun recycle() {
        recycleImage()
        recycleTexture(mGLWrapper)
        mIsRecycled = true
    }

    protected fun recycleImage() {
        mImage.recycle()
    }

    protected fun recycleTexture(gl: GL10?) {
        if (gl != null && mTextureId[0] != 0) {
            if (getAndroidVersion() < 3) {
                gl.glDeleteTextures(1, mTextureId, 0)
                mTextureId[0] = 0
                mGLWrapper = null
                mIsValid = false
            } else if (mGLWrapper != null) {
                val glSurfaceView = mGLWrapper?.glSurfaceView
                glSurfaceView?.queueEvent(PLRecycleTextureRunnable(this))
            }
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            recycle()
        } catch (e: Throwable) {
            PLLog.error("PLTexture", e)
        }
    }

    protected inner class PLRecycleTextureRunnable(texture: PLTexture) : Runnable {
        private var mTexture: PLTexture?
        private var mGLWrapper: IGLWrapper?
        override fun run() {
            mGLWrapper!!.glDeleteTextures(1, mTextureId, 0)
            mTexture!!.mTextureId[0] = 0
            mGLWrapper = null
            mTexture!!.mIsValid = false
        }

        @Throws(Throwable::class)
        protected fun finalize() {
            mTexture = null
        }

        init {
            mTexture = texture
            mGLWrapper = texture.mGLWrapper
        }
    }
}