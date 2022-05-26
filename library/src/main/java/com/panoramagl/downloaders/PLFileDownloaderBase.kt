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
package com.panoramagl.downloaders

import kotlin.jvm.JvmOverloads
import com.panoramagl.PLObjectBase
import kotlin.Throws

abstract class PLFileDownloaderBase @JvmOverloads constructor(var url: String, givenListener: PLFileDownloaderListener? = null) :
    PLObjectBase(),
    PLIFileDownloader {
    private var running = false
    private var thread: Thread? = null
    private var threadRunnable: Runnable? = null
    private var localListener: PLFileDownloaderListener?

    init {
        localListener = givenListener
    }

    override fun initializeValues() {
        running = false
        thread = null
        threadRunnable = null
    }

    override fun isRunning(): Boolean {
        return running
    }

    protected fun setRunning(isRunning: Boolean) {
        running = isRunning
    }

    override fun getListener(): PLFileDownloaderListener? {
        return localListener
    }

    override fun setListener(listener: PLFileDownloaderListener): PLIFileDownloader {
        if (!running) {
            synchronized(this) { localListener = listener }
        }
        return this
    }

    /**
     * download methods
     */
    protected abstract fun downloadFile(): ByteArray
    override fun download(): ByteArray? {
        if (!running) {
            synchronized(this) { return downloadFile() }
        }
        return null
    }

    override fun downloadAsynchronously(): Boolean {
        if (!running) {
            synchronized(this) {
                if (threadRunnable == null) {
                    threadRunnable = Runnable { downloadFile() }
                }
                thread = Thread(threadRunnable)
                thread?.start()
                return true
            }
        }
        return false
    }

    /**
     * stop methods
     */
    override fun stop(): Boolean {
        if (running) {
            synchronized(this) {
                running = false
                thread = null
                localListener?.didStopDownload(url)
                return true
            }
        }
        return false
    }

    @Throws(Throwable::class)
    protected open fun finalize() {
        thread = null
        threadRunnable = null
    }

}
