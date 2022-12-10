/*
 * @(#) DefsHandler.kt
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

package io.kjson.schema.handlers

import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.schema.JSONSchema
import io.kjson.schema.KeywordHandler
import io.kjson.schema.loader.SchemaLoader
import io.kjson.pointer.forEachKey

object DefsHandler : KeywordHandler {

    override fun process(loadContext: SchemaLoader.LoadContext): JSONSchema.Element? {
        TODO("Not yet implemented")
    }


    override fun preScan(preLoadContext: SchemaLoader.PreLoadContext) {
        preLoadContext.ref.asRef<JSONObject>().forEachKey<JSONValue> {
            preLoadContext.copy(ref = this).scan()
        }
    }

}
