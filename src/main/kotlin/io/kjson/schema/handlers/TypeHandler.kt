/*
 * @(#) TypeHandler.kt
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

import io.kjson.JSONArray
import io.kjson.JSONString
import io.kjson.JSONValue
import io.kjson.pointer.JSONRef
import io.kjson.pointer.child
import io.kjson.schema.JSONSchema
import io.kjson.schema.JSONSchemaException
import io.kjson.schema.KeywordHandler
import io.kjson.schema.SchemaLocation
import io.kjson.schema.validator.TypeValidator

object TypeHandler : KeywordHandler {

    override fun process(schemaLocation: SchemaLocation, ref: JSONRef<JSONValue>): JSONSchema.Element {
        val types: List<JSONSchema.Type> = when {
            ref.isRef<JSONString>() -> listOf(checkType(ref))
            ref.isRef<JSONArray>() -> {
                val arrayRef = ref.asRef<JSONArray>()
                List(arrayRef.node.size) { i -> checkType(arrayRef.child<JSONValue>(i)) }
            }
            else -> throw JSONSchemaException("Invalid type - $ref")
        }
        return TypeValidator(schemaLocation, types)
    }

    private fun checkType(item: JSONRef<*>): JSONSchema.Type {
        if (item.isRef<JSONString>()) {
            val value = item.asRef<JSONString>().node.value
            for (type in JSONSchema.Type.values()) {
                if (value == type.value)
                    return type
            }
        }
        throw JSONSchemaException("Invalid type $item")
    }

}
