package com.panoramagl.utils

import android.opengl.GLES20
import com.panoramagl.PLRenderer
import com.panoramagl.enumerations.PLOpenGLVersion
import timber.log.Timber
import javax.microedition.khronos.opengles.GL10

object PLOpenGLSupport {
    private var sGLVersion: PLOpenGLVersion? = null
    private var sIsHigherThanOpenGL1FirstTime = true
    private var sIsHigherThanOpenGL1 = false

    fun getOpenGLVersion(gl: GL10): PLOpenGLVersion? {
        if (sGLVersion == null) {
            val version = gl.glGetString(GL10.GL_VERSION)
            sGLVersion = if (isEmulator()) {
                PLOpenGLVersion.PLOpenGLVersion1_1
            } else {
                when {
                    version.contains("1.0") -> PLOpenGLVersion.PLOpenGLVersion1_0
                    version.contains("1.1") -> PLOpenGLVersion.PLOpenGLVersion1_1
                    version.contains("2.0") -> PLOpenGLVersion.PLOpenGLVersion2_0
                    version.contains("3.0") -> PLOpenGLVersion.PLOpenGLVersion3_0
                    version.contains("3.1") -> PLOpenGLVersion.PLOpenGLVersion3_1
                    else -> PLOpenGLVersion.PLOpenGLVersion3_1
                }
            }
            Timber.d("'${sGLVersion?.name}' found $version")
            val vendorGL = GLES20.glGetString(GLES20.GL_VENDOR) ?: "unknown"
            val rendererGL = GLES20.glGetString(GLES20.GL_RENDERER) ?: "unknown"
            Timber.d("Vendor $vendorGL")
            Timber.d("Render $rendererGL")
        }
        return sGLVersion
    }

    @JvmStatic
    fun isHigherThanOpenGL1(gl: GL10): Boolean {
        if (sIsHigherThanOpenGL1FirstTime) {
            sIsHigherThanOpenGL1FirstTime = false
            sIsHigherThanOpenGL1 = getOpenGLVersion(gl)!!.ordinal > PLOpenGLVersion.PLOpenGLVersion1_0.ordinal
        }
        return sIsHigherThanOpenGL1
    }

    fun checkIfSupportsFrameBufferObject(gl: GL10): Boolean {
        return checkIfSupportsExtension(gl, "GL_OES_framebuffer_object")
    }

    private fun checkIfSupportsExtension(gl: GL10, extension: String): Boolean {
        return " ${gl.glGetString(GL10.GL_EXTENSIONS)} ".contains(" $extension ")
    }
}
