/*
 * @(#) draft201909/Validation.kt
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

package io.kjson.schema.vocabulary.draft201909

import java.net.URI

import io.kjson.schema.KeywordHandler
import io.kjson.schema.Vocabulary
import io.kjson.schema.handlers.MaxItemsHandler
import io.kjson.schema.handlers.MaxPropertiesHandler
import io.kjson.schema.handlers.MaximumHandler
import io.kjson.schema.handlers.MinItemsHandler
import io.kjson.schema.handlers.MinPropertiesHandler
import io.kjson.schema.handlers.MinimumHandler
import io.kjson.schema.handlers.RequiredHandler
import io.kjson.schema.handlers.TypeHandler
import io.kjson.schema.handlers.UnimplementedKeywordHandler

object Validation : Vocabulary {

    override val id = URI("https://json-schema.org/draft/2019-09/vocab/validation")

    override fun findHandler(keyword: String): KeywordHandler? = when (keyword) {
        "multipleOf" -> UnimplementedKeywordHandler // TODO
        "maximum" -> MaximumHandler
        "exclusiveMaximum" -> UnimplementedKeywordHandler // TODO
        "minimum" -> MinimumHandler
        "exclusiveMinimum" -> UnimplementedKeywordHandler // TODO
        "maxLength" -> UnimplementedKeywordHandler // TODO
        "minLength" -> UnimplementedKeywordHandler // TODO
        "pattern" -> UnimplementedKeywordHandler // TODO
        "maxItems" -> MaxItemsHandler
        "minItems" -> MinItemsHandler
        "uniqueItems" -> UnimplementedKeywordHandler // TODO
        "maxContains" -> UnimplementedKeywordHandler // TODO
        "minContains" -> UnimplementedKeywordHandler // TODO
        "maxProperties" -> MaxPropertiesHandler
        "minProperties" -> MinPropertiesHandler
        "required" -> RequiredHandler
        "dependentRequired" -> UnimplementedKeywordHandler // TODO
        "const" -> UnimplementedKeywordHandler // TODO
        "enum" -> UnimplementedKeywordHandler // TODO
        "type" -> TypeHandler
        else -> null
    }

}
