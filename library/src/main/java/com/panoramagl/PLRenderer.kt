/*
 * PanoramaGL library
 * Version 0.2 beta
 * Copyright (c) 2010 Javier Baez <javbaezga@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.panoramagl

import javax.microedition.khronos.opengles.GL11ExtensionPack
import com.panoramagl.utils.PLLog
import javax.microedition.khronos.opengles.GL10
import android.opengl.GLU
import com.panoramagl.ios.structs.CGRect
import com.panoramagl.ios.structs.CGSize
import com.panoramagl.opengl.GLWrapper
import kotlin.Throws
import com.panoramagl.opengl.IGLWrapper
import com.panoramagl.utils.PLOpenGLSupport
import com.panoramagl.opengl.matrix.MatrixTrackingGL
import javax.microedition.khronos.egl.EGLConfig

class PLRenderer(view: PLIView?, scene: PLIScene?) : PLObjectBase(), PLIRenderer {
    private var mBackingWidth: IntArray = IntArray(1)
    private var mBackingHeight: IntArray = IntArray(1)
    private var mDefaultFramebuffer: IntArray = IntArray(1)
    private var mColorRenderbuffer: IntArray =IntArray(1)
    override var internalView: PLIView? = null
    override var internalScene: PLIScene? = null
    override var isRendering = false
        private set
    override var isRunning = false
        private set
    private var mViewport: CGRect = CGRect.CGRectMake(CGRect.CGRectMake(0, 0, PLConstants.kViewportSize, PLConstants.kViewportSize).also { mTempViewport = it })
    private var mTempViewport: CGRect? = null
    private var mSize: CGSize = CGSize.CGSizeMake(CGSize.CGSizeMake(0.0f, 0.0f).also { mTempSize = it })
    private var mTempSize: CGSize? = null
    protected var contextSupportsFrameBufferObject = false
        private set
    override var internalListener: PLRendererListener? = null

    protected var isGLContextCreated = false
        private set
    protected var gLWrapper: IGLWrapper? = null
        private set
    override val gLContext: GL10?
        get() = gLWrapper

    init {
        isRunning = isRendering
    }
    override fun initializeValues()  = Unit

    override val backingWidth: Int
        get() = mBackingWidth[0]
    override val backingHeight: Int
        get() = mBackingHeight[0]
    override val viewport: CGRect
        get() = mTempViewport!!.setValues(mViewport)
    override val size: CGSize
        get() = mTempSize!!.setValues(mSize)

    /**
     * buffer methods
     */
    protected fun createFrameBuffer(gl11ep: GL11ExtensionPack) {
        if (contextSupportsFrameBufferObject) {
            gl11ep.glGenFramebuffersOES(1, mDefaultFramebuffer, 0)
            if (mDefaultFramebuffer[0] <= 0) PLLog.error("PLRenderer::createFrameBuffer", "Invalid framebuffer id returned!")
            gl11ep.glGenRenderbuffersOES(1, mColorRenderbuffer, 0)
            if (mColorRenderbuffer[0] <= 0) PLLog.error("PLRenderer::createFrameBuffer", "Invalid renderbuffer id returned!")
            gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mDefaultFramebuffer[0])
            gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0])
        }
    }

    protected fun destroyFramebuffer(gl11ep: GL11ExtensionPack?) {
        if (contextSupportsFrameBufferObject) {
            if (mDefaultFramebuffer[0] != 0) {
                gl11ep!!.glDeleteFramebuffersOES(1, mDefaultFramebuffer, 0)
                mDefaultFramebuffer[0] = 0
            }
            if (mColorRenderbuffer[0] != 0) {
                gl11ep!!.glDeleteRenderbuffersOES(1, mColorRenderbuffer, 0)
                mColorRenderbuffer[0] = 0
            }
        }
    }

    /**
     * resize methods
     */
    override fun resizeFromLayer(): Boolean {
        return this.resizeFromLayer(null)
    }

    override fun resizeFromLayer(gl11ep: GL11ExtensionPack?): Boolean {
        if (contextSupportsFrameBufferObject && gl11ep != null) {
            synchronized(this) {
                if (mBackingWidth[0] != mSize.width || mBackingHeight[0] != mSize.height) {
                    val isRunning = isRunning
                    if (isRunning) this.isRunning = false
                    destroyFramebuffer(gl11ep)
                    createFrameBuffer(gl11ep)
                    gl11ep.glRenderbufferStorageOES(
                        GL11ExtensionPack.GL_RENDERBUFFER_OES,
                        GL11ExtensionPack.GL_RGBA8, mSize.width, mSize.height
                    )
                    gl11ep.glFramebufferRenderbufferOES(
                        GL11ExtensionPack.GL_FRAMEBUFFER_OES,
                        GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES,
                        GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0]
                    )
                    gl11ep.glGetRenderbufferParameterivOES(
                        GL11ExtensionPack.GL_RENDERBUFFER_OES,
                        GL11ExtensionPack.GL_RENDERBUFFER_WIDTH_OES,
                        mBackingWidth,
                        0
                    )
                    gl11ep.glGetRenderbufferParameterivOES(
                        GL11ExtensionPack.GL_RENDERBUFFER_OES,
                        GL11ExtensionPack.GL_RENDERBUFFER_HEIGHT_OES,
                        mBackingHeight,
                        0
                    )
                    mViewport.x = -(mViewport.width / 2 - mSize.width / 2)
                    mViewport.y = -(mViewport.height / 2 - mSize.height / 2)
                    if (gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES) != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
                        PLLog.error(
                            "PLRenderer::resizeFromLayer",
                            "Failed to make complete framebuffer object %x",
                            gl11ep.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES)
                        )
                        return false
                    }
                    if (isRunning) this.isRunning = true
                    return true
                }
            }
        } else {
            synchronized(this) {
                mViewport.x = -(mViewport.width / 2 - mSize.width / 2)
                mViewport.y = -(mViewport.height / 2 - mSize.height / 2)
            }
        }
        return false
    }

    /**
     * render methods
     */
    protected fun renderScene(gl: GL10, scene: PLIScene?, camera: PLICamera?) {
        if (scene != null && camera != null) {
            gl.glMatrixMode(GL10.GL_PROJECTION)
            gl.glLoadIdentity()
            GLU.gluPerspective(gl, camera.fov, PLConstants.kPerspectiveAspect, PLConstants.kPerspectiveZNear, PLConstants.kPerspectiveZFar)
            gl.glMatrixMode(GL10.GL_MODELVIEW)
            scene.render(gl, this)
        }
    }

    override fun render(gl: GL10?) {
        try {
            if (gl != null && isRunning) {
                isRendering = true
                if (contextSupportsFrameBufferObject) {
                    val gl11ep = gl as GL11ExtensionPack
                    gl11ep.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mDefaultFramebuffer[0])
                }
                gl.glViewport(mViewport.x, mViewport.y, mViewport.width, mViewport.height)
                gl.glMatrixMode(GL10.GL_MODELVIEW)
                gl.glLoadIdentity()
                gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
                gl.glClearDepthf(1.0f)
                gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
                gl.glClear(GL10.GL_DEPTH_BUFFER_BIT or GL10.GL_COLOR_BUFFER_BIT)
                gl.glEnable(GL10.GL_DEPTH_TEST)
                gl.glDepthFunc(GL10.GL_ALWAYS)
                gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST)
                gl.glScalef(1.0f, 1.0f, PLConstants.kViewportScale)
                gl.glTranslatef(0.0f, 0.0f, 0.0f)
                gl.glRotatef(180.0f, 0.0f, 1.0f, 0.0f)
                if (internalView != null && internalView!!.isValidForTransition) {
                    val currentTransition = internalView!!.currentTransition
                    if (currentTransition != null && currentTransition.isValid) {
                        renderScene(gl, currentTransition.currentPanorama, currentTransition.currentPanoramaCamera)
                        renderScene(gl, currentTransition.newPanorama, currentTransition.newPanoramaCamera)
                    } else renderScene(gl, internalScene, internalScene!!.camera)
                } else renderScene(gl, internalScene, internalScene!!.camera)
                if (contextSupportsFrameBufferObject) {
                    val gl11ep = gl as GL11ExtensionPack
                    gl11ep.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mColorRenderbuffer[0])
                }
                isRendering = false
            }
        } catch (e: Throwable) {
            isRendering = false
            PLLog.debug("PLRenderer::render", e)
        }
    }

    override fun renderNTimes(gl: GL10?, times: Int) {
        for (i in 0 until times) render(gl)
    }

    /**
     * control methods
     */
    override fun start(): Boolean {
        if (!isRunning) {
            synchronized(this) {
                isRunning = true
                return true
            }
        }
        return false
    }

    override fun stop(): Boolean {
        if (isRunning) {
            synchronized(this) {
                isRunning = false
                return true
            }
        }
        return false
    }

    /**
     * PLIReleaseView methods
     */
    override fun releaseView() {
        if (!isRunning) {
            internalView = null
            internalScene = null
            internalListener = null
        }
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            stop()
            if (contextSupportsFrameBufferObject) destroyFramebuffer(gLWrapper as GL11ExtensionPack?)
        } catch (e: Throwable) {
        }
        mBackingWidth = mBackingHeight
        mDefaultFramebuffer = mColorRenderbuffer
        internalView = null
        internalScene = null
        mTempViewport = null
        mTempSize = null
        internalListener = null
        gLWrapper = null
    }

    override fun onSurfaceCreated(gl: GL10, config: EGLConfig) {
        try {
            isGLContextCreated = false
            gLWrapper = if (PLOpenGLSupport.isHigherThanOpenGL1(gl)) GLWrapper(gl, internalView!!.glSurfaceView) else MatrixTrackingGL(
                gl,
                internalView!!.glSurfaceView
            )
            //mContextSupportsFrameBufferObject = PLOpenGLSupport.checkIfContextSupportsFrameBufferObject(gl);
            start()
            if (internalListener != null) internalListener!!.rendererCreated(this)
        } catch (e: Throwable) {
            PLLog.error("PLRenderer::onSurfaceCreated", e)
        }
    }

    override fun onSurfaceChanged(gl: GL10, width: Int, height: Int) {
        mSize.setValues(width, height)
        this.resizeFromLayer(if (contextSupportsFrameBufferObject) gLWrapper as GL11ExtensionPack? else null)
        if (!isGLContextCreated) {
            if (internalListener != null) internalListener!!.rendererFirstChanged(gLWrapper, this, width, height)
            isGLContextCreated = true
        }
        if (internalListener != null) internalListener!!.rendererChanged(this, width, height)
    }

    override fun onDrawFrame(gl: GL10) {
        if (isGLContextCreated && internalView != null) render(gLWrapper)
    }

    /**
     * init methods
     */
    init {
        internalView = view
        internalScene = scene
    }
}