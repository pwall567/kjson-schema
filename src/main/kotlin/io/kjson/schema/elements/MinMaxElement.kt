/*
 * @(#) MinMaxItemsElement.kt
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

import io.kjson.JSON.asArray
import io.kjson.JSONStructure
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

abstract class MinMaxElement(location: SchemaLocation, val limit: Int) : JSONSchema.Element(location) {

    abstract val relationship: String

    abstract fun expected(n: Int): String

    abstract fun correctType(node: JSONValue?): Boolean

    abstract fun withinLimit(size: Int): Boolean

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean {
        if (!correctType(instance.node))
            return true
        return withinLimit(instance.asRef<JSONStructure<*>>().node.size)
    }

    override fun getBasicOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): BasicOutput = if (validate(parent, instance))
        BasicOutput(valid = true)
    else
        createBasicErrorOutput(instance, relativeLocation,
                "Expected $relationship ${expected(limit)} but found ${instance.node.asArray.size}")

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): Output {
        TODO("Not yet implemented")
    }

    override fun getVerboseOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): Output {
        TODO("Not yet implemented")
    }

}