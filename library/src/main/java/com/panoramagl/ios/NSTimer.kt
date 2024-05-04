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
package com.panoramagl.ios

import android.os.SystemClock
import timber.log.Timber
import java.util.*

class NSTimer(date: Date, interval: Float, target: Runnable?, userInfo: Array<Any>?, repeats: Boolean) : Any() {
    var isValid = true
        private set
    private val mInterval: Long
    private var mTarget: Runnable?
    private var mUserInfo: Array<Any>?
    private val mRepeats: Boolean
    private var mThread: Thread?
    private var mLastTime: Long
    private var mTime: Long = 0

    init {
        mInterval = (interval * 1000.0f).toLong()
        mTarget = target
        mUserInfo = userInfo
        mRepeats = repeats
        mLastTime = date.time
        mThread = Thread {
            while (isValid) {
                mTime = SystemClock.uptimeMillis()
                if (mTime - mLastTime >= mInterval) {
                    try {
                        mTarget!!.run(this@NSTimer, mUserInfo)
                    } catch (e: Throwable) {
                        Timber.e(e)
                    }
                    if (!mRepeats) invalidate()
                }
                mLastTime = mTime
                try {
                    Thread.sleep(mInterval)
                } catch (ignored: Throwable) {
                }
            }
        }
        mThread!!.start()
    }

    fun invalidate() {
        isValid = false
        mThread = null
        mTarget = null
        mUserInfo = null
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        try {
            invalidate()
        } catch (ignored: Throwable) {
        }
    }

    interface Runnable {
        fun run(target: NSTimer?, userInfo: Array<Any>?)
    }

    companion object {
        @JvmStatic
        fun scheduledTimerWithTimeInterval(interval: Float, target: Runnable?, userInfo: Array<Any>?, repeats: Boolean): NSTimer {
            return NSTimer(Date(SystemClock.uptimeMillis()), interval, target, userInfo, repeats)
        }
    }
}
