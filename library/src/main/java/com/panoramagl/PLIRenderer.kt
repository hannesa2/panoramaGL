package com.panoramagl

import android.opengl.GLSurfaceView
import com.panoramagl.ios.structs.CGRect
import com.panoramagl.ios.structs.CGSize
import javax.microedition.khronos.opengles.GL10
import javax.microedition.khronos.opengles.GL11ExtensionPack

interface PLIRenderer : GLSurfaceView.Renderer, PLIReleaseView {

    val backingWidth: Int
    val backingHeight: Int
    var internalView: PLIView?
    var internalScene: PLIScene?
    val isRunning: Boolean
    val isRendering: Boolean
    val viewport: CGRect?
    val size: CGSize
    var internalListener: PLRendererListener?
    val gLContext: GL10?

    fun resizeFromLayer(): Boolean
    fun resizeFromLayer(gl11ep: GL11ExtensionPack?): Boolean

    fun render(gl: GL10?)
    fun renderNTimes(gl: GL10?, times: Int)

    fun start(): Boolean
    fun stop(): Boolean
}
