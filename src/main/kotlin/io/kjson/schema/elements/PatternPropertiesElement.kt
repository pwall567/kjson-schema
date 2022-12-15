/*
 * @(#) PatternPropertiesElement.kt
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

import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.pointer.forEachKey
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

class PatternPropertiesElement(location: SchemaLocation, val properties: List<Pair<Regex, JSONSchema>>) :
        JSONSchema.Element(location) {

    override val keyword: String = "patternProperties"

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean {
        if (!instance.isRef<JSONObject>())
            return true
        val refObject = instance.asRef<JSONObject>()
        for ((propertyRegex, propertySchema) in properties) {
            refObject.forEachKey<JSONValue?> {
                if (propertyRegex.containsMatchIn(it) && !propertySchema.validate(this))
                    return false
            }
        }
        return true
    }

    override fun getBasicOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): BasicOutput {
        if (!instance.isRef<JSONObject>())
            return BasicOutput(valid = true)
        val refObject = instance.asRef<JSONObject>()
        val errors = mutableListOf<BasicOutput.Entry>()
        for ((propertyRegex, propertySchema) in properties) {
            refObject.forEachKey<JSONValue?> {
                if (propertyRegex.containsMatchIn(it)) {
                    val propertyResult = propertySchema.getBasicOutput(this, relativeLocation.child(it))
                    if (!propertyResult.valid)
                        propertyResult.errors?.let { e -> errors.addAll(e) }
                }
            }
        }
        return basicResult(instance, relativeLocation, errors)
    }

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        if (!instance.isRef<JSONObject>())
            return createValidOutput(instance, relativeLocation)
        TODO("Not yet implemented")
    }

    override fun getVerboseOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        if (!instance.isRef<JSONObject>())
            return createValidOutput(instance, relativeLocation)
        TODO("Not yet implemented")
    }

}
