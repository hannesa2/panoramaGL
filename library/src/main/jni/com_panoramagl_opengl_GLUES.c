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

#include "com_panoramagl_opengl_GLUES.h"

#include <android/log.h>

#include "glues_quad.c"
#include "glues_error.c"
#include "glues_mipmap.c"
#include "glues_project.c"

#define  LOG_TAG    "libglues"
#define  LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define  LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)

/********************** 
 *   Quadric methods  *
 **********************/

typedef void (APIENTRY * _GLUESfuncptr)(GLint);

GLUquadric * gluesNewQuadric(GLint normals, GLint textureCoords, GLint orientation, GLint drawStyle, jboolean hasCallback, _GLUESfuncptr fn)
{
	GLUquadric *result = (GLUquadric *)malloc(sizeof(GLUquadric));
	if (result)
	{
		result->normals = normals;
		result->textureCoords = textureCoords;
		result->orientation = orientation;
		result->drawStyle = drawStyle;
		result->errorCallback = (hasCallback ? fn : NULL);
	}
	return result;
}

GLUquadric gluesQuadric(GLint normals, GLint textureCoords, GLint orientation, GLint drawStyle, jboolean hasCallback, _GLUESfuncptr fn)
{
	return (GLUquadric){ normals, textureCoords, orientation, drawStyle, (hasCallback ? fn : NULL) };
}

/**********************
 *   GLUES methods    *
 **********************/

#define GLUES_ERROR_CODE -1
GLint gluesErrorCode = GLUES_ERROR_CODE;

void APIENTRY gluesErrorCallback(GLint errorCode)
{
	gluesErrorCode = errorCode;
}

void gluesCallErrorCallback(JNIEnv *env, jclass sclass, jobject qobj)
{
	if (gluesErrorCode != GLUES_ERROR_CODE)
	{
		jmethodID methodID = (*env)->GetStaticMethodID(env, sclass, "gluQuadricError", "(Lcom/panoramagl/opengl/GLUquadric;I)V");
		if (methodID)
		{
			(*env)->CallStaticVoidMethod(env, sclass, methodID, (jvalue*)qobj, (jvalue*)(jint)gluesErrorCode);
		}
		gluesErrorCode = GLUES_ERROR_CODE;
	}
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluCylinderAndroid
(JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jfloat baseRadius, jfloat topRadius, jfloat height, jint slices, jint stacks, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	gluCylinder(&quad, (GLfloat)baseRadius, (GLfloat)topRadius, (GLfloat)height, (GLint)slices, (GLint)stacks);
	gluesCallErrorCallback(env, sclass, qobj);
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluDiskAndroid
(JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jfloat innerRadius, jfloat outerRadius, jint slices, jint loops, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	gluDisk(&quad, innerRadius, outerRadius, slices, loops);
	gluesCallErrorCallback(env, sclass, qobj);
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluPartialDiskAndroid
(JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jfloat innerRadius, jfloat outerRadius, jint slices, jint loops, jfloat startAngle, jfloat sweepAngle, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	gluPartialDisk(&quad, innerRadius, outerRadius, slices, loops, startAngle, sweepAngle);
	gluesCallErrorCallback(env, sclass, qobj);
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluSphereAndroid
(JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jfloat radius, jint slices, jint stacks, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	gluSphere(&quad, radius, slices, stacks);
	gluesCallErrorCallback(env, sclass, qobj);
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluHemisphereAndroid
  (JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jboolean positive, jfloat radius, jint slices, jint stacks, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	gluHemisphere(&quad, positive, radius, slices, stacks);
	gluesCallErrorCallback(env, sclass, qobj);
}

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_glu3DArcAndroid
(JNIEnv *env, jclass sclass, jobject gl, jobject qobj, jfloat angleWidth, jfloat offsetAngle, jboolean positive, jfloat radius, jint slices, jint stacks, jint qnormals, jint qtextureCoords, jint qorientation, jint qdrawStyle, jboolean qhasCallback)
{
	GLUquadric quad = gluesQuadric(qnormals, qtextureCoords, qorientation, qdrawStyle, qhasCallback, gluesErrorCallback);
	glu3DArc(&quad, angleWidth, offsetAngle, positive, radius, slices, stacks);
	gluesCallErrorCallback(env, sclass, qobj);
}

/********************** 
 *  project methods   *
 **********************/

JNIEXPORT void JNICALL Java_com_panoramagl_opengl_GLUES_gluPerspective
(JNIEnv *env, jclass sclass, jobject gl, jfloat fovy, jfloat aspect, jfloat zNear, jfloat zFar)
{
	gluPerspective(fovy, aspect, zNear, zFar);
}
