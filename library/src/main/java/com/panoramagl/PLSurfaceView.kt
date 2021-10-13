package com.panoramagl

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView

@SuppressLint("ViewConstructor")
internal class PLSurfaceView(context: Context, renderer: Renderer) : GLSurfaceView(context) {
    init {
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
    }
}