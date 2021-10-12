package com.panoramagl

abstract class PLObjectBase : Any() {

    init {
        initializeValues()
    }

    protected abstract fun initializeValues()

    override fun equals(other: Any?): Boolean {
        return other != null && other.hashCode() == this.hashCode()
    }

}
