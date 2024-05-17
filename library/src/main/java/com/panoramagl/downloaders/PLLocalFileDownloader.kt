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

import android.content.Context
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

class PLLocalFileDownloader(private var context: Context, url: String?, listener: PLFileDownloaderListener?) :
    PLFileDownloaderBase(url!!, listener) {

    override fun downloadFile(): ByteArray {
        this.isRunning = true
        var result: ByteArray? = null
        var `is`: InputStream? = null
        val url = url
        val listener = this.listener
        val hasListener = listener != null
        val startTime = System.currentTimeMillis()
        try {
            if (this.isRunning) {
                if (hasListener) listener!!.didBeginDownload(url, startTime)
                if (url.startsWith("res://")) {
                    val sepPos = url.lastIndexOf("/")
                    val resourceId = context.resources.getIdentifier(url.substring(sepPos + 1), url.substring(6, sepPos), context.packageName)
                    `is` = context.resources.openRawResource(resourceId)
                } else if (url.startsWith("file://")) {
                    val file = File(url.substring(7))
                    if (file.canRead()) `is` = FileInputStream(file)
                }
            } else
                throw PLRequestInvalidatedException(url)
            if (this.isRunning) {
                result = ByteArray(`is`!!.available())
                `is`.read(result)
                if (hasListener) {
                    listener!!.didProgressDownload(url, 100)
                    listener.didEndDownload(url, result, System.currentTimeMillis() - startTime)
                }
            } else
                throw PLRequestInvalidatedException(url)
        } catch (e: Throwable) {
            if (this.isRunning) {
                Timber.e(e)
                if (hasListener) listener!!.didErrorDownload(url, e.toString(), -1, result)
            }
        } finally {
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    Timber.e(e)
                }
            }
        }
        this.isRunning = false
        return result!!
    }

}
