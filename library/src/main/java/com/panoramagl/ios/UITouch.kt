package com.panoramagl.ios

import android.view.View
import kotlin.jvm.JvmOverloads
import com.panoramagl.ios.structs.CGPoint
import com.panoramagl.PLIReleaseView
import kotlin.Throws

class UITouch @JvmOverloads constructor(var view: View?, position: CGPoint? = CGPoint.CGPointMake(0.0f, 0.0f), tapCount: Int = 1) : PLIReleaseView {
    private var internalTapCount: Int
    private var internalPosition: CGPoint = CGPoint.CGPointMake(position)

    init {
        internalTapCount = tapCount
    }

    var tapCount: Int
        get() = internalTapCount
        set(tapCount) {
            if (tapCount > 0) internalTapCount = tapCount
        }
    var position: CGPoint?
        get() = internalPosition
        set(point) {
            if (point != null) internalPosition.setValues(point)
        }

    fun setPosition(x: Float, y: Float) {
        internalPosition.x = x
        internalPosition.y = y
    }

    fun locationInView(): CGPoint {
        return internalPosition
    }

    override fun releaseView() {
        view = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        view = null
    }

    override fun toString(): String {
        return "${internalPosition.x}/${internalPosition.y}"
    }
}
