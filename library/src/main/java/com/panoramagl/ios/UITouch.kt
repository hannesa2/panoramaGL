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
package com.panoramagl.ios

import android.view.View
import kotlin.jvm.JvmOverloads
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.PLIReleaseView
import kotlin.Throws

class UITouch @JvmOverloads constructor(var view: View?, position: CGPoint? = CGPoint.CGPointMake(0.0f, 0.0f), tapCount: Int = 1) : PLIReleaseView {
    private var mTapCount: Int
    private var mPosition: CGPoint?
    var tapCount: Int
        get() = mTapCount
        set(tapCount) {
            if (tapCount > 0) mTapCount = tapCount
        }
    var position: CGPoint?
        get() = mPosition
        set(point) {
            if (point != null) mPosition!!.setValues(point)
        }

    fun setPosition(x: Float, y: Float) {
        mPosition!!.x = x
        mPosition!!.y = y
    }

    fun locationInView(view: View?): CGPoint? {
        return mPosition
    }

    override fun releaseView() {
        view = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        view = null
        mPosition = null
        super.finalize()
    }

    init {
        mPosition = CGPoint.CGPointMake(position)
        mTapCount = tapCount
    }
}