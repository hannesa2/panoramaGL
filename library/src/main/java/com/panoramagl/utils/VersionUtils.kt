package com.panoramagl.utils

import android.os.Build

fun getAndroidVersion() = Build.VERSION.RELEASE.trim().split(".")[0].toInt()

fun isEmulator() = Build.PRODUCT.contains("sdk")
