/*
 * @(#) ItemsElement.kt
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
import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.pointer.forEach
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

class ItemsElement(location: SchemaLocation, val schema: JSONSchema) : JSONSchema.Element(location) {

    // TODO different behaviour for draft 2020-12 and 2019-09

    override val keyword: String = "items"

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean {
        if (!instance.isRef<JSONArray>())
            return true
        val refArray = instance.asRef<JSONArray>()
        refArray.forEach<JSONValue?> {
            if (!schema.validate(this))
                return false
        }
        return true
    }

    override fun getBasicOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): BasicOutput {
        if (!instance.isRef<JSONArray>())
            return BasicOutput(valid = true)
        val errors = mutableListOf<BasicOutput.Entry>()
        instance.asRef<JSONArray>().forEach<JSONValue?> {
            val result = schema.getBasicOutput(this, relativeLocation)
            if (!result.valid) {
                result.errors?.let { e -> errors.addAll(e) }
            }
        }
        if (errors.isEmpty())
            return BasicOutput(valid = true)
        return BasicOutput(valid = false,  errors = errors)
    }

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        if (!instance.isRef<JSONArray>())
            return createValidOutput(instance, relativeLocation)
        val errors = mutableListOf<Output>()
        instance.asRef<JSONArray>().forEach<JSONValue?> {
            val result = schema.getDetailedOutput(this, relativeLocation)
            if (!result.valid)
                errors.add(result)
        }
        return createDetailedOutput(instance, relativeLocation, errors)
    }

    override fun getVerboseOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        if (!instance.isRef<JSONArray>())
            return createValidOutput(instance, relativeLocation)
        TODO("Not yet implemented")
    }

}
