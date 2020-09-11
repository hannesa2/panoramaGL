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

#include <jni.h>
/* Header for class com_panoramagl_opengl_GLUES */

#ifndef _Included_com_panoramagl_opengl_GLUES
#define _Included_com_panoramagl_opengl_GLUES
#ifdef __cplusplus
extern "C" {
#endif

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluCylinderAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;FFFIIIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluCylinderAndroid
  (JNIEnv *, jclass, jobject, jobject, jfloat, jfloat, jfloat, jint, jint, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluDiskAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;FFIIIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluDiskAndroid
  (JNIEnv *, jclass, jobject, jobject, jfloat, jfloat, jint, jint, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluPartialDiskAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;FFIIFFIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluPartialDiskAndroid
  (JNIEnv *, jclass, jobject, jobject, jfloat, jfloat, jint, jint, jfloat, jfloat, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluSphereAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;FIIIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluSphereAndroid
  (JNIEnv *, jclass, jobject, jobject, jfloat, jint, jint, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluHemisphereAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;ZFIIIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluHemisphereAndroid
  (JNIEnv *, jclass, jobject, jobject, jboolean, jfloat, jint, jint, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluHemisphereAndroid
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;Lcom/panoramagl/opengl/GLUquadric;FFZFIIIIIIZ)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_glu3DArcAndroid
  (JNIEnv *, jclass, jobject, jobject, jfloat, jfloat, jboolean, jfloat, jint, jint, jint, jint, jint, jint, jboolean);

/*
 * Class:     com_panoramagl_opengl_GLUES
 * Method:    gluPerspective
 * Signature: (Ljavax/microedition/khronos/opengles/GL10;FFFF)V
 */
JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluPerspective
  (JNIEnv *, jclass, jobject, jfloat, jfloat, jfloat, jfloat);

#ifdef __cplusplus
}
#endif
#endif
