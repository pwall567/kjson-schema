/*
 * @(#) AdditionalPropertiesElement.kt
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

class AdditionalPropertiesElement(location: SchemaLocation, val schema: JSONSchema) :
        JSONSchema.Element(location) {

    override val keyword: String = "additionalProperties"

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean {
        if (!instance.isRef<JSONObject>())
            return true
        val refObject = instance.asRef<JSONObject>()
        refObject.forEachKey<JSONValue?> {
            if (!inProperties(parent, it) && !inPatternProperties(parent, it) && !schema.validate(this))
                return false
        }
        return true
    }

    private fun inProperties(parent: JSONSchema.ObjectSchema, name: String): Boolean {
        return parent.elements.findIsInstance<PropertiesElement>()?.properties?.any {
            it.first == name
        } ?: false
    }

    private fun inPatternProperties(parent: JSONSchema.ObjectSchema, name: String): Boolean {
        return parent.elements.findIsInstance<PatternPropertiesElement>()?.properties?.any {
            it.first.containsMatchIn(name)
        } ?: false
    }

    private inline fun <reified R> Iterable<*>.findIsInstance(): R? {
        for (item in this)
            if (item is R)
                return item
        return null
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
        refObject.forEachKey<JSONValue?> {
            if (!inProperties(parent, it) && !inPatternProperties(parent, it)) {
                val result = schema.getBasicOutput(this, relativeLocation)
                if (!result.valid) {
                    errors.add(createBasicOutputUnit(this, relativeLocation,
                        "Additional property '$it' found but was invalid"))
                    result.errors?.let { e -> errors.addAll(e) }
                }
            }
        }
        if (errors.isEmpty())
            return BasicOutput(true)
        return BasicOutput(false, errors)
    }

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ): Output {
        if (!instance.isRef<JSONObject>())
            return createValidOutput(instance, relativeLocation)
        val refObject = instance.asRef<JSONObject>()
        val errors = mutableListOf<Output>()
        refObject.forEachKey<JSONValue?> {
            if (!inProperties(parent, it) && !inPatternProperties(parent, it)) {
                val result = schema.getDetailedOutput(this, relativeLocation)
                if (!result.valid) {
                    errors.add(createErrorOutput(this, relativeLocation,
                            "Additional property '$it' found but was invalid"))
                    result.errors?.let { e -> errors.addAll(e) } // ?????
                }
            }
        }
        return createDetailedOutput(instance, relativeLocation, errors)
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
