/*
 * @(#) JSONSchemaTest.kt
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
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.expect

import io.kjson.pointer.JSONRef
import io.kjson.schema.test.SampleData
import io.kjson.schema.test.SampleData.testURI

class JSONSchemaTest {

    @Test fun `should create true boolean schema`() {
        val schemaLocation = SchemaLocation(testURI)
        val schema = JSONSchema.BooleanSchema(schemaLocation, true)
        expect(schemaLocation) { schema.location }
        assertTrue(schema.value)
        val dummyRef = JSONRef(SampleData.sampleObject)
        assertTrue(schema.validate(dummyRef))
    }

    @Test fun `should create false boolean schema`() {
        val schemaLocation = SchemaLocation(testURI)
        val schema = JSONSchema.BooleanSchema(schemaLocation, false)
        assertIs<JSONSchema.BooleanSchema>(schema)
        expect(schemaLocation) { schema.location }
        assertFalse(schema.value)
        val dummyRef = JSONRef(SampleData.sampleObject)
        assertFalse(schema.validate(dummyRef))
    }

}
