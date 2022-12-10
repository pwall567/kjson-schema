/*
 * @(#) TypeValidator.kt
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

package io.kjson.schema.validator

import io.kjson.JSONArray
import io.kjson.JSONBoolean
import io.kjson.JSONNumber
import io.kjson.JSONObject
import io.kjson.JSONString
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation

class TypeValidator(location: SchemaLocation, val types: List<Type>) : JSONSchema.Element(location) {

    override val keyword: String = "type"

    override fun validate(instance: JSONRef<*>): Boolean {
        val node = instance.node
        for (type in types) {
            when (type) {
                Type.NULL -> if (node == null) return true
                Type.BOOLEAN -> if (node is JSONBoolean) return true
                Type.OBJECT -> if (node is JSONObject) return true
                Type.ARRAY -> if (node is JSONArray) return true
                Type.STRING -> if (node is JSONString) return true
                Type.NUMBER -> if (node is JSONNumber) return true
                Type.INTEGER -> if (node is JSONNumber && node.isIntegral()) return true
            }
        }
        return false
    }

}
