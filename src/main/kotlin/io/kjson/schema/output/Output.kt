/*
 * @(#) Output.kt
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

package io.kjson.schema.output

import java.net.URI

import io.kjson.pointer.JSONPointer

data class Output(
    val valid: Boolean,
    val keywordLocation: JSONPointer,
    val absoluteKeywordLocation: URI? = null,
    val instanceLocation: JSONPointer,
    val error: String? = null,
    val errors: List<Output>? = null,
    val annotation: String? = null,
    val annotations: List<Output>? = null
) {

    init {
        if (!valid) {
            require(error != null || errors != null)
            require(annotation == null && annotations == null) // https://json-schema.org/draft/2020-12/json-schema-core.html#section-7.7.1.2
        }
    }

}
