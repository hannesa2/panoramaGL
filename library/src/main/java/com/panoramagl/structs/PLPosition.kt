package com.panoramagl.structs

import com.panoramagl.ios.structs.UIAcceleration

class PLPosition @JvmOverloads constructor(
    @kotlin.jvm.JvmField var x: Float = 0.0f,
    @kotlin.jvm.JvmField var y: Float = 0.0f,
    @kotlin.jvm.JvmField var z: Float = 0.0f
) : PLIStruct<PLPosition> {
    constructor(position: PLPosition) : this(position.x, position.y, position.z)

    override val isResetted: Boolean
        get() = x == 0.0f && y == 0.0f && z == 0.0f

    override fun reset(): PLPosition {
        z = 0.0f
        y = z
        x = y
        return this
    }

    override fun setValues(obj: PLPosition): PLPosition {
        x = obj.x
        y = obj.y
        z = obj.z
        return this
    }

    fun setValues(acceleration: UIAcceleration): PLPosition {
        x = acceleration.x
        y = acceleration.y
        z = acceleration.z
        return this
    }

    fun setValues(values: FloatArray): PLPosition {
        x = values[0]
        y = values[1]
        z = values[2]
        return this
    }

    fun setValues(x: Float, y: Float, z: Float): PLPosition {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun clone(): PLPosition {
        return PLPosition(x, y, z)
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is PLPosition) {
            if (this === other) return true
            val position = other
            return x == position.x && y == position.y && z == position.z
        }
        return false
    }

    companion object {
        fun PLPositionMake(): PLPosition {
            return PLPosition()
        }

        @JvmStatic
        fun PLPositionMake(x: Float, y: Float, z: Float): PLPosition {
            return PLPosition(x, y, z)
        }
    }

}
