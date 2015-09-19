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

package com.panoramagl.interpreters;

import java.util.List;

public interface PLITokenInfo
{
	/**property methods*/
	
	String getName();
	void setName(String name);
	
	List<Object> getValues();
	void setValues(List<Object> values);
	
	/**value methods*/
	
	boolean hasValue(int index);
	Object getValue(int index);
	String getString(int index);
	boolean getBoolean(int index);
	int getInt(int index);
	float getFloat(int index);
	double getDouble(int index);
	PLITokenInfo getTokenInfo(int index);
	
	int valuesLength();
	boolean addValue(Object value);
	boolean insertValue(Object value, int index);
	boolean removeValue(Object value);
	Object removeValueAtIndex(int index);
	boolean removeAllValues();
}