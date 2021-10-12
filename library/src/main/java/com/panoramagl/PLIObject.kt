package com.panoramagl

import com.panoramagl.structs.PLPosition
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLRotation

interface PLIObject {
    
    fun reset()

    var isXAxisEnabled: Boolean
    var isYAxisEnabled: Boolean
    var isZAxisEnabled: Boolean
    var xRange: PLRange
    fun setXRange(min: Float, max: Float)
    var xMin: Float
    var xMax: Float
    var yRange: PLRange
    fun setYRange(min: Float, max: Float)
    var yMin: Float
    var yMax: Float
    var zRange: PLRange
    fun setZRange(min: Float, max: Float)
    var zMin: Float
    var zMax: Float
    var isPitchEnabled: Boolean
    var isYawEnabled: Boolean
    var isRollEnabled: Boolean
    var isReverseRotation: Boolean
    var isYZAxisInverseRotation: Boolean
    var pitchRange: PLRange
    fun setPitchRange(min: Float, max: Float)
    var pitchMin: Float
    var pitchMax: Float
    var yawRange: PLRange
    fun setYawRange(min: Float, max: Float)
    var yawMin: Float
    var yawMax: Float
    var rollRange: PLRange
    fun setRollRange(min: Float, max: Float)
    var rollMin: Float
    var rollMax: Float
    var alpha: Float
    var defaultAlpha: Float
    var position: PLPosition
    fun setPosition(x: Float, y: Float, z: Float)
    var x: Float
    var y: Float
    var z: Float
    var rotation: PLRotation
    fun setRotation(pitch: Float, yaw: Float)
    fun setRotation(pitch: Float, yaw: Float, roll: Float)
    var pitch: Float
    var yaw: Float
    var roll: Float

    fun translate(position: PLPosition)
    fun translate(x: Float, y: Float)
    fun translate(x: Float, y: Float, z: Float)

    fun rotate(rotation: PLRotation)
    fun rotate(pitch: Float, yaw: Float)
    fun rotate(pitch: Float, yaw: Float, roll: Float)

    fun clonePropertiesOf(`object`: PLIObject): Boolean
}
