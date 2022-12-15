/*
 * @(#) TypeElement.kt
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

package io.kjson.schema.elements

import io.kjson.JSONArray
import io.kjson.JSONBoolean
import io.kjson.JSONDecimal
import io.kjson.JSONInt
import io.kjson.JSONLong
import io.kjson.JSONNumber
import io.kjson.JSONObject
import io.kjson.JSONString
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

class TypeElement(location: SchemaLocation, val types: List<JSONSchema.Type>) : JSONSchema.Element(location) {

    override val keyword: String = "type"

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean {
        val node = instance.node
        for (type in types) {
            when (type) {
                JSONSchema.Type.NULL -> if (node == null) return true
                JSONSchema.Type.BOOLEAN -> if (node is JSONBoolean) return true
                JSONSchema.Type.OBJECT -> if (node is JSONObject) return true
                JSONSchema.Type.ARRAY -> if (node is JSONArray) return true
                JSONSchema.Type.STRING -> if (node is JSONString) return true
                JSONSchema.Type.NUMBER -> if (node is JSONNumber) return true
                JSONSchema.Type.INTEGER -> if (node is JSONNumber && node.isIntegral()) return true
            }
        }
        return false
    }

    override fun getBasicOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): BasicOutput =
        if (validate(parent, instance))
            BasicOutput(valid = true)
        else
            createBasicErrorOutput(instance, relativeLocation,
                "Incorrect type, expected ${types.joinToString(separator = " or ") { it.value }}" +
                        " but was ${instance.node.typeName}")

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        TODO("Not yet implemented")
    }

    override fun getVerboseOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        TODO("Not yet implemented")
    }

    companion object {

        val JSONValue?.typeName: String
            get() = when (this) {
                null -> "null"
                is JSONInt -> "integer"
                is JSONLong -> "integer"
                is JSONDecimal -> "number"
                is JSONString -> "string"
                is JSONBoolean -> "boolean"
                is JSONArray -> "array"
                is JSONObject -> "object"
            }

    }

}
