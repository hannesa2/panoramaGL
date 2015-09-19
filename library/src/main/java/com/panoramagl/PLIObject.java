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

import com.panoramagl.structs.PLPosition;
import com.panoramagl.structs.PLRange;
import com.panoramagl.structs.PLRotation;

public interface PLIObject
{
	/**reset methods*/
	
	void reset();
	
	/**property methods*/
	
	boolean isXAxisEnabled();
	void setXAxisEnabled(boolean isXAxisEnabled);
	
	boolean isYAxisEnabled();
	void setYAxisEnabled(boolean isYAxisEnabled);
	
	boolean isZAxisEnabled();
	void setZAxisEnabled(boolean isZAxisEnabled);
	
	PLRange getXRange();
	void setXRange(PLRange xRange);
	void setXRange(float min, float max);
	float getXMin();
	void setXMin(float min);
	float getXMax();
	void setXMax(float max);
	
	PLRange getYRange();
	void setYRange(PLRange yRange);
	void setYRange(float min, float max);
	float getYMin();
	void setYMin(float min);
	float getYMax();
	void setYMax(float max);
	
	PLRange getZRange();
	void setZRange(PLRange zRange);
	void setZRange(float min, float max);
	float getZMin();
	void setZMin(float min);
	float getZMax();
	void setZMax(float max);
	
	boolean isPitchEnabled();
	void setPitchEnabled(boolean isPitchEnabled);
	
	boolean isYawEnabled();
	void setYawEnabled(boolean isYawEnabled);
	
	boolean isRollEnabled();
	void setRollEnabled(boolean isRollEnabled);
	
	boolean isReverseRotation();
	void setReverseRotation(boolean isReverseRotation);
	
	boolean isYZAxisInverseRotation();
	void setYZAxisInverseRotation(boolean isYZAxisInverseRotation);
	
	PLRange getPitchRange();
	void setPitchRange(PLRange pitchRange);
	void setPitchRange(float min, float max);
	float getPitchMin();
	void setPitchMin(float min);
	float getPitchMax();
	void setPitchMax(float max);
	
	PLRange getYawRange();
	void setYawRange(PLRange yawRange);
	void setYawRange(float min, float max);
	float getYawMin();
	void setYawMin(float min);
	float getYawMax();
	void setYawMax(float max);
	
	PLRange getRollRange();
	void setRollRange(PLRange rollRange);
	void setRollRange(float min, float max);
	float getRollMin();
	void setRollMin(float min);
	float getRollMax();
	void setRollMax(float max);
	
	float getAlpha();
	void setAlpha(float alpha);
	
	float getDefaultAlpha();
	void setDefaultAlpha(float defaultAlpha);
	
	PLPosition getPosition();
	void setPosition(PLPosition position);
	void setPosition(float x, float y, float z);
	
	float getX();
	void setX(float x);
	
	float getY();
	void setY(float y);
	
	float getZ();
	void setZ(float z);
	
	PLRotation getRotation();
	void setRotation(PLRotation rotation);
	void setRotation(float pitch, float yaw);
	void setRotation(float pitch, float yaw, float roll);
	
	float getPitch();
	void setPitch(float pitch);
	
	float getYaw();
	void setYaw(float yaw);
	
	float getRoll();
	void setRoll(float roll);
	
	/**translate methods*/
	
	void translate(PLPosition position);
	void translate(float x, float y);
	void translate(float x, float y, float z);
	
	/**rotate methods*/
	
	void rotate(PLRotation rotation);
	void rotate(float pitch, float yaw);
	void rotate(float pitch, float yaw, float roll);
	
	/**clone methods*/
	
	boolean clonePropertiesOf(PLIObject object);
}