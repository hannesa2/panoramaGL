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

package com.panoramagl.interpreters;

import com.panoramagl.enumerations.PLTokenType;

public class PLCommandTokenizer extends PLTokenizer
{
	/**init methods*/
	
	@Override
	protected void initializeValues()
	{
		super.initializeValues();
		this.addToken(PLTokenType.PLTokenTypeFunction, "load|BLEND|lookAtAndZoom|lookAt|zoom|fov|null");
		this.addToken(PLTokenType.PLTokenTypeString, "'[^\"\'\n\r]*'");
		this.addToken(PLTokenType.PLTokenTypeOpenBracket, "\\(");
		this.addToken(PLTokenType.PLTokenTypeParameterSeparator, ",");
		this.addToken(PLTokenType.PLTokenTypeCloseBracket, "\\)");
		this.addToken(PLTokenType.PLTokenTypePlusOrMinus, "\\+|-");
		this.addToken(PLTokenType.PLTokenTypeMultOrDivide, "\\*|/");
		this.addToken(PLTokenType.PLTokenTypeBoolean, "true|false");
		this.addToken(PLTokenType.PLTokenTypeNumber, "[0-9]+(.[0-9]+)?");
		this.addToken(PLTokenType.PLTokenTypeConst, "[A-Z][A-Z0-9_]*");
		this.addToken(PLTokenType.PLTokenTypeVariable, "[a-zA-Z][a-zA-Z0-9_]*");
		this.addToken(PLTokenType.PLTokenTypeEOS, ";");
	}
}