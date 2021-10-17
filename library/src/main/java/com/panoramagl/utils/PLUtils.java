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

package com.panoramagl.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;

import com.panoramagl.enumerations.PLTextureColorFormat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class PLUtils {

    private static float sAndroidVersion = 0.0f;

    public static IntBuffer makeIntBuffer(int[] array) {
        final int integerSize = Integer.SIZE / 8;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * integerSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        IntBuffer intBuffer = byteBuffer.asIntBuffer();
        intBuffer.put(array);
        intBuffer.position(0);
        return intBuffer;
    }

    public static ByteBuffer makeByteBuffer(byte[] array) {
        final int SIZE = Byte.SIZE / 8;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * SIZE);
        byteBuffer.order(ByteOrder.nativeOrder());
        byteBuffer.put(array);
        byteBuffer.position(0);
        return byteBuffer;
    }

    public static FloatBuffer makeFloatBuffer(int length) {
        final int floatSize = Float.SIZE / 8;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(length * floatSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static FloatBuffer makeFloatBuffer(float[] array) {
        final int floatSize = Float.SIZE / 8;
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(array.length * floatSize);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(array);
        floatBuffer.position(0);
        return floatBuffer;
    }

    public static FloatBuffer makeFloatBuffer(float[][] array, int rows, int cols) {
        float[] result = new float[rows * cols];
        for (int i = 0, k = 0; i < rows; i++)
            for (int j = 0; j < cols; j++)
                result[k++] = array[i][j];
        return makeFloatBuffer(result);
    }

    /**
     * conversion methods
     */

    public static Config convertTextureColorFormatToBitmapConfig(PLTextureColorFormat colorFormat) {
        Config config = Config.ARGB_8888;
        switch (colorFormat) {
            case PLTextureColorFormatRGB565:
                config = Config.RGB_565;
                break;
            case PLTextureColorFormatRGBA4444:
                config = Config.ARGB_4444;
                break;
            default:
                break;
        }
        return config;
    }

    public static Bitmap convertBitmap(Bitmap bitmap, PLTextureColorFormat colorFormat) {
        return convertBitmap(bitmap, convertTextureColorFormatToBitmapConfig(colorFormat));
    }

    public static Bitmap convertBitmap(Bitmap bitmap, Config config) {
        if (bitmap != null && bitmap.getConfig() != config) {
            try {
                Bitmap newBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), config);
                Canvas canvas = new Canvas();
                canvas.setBitmap(newBitmap);
                Paint paint = new Paint();
                paint.setFilterBitmap(true);
                canvas.drawBitmap(bitmap, 0, 0, paint);
                return newBitmap;
            } catch (Throwable e) {
                PLLog.error("PLUtils::convertBitmap", e);
            }
        }
        return bitmap;
    }

    /**
     * bitmap methods
     */

    public static Bitmap getBitmap(byte[] data) {
        return getBitmap(data, Config.ARGB_8888);
    }

    public static Bitmap getBitmap(byte[] data, PLTextureColorFormat colorFormat) {
        return getBitmap(data, convertTextureColorFormatToBitmapConfig(colorFormat));
    }

    public static Bitmap getBitmap(byte[] data, Config config) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            return BitmapFactory.decodeByteArray(data, 0, data.length, options);
        } catch (Throwable e) {
            PLLog.error("PLUtils::getBitmap", e);
        }
        return null;
    }

    public static Bitmap getBitmap(Context context, String url) {
        return getBitmap(context, url, Config.ARGB_8888);
    }

    public static Bitmap getBitmap(Context context, String url, PLTextureColorFormat colorFormat) {
        return getBitmap(context, url, convertTextureColorFormatToBitmapConfig(colorFormat));
    }

    public static Bitmap getBitmap(Context context, String url, Config config) {
        try {
            url = url.trim();
            InputStream is = null;
            if (url.startsWith("res://")) {
                int sepPos = url.lastIndexOf("/");
                int resourceId = context.getResources().getIdentifier(url.substring(sepPos + 1), url.substring(6, sepPos), context.getPackageName());
                is = context.getResources().openRawResource(resourceId);
            } else if (url.startsWith("file://")) {
                File file = new File(url.substring(7));
                if (file.canRead())
                    is = new FileInputStream(file);
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            is.close();
            return bitmap;
        } catch (Throwable e) {
            PLLog.error("PLUtils::getBitmap", e);
        }
        return null;
    }

    public static Bitmap getBitmap(Context context, int resourceId) {
        return getBitmap(context, resourceId, Config.ARGB_8888);
    }

    public static Bitmap getBitmap(Context context, int resourceId, PLTextureColorFormat colorFormat) {
        return getBitmap(context, resourceId, convertTextureColorFormatToBitmapConfig(colorFormat));
    }

    public static Bitmap getBitmap(Context context, int resourceId, Config config) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = true;
            options.inPreferredConfig = config;
            InputStream is = context.getResources().openRawResource(resourceId);
            Bitmap bitmap = BitmapFactory.decodeStream(is, new Rect(), options);
            is.close();
            return bitmap;
        } catch (Throwable e) {
            PLLog.error("PLUtils::getBitmap", e);
        }
        return null;
    }

    /**
     * device methods
     */

    public static float getDisplayPPI() {
        DisplayMetrics displayMetrics = Resources.getSystem().getDisplayMetrics();
        return (displayMetrics.xdpi + displayMetrics.ydpi) / 2.0f;
    }

    public static float getAndroidVersion() {
        if (sAndroidVersion == 0.0f) {
            String androidVersion = Build.VERSION.RELEASE.trim();
            String[] arr = androidVersion.split("\\.");
            sAndroidVersion = Float.parseFloat(arr[0]);
            if (arr.length > 1) {
                sAndroidVersion = Float.parseFloat(arr[0] + "." + arr[1]);
            }
        }
        return sAndroidVersion;
    }

    public static boolean isEmulator() {
        String product = Build.PRODUCT;
        return (product != null && (product.equals("sdk") || product.contains("_sdk") || product.contains("sdk_")));
    }
}