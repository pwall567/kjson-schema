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

import io.kjson.pointer.JSONRef

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

    class TrueSchema(location: SchemaLocation) : JSONSchema(location) {
        override fun validate(instance: JSONRef<*>): Boolean = true
    }

    class FalseSchema(location: SchemaLocation) : JSONSchema(location) {
        override fun validate(instance: JSONRef<*>): Boolean = false
    }

    abstract class Element(location: SchemaLocation) : JSONSchema(location) {
        abstract val keyword: String
    }

    class ObjectSchema(
        location: SchemaLocation,
        val id: URI,
        private val elements: List<Element>,
    ) : JSONSchema(location) {

        override fun validate(instance: JSONRef<*>): Boolean = elements.all { it.validate(instance) }

    }

    companion object {

        fun booleanSchema(booleanValue: Boolean, location: SchemaLocation) =
                if (booleanValue) TrueSchema(location) else FalseSchema(location)

    }

}
