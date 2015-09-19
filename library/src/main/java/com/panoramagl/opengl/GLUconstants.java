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

package com.panoramagl.opengl;

public class GLUconstants
{
	/**error code constants*/
	
	public static final int GLU_INVALID_ENUM =                 	100900;
	public static final int GLU_INVALID_VALUE =                 100901;
	public static final int GLU_OUT_OF_MEMORY =                 100902;
	public static final int GLU_INCOMPATIBLE_GL_VERSION =       100903;
	public static final int GLU_INVALID_OPERATION =             100904;
	
	/**quadric draw style constants*/
	
	public static final int GLU_POINT =                         100010;
	public static final int GLU_LINE =                          100011;
	public static final int GLU_FILL =                          100012;
	public static final int GLU_SILHOUETTE =                   	100013;
	
	/**quadric callback constants*/
	
	public static final int GLU_ERROR =                        	100103;
	
	/**quadric normal constants*/
	
	public static final int GLU_SMOOTH =                        100000;
	public static final int GLU_FLAT =                          100001;
	public static final int GLU_NONE =                          100002;
	
	/**quadric orientation constants*/
	
	public static final int GLU_OUTSIDE =                       100020;
	public static final int GLU_INSIDE  =                       100021;
}