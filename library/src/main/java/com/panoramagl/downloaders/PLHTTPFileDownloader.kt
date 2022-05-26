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

import com.panoramagl.downloaders.ssl.EasySSLSocketFactory
import org.apache.commons.httpclient.HttpMethod
import org.apache.commons.httpclient.methods.GetMethod
import org.apache.commons.httpclient.params.HttpMethodParams
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler
import org.apache.commons.httpclient.HttpStatus
import com.panoramagl.utils.PLLog
import org.apache.commons.httpclient.HttpClient
import org.apache.commons.httpclient.protocol.Protocol
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

class PLHTTPFileDownloader : PLFileDownloaderBase {

    init {
        Protocol.registerProtocol("https", Protocol("https", EasySSLSocketFactory(), 443))
    }

    constructor(url: String) : super(url)
    constructor(url: String, listener: PLFileDownloaderListener?) : super(url, listener)

    override fun downloadFile(): ByteArray {
        this.isRunning = true
        var result: ByteArray? = null
        var `is`: InputStream? = null
        var bas: ByteArrayOutputStream? = null
        val url = url
        val listener = this.listener
        val hasListener = listener != null
        var responseCode = -1
        val startTime = System.currentTimeMillis()
        // HttpClient instance
        val client = HttpClient()
        // Method instance
        val method: HttpMethod = GetMethod(url)
        // Method parameters
        val methodParams = method.params
        methodParams.setParameter(HttpMethodParams.USER_AGENT, "PanoramaGL Android")
        methodParams.setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8")
        methodParams.setParameter(HttpMethodParams.RETRY_HANDLER, DefaultHttpMethodRetryHandler(maxAttempts, false))
        try {
            // Execute the method
            responseCode = client.executeMethod(method)
            if (responseCode != HttpStatus.SC_OK) throw IOException(method.statusText)
            // Get content length
            val header = method.getRequestHeader("Content-Length")
            val contentLength = header?.value?.toLong() ?: 1
            if (this.isRunning) {
                if (hasListener) listener!!.didBeginDownload(url, startTime)
            } else throw PLRequestInvalidatedException(url)
            // Get response body as stream
            `is` = method.responseBodyAsStream
            bas = ByteArrayOutputStream()
            val buffer = ByteArray(256)
            var length: Int
            var total = 0
            // Read stream
            while (`is`.read(buffer).also { length = it } != -1) {
                if (this.isRunning) {
                    bas.write(buffer, 0, length)
                    total += length
                    if (hasListener) listener!!.didProgressDownload(url, (total.toFloat() / contentLength.toFloat() * 100.0f).toInt())
                } else throw PLRequestInvalidatedException(url)
            }
            if (total == 0) throw IOException("Request data has invalid size (0)")
            // Get data
            if (this.isRunning) {
                result = bas.toByteArray()
                if (hasListener) listener!!.didEndDownload(url, result, System.currentTimeMillis() - startTime)
            } else throw PLRequestInvalidatedException(url)
        } catch (e: Throwable) {
            if (this.isRunning) {
                PLLog.error("PLHTTPFileDownloader::downloadFile", e)
                if (hasListener) listener!!.didErrorDownload(url, e.toString(), responseCode, result)
            }
        } finally {
            if (bas != null) {
                try {
                    bas.close()
                } catch (e: IOException) {
                    PLLog.error("PLHTTPFileDownloader::downloadFile", e)
                }
            }
            if (`is` != null) {
                try {
                    `is`.close()
                } catch (e: IOException) {
                    PLLog.error("PLHTTPFileDownloader::downloadFile", e)
                }
            }
            // Release the connection
            method.releaseConnection()
        }
        this.isRunning = false
        return result!!
    }
}
