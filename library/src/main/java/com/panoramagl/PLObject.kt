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

import com.panoramagl.computation.PLMath
import com.panoramagl.structs.PLPosition
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLRange.Companion.PLRangeMake
import com.panoramagl.structs.PLRotation

open class PLObject : PLObjectBase(), PLIObject {
    override var isXAxisEnabled: Boolean = false
    override var isYAxisEnabled: Boolean = false
    override var isZAxisEnabled: Boolean = false
    private var mPosition: PLPosition = PLPosition()
    private var mXRange: PLRange? = null
    private var mYRange: PLRange? = null
    private var mZRange: PLRange? = null

    override var isPitchEnabled: Boolean = false
    override var isYawEnabled: Boolean = false
    override var isRollEnabled: Boolean = false
    override var isReverseRotation: Boolean = false
    override var isYZAxisInverseRotation: Boolean = false
    private var mRotation: PLRotation = PLRotation()
    private var mPitchRange: PLRange? = null
    private var mYawRange: PLRange? = null
    private var mRollRange: PLRange? = null
    protected var tempRange: PLRange? = null
        private set

    override var alpha: Float = 0f
    override var defaultAlpha: Float = 0f

    override fun initializeValues() {
        mXRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)
        mYRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)
        mZRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)

        mPitchRange = PLRangeMake(PLConstants.kDefaultPitchMinRange, PLConstants.kDefaultPitchMaxRange)
        mYawRange = PLRangeMake(PLConstants.kDefaultYawMinRange, PLConstants.kDefaultYawMaxRange)
        mRollRange = PLRangeMake(PLConstants.kDefaultRollMinRange, PLConstants.kDefaultRollMaxRange)
        this.tempRange = PLRangeMake(0.0f, 0.0f)

        this.isZAxisEnabled = true
        this.isYAxisEnabled = this.isZAxisEnabled
        this.isXAxisEnabled = this.isYAxisEnabled
        this.isRollEnabled = true
        this.isYawEnabled = this.isRollEnabled
        this.isPitchEnabled = this.isYawEnabled

        this.isReverseRotation = false

        this.isYZAxisInverseRotation = true

        mPosition = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f)
        mRotation = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f)

        defaultAlpha = PLConstants.kDefaultAlpha
        alpha = defaultAlpha
    }

    override fun reset() {
        this.setRotation(0.0f, 0.0f, 0.0f)
        alpha = defaultAlpha
    }

    override var position: PLPosition
        get() = mPosition
        set(position) {
            run {
                this.x = position.x
                this.y = position.y
                this.z = position.z
            }
        }

    override fun setPosition(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    override var x: Float
        get() = mPosition.x
        set(x) {
            if (this.isXAxisEnabled) mPosition.x = PLMath.valueInRange(x, mXRange)
        }

    protected fun setInternalX(x: Float) {
        mPosition.x = PLMath.valueInRange(x, mXRange)
    }

    override var y: Float
        get() = mPosition.y
        set(y) {
            if (this.isYAxisEnabled) mPosition.y = PLMath.valueInRange(y, mYRange)
        }

    protected fun setInternalY(y: Float) {
        mPosition.y = PLMath.valueInRange(y, mYRange)
    }

    override var z: Float
        get() = mPosition.z
        set(z) {
            if (this.isZAxisEnabled) mPosition.z = PLMath.valueInRange(z, mZRange)
        }

    protected fun setInternalZ(z: Float) {
        mPosition.z = PLMath.valueInRange(z, mZRange)
    }

    override var rotation: PLRotation
        get() = mRotation
        set(rotation) {
            this.pitch = rotation.pitch
            this.yaw = rotation.yaw
            this.roll = rotation.roll
        }

    override fun setRotation(pitch: Float, yaw: Float) {
        this.pitch = pitch
        this.yaw = yaw
    }

    override fun setRotation(pitch: Float, yaw: Float, roll: Float) {
        this.pitch = pitch
        this.yaw = yaw
        this.roll = roll
    }

    override var pitch: Float
        get() = mRotation.pitch
        set(pitch) {
            if (this.isPitchEnabled) mRotation.pitch = this.getRotationAngleNormalized(pitch, mPitchRange!!)
        }

    protected fun setInternalPitch(pitch: Float) {
        mRotation.pitch = this.getRotationAngleNormalized(pitch, mPitchRange!!)
    }

    override var yaw: Float
        get() = mRotation.yaw
        set(yaw) {
            if (this.isYawEnabled) mRotation.yaw = this.getRotationAngleNormalized(yaw, mYawRange!!)
        }

    protected fun setInternalYaw(yaw: Float) {
        mRotation.yaw = this.getRotationAngleNormalized(yaw, mYawRange!!)
    }

    override var roll: Float
        get() = mRotation.roll
        set(roll) {
            if (this.isRollEnabled) mRotation.roll = this.getRotationAngleNormalized(roll, mRollRange!!)
        }

    protected fun setInternalRoll(roll: Float) {
        mRotation.roll = this.getRotationAngleNormalized(roll, mRollRange!!)
    }

    override var xRange: PLRange
        get() = mXRange!!
        set(xRange) {
            mXRange!!.setValues(xRange)
        }

    override fun setXRange(min: Float, max: Float) {
        mXRange!!.setValues(min, max)
    }

    override var xMin: Float
        get() = mXRange!!.min
        set(min) {
            mXRange!!.min = min
        }

    override var xMax: Float
        get() = mXRange!!.max
        set(max) {
            mXRange!!.max = max
        }

    override var yRange: PLRange
        get() = mYRange!!
        set(yRange) {
            mYRange!!.setValues(yRange)
        }

    override fun setYRange(min: Float, max: Float) {
        mYRange!!.setValues(min, max)
    }

    override var yMin: Float
        get() = mYRange!!.min
        set(min) {
            mYRange!!.min = min
        }

    override var yMax: Float
        get() = mYRange!!.max
        set(max) {
            mYRange!!.max = max
        }

    override var zRange: PLRange
        get() = mZRange!!
        set(zRange) {
            mZRange!!.setValues(zRange)
        }

    override fun setZRange(min: Float, max: Float) {
        mZRange!!.setValues(min, max)
    }

    override var zMin: Float
        get() = mZRange!!.min
        set(min) {
            mZRange!!.min = min
        }

    override var zMax: Float
        get() = mZRange!!.max
        set(max) {
            mZRange!!.max = max
        }

    override var pitchRange: PLRange
        get() = mPitchRange!!
        set(pitchRange) {
            mPitchRange!!.setValues(pitchRange)
        }

    override fun setPitchRange(min: Float, max: Float) {
        mPitchRange!!.setValues(min, max)
    }

    override var pitchMin: Float
        get() = mPitchRange!!.min
        set(min) {
            mPitchRange!!.min = min
        }

    override var pitchMax: Float
        get() = mPitchRange!!.max
        set(max) {
            mPitchRange!!.max = max
        }

    override var yawRange: PLRange
        get() = mYawRange!!
        set(yawRange) {
            mYawRange!!.setValues(yawRange)
        }

    override fun setYawRange(min: Float, max: Float) {
        mYawRange!!.setValues(min, max)
    }

    override var yawMin: Float
        get() = mYawRange!!.min
        set(min) {
            mYawRange!!.min = min
        }

    override var yawMax: Float
        get() = mYawRange!!.max
        set(max) {
            mYawRange!!.max = max
        }

    override var rollRange: PLRange
        get() = mRollRange!!
        set(rollRange) {
            mRollRange!!.setValues(rollRange)
        }

    override fun setRollRange(min: Float, max: Float) {
        mRollRange!!.setValues(min, max)
    }

    override var rollMin: Float
        get() = mRollRange!!.min
        set(min) {
            mRollRange!!.min = min
        }

    override var rollMax: Float
        get() = mRollRange!!.max
        set(max) {
            mRollRange!!.max = max
        }

    protected fun getRotationAngleNormalized(angle: Float, range: PLRange): Float {
        return PLMath.normalizeAngle(angle, tempRange!!.setValues(-range.max, -range.min))
    }

    override fun translate(position: PLPosition) {
        this.x = position.x
        this.y = position.y
        this.z = position.z
    }

    override fun translate(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    override fun translate(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    /**
     * rotate methods
     */
    override fun rotate(rotation: PLRotation) {
        this.pitch = rotation.pitch
        this.yaw = rotation.yaw
        this.roll = rotation.roll
    }

    override fun rotate(pitch: Float, yaw: Float) {
        this.pitch = pitch
        this.yaw = yaw
    }

    override fun rotate(pitch: Float, yaw: Float, roll: Float) {
        this.pitch = pitch
        this.yaw = yaw
        this.roll = roll
    }

    /**
     * clone methods
     */
    override fun clonePropertiesOf(`object`: PLIObject): Boolean {
        this.isXAxisEnabled = `object`.isXAxisEnabled
        this.isYAxisEnabled = `object`.isYAxisEnabled
        this.isZAxisEnabled = `object`.isZAxisEnabled

        this.isPitchEnabled = `object`.isPitchEnabled
        this.isYawEnabled = `object`.isYawEnabled
        this.isRollEnabled = `object`.isRollEnabled

        this.isReverseRotation = `object`.isReverseRotation

        this.isYZAxisInverseRotation = `object`.isYZAxisInverseRotation

        this.xRange = `object`.xRange
        this.yRange = `object`.yRange
        this.zRange = `object`.zRange

        this.pitchRange = `object`.pitchRange
        this.yawRange = `object`.yawRange
        this.rollRange = `object`.rollRange

        this.x = `object`.x
        this.y = `object`.y
        this.z = `object`.z

        this.pitch = `object`.pitch
        this.yaw = `object`.yaw
        this.roll = `object`.roll

        this.defaultAlpha = `object`.defaultAlpha
        this.alpha = `object`.alpha

        return true
    }

    /**
     * dealloc methods
     */
    @Throws(Throwable::class)
    protected open fun finalize() {
        mPosition = PLPosition()
        this.tempRange = null
        mZRange = null
        mYRange = null
        mXRange = null
        mRollRange = null
        mYawRange = null
        mPitchRange = null
    }
}