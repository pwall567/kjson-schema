/*
 * @(#) SchemaDialect.kt
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

package io.kjson.schema

import java.net.URI

import io.kjson.JSONObject
import io.kjson.schema.handlers.NullKeywordHandler

class SchemaDialect(
    val uri: URI,
    val vocabularySet: Collection<Vocabulary> = emptyList(),
    val handlers: List<Pair<String, KeywordHandler>> = emptyList(),
    val unrecognisedKeywordHandler: KeywordHandler = NullKeywordHandler,
) {

    fun findKeywordHandler(keyword: String): KeywordHandler {
        for (vocabulary in vocabularySet)
            vocabulary.findHandler(keyword)?.let { return it }
        for (handler in handlers)
            if (handler.first == keyword)
                return handler.second
        return unrecognisedKeywordHandler
    }

    companion object {

        val DRAFT_2020_12 = SchemaDialect(
            uri = URI("https://json-schema.org/draft/2020-12/schema"),
            vocabularySet = listOf(
                io.kjson.schema.vocabulary.draft202012.Core,
                io.kjson.schema.vocabulary.draft202012.Applicator,
                io.kjson.schema.vocabulary.draft202012.Unevaluated,
                io.kjson.schema.vocabulary.draft202012.Validation,
                io.kjson.schema.vocabulary.draft202012.MetaData,
                io.kjson.schema.vocabulary.draft202012.FormatAnnotation,
                io.kjson.schema.vocabulary.draft202012.Content,
            ),
            handlers = listOf(
                "definitions" to NullKeywordHandler,
                "dependencies" to NullKeywordHandler,
                "\$recursiveAnchor" to NullKeywordHandler,
                "\$recursiveRef" to NullKeywordHandler,
            )
        )

        val DRAFT_2019_09 = SchemaDialect(
            uri = URI("https://json-schema.org/draft/2019-09/schema"),
            vocabularySet = listOf(
                io.kjson.schema.vocabulary.draft201909.Core,
                io.kjson.schema.vocabulary.draft201909.Applicator,
                io.kjson.schema.vocabulary.draft201909.Validation,
                io.kjson.schema.vocabulary.draft201909.MetaData,
                io.kjson.schema.vocabulary.draft201909.FormatAnnotation,
                io.kjson.schema.vocabulary.draft201909.Content,
            ),
            handlers = listOf(
                "definitions" to NullKeywordHandler,
                "dependencies" to NullKeywordHandler,
            )
        )

        val DRAFT_07 = SchemaDialect(
            uri = URI("http://json-schema.org/draft-07/schema#"),
            handlers = listOf(
                "\$id" to NullKeywordHandler,
                "\$schema" to NullKeywordHandler,
                "\$ref" to NullKeywordHandler,
                "\$comment" to NullKeywordHandler,
                "title" to NullKeywordHandler,
                "description" to NullKeywordHandler,
                "default" to NullKeywordHandler,
                "readOnly" to NullKeywordHandler,
                "writeOnly" to NullKeywordHandler,
                "examples" to NullKeywordHandler,
                "multipleOf" to NullKeywordHandler,
                "maximum" to NullKeywordHandler,
                "exclusiveMaximum" to NullKeywordHandler,
                "minimum" to NullKeywordHandler,
                "minimum" to NullKeywordHandler,
                "exclusiveMinimum" to NullKeywordHandler,
                "maxLength" to NullKeywordHandler,
                "minLength" to NullKeywordHandler,
                "pattern" to NullKeywordHandler,
                "additionalItems" to NullKeywordHandler,
                "items" to NullKeywordHandler,
                "maxItems" to NullKeywordHandler,
                "minItems" to NullKeywordHandler,
                "uniqueItems" to NullKeywordHandler,
                "contains" to NullKeywordHandler,
                "maxProperties" to NullKeywordHandler,
                "minProperties" to NullKeywordHandler,
                "required" to NullKeywordHandler,
                "additionalProperties" to NullKeywordHandler,
                "definitions" to NullKeywordHandler,
                "properties" to NullKeywordHandler,
                "patternProperties" to NullKeywordHandler,
                "dependencies" to NullKeywordHandler,
                "propertyNames" to NullKeywordHandler,
                "const" to NullKeywordHandler,
                "enum" to NullKeywordHandler,
                "type" to NullKeywordHandler,
                "format" to NullKeywordHandler,
                "contentMediaType" to NullKeywordHandler,
                "contentEncoding" to NullKeywordHandler,
                "if" to NullKeywordHandler,
                "then" to NullKeywordHandler,
                "else" to NullKeywordHandler,
                "allOf" to NullKeywordHandler,
                "anyOf" to NullKeywordHandler,
                "oneOf" to NullKeywordHandler,
                "not" to NullKeywordHandler,
            )
        )
        val schemaDialects = listOf(
            DRAFT_2020_12,
            DRAFT_2019_09,
            DRAFT_07,
        )

        fun findDialect(json: JSONObject): SchemaDialect {
            return DRAFT_2020_12 // TODO complete this
        }

        fun latest(): SchemaDialect = schemaDialects[0]

    }

}
