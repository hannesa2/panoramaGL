package com.panoramagl

import javax.microedition.khronos.opengles.GL10

interface PLRendererListener {
    fun rendererCreated(render: PLIRenderer?)
    fun rendererChanged(render: PLIRenderer?, width: Int, height: Int)
    fun rendererFirstChanged(gl: GL10?, render: PLIRenderer?, width: Int, height: Int)
    fun rendererDestroyed(render: PLIRenderer?)
}
