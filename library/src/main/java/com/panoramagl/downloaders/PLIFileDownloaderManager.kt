package com.panoramagl.downloaders

interface PLIFileDownloaderManager {
    val isRunning: Boolean

    fun add(fileDownloader: PLIFileDownloader?)

    fun remove(fileDownloader: PLIFileDownloader?): Boolean

    fun removeAll(): Boolean

    fun download(fileDownloader: PLIFileDownloader?)

    fun start(): Boolean

    fun stop(): Boolean
}
