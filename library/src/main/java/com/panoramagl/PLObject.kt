package com.panoramagl

import com.panoramagl.computation.PLMath
import com.panoramagl.structs.PLPosition
import com.panoramagl.structs.PLRange
import com.panoramagl.structs.PLRange.Companion.PLRangeMake
import com.panoramagl.structs.PLRotation

open class PLObject : PLObjectBase(), PLIObject {
    override var isXAxisEnabled = true
    override var isYAxisEnabled = true
    override var isZAxisEnabled = true
    private var mXRange: PLRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)
    private var mYRange: PLRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)
    private var mZRange: PLRange = PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue)
    override var isPitchEnabled = true
    override var isYawEnabled = true
    override var isRollEnabled = true
    override var isReverseRotation = false
    override var isYZAxisInverseRotation = true

    private var mRotation = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f)
    private var mPosition = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f)
    private var mPitchRange= PLRangeMake(PLConstants.kDefaultPitchMinRange, PLConstants.kDefaultPitchMaxRange)
    private var mYawRange = PLRangeMake(PLConstants.kDefaultYawMinRange, PLConstants.kDefaultYawMaxRange)
    private var mRollRange = PLRangeMake(PLConstants.kDefaultRollMinRange, PLConstants.kDefaultRollMaxRange)
    protected var tempRange: PLRange = PLRangeMake(0.0f, 0.0f)
        private set
    final override var defaultAlpha = PLConstants.kDefaultAlpha
    override var alpha = defaultAlpha

    override fun initializeValues() = Unit

    override fun reset() {
        this.setRotation(0.0f, 0.0f, 0.0f)
        alpha = defaultAlpha
    }

    override var position: PLPosition
        get() = mPosition
        set(position) {
            x = position.x
            y = position.y
            z = position.z
        }

    override fun setPosition(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }

    override var x: Float
        get() = mPosition.x
        set(x) {
            if (isXAxisEnabled) mPosition.x = PLMath.valueInRange(x, mXRange)
        }

    protected fun setInternalX(x: Float) {
        mPosition.x = PLMath.valueInRange(x, mXRange)
    }

    override var y: Float
        get() = mPosition.y
        set(y) {
            if (isYAxisEnabled) mPosition.y = PLMath.valueInRange(y, mYRange)
        }

    protected fun setInternalY(y: Float) {
        mPosition.y = PLMath.valueInRange(y, mYRange)
    }

    override var z: Float
        get() = mPosition.z
        set(z) {
            if (isZAxisEnabled) mPosition.z = PLMath.valueInRange(z, mZRange)
        }

    protected fun setInternalZ(z: Float) {
        mPosition.z = PLMath.valueInRange(z, mZRange)
    }

    override var rotation: PLRotation
        get() = mRotation
        set(rotation) {
            pitch = rotation.pitch
            yaw = rotation.yaw
            roll = rotation.roll
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
            if (isPitchEnabled) mRotation.pitch = getRotationAngleNormalized(pitch, mPitchRange)
        }

    protected fun setInternalPitch(pitch: Float) {
        mRotation.pitch = getRotationAngleNormalized(pitch, mPitchRange)
    }

    override var yaw: Float
        get() = mRotation.yaw
        set(yaw) {
            if (isYawEnabled) mRotation.yaw = getRotationAngleNormalized(yaw, mYawRange)
        }

    protected fun setInternalYaw(yaw: Float) {
        mRotation.yaw = getRotationAngleNormalized(yaw, mYawRange)
    }

    override var roll: Float
        get() = mRotation.roll
        set(roll) {
            if (isRollEnabled) mRotation.roll = getRotationAngleNormalized(roll, mRollRange)
        }

    protected fun setInternalRoll(roll: Float) {
        mRotation.roll = getRotationAngleNormalized(roll, mRollRange)
    }

    override var xRange: PLRange
        get() = mXRange
        set(xRange) {
            mXRange.setValues(xRange)
        }

    override fun setXRange(min: Float, max: Float) {
        mXRange.setValues(min, max)
    }

    protected fun setInternalXRange(min: Float, max: Float) {
        mXRange.setValues(min, max)
    }

    override var xMin: Float
        get() = mXRange.min
        set(min) {
            mXRange.min = min
        }
    override var xMax: Float
        get() = mXRange.max
        set(max) {
            mXRange.max = max
        }
    override var yRange: PLRange
        get() = mYRange
        set(yRange) {
            mYRange.setValues(yRange)
        }

    override fun setYRange(min: Float, max: Float) {
        mYRange.setValues(min, max)
    }

    protected fun setInternalYRange(min: Float, max: Float) {
        mYRange.setValues(min, max)
    }

    override var yMin: Float
        get() = mYRange.min
        set(min) {
            mYRange.min = min
        }
    override var yMax: Float
        get() = mYRange.max
        set(max) {
            mYRange.max = max
        }
    override var zRange: PLRange
        get() = mZRange
        set(zRange) {
            mZRange.setValues(zRange)
        }

    override fun setZRange(min: Float, max: Float) {
        mZRange.setValues(min, max)
    }

    protected fun setInternalZRange(min: Float, max: Float) {
        mZRange.setValues(min, max)
    }

    override var zMin: Float
        get() = mZRange.min
        set(min) {
            mZRange.min = min
        }
    override var zMax: Float
        get() = mZRange.max
        set(max) {
            mZRange.max = max
        }
    override var pitchRange: PLRange
        get() = mPitchRange
        set(pitchRange) {
            mPitchRange.setValues(pitchRange)
        }

    override fun setPitchRange(min: Float, max: Float) {
        mPitchRange.setValues(min, max)
    }

    protected fun setInternalPitchRange(min: Float, max: Float) {
        mPitchRange.setValues(min, max)
    }

    override var pitchMin: Float
        get() = mPitchRange.min
        set(min) {
            mPitchRange.min = min
        }
    override var pitchMax: Float
        get() = mPitchRange.max
        set(max) {
            mPitchRange.max = max
        }
    override var yawRange: PLRange
        get() = mYawRange
        set(yawRange) {
            mYawRange.setValues(yawRange)
        }

    override fun setYawRange(min: Float, max: Float) {
        mYawRange.setValues(min, max)
    }

    protected fun setInternalYawRange(min: Float, max: Float) {
        mYawRange.setValues(min, max)
    }

    override var yawMin: Float
        get() = mYawRange.min
        set(min) {
            mYawRange.min = min
        }
    override var yawMax: Float
        get() = mYawRange.max
        set(max) {
            mYawRange.max = max
        }
    override var rollRange: PLRange
        get() = mRollRange
        set(rollRange) {
            mRollRange.setValues(rollRange)
        }

    override fun setRollRange(min: Float, max: Float) {
        mRollRange.setValues(min, max)
    }

    protected fun setInternalRollRange(min: Float, max: Float) {
        mRollRange.setValues(min, max)
    }

    override var rollMin: Float
        get() = mRollRange.min
        set(min) {
            mRollRange.min = min
        }
    override var rollMax: Float
        get() = mRollRange.max
        set(max) {
            mRollRange.max = max
        }

    protected fun getRotationAngleNormalized(angle: Float, range: PLRange?): Float {
        return PLMath.normalizeAngle(angle, tempRange.setValues(-range!!.max, -range.min))
    }

    override fun translate(position: PLPosition) {
        x = position.x
        y = position.y
        z = position.z
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

    override fun rotate(rotation: PLRotation) {
        pitch = rotation.pitch
        yaw = rotation.yaw
        roll = rotation.roll
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
        isXAxisEnabled = `object`.isXAxisEnabled
        isYAxisEnabled = `object`.isYAxisEnabled
        isZAxisEnabled = `object`.isZAxisEnabled
        isPitchEnabled = `object`.isPitchEnabled
        isYawEnabled = `object`.isYawEnabled
        isRollEnabled = `object`.isRollEnabled
        isReverseRotation = `object`.isReverseRotation
        isYZAxisInverseRotation = `object`.isYZAxisInverseRotation
        xRange = `object`.xRange
        yRange = `object`.yRange
        zRange = `object`.zRange
        pitchRange = `object`.pitchRange
        yawRange = `object`.yawRange
        rollRange = `object`.rollRange
        x = `object`.x
        y = `object`.y
        z = `object`.z
        pitch = `object`.pitch
        yaw = `object`.yaw
        roll = `object`.roll
        defaultAlpha = `object`.defaultAlpha
        alpha = `object`.alpha
        return true
    }

    @Throws(Throwable::class)
    protected open fun finalize() {
        mZRange = tempRange
        mYRange = mZRange
        mXRange = mYRange
        mYawRange = mRollRange
        mPitchRange = mYawRange
    }
}
