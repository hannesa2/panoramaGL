package com.panoramagl.utils

import com.panoramagl.enumerations.PLOpenGLVersion
import javax.microedition.khronos.opengles.GL10

object PLOpenGLSupport {
    private var sGLVersion: PLOpenGLVersion? = null
    private var sIsHigherThanOpenGL1FirstTime = true
    private var sIsHigherThanOpenGL1 = false
    private fun getOpenGLVersion(gl: GL10): PLOpenGLVersion? {
        if (sGLVersion == null) {
            val version = gl.glGetString(GL10.GL_VERSION)
            sGLVersion = if (PLUtils.isEmulator())
                (if (PLUtils.getAndroidVersion() < 3)
                    PLOpenGLVersion.PLOpenGLVersion1_0
                else
                    PLOpenGLVersion.PLOpenGLVersion1_1)
            else {
                when {
                    version.contains("1.0") -> PLOpenGLVersion.PLOpenGLVersion1_0
                    version.contains("1.1") -> PLOpenGLVersion.PLOpenGLVersion1_1
                    else -> PLOpenGLVersion.PLOpenGLVersion2_0
                }
            }
            PLLog.debug("PLOpenGLSupport::getOpenGLVersion", "Use '${sGLVersion?.name}' found $version")
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
