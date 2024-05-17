package com.panoramagl.downloaders

interface PLFileDownloaderListener {
    fun didBeginDownload(url: String?, startTime: Long)

    fun didProgressDownload(url: String?, progress: Int)

    fun didStopDownload(url: String?)

    fun didEndDownload(url: String?, data: ByteArray?, elapsedTime: Long)

    fun didErrorDownload(url: String?, error: String?, responseCode: Int, data: ByteArray?)
}