package com.panoramagl

import javax.microedition.khronos.opengles.GL10
import com.panoramagl.enumerations.PLTextureColorFormat

class PLNullTexture : PLITexture {
    override fun getImage(): PLIImage? {
        return null
    }

    override fun getTextureId(gl: GL10): Int {
        return 0
    }

    override fun getWidth(): Int {
        return 0
    }

    override fun getHeight(): Int {
        return 0
    }

    override fun isValid(): Boolean {
        return false
    }

    override fun isRecycled(): Boolean {
        return true
    }

    override fun isRecycledByParent(): Boolean {
        return true
    }

    override fun setRecycledByParent(isRecycledByParent: Boolean) {}
    override fun getColorFormat(): PLTextureColorFormat {
        return PLTextureColorFormat.PLTextureColorFormatUnknown
    }

    override fun setColorFormat(colorFormat: PLTextureColorFormat) {}
    override fun getListener(): PLTextureListener? {
        return null
    }

    override fun setListener(listener: PLTextureListener) {}
    override fun recycle() {}
}