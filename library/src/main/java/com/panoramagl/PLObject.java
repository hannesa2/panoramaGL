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

package com.panoramagl;

import com.panoramagl.computation.PLMath;
import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLRotation;

public class PLObject extends PLObjectBase implements PLIObject
{
	/**member variables*/
	
	private boolean mIsXAxisEnabled, mIsYAxisEnabled, mIsZAxisEnabled;
	private PLPosition mPosition;
	private PLRange mXRange, mYRange, mZRange;
	
	private boolean mIsPitchEnabled, mIsYawEnabled, mIsRollEnabled, mIsReverseRotation, mIsYZAxisInverseRotation;
	private PLRotation mRotation;
	private PLRange mPitchRange, mYawRange, mRollRange, mTempRange;
	
	private float mAlpha, mDefaultAlpha;
	
	/**init methods*/
	
	public PLObject()
	{
		super();
	}
	
	@Override
	protected void initializeValues()
	{
		mXRange = PLRange.PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue);
		mYRange = PLRange.PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue);
		mZRange = PLRange.PLRangeMake(PLConstants.kFloatMinValue, PLConstants.kFloatMaxValue);
		
		mPitchRange = PLRange.PLRangeMake(PLConstants.kDefaultPitchMinRange, PLConstants.kDefaultPitchMaxRange);
		mYawRange = PLRange.PLRangeMake(PLConstants.kDefaultYawMinRange, PLConstants.kDefaultYawMaxRange);
		mRollRange = PLRange.PLRangeMake(PLConstants.kDefaultRollMinRange, PLConstants.kDefaultRollMaxRange);
		mTempRange = PLRange.PLRangeMake(0.0f, 0.0f);
		
		mIsXAxisEnabled = mIsYAxisEnabled = mIsZAxisEnabled = true;
		mIsPitchEnabled = mIsYawEnabled = mIsRollEnabled = true;
		
		mIsReverseRotation = false;
		
		mIsYZAxisInverseRotation = true;
		
		mPosition = PLPosition.PLPositionMake(0.0f, 0.0f, 0.0f);
		mRotation = PLRotation.PLRotationMake(0.0f, 0.0f, 0.0f);
		
		mAlpha = mDefaultAlpha = PLConstants.kDefaultAlpha;
	}
	
	/**reset methods*/
	
	@Override
	public void reset()
	{
		this.setRotation(0.0f, 0.0f, 0.0f);
		this.setInternalAlpha(mDefaultAlpha);
	}
	
	/**property methods*/
	
	@Override
	public PLPosition getPosition()
	{
		return mPosition;
	}
	
	@Override
	public void setPosition(PLPosition position)
	{
		if(position != null)
		{
			this.setX(position.x);
			this.setY(position.y);
			this.setZ(position.z);
		}
	}
	
	@Override
	public void setPosition(float x, float y, float z)
	{
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	@Override
	public float getX()
	{
		return mPosition.x;	
	}
	
	@Override
	public void setX(float x)
	{
		if(mIsXAxisEnabled)
			mPosition.x = PLMath.valueInRange(x, mXRange);
	}
	
	protected void setInternalX(float x)
	{
		mPosition.x = PLMath.valueInRange(x, mXRange);
	}
	
	@Override
	public float getY()
	{
		return mPosition.y;
	}
	
	@Override
	public void setY(float y)
	{
		if(mIsYAxisEnabled)
			mPosition.y = PLMath.valueInRange(y, mYRange);
	}
	
	protected void setInternalY(float y)
	{
		mPosition.y = PLMath.valueInRange(y, mYRange);
	}
	
	@Override
	public float getZ()
	{
		return mPosition.z;
	}
	
	@Override
	public void setZ(float z)
	{
		if(mIsZAxisEnabled)
			mPosition.z = PLMath.valueInRange(z, mZRange);
	}
	
	protected void setInternalZ(float z)
	{
		mPosition.z = PLMath.valueInRange(z, mZRange);
	}
	
	@Override
	public PLRotation getRotation()
	{
		return mRotation;
	}
	
	@Override
	public void setRotation(PLRotation rotation)
	{
		if(rotation != null)
		{
			this.setPitch(rotation.pitch);
			this.setYaw(rotation.yaw);
			this.setRoll(rotation.roll);
		}
	}
	
	@Override
	public void setRotation(float pitch, float yaw)
	{
		this.setPitch(pitch);
		this.setYaw(yaw);
	}
	
	@Override
	public void setRotation(float pitch, float yaw, float roll)
	{
		this.setPitch(pitch);
		this.setYaw(yaw);
		this.setRoll(roll);
	}
	
	@Override
	public float getPitch()
	{
		return mRotation.pitch;
	}
	
	@Override
	public void setPitch(float pitch)
	{
		if(mIsPitchEnabled)
			mRotation.pitch = this.getRotationAngleNormalized(pitch, mPitchRange);
	}
	
	protected void setInternalPitch(float pitch)
	{
		mRotation.pitch = this.getRotationAngleNormalized(pitch, mPitchRange);
	}
	
	@Override
	public float getYaw()
	{
		return mRotation.yaw;
	}
	
	@Override
	public void setYaw(float yaw)
	{
		if(mIsYawEnabled)
			mRotation.yaw = this.getRotationAngleNormalized(yaw, mYawRange);
	}
	
	protected void setInternalYaw(float yaw)
	{
		mRotation.yaw = this.getRotationAngleNormalized(yaw, mYawRange);
	}
	
	@Override
	public float getRoll()
	{
		return mRotation.roll;
	}
	
	@Override
	public void setRoll(float roll)
	{
		if(mIsRollEnabled)
			mRotation.roll = this.getRotationAngleNormalized(roll, mRollRange);
	}
	
	protected void setInternalRoll(float roll)
	{
		mRotation.roll = this.getRotationAngleNormalized(roll, mRollRange);
	}
	
	@Override
	public boolean isXAxisEnabled()
	{
		return mIsXAxisEnabled;
	}
	
	@Override
	public void setXAxisEnabled(boolean isXAxisEnabled)
	{
		mIsXAxisEnabled = isXAxisEnabled;
	}
	
	@Override
	public boolean isYAxisEnabled()
	{
		return mIsYAxisEnabled;
	}
	
	@Override
	public void setYAxisEnabled(boolean isYAxisEnabled)
	{
		mIsYAxisEnabled = isYAxisEnabled;
	}
	
	@Override
	public boolean isZAxisEnabled()
	{
		return mIsZAxisEnabled;
	}
	
	@Override
	public void setZAxisEnabled(boolean isZAxisEnabled)
	{
		mIsZAxisEnabled = isZAxisEnabled;
	}
	
	@Override
	public PLRange getXRange()
	{
		return mXRange;
	}
	
	@Override
	public void setXRange(PLRange xRange)
	{
		mXRange.setValues(xRange);
	}
	
	@Override
	public void setXRange(float min, float max)
	{
		mXRange.setValues(min, max);
	}
	
	protected void setInternalXRange(float min, float max)
	{
		mXRange.setValues(min, max);
	}
	
	@Override
	public float getXMin()
	{
		return mXRange.min;
	}
	
	@Override
	public void setXMin(float min)
	{
		mXRange.min = min;
	}
	
	@Override
	public float getXMax()
	{
		return mXRange.max;
	}
	
	@Override
	public void setXMax(float max)
	{
		mXRange.max = max;
	}
	
	@Override
	public PLRange getYRange()
	{
		return mYRange;
	}
	
	@Override
	public void setYRange(PLRange yRange)
	{
		mYRange.setValues(yRange);
	}
	
	@Override
	public void setYRange(float min, float max)
	{
		mYRange.setValues(min, max);
	}
	
	protected void setInternalYRange(float min, float max)
	{
		mYRange.setValues(min, max);
	}
	
	@Override
	public float getYMin()
	{
		return mYRange.min;
	}
	
	@Override
	public void setYMin(float min)
	{
		mYRange.min = min;
	}
	
	@Override
	public float getYMax()
	{
		return mYRange.max;
	}
	
	@Override
	public void setYMax(float max)
	{
		mYRange.max = max;
	}
	
	@Override
	public PLRange getZRange()
	{
		return mZRange;
	}
	
	@Override
	public void setZRange(PLRange zRange)
	{
		mZRange.setValues(zRange);
	}
	
	@Override
	public void setZRange(float min, float max)
	{
		mZRange.setValues(min, max);
	}
	
	protected void setInternalZRange(float min, float max)
	{
		mZRange.setValues(min, max);
	}
	
	@Override
	public float getZMin()
	{
		return mZRange.min;
	}
	
	@Override
	public void setZMin(float min)
	{
		mZRange.min = min;
	}
	
	@Override
	public float getZMax()
	{
		return mZRange.max;
	}
	
	@Override
	public void setZMax(float max)
	{
		mZRange.max = max;
	}
	
	@Override
	public boolean isPitchEnabled()
	{
		return mIsPitchEnabled;
	}
	
	@Override
	public void setPitchEnabled(boolean isPitchEnabled)
	{
		mIsPitchEnabled = isPitchEnabled;
	}
	
	@Override
	public boolean isYawEnabled()
	{
		return mIsYawEnabled;
	}
	
	@Override
	public void setYawEnabled(boolean isYawEnabled)
	{
		mIsYawEnabled = isYawEnabled;
	}
	
	@Override
	public boolean isRollEnabled()
	{
		return mIsRollEnabled;
	}
	
	@Override
	public void setRollEnabled(boolean isRollEnabled)
	{
		mIsRollEnabled = isRollEnabled;
	}
	
	@Override
	public boolean isReverseRotation()
	{
		return mIsReverseRotation;
	}
	
	@Override
	public void setReverseRotation(boolean isReverseRotation)
	{
		mIsReverseRotation = isReverseRotation;
	}
	
	@Override
	public boolean isYZAxisInverseRotation()
	{
		return mIsYZAxisInverseRotation;
	}
	
	@Override
	public void setYZAxisInverseRotation(boolean isYZAxisInverseRotation)
	{
		mIsYZAxisInverseRotation = isYZAxisInverseRotation;
	}
	
	@Override
	public PLRange getPitchRange()
	{
		return mPitchRange;
	}
	
	@Override
	public void setPitchRange(PLRange pitchRange)
	{
		mPitchRange.setValues(pitchRange);
	}
	
	@Override
	public void setPitchRange(float min, float max)
	{
		mPitchRange.setValues(min, max);
	}
	
	protected void setInternalPitchRange(float min, float max)
	{
		mPitchRange.setValues(min, max);
	}
	
	@Override
	public float getPitchMin()
	{
		return mPitchRange.min;
	}
	
	@Override
	public void setPitchMin(float min)
	{
		mPitchRange.min = min;
	}
	
	@Override
	public float getPitchMax()
	{
		return mPitchRange.max;
	}
	
	@Override
	public void setPitchMax(float max)
	{
		mPitchRange.max = max;
	}
	
	@Override
	public PLRange getYawRange()
	{
		return mYawRange;
	}
	
	@Override
	public void setYawRange(PLRange yawRange)
	{
		mYawRange.setValues(yawRange);
	}
	
	@Override
	public void setYawRange(float min, float max)
	{
		mYawRange.setValues(min, max);
	}
	
	protected void setInternalYawRange(float min, float max)
	{
		mYawRange.setValues(min, max);
	}
	
	@Override
	public float getYawMin()
	{
		return mYawRange.min;
	}
	
	@Override
	public void setYawMin(float min)
	{
		mYawRange.min = min;
	}
	
	@Override
	public float getYawMax()
	{
		return mYawRange.max;
	}
	
	@Override
	public void setYawMax(float max)
	{
		mYawRange.max = max;
	}
	
	@Override
	public PLRange getRollRange()
	{
		return mRollRange;
	}
	
	@Override
	public void setRollRange(PLRange rollRange)
	{
		mRollRange.setValues(rollRange);
	}
	
	@Override
	public void setRollRange(float min, float max)
	{
		mRollRange.setValues(min, max);
	}
	
	protected void setInternalRollRange(float min, float max)
	{
		mRollRange.setValues(min, max);
	}
	
	@Override
	public float getRollMin()
	{
		return mRollRange.min;
	}
	
	@Override
	public void setRollMin(float min)
	{
		mRollRange.min = min;
	}
	
	@Override
	public float getRollMax()
	{
		return mRollRange.max;
	}
	
	@Override
	public void setRollMax(float max)
	{
		mRollRange.max = max;
	}
	
	protected PLRange getTempRange()
	{
		return mTempRange;
	}
	
	@Override
	public float getAlpha()
	{
		return mAlpha;
	}
	
	@Override
	public void setAlpha(float alpha)
	{
		mAlpha = alpha;
	}
	
	protected void setInternalAlpha(float alpha)
	{
		mAlpha = alpha;
	}
	
	@Override
	public float getDefaultAlpha()
	{
		return mDefaultAlpha;
	}
	
	@Override
	public void setDefaultAlpha(float defaultAlpha)
	{
		mDefaultAlpha = defaultAlpha;
	}
	
	protected void setInternalDefaultAlpha(float defaultAlpha)
	{
		mDefaultAlpha = defaultAlpha;
	}
	
	/**normalize methods*/
	
	protected float getRotationAngleNormalized(float angle, PLRange range)
	{
		return PLMath.normalizeAngle(angle, mTempRange.setValues(-range.max, -range.min));
	}
	
	/**translate methods*/
	
	@Override
	public void translate(PLPosition position)
	{
		if(position != null)
		{
			this.setX(position.x);
			this.setY(position.y);
			this.setZ(position.z);
		}
	}
	
	@Override
	public void translate(float x, float y)
	{
		this.setX(x);
		this.setY(y);
	}
	
	@Override
	public void translate(float x, float y, float z)
	{
		this.setX(x);
		this.setY(y);
		this.setZ(z);
	}
	
	/**rotate methods*/
	
	@Override
	public void rotate(PLRotation rotation)
	{
		if(rotation != null)
		{
			this.setPitch(rotation.pitch);
			this.setYaw(rotation.yaw);
			this.setRoll(rotation.roll);
		}
	}
	
	@Override
	public void rotate(float pitch, float yaw)
	{
		this.setPitch(pitch);
		this.setYaw(yaw);
	}
	
	@Override
	public void rotate(float pitch, float yaw, float roll)
	{
		this.setPitch(pitch);
		this.setYaw(yaw);
		this.setRoll(roll);
	}
	
	/**clone methods*/
	
	@Override
	public boolean clonePropertiesOf(PLIObject object)
	{
		if(object != null)
		{
			this.setXAxisEnabled(object.isXAxisEnabled());
			this.setYAxisEnabled(object.isYAxisEnabled());
			this.setZAxisEnabled(object.isZAxisEnabled());
			
			this.setPitchEnabled(object.isPitchEnabled());
			this.setYawEnabled(object.isYawEnabled());
			this.setRollEnabled(object.isRollEnabled());
			
			this.setReverseRotation(object.isReverseRotation());
			
			this.setYZAxisInverseRotation(object.isYZAxisInverseRotation());
			
			this.setXRange(object.getXRange());
			this.setYRange(object.getYRange());
			this.setZRange(object.getZRange());
			
			this.setPitchRange(object.getPitchRange());
			this.setYawRange(object.getYawRange());
			this.setRollRange(object.getRollRange());
			
			this.setX(object.getX());
			this.setY(object.getY());
			this.setZ(object.getZ());
			
			this.setPitch(object.getPitch());
			this.setYaw(object.getYaw());
			this.setRoll(object.getRoll());
			
			this.setDefaultAlpha(object.getDefaultAlpha());
			this.setAlpha(object.getAlpha());
			
			return true;
		}
		return false;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mPosition = null;
		mXRange = mYRange = mZRange = mTempRange = null;
		mRotation = null;
		mPitchRange = mYawRange = mRollRange = null;
		super.finalize();
	}
}