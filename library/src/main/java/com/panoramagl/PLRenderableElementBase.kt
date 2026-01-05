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

import com.panoramagl.structs.PLRotation
import timber.log.Timber
import javax.microedition.khronos.opengles.GL10

abstract class PLRenderableElementBase : PLObject(), PLIRenderableElement {
    override var isVisible: Boolean = false
    override var isValid: Boolean = false
        protected set
    override var isRendering: Boolean = false
        protected set

    override fun initializeValues() {
        super.initializeValues()
        this.isValid = true
        this.isVisible = this.isValid
        this.isRendering = false
    }

    protected open fun translate(gl: GL10) {
        val isYZAxisInverseRotation = this.isYZAxisInverseRotation
        val position = this.position
        val y = (if (isYZAxisInverseRotation) position.z else position.y)
        val z = (if (isYZAxisInverseRotation) position.y else position.z)
        gl.glTranslatef(if (this.isXAxisEnabled) position.x else 0.0f, if (this.isYAxisEnabled) y else 0.0f, if (this.isZAxisEnabled) z else 0.0f)
    }

    protected fun rotate(gl: GL10) {
        this.internalRotate(gl, this.rotation)
    }

    protected fun internalRotate(gl: GL10, rotation: PLRotation) {
        val isYZAxisInverseRotation = this.isYZAxisInverseRotation
        val isReverseRotation = this.isReverseRotation
        val yDirection = (if (isYZAxisInverseRotation) 1.0f else 0.0f)
        val zDirection = (if (isYZAxisInverseRotation) 0.0f else 1.0f)
        if (this.isPitchEnabled) gl.glRotatef(if (isReverseRotation) rotation.pitch else -rotation.pitch, 1.0f, 0.0f, 0.0f)
        if (this.isYawEnabled) gl.glRotatef(if (isReverseRotation) rotation.yaw else -rotation.yaw, 0.0f, yDirection, zDirection)
        if (this.isRollEnabled) gl.glRotatef(if (isReverseRotation) rotation.roll else -rotation.roll, 0.0f, zDirection, yDirection)
    }

    protected fun beginAlpha(gl: GL10) {
        gl.glEnable(GL10.GL_BLEND)
        gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA)
        gl.glColor4f(1.0f, 1.0f, 1.0f, this.alpha)
    }

    protected fun endAlpha(gl: GL10) {
        gl.glDisable(GL10.GL_BLEND)
        gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }

    override fun clear() {
        val isVisible = this.isVisible
        if (isVisible) this.isVisible = false
        while (this.isRendering);
        this.internalClear()
        if (isVisible) this.isVisible = true
    }

    protected abstract fun internalClear()

    protected open fun beginRender(gl: GL10, renderer: PLIRenderer?) {
        gl.glPushMatrix()
        this.rotate(gl)
        this.translate(gl)
        this.beginAlpha(gl)
    }

    protected open fun endRender(gl: GL10, renderer: PLIRenderer?) {
        this.endAlpha(gl)
        gl.glPopMatrix()
    }

    override fun render(gl: GL10, renderer: PLIRenderer): Boolean {
        try {
            if (this.isVisible && this.isValid) {
                this.isRendering = true
                this.beginRender(gl, renderer)
                this.internalRender(gl, renderer)
                this.endRender(gl, renderer)
                this.isRendering = false
                return true
            }
        } catch (e: Throwable) {
            this.isRendering = false
            Timber.e(e)
        }
        return false
    }

    protected abstract fun internalRender(gl: GL10?, renderer: PLIRenderer?)
}
