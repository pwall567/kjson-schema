/*
 * @(#) JSONSchemaNode.kt
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

import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

abstract class JSONSchemaNode(val location: SchemaLocation) {

    fun createOutput(
        valid: Boolean,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        error: String? = null,
        errors: List<Output>? = null,
        annotation: String? = null,
        annotations: List<Output>? = null,
    ) = Output(
        valid = valid,
        keywordLocation = relativeLocation,
        absoluteKeywordLocation = location.toAbsoluteKeywordLocation(),
        instanceLocation = instance.pointer,
        error = error,
        errors = errors,
        annotation = annotation,
        annotations = annotations,
    )

    fun createValidOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        annotation: String? = null,
        annotations: List<Output>? = null,
    ) = createOutput(
        valid = true,
        instance = instance,
        relativeLocation = relativeLocation,
        annotation = annotation,
        annotations = annotations,
    )

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

    fun createDetailedOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        errors: List<Output>,
    ): Output = when (errors.size) {
        0 -> createValidOutput(instance, relativeLocation)
        1 -> errors[0]
        else -> createOutput(
            valid = false,
            instance = instance,
            relativeLocation = relativeLocation,
            errors = errors,
        )
    }

    fun createVerboseOutput(
        instance: JSONRef<*>,
        relativeLocation: JSONPointer,
        results: List<Output>,
        keyword: String? = null,
    ): Output {
        println("createVerboseOutput(instance=$instance,relativeLocation=$relativeLocation,results=$results,keyword=$keyword)")
        return if (results.any { !it.valid })
            createOutput(
                valid = false,
                instance = instance,
                relativeLocation = relativeLocation,
                errors = results,
            )
        else
            createOutput(
                valid = true,
                instance = instance,
                relativeLocation = relativeLocation,
                annotations = results,
                annotation = if (results.isEmpty()) keyword else null,
            )
    }

}
