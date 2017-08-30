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

import android.os.Handler;

import com.panoramagl.PLConstants;
import com.panoramagl.PLICamera;
import com.panoramagl.PLIView;
import com.panoramagl.PLObjectBase;
import com.panoramagl.enumerations.PLTokenType;
import com.panoramagl.loaders.PLJSONLoader;
import com.panoramagl.transitions.PLTransitionBlend;
import com.panoramagl.utils.PLLog;

import java.util.List;

public class PLCommandInterpreter extends PLObjectBase implements PLIInterpreter
{
	/**member variables*/
	
	private PLIView mView;
	
	/**init methods*/
	
	public PLCommandInterpreter()
	{
		super();
	}
	
	@Override
	protected void initializeValues()
	{
		mView = null;
	}
	
	/**property methods*/
	
	protected PLIView getView()
	{
		return mView;
	}
	
	protected void setView(PLIView view)
	{
		mView = view;
	}
	
	/**interpret methods*/
	
	@Override
	public boolean interpret(PLIView view, String text)
	{
		mView = view;
		try
		{
			PLCommandTokenizer tokenizer = new PLCommandTokenizer();
			tokenizer.tokenize(text);
			this.parseCommands(tokenizer.getTokens(), 0);
		}
		catch(Throwable e)
		{
			PLLog.error("PLCommandInterpreter::interpret", e);
			return false;
		}
		finally
		{
			mView = null;
		}
		return true;
	}
	
	/**parse methods*/
	
	protected void parseCommands(List<PLIToken> tokens, int tokenIndex)
	{
		if(tokenIndex < tokens.size())
		{
			PLIToken token = tokens.get(tokenIndex++);
			if(token.getType() == PLTokenType.PLTokenTypeFunction)
			{
				String fx = token.getSequence();
				PLITokenInfo tokenInfo = new PLTokenInfo(fx);
				if(fx.equals("load"))
				{
					tokenIndex = this.parseFunction(tokens, tokenIndex, tokenInfo, PLTokenType.PLTokenTypeString.ordinal(), PLTokenType.PLTokenTypeBoolean.ordinal() | PLTokenType.PLTokenTypeOptional, PLTokenType.PLTokenTypeFunction.ordinal() | PLTokenType.PLTokenTypeOptional, PLTokenType.PLTokenTypeNumber.ordinal() | PLTokenType.PLTokenTypeOptional, PLTokenType.PLTokenTypeNumber.ordinal() | PLTokenType.PLTokenTypeOptional);
					new Handler(mView.getContext().getMainLooper()).post(new PLCommandRunnable(mView, tokenInfo));
				}
				else if(fx.equals("lookAt"))
				{
					tokenIndex = this.parseFunction(tokens, tokenIndex, tokenInfo, PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeBoolean.ordinal() | PLTokenType.PLTokenTypeOptional);
					PLICamera camera = mView.getCamera();
					if(camera != null)
						camera.lookAt(tokenInfo.getFloat(0), tokenInfo.getFloat(1), tokenInfo.hasValue(2) ? tokenInfo.getBoolean(2) : false);
				}
				else if(fx.equals("lookAtAndZoom"))
				{
					tokenIndex = this.parseFunction(tokens, tokenIndex, tokenInfo, PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeBoolean.ordinal() | PLTokenType.PLTokenTypeOptional);
					PLICamera camera = mView.getCamera();
					if(camera != null)
						camera.lookAtAndZoomFactor(tokenInfo.getFloat(0), tokenInfo.getFloat(1), tokenInfo.getFloat(2), tokenInfo.hasValue(3) ? tokenInfo.getBoolean(3) : false);
				}
				else if(fx.equals("zoom"))
				{
					tokenIndex = this.parseFunction(tokens, tokenIndex, tokenInfo, PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeBoolean.ordinal() | PLTokenType.PLTokenTypeOptional);
					PLICamera camera = mView.getCamera();
					if(camera != null)
						camera.setZoomFactor(tokenInfo.getFloat(0), tokenInfo.hasValue(1) ? tokenInfo.getBoolean(1) : false);
				}
				else if(fx.equals("fov"))
				{
					tokenIndex = this.parseFunction(tokens, tokenIndex, tokenInfo, PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeBoolean.ordinal() | PLTokenType.PLTokenTypeOptional);
					PLICamera camera = mView.getCamera();
					if(camera != null)
						camera.setFov(tokenInfo.getFloat(0), tokenInfo.hasValue(1) ? tokenInfo.getBoolean(1) : false);
				}
				else
					throw new RuntimeException("parseCommands expected a valid function name");
				this.parseCommands(tokens, tokenIndex);
			}
			else if(token.getType() == PLTokenType.PLTokenTypeEOS)
				this.parseCommands(tokens, tokenIndex);
			else
				throw new RuntimeException("parseCommands expected a valid command");
		}
	}
	
	protected int parseFunction(List<PLIToken> tokens, int tokenIndex, PLITokenInfo tokenInfo, int ... parameters)
	{
		PLIToken token = tokens.get(tokenIndex++);
		if(token.getType() == PLTokenType.PLTokenTypeOpenBracket)
		{
			for(int i = 0, parametersLength = parameters.length; i < parametersLength; i++)
			{
				token = tokens.get(tokenIndex++);
				int parameter = parameters[i], tokenType = token.getType().ordinal();
				boolean parseParameterSeparator = false;
				boolean isOptional = ((parameter & PLTokenType.PLTokenTypeOptional) == PLTokenType.PLTokenTypeOptional);
				if(isOptional)
					parameter = (parameter & ~PLTokenType.PLTokenTypeOptional);
				if(parameter == PLTokenType.PLTokenTypeFunction.ordinal())
				{
					if(parameter == tokenType)
					{
						String fx = token.getSequence();
						if(fx.equals("BLEND"))
						{
							PLITokenInfo fxTokenInfo = new PLTokenInfo(fx);
							tokenIndex = this.parseFunction(tokens, tokenIndex, fxTokenInfo, PLTokenType.PLTokenTypeNumber.ordinal(), PLTokenType.PLTokenTypeNumber.ordinal() | PLTokenType.PLTokenTypeOptional);
							tokenInfo.addValue(fxTokenInfo);
							parseParameterSeparator = true;
						}
						else if(fx.equals("null"))
						{
							tokenInfo.addValue(new PLTokenInfo(fx));
							parseParameterSeparator = true;
						}
					}
				}
				else if(parameter == PLTokenType.PLTokenTypeNumber.ordinal())
				{
					String signNumber = "";
					if(tokenType == PLTokenType.PLTokenTypePlusOrMinus.ordinal())
					{
						signNumber = token.getSequence();
						token = tokens.get(tokenIndex++);
						tokenType = token.getType().ordinal();
						if(parameter != tokenType)
							throw new RuntimeException("parseFunction expected a number");
					}
					if(parameter == tokenType)
					{
						tokenInfo.addValue(signNumber + token.getSequence());
						parseParameterSeparator = true;
					}
				}
				else if(parameter == tokenType)
				{
					String sequence = token.getSequence();
					if(tokenType == PLTokenType.PLTokenTypeString.ordinal())
						sequence = sequence.substring(1, sequence.length() - 1);
					tokenInfo.addValue(sequence);
					parseParameterSeparator = true;
				}
				if(parseParameterSeparator)
				{
					if(i < parametersLength - 1)
					{
						token = tokens.get(tokenIndex++);
						if(token.getType() == PLTokenType.PLTokenTypeCloseBracket)
						{
							tokenIndex--;
							break;
						}
						else if(token.getType() != PLTokenType.PLTokenTypeParameterSeparator)
							throw new RuntimeException("parseFunction expected , character");
					}
				}
				else if(isOptional)
				{
					tokenIndex--;
					break;
				}
				else
					throw new RuntimeException("parseFunction expected a valid parameter");
			}
			token = tokens.get(tokenIndex++);
			if(token.getType() != PLTokenType.PLTokenTypeCloseBracket)
				throw new RuntimeException("parseFunction expected ) character");
		}
		else
			throw new RuntimeException("parseFunction expected ( character");
		return tokenIndex;
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mView = null;
		super.finalize();
	}
	
	/**internal classes declaration*/
	
	protected class PLCommandRunnable implements Runnable
	{
		/**member variables*/
		
		private PLIView mView;
		private PLITokenInfo mTokenInfo;
		
		/**init methods*/
		
		public PLCommandRunnable(PLIView view, PLITokenInfo tokenInfo)
		{
			super();
			mView = view;
			mTokenInfo = tokenInfo;
		}
		
		/**Runnable methods*/

		@Override
		public void run()
		{
			try
			{
				if(mTokenInfo.getName().equals("load"))
				{
					PLITokenInfo transitionTokenInfo = (mTokenInfo.hasValue(2) ? mTokenInfo.getTokenInfo(2) : null);
					if(transitionTokenInfo != null && transitionTokenInfo.getName().equals("null"))
						transitionTokenInfo = null;
					mView.load(new PLJSONLoader(mTokenInfo.getString(0)), mTokenInfo.hasValue(1) ? mTokenInfo.getBoolean(1) : false, transitionTokenInfo != null ? new PLTransitionBlend(transitionTokenInfo.getFloat(0), transitionTokenInfo.hasValue(1) ? transitionTokenInfo.getFloat(1) : -1.0f) : null, mTokenInfo.hasValue(3) ? mTokenInfo.getFloat(3) : PLConstants.kFloatUndefinedValue, mTokenInfo.hasValue(4) ? mTokenInfo.getFloat(4) : PLConstants.kFloatUndefinedValue);
				}
			}
			catch(Throwable e)
			{
				PLLog.error("PLCommandRunnable::run", e);
			}
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			mView = null;
			mTokenInfo = null;
			super.finalize();
		}
	}
}