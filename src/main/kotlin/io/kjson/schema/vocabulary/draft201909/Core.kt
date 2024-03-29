/*
 * @(#) draft201909/Core.kt
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
import io.kjson.schema.handlers.AnchorHandler
import io.kjson.schema.handlers.CommentHandler
import io.kjson.schema.handlers.DefsHandler
import io.kjson.schema.handlers.NullKeywordHandler
import io.kjson.schema.handlers.RefHandler

object Core : Vocabulary {

    override val id = URI("https://json-schema.org/draft/2019-09/vocab/core")

    override fun findHandler(keyword: String): KeywordHandler? = when (keyword) {
        "\$id" -> NullKeywordHandler
        "\$schema" -> NullKeywordHandler
        "\$anchor" -> AnchorHandler
        "\$ref" -> RefHandler
        "\$recursiveRef" -> NullKeywordHandler
        "\$recursiveAnchor" -> NullKeywordHandler
        "\$vocabulary" -> NullKeywordHandler
        "\$comment" -> CommentHandler
        "\$defs" -> DefsHandler
        else -> null
    }

}
