/*
 * @(#) SchemaLocationTest.kt
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

import kotlin.test.Test
import kotlin.test.expect

import io.kjson.pointer.JSONPointer
import io.kjson.schema.test.SampleData.sampleArrayPointer
import io.kjson.schema.test.SampleData.sampleObjectPointer
import io.kjson.schema.test.SampleData.testURI

class SchemaLocationTest {

    @Test fun `should create SchemaLocation with default pointer`() {
        val schemaLocation = SchemaLocation(testURI)
        expect(testURI) { schemaLocation.uri }
        expect(JSONPointer.root) { schemaLocation.pointer }
        expect("https://kjson.io/schema/test#") { schemaLocation.toString() }
    }

    @Test fun `should create SchemaLocation with explicit pointer`() {
        val schemaLocation = SchemaLocation(testURI, sampleObjectPointer)
        expect(testURI) { schemaLocation.uri }
        expect(sampleObjectPointer) { schemaLocation.pointer }
        expect("https://kjson.io/schema/test#/field1") { schemaLocation.toString() }
    }

    @Test fun `should create child SchemaLocation with name`() {
        val baseSchemaLocation = SchemaLocation(testURI)
        val schemaLocation = baseSchemaLocation.child("field1")
        expect(testURI) { schemaLocation.uri }
        expect(sampleObjectPointer) { schemaLocation.pointer }
        expect("https://kjson.io/schema/test#/field1") { schemaLocation.toString() }
    }

    @Test fun `should create child SchemaLocation with index`() {
        val baseSchemaLocation = SchemaLocation(testURI)
        val schemaLocation = baseSchemaLocation.child(0)
        expect(testURI) { schemaLocation.uri }
        expect(sampleArrayPointer) { schemaLocation.pointer }
        expect("https://kjson.io/schema/test#/0") { schemaLocation.toString() }
    }

}
