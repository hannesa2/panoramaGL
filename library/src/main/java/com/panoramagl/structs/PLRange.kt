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
package com.panoramagl.structs

import kotlin.jvm.JvmOverloads

class PLRange @JvmOverloads constructor(@kotlin.jvm.JvmField var min: Float = 0.0f,@kotlin.jvm.JvmField var max: Float = 0.0f) : PLIStruct<PLRange> {

    override val isResetted: Boolean
        get() = min == 0.0f && max == 0.0f

    override fun setValues(obj: PLRange): PLRange {
        min = obj.min
        max = obj.max
        return this
    }

    override fun reset(): PLRange {
        max = 0.0f
        min = max
        return this
    }

    fun setValues(min: Float, max: Float): PLRange {
        this.min = min
        this.max = max
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (other is PLRange) {
            if (this === other) return true
            val range = other
            return min == range.min && max == range.max
        }
        return false
    }

    companion object {

        @JvmStatic
        fun PLRangeMake(min: Float, max: Float): PLRange {
            return PLRange(min, max)
        }
    }

}
