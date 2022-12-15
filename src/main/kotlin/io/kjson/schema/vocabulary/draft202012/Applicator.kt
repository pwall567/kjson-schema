/*
 * @(#) draft202012/Applicator.kt
 *
 * kjson-schema  Kotlin implementation of JSON Schema
 * Copyright (c) 2022 Peter Wall
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.kjson.schema.vocabulary.draft202012

import java.net.URI

import io.kjson.schema.KeywordHandler
import io.kjson.schema.Vocabulary
import io.kjson.schema.handlers.AdditionalPropertiesHandler
import io.kjson.schema.handlers.NullKeywordHandler
import io.kjson.schema.handlers.PatternPropertiesHandler
import io.kjson.schema.handlers.PropertiesHandler

object Applicator : Vocabulary {

    override val id = URI("https://json-schema.org/draft/2020-12/vocab/applicator")

    override fun findHandler(keyword: String): KeywordHandler? = when (keyword) {
        "prefixItems" -> NullKeywordHandler // TODO pre-scan should examine schemata
        "items" -> NullKeywordHandler // TODO pre-scan should examine schema
        "contains" -> NullKeywordHandler // TODO pre-scan should examine schema???
        "additionalProperties" -> AdditionalPropertiesHandler
        "properties" -> PropertiesHandler
        "patternProperties" -> PatternPropertiesHandler
        "dependentSchemas" -> NullKeywordHandler // TODO pre-scan should examine schema???
        "propertyNames" -> NullKeywordHandler // TODO pre-scan should examine schema
        "if" -> NullKeywordHandler // TODO pre-scan should examine schema
        "then" -> NullKeywordHandler // TODO pre-scan should examine schema
        "else" -> NullKeywordHandler // TODO pre-scan should examine schema
        "allOf" -> NullKeywordHandler // TODO pre-scan should examine schemata
        "anyOf" -> NullKeywordHandler // TODO pre-scan should examine schemata
        "oneOf" -> NullKeywordHandler // TODO pre-scan should examine schemata
        "not" -> NullKeywordHandler // TODO pre-scan should examine schema
        else -> null
    }

}
