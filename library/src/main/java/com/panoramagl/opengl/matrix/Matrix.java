/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.panoramagl.opengl.matrix;

public class Matrix extends android.opengl.Matrix
{
	/**
     * Inverts a 4 x 4 matrix.
     *
     * @param mInv the array that holds the output inverted matrix
     * @param mInvOffset an offset into mInv where the inverted matrix is
     *        stored.
     * @param m the input array
     * @param mOffset an offset into m where the matrix is stored.
     * @return true if the matrix could be inverted, false if it could not.
     */
	public static boolean invertM(float[] mInv, int mInvOffset, float[] m, int mOffset)
	{
        // Invert a 4 x 4 matrix using Cramer's Rule

        // array of transpose source matrix
        float src0 = m[mOffset],
        src4 = m[mOffset + 1],
		src8 = m[mOffset + 2],
		src12 = m[mOffset + 3],
		src1 = m[mOffset + 4],
		src5 = m[mOffset + 5],
		src9 = m[mOffset + 6],
		src13 = m[mOffset + 7],
		src2 = m[mOffset + 8],
		src6 = m[mOffset + 9],
		src10 = m[mOffset + 10],
		src14 = m[mOffset + 11],
		src3 = m[mOffset + 12],
		src7 = m[mOffset + 13],
		src11 = m[mOffset + 14],
		src15 = m[mOffset + 15];
        
        // calculate pairs for first 8 elements (cofactors)
        float tmp0 = src10 * src15,
        tmp1 = src11 * src14,
        tmp2 = src9 * src15,
        tmp3 = src11 * src13,
        tmp4 = src9 * src14,
        tmp5 = src10 * src13,
        tmp6 = src8 * src15,
        tmp7 = src11 * src12,
        tmp8 = src8 * src14,
        tmp9 = src10 * src12,
        tmp10 = src8 * src13,
        tmp11 = src9 * src12;

        // calculate first 8 elements (cofactors)
        float dst0 = tmp0 * src5 + tmp3 * src6 + tmp4 * src7;
        dst0 -= tmp1 * src5 + tmp2 * src6 + tmp5 * src7;
        float dst1 = tmp1 * src4 + tmp6 * src6 + tmp9 * src7;
        dst1 -= tmp0 * src4 + tmp7 * src6 + tmp8 * src7;
        float dst2 = tmp2 * src4 + tmp7 * src5 + tmp10 * src7;
        dst2 -= tmp3 * src4 + tmp6 * src5 + tmp11 * src7;
        float dst3 = tmp5 * src4 + tmp8 * src5 + tmp11 * src6;
        dst3 -= tmp4 * src4 + tmp9 * src5 + tmp10 * src6;
        float dst4 = tmp1 * src1 + tmp2 * src2 + tmp5 * src3;
        dst4 -= tmp0 * src1 + tmp3 * src2 + tmp4 * src3;
        float dst5 = tmp0 * src0 + tmp7 * src2 + tmp8 * src3;
        dst5 -= tmp1 * src0 + tmp6 * src2 + tmp9 * src3;
        float dst6 = tmp3 * src0 + tmp6 * src1 + tmp11 * src3;
        dst6 -= tmp2 * src0 + tmp7 * src1 + tmp10 * src3;
        float dst7 = tmp4 * src0 + tmp9 * src1 + tmp10 * src2;
        dst7 -= tmp5 * src0 + tmp8 * src1 + tmp11 * src2;

        // calculate pairs for second 8 elements (cofactors)
        tmp0 = src2 * src7;
        tmp1 = src3 * src6;
        tmp2 = src1 * src7;
        tmp3 = src3 * src5;
        tmp4 = src1 * src6;
        tmp5 = src2 * src5;
        tmp6 = src0 * src7;
        tmp7 = src3 * src4;
        tmp8 = src0 * src6;
        tmp9 = src2 * src4;
        tmp10 = src0 * src5;
        tmp11 = src1 * src4;

        // calculate second 8 elements (cofactors)
        float dst8 = tmp0 * src13 + tmp3 * src14 + tmp4 * src15;
        dst8 -= tmp1 * src13 + tmp2 * src14 + tmp5 * src15;
        float dst9 = tmp1 * src12 + tmp6 * src14 + tmp9 * src15;
        dst9 -= tmp0 * src12 + tmp7 * src14 + tmp8 * src15;
        float dst10 = tmp2 * src12 + tmp7 * src13 + tmp10 * src15;
        dst10 -= tmp3 * src12 + tmp6 * src13 + tmp11 * src15;
        float dst11 = tmp5 * src12 + tmp8 * src13 + tmp11 * src14;
        dst11 -= tmp4 * src12 + tmp9 * src13 + tmp10 * src14;
        float dst12 = tmp2 * src10 + tmp5 * src11 + tmp1 * src9;
        dst12 -= tmp4 * src11 + tmp0 * src9 + tmp3 * src10;
        float dst13 = tmp8 * src11 + tmp0 * src8 + tmp7 * src10;
        dst13 -= tmp6 * src10 + tmp9 * src11 + tmp1 * src8;
        float dst14 = tmp6 * src9 + tmp11 * src11 + tmp3 * src8;
        dst14 -= tmp10 * src11 + tmp2 * src8 + tmp7 * src9;
        float dst15 = tmp10 * src10 + tmp4 * src8 + tmp9 * src9;
        dst15 -= tmp8 * src9 + tmp11 * src10 + tmp5 * src8;

        // calculate determinant
        float det = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

        if (det == 0.0f) {

        }

        // calculate matrix inverse
        det = 1.0f / det;
        
        mInv[mInvOffset + 0] = dst0 * det;
        mInv[mInvOffset + 1] = dst1 * det;
        mInv[mInvOffset + 2] = dst2 * det;
        mInv[mInvOffset + 3] = dst3 * det;
        mInv[mInvOffset + 4] = dst4 * det;
        mInv[mInvOffset + 5] = dst5 * det;
        mInv[mInvOffset + 6] = dst6 * det;
        mInv[mInvOffset + 7] = dst7 * det;
        mInv[mInvOffset + 8] = dst8 * det;
        mInv[mInvOffset + 9] = dst9 * det;
        mInv[mInvOffset + 10] = dst10 * det;
        mInv[mInvOffset + 11] = dst11 * det;
        mInv[mInvOffset + 12] = dst12 * det;
        mInv[mInvOffset + 13] = dst13 * det;
        mInv[mInvOffset + 14] = dst14 * det;
        mInv[mInvOffset + 15] = dst15 * det;

        return true;
    }
}