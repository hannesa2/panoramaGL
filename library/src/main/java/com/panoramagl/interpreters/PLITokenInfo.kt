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
package com.panoramagl.interpreters

interface PLITokenInfo {
    var name: String?
    fun getValues(): List<Any>?
    fun setValues(values: List<Any>)
    fun hasValue(index: Int): Boolean
    fun getValue(index: Int): Any?
    fun getString(index: Int): String?
    fun getBoolean(index: Int): Boolean
    fun getInt(index: Int): Int
    fun getFloat(index: Int): Float
    fun getDouble(index: Int): Double
    fun getTokenInfo(index: Int): PLITokenInfo?
    fun valuesLength(): Int
    fun addValue(value: Any): Boolean
    fun insertValue(value: Any, index: Int): Boolean
    fun removeValue(value: Any): Boolean
    fun removeValueAtIndex(index: Int): Any?
    fun removeAllValues(): Boolean
}
