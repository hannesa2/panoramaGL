package com.panoramagl.downloaders

class PLRequestInvalidatedException(url: String) : RuntimeException(String.format("Request to %s was invalidated", url))