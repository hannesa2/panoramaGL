package com.panoramagl.utils

import android.os.Build
import com.panoramagl.enumerations.PLOpenGLVersion
import timber.log.Timber
import javax.microedition.khronos.opengles.GL10

private var versionGL: String? = null

fun isEmulator() = Build.PRODUCT.contains("sdk")

fun GL10.getOpenGLVersion(): PLOpenGLVersion {
    if (versionGL == null) {
        versionGL = this.glGetString(GL10.GL_VERSION)
    }
    val version = if (isEmulator()) {
        PLOpenGLVersion.PLOpenGLVersion1_1
    } else {
        when {
            versionGL!!.contains("1.0") -> PLOpenGLVersion.PLOpenGLVersion1_0
            versionGL!!.contains("1.1") -> PLOpenGLVersion.PLOpenGLVersion1_1
            versionGL!!.contains("2.0") -> PLOpenGLVersion.PLOpenGLVersion2_0
            versionGL!!.contains("3.0") -> PLOpenGLVersion.PLOpenGLVersion3_0
            versionGL!!.contains("3.1") -> PLOpenGLVersion.PLOpenGLVersion3_1
            else -> PLOpenGLVersion.PLOpenGLVersion3_1
        }
    }
    Timber.d("getOpenGLVersion '${version.name}' found $versionGL")
    return version
}
