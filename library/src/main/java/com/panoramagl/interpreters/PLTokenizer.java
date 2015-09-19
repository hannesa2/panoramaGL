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

/*
 * This class is based in the article "Writing a Parser in Java: The Tokenizer" by Cogito Learning.
 * See:
 * http://cogitolearning.co.uk/?p=523
 * http://cogitolearning.co.uk/wp-content/uploads/2013/04/CogitoLearningTokenizer.zip
 */

package com.panoramagl.interpreters;

import com.panoramagl.PLObjectBase;
import com.panoramagl.enumerations.PLTokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PLTokenizer extends PLObjectBase implements PLITokenizer
{
	/**member variables*/
	
	private List<PLIToken> mTokens;
	private List<PLTokenData> mTokensData;
	
	/**init methods*/
	
	public PLTokenizer()
	{
		super();
	}
	
	@Override
	protected void initializeValues()
	{
		mTokens = new ArrayList<PLIToken>();
		mTokensData = new ArrayList<PLTokenData>();
	}
	
	/**property methods*/
	
	@Override
	public List<PLIToken> getTokens()
	{
		return mTokens;
	}
	
	protected List<PLTokenData> getTokensData()
	{
		return mTokensData;
	}
	
	/**token methods*/
	
	@Override
	public void addToken(PLTokenType tokenType, String regex)
	{
		mTokensData.add(PLTokenData.PLTokenDataMake(tokenType, Pattern.compile("^(" + regex + ")")));
	}
	
	@Override
	public void tokenize(String input)
	{
		String inputText = input.trim();
		mTokens.clear();
		while(!inputText.equals(""))
		{
			boolean match = false;
			for(int i = 0, tokensDataLength = mTokensData.size(); i < tokensDataLength; i++)
			{
				PLTokenData tokenData = mTokensData.get(i);
				Matcher matcher = tokenData.regex.matcher(inputText);
				if(matcher.find())
				{
					match = true;
					String sequence = matcher.group().trim();
					inputText = matcher.replaceFirst("").trim();
					mTokens.add(new PLToken(tokenData.type, sequence));
					break;
				}
			}
			if(!match)
				throw new RuntimeException("Unexpected character in input: " + inputText);
		}
	}
	
	/**dealloc methods*/
	
	@Override
	protected void finalize() throws Throwable
	{
		mTokens.clear();
		mTokens = null;
		mTokensData.clear();
		mTokensData = null;
		super.finalize();
	}
	
	/**internal classes declaration*/
	
	protected static class PLTokenData
	{
		/**member variables*/
		
		public PLTokenType type;
		public Pattern regex;
		
		/**init methods*/
		
		public PLTokenData(PLTokenType typeValue, Pattern regexValue)
		{
			super();
			type = typeValue;
			regex = regexValue;
		}
		
		public static PLTokenData PLTokenDataMake(PLTokenType type, Pattern regex)
		{
			return new PLTokenData(type, regex);
		}
		
		/**dealloc methods*/
		
		@Override
		protected void finalize() throws Throwable
		{
			type = null;
			regex = null;
			super.finalize();
		}
	}
}