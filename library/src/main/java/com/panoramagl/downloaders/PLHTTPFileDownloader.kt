package com.panoramagl.downloaders

import okhttp3.OkHttpClient
import okhttp3.Request

class PLHTTPFileDownloader : PLFileDownloaderBase {

    constructor(url: String) : super(url)
    constructor(url: String, listener: PLFileDownloaderListener?) : super(url, listener)

    override fun downloadFile(): ByteArray = OkHttpClient().newCall(Request.Builder().url(url).build())
        .execute().body!!.bytes()

}
