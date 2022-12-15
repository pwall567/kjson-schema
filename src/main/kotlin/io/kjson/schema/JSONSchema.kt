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

sealed class JSONSchema(val location: SchemaLocation) {

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

    private fun createOutput(
        valid: Boolean,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        error: String? = null,
        errors: List<Output>? = null,
        annotation: String? = null,
    ) = Output(
        valid = valid,
        keywordLocation = relativeLocation,
        absoluteKeywordLocation = location.toAbsoluteKeywordLocation(),
        instanceLocation = instance.pointer,
        error = error,
        errors = errors,
        annotation = annotation,
    )

    fun createValidOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
    ) = createOutput(true, instance, relativeLocation)

    fun createErrorOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        error: String? = null,
        errors: List<Output>? = null,
    ) = createOutput(false, instance, relativeLocation, error = error, errors = errors)

    fun createAnnotation(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        annotation: String? = null,
    ) = createOutput(true, instance, relativeLocation, annotation = annotation)

    fun createBasicErrorOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        error: String,
    ): BasicOutput {
        return BasicOutput(
            valid = false,
            errors = listOf(createBasicOutputUnit(instance, relativeLocation, error))
        )
    }

    fun basicResult(instance: JSONRef<*>, relativeLocation: JSONPointer, errors: List<BasicOutput.Entry>): BasicOutput {
        return if (errors.isEmpty())
            BasicOutput(valid = true)
        else {
            mutableListOf<BasicOutput.Entry>().let {
                it.add(createBasicOutputUnit(instance, relativeLocation, "A subschema had errors"))
                it.addAll(errors)
                BasicOutput(valid = false, errors = it)
            }
        }
    }

    fun createBasicOutputUnit(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        error: String,
    ): BasicOutput.Entry = BasicOutput.Entry(
        keywordLocation = relativeLocation,
        absoluteKeywordLocation = location.toAbsoluteKeywordLocation(),
        instanceLocation = instance.pointer,
        error = error,
    )

    class InstanceContext(val ref: JSONRef<*>) {
        var propertiesUsed: MutableSet<String>? = null
        var itemsUsed: BitSet? = null
    }

    class BooleanSchema(location: SchemaLocation, val value: Boolean) : JSONSchema(location) {

        override fun validate(instance: JSONRef<*>): Boolean = value

        override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput {
            TODO("Not yet implemented")
        }

        override fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

        override fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
            TODO("Not yet implemented")
        }

    }

    abstract class Element(location: SchemaLocation) : JSONSchema(location) {
        abstract val keyword: String
    }

    class ObjectSchema(
        location: SchemaLocation,
        val id: URI?,
        val elements: List<Element>,
    ) : JSONSchema(location) {

        override fun validate(instance: JSONRef<*>): Boolean = elements.all { it.validate(instance) }

        override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput {
            // TODO review this
            val errors = mutableListOf<BasicOutput.Entry>()
            for (element in elements) {
                val childLocation = relativeLocation.child(element.keyword)
                element.getBasicOutput(instance, childLocation).let { result ->
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
