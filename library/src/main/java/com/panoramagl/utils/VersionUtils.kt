package com.panoramagl.utils

import android.os.Build
import com.panoramagl.enumerations.PLOpenGLVersion
import javax.microedition.khronos.opengles.GL10

private fun getAndroidVersion() = Build.VERSION.RELEASE.trim().split(".")[0].toInt()

fun isEmulator() = Build.PRODUCT.contains("sdk")

fun GL10.isHigherThanOpenGL1() = this.getOpenGLVersion().ordinal > PLOpenGLVersion.PLOpenGLVersion1_0.ordinal

fun GL10.getOpenGLVersion(): PLOpenGLVersion {
    val versionGL = this.glGetString(GL10.GL_VERSION)
    val version = if (isEmulator()) {
        (if (getAndroidVersion() < 3)
            PLOpenGLVersion.PLOpenGLVersion1_0
        else
            PLOpenGLVersion.PLOpenGLVersion1_1)
    } else {
        when {
            versionGL.contains("1.0") -> PLOpenGLVersion.PLOpenGLVersion1_0
            versionGL.contains("1.1") -> PLOpenGLVersion.PLOpenGLVersion1_1
            versionGL.contains("2.0") -> PLOpenGLVersion.PLOpenGLVersion2_0
            versionGL.contains("3.0") -> PLOpenGLVersion.PLOpenGLVersion3_0
            versionGL.contains("3.1") -> PLOpenGLVersion.PLOpenGLVersion3_1
            else -> PLOpenGLVersion.PLOpenGLVersion3_1
        }
    }
    PLLog.debug("getOpenGLVersion", "Use '${version.name}' found $versionGL")
    return version
}
