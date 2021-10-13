package com.panoramagl.utils

import com.panoramagl.enumerations.PLOpenGLVersion
import javax.microedition.khronos.opengles.GL10
import com.panoramagl.utils.PLOpenGLSupport
import com.panoramagl.utils.PLUtils

object PLOpenGLSupport {

    private var sGLVersion: PLOpenGLVersion? = null
    private var sIsHigherThanOpenGL1FirstTime = true
    private var sIsHigherThanOpenGL1 = false

    private fun getOpenGLVersion(gl: GL10): PLOpenGLVersion? {
        if (sGLVersion == null) {
            if (PLUtils.isEmulator()) sGLVersion =
                (if (PLUtils.getAndroidVersion() < 3) PLOpenGLVersion.PLOpenGLVersion1_0 else PLOpenGLVersion.PLOpenGLVersion1_1) else {
                val version = gl.glGetString(GL10.GL_VERSION)
                if (version.contains("1.0")) sGLVersion = PLOpenGLVersion.PLOpenGLVersion1_0 else if (version.contains("1.1")) sGLVersion =
                    PLOpenGLVersion.PLOpenGLVersion1_1 else sGLVersion = PLOpenGLVersion.PLOpenGLVersion2_0
            }
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
        return (" " + gl.glGetString(GL10.GL_EXTENSIONS) + " ").contains(" $extension ")
    }
}
