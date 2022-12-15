/*
 * @(#) JSONSchema.kt
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
import java.util.BitSet

import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

sealed class JSONSchema(location: SchemaLocation) : JSONSchemaNode(location) {

    enum class Type(val value: String) {
        NULL("null"),
        BOOLEAN("boolean"),
        OBJECT("object"),
        ARRAY("array"),
        STRING("string"),
        NUMBER("number"),
        INTEGER("integer"),
    }

    abstract fun validate(instance: JSONRef<*>): Boolean

    abstract fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer = instance.pointer): BasicOutput

    abstract fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer = instance.pointer): Output

    abstract fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer = instance.pointer): Output

    // TODO in order to handle unevaluatedProperties etc., we need to pass a context object, not just the JSONRef
    // This object would be created in the outer validate, getBasicOutput etc. and a nested one would be created in
    // PropertiesElement etc.  It would have a Set of property names accessed and a BitSet of item numbers.
    // (only do this when unevaluatedProperties or unevaluatedItems present in schema?)
    // Check the difference between AdditionalProperties and UnevaluatedProperties.
    // The context object could be used to acquire annotations.
    fun validate(json: JSONValue?): Boolean = validate(JSONRef(json))

    fun getBasicOutput(json: JSONValue?): BasicOutput = getBasicOutput(JSONRef(json))

    fun getDetailedOutput(json: JSONValue?): Output = getDetailedOutput(JSONRef(json))

    fun getVerboseOutput(json: JSONValue?): Output = getVerboseOutput(JSONRef(json))

    class InstanceContext(val ref: JSONRef<*>) {
        var propertiesUsed: MutableSet<String>? = null
        var itemsUsed: BitSet? = null
    }

    class BooleanSchema(location: SchemaLocation, val value: Boolean) : JSONSchema(location) {

        override fun validate(instance: JSONRef<*>): Boolean = value

        override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput {
            return BasicOutput(value, if (value) null else emptyList())
        }

        override fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

        override fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

    }

    abstract class Element(location: SchemaLocation) : JSONSchemaNode(location) {

        abstract val keyword: String

        abstract fun validate(parent: ObjectSchema, instance: JSONRef<*>): Boolean

        abstract fun getBasicOutput(
            parent: ObjectSchema,
            instance: JSONRef<*>,
            relativeLocation: JSONPointer,
        ): BasicOutput

        abstract fun getDetailedOutput(
            parent: ObjectSchema,
            instance: JSONRef<*>,
            relativeLocation: JSONPointer,
        ): Output

        abstract fun getVerboseOutput(
            parent: ObjectSchema,
            instance: JSONRef<*>,
            relativeLocation: JSONPointer,
        ): Output

    }

    class ObjectSchema(
        location: SchemaLocation,
        val id: URI?,
        val elements: List<Element>,
    ) : JSONSchema(location) {

        override fun validate(instance: JSONRef<*>): Boolean = elements.all { it.validate(this, instance) }

        override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput {
            // TODO review this
            val errors = mutableListOf<BasicOutput.Entry>()
            for (element in elements) {
                val childLocation = relativeLocation.child(element.keyword)
                element.getBasicOutput(this, instance, childLocation).let { result ->
                    if (!result.valid)
                        result.errors?.let { errors.addAll(it) }
                }
            }
            return basicResult(instance, relativeLocation, errors)
        }

        override fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

        override fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

    }

}
