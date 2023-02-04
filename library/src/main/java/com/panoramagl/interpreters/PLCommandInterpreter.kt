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
package com.panoramagl.interpreters

import android.os.Handler
import com.panoramagl.PLConstants
import com.panoramagl.PLIView
import com.panoramagl.PLObjectBase
import com.panoramagl.enumerations.PLTokenType
import com.panoramagl.loaders.PLJSONLoader
import com.panoramagl.transitions.PLTransitionBlend
import timber.log.Timber

@Suppress("MemberVisibilityCanBePrivate")
open class PLCommandInterpreter : PLObjectBase(), PLIInterpreter {
    protected var view: PLIView? = null
    override fun initializeValues() {
        view = null
    }

    override fun interpret(view: PLIView, text: String): Boolean {
        this.view = view
        try {
            val tokenizer = PLCommandTokenizer()
            tokenizer.tokenize(text)
            parseCommands(tokenizer.tokens, 0)
        } catch (e: Throwable) {
            Timber.e(e)
            return false
        } finally {
            this.view = null
        }
        return true
    }

    protected fun parseCommands(tokens: List<PLIToken>, tokenIndexGiven: Int) {
        var tokenIndex = tokenIndexGiven
        if (tokenIndex < tokens.size) {
            val token = tokens[tokenIndex++]
            when (token.type) {
                PLTokenType.PLTokenTypeFunction -> {
                    val fx = token.sequence
                    val tokenInfo: PLITokenInfo = PLTokenInfo(fx)
                    when (fx) {
                        "load" -> {
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                tokenInfo,
                                PLTokenType.PLTokenTypeString.ordinal,
                                PLTokenType.PLTokenTypeBoolean.ordinal or PLTokenType.PLTokenTypeOptional,
                                PLTokenType.PLTokenTypeFunction.ordinal or PLTokenType.PLTokenTypeOptional,
                                PLTokenType.PLTokenTypeNumber.ordinal or PLTokenType.PLTokenTypeOptional,
                                PLTokenType.PLTokenTypeNumber.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            Handler(view!!.context.mainLooper).post(PLCommandRunnable(view, tokenInfo))
                        }
                        "lookAt" -> {
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                tokenInfo,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeBoolean.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            val camera = view!!.camera
                            camera?.lookAt(tokenInfo.getFloat(0), tokenInfo.getFloat(1), tokenInfo.hasValue(2) && tokenInfo.getBoolean(2))
                        }
                        "lookAtAndZoom" -> {
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                tokenInfo,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeBoolean.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            val camera = view!!.camera
                            camera?.lookAtAndZoomFactor(
                                tokenInfo.getFloat(0),
                                tokenInfo.getFloat(1),
                                tokenInfo.getFloat(2),
                                tokenInfo.hasValue(3) && tokenInfo.getBoolean(3)
                            )
                        }
                        "zoom" -> {
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                tokenInfo,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeBoolean.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            val camera = view!!.camera
                            camera?.setZoomFactor(tokenInfo.getFloat(0), tokenInfo.hasValue(1) && tokenInfo.getBoolean(1))
                        }
                        "fov" -> {
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                tokenInfo,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeBoolean.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            val camera = view!!.camera
                            camera?.setFov(tokenInfo.getFloat(0), tokenInfo.hasValue(1) && tokenInfo.getBoolean(1))
                        }
                        else -> throw RuntimeException("parseCommands expected a valid function name")
                    }
                    parseCommands(tokens, tokenIndex)
                }
                PLTokenType.PLTokenTypeEOS -> parseCommands(
                    tokens,
                    tokenIndex
                )
                else -> throw RuntimeException("parseCommands expected a valid command")
            }
        }
    }

    protected fun parseFunction(tokens: List<PLIToken>, tokenIndexGiven: Int, tokenInfo: PLITokenInfo, vararg parameters: Int): Int {
        var tokenIndex = tokenIndexGiven
        var token = tokens[tokenIndex++]
        if (token.type == PLTokenType.PLTokenTypeOpenBracket) {
            var i = 0
            val parametersLength = parameters.size
            while (i < parametersLength) {
                token = tokens[tokenIndex++]
                var parameter = parameters[i]
                var tokenType = token.type.ordinal
                var parseParameterSeparator = false
                val isOptional = parameter and PLTokenType.PLTokenTypeOptional == PLTokenType.PLTokenTypeOptional
                if (isOptional) parameter = parameter and PLTokenType.PLTokenTypeOptional.inv()
                if (parameter == PLTokenType.PLTokenTypeFunction.ordinal) {
                    if (parameter == tokenType) {
                        val fx = token.sequence
                        if (fx == "BLEND") {
                            val fxTokenInfo: PLITokenInfo = PLTokenInfo(fx)
                            tokenIndex = parseFunction(
                                tokens,
                                tokenIndex,
                                fxTokenInfo,
                                PLTokenType.PLTokenTypeNumber.ordinal,
                                PLTokenType.PLTokenTypeNumber.ordinal or PLTokenType.PLTokenTypeOptional
                            )
                            tokenInfo.addValue(fxTokenInfo)
                            parseParameterSeparator = true
                        } else if (fx == "null") {
                            tokenInfo.addValue(PLTokenInfo(fx))
                            parseParameterSeparator = true
                        }
                    }
                } else if (parameter == PLTokenType.PLTokenTypeNumber.ordinal) {
                    var signNumber = ""
                    if (tokenType == PLTokenType.PLTokenTypePlusOrMinus.ordinal) {
                        signNumber = token.sequence
                        token = tokens[tokenIndex++]
                        tokenType = token.type.ordinal
                        if (parameter != tokenType) throw RuntimeException("parseFunction expected a number")
                    }
                    if (parameter == tokenType) {
                        tokenInfo.addValue(signNumber + token.sequence)
                        parseParameterSeparator = true
                    }
                } else if (parameter == tokenType) {
                    var sequence = token.sequence
                    if (tokenType == PLTokenType.PLTokenTypeString.ordinal) sequence = sequence.substring(1, sequence.length - 1)
                    tokenInfo.addValue(sequence)
                    parseParameterSeparator = true
                }
                if (parseParameterSeparator) {
                    if (i < parametersLength - 1) {
                        token = tokens[tokenIndex++]
                        if (token.type == PLTokenType.PLTokenTypeCloseBracket) {
                            tokenIndex--
                            break
                        } else if (token.type != PLTokenType.PLTokenTypeParameterSeparator) throw RuntimeException("parseFunction expected , character")
                    }
                } else if (isOptional) {
                    tokenIndex--
                    break
                } else throw RuntimeException("parseFunction expected a valid parameter")
                i++
            }
            token = tokens[tokenIndex++]
            if (token.type != PLTokenType.PLTokenTypeCloseBracket) throw RuntimeException("parseFunction expected ) character")
        } else throw RuntimeException("parseFunction expected ( character")
        return tokenIndex
    }

    @Throws(Throwable::class)
    protected fun finalize() {
        view = null
    }

    protected inner class PLCommandRunnable(private var mView: PLIView?, private var mTokenInfo: PLITokenInfo?) : Runnable {
        override fun run() {
            try {
                if (mTokenInfo!!.name == "load") {
                    var transitionTokenInfo = if (mTokenInfo!!.hasValue(2)) mTokenInfo!!.getTokenInfo(2) else null
                    if (transitionTokenInfo != null && transitionTokenInfo.name == "null") transitionTokenInfo = null
                    mView!!.load(
                        PLJSONLoader(mTokenInfo!!.getString(0)),
                        mTokenInfo!!.hasValue(1) && mTokenInfo!!.getBoolean(1),
                        if (transitionTokenInfo != null) PLTransitionBlend(
                            transitionTokenInfo.getFloat(0),
                            if (transitionTokenInfo.hasValue(1)) transitionTokenInfo.getFloat(1) else -1.0f
                        ) else null,
                        if (mTokenInfo!!.hasValue(3)) mTokenInfo!!.getFloat(3) else PLConstants.kFloatUndefinedValue,
                        if (mTokenInfo!!.hasValue(4)) mTokenInfo!!.getFloat(4) else PLConstants.kFloatUndefinedValue
                    )
                }
            } catch (e: Throwable) {
                Timber.e(e)
            }
        }

        @Throws(Throwable::class)
        protected fun finalize() {
            mView = null
            mTokenInfo = null
        }
    }
}
