package com.panoramagl.structs

interface PLIStruct<T> {
    val isResetted: Boolean
    fun reset(): T
    fun setValues(obj: T): T
}
