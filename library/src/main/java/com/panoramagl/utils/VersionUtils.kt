package com.panoramagl.utils

import android.os.Build

fun isEmulator() = Build.PRODUCT.contains("sdk")
