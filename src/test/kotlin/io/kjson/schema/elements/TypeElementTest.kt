/*
 * @(#) TypeElementTest.kt
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

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.expect

import io.kjson.JSONInt
import io.kjson.JSONObject
import io.kjson.JSONString
import io.kjson.pointer.JSONPointer
import io.kjson.schema.loader.SchemaLoader

class TypeElementTest {

    @Test fun `should validate node is integer`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("type", "integer")
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertTrue(schema.validate(JSONInt(27)))
        assertFalse(schema.validate(JSONString("X")))
    }

    @Test fun `should return basic output when node is incorrect type`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("type", "integer")
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        val output = schema.getBasicOutput(JSONString("X"))
        assertFalse(output.valid)
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(2) { outputErrors.size }
        with(outputErrors[0]) {
            expect(JSONPointer.root) { keywordLocation }
            assertNull(absoluteKeywordLocation)
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[1]) {
            expect(JSONPointer("/type")) { keywordLocation }
            assertNull(absoluteKeywordLocation)
            expect(JSONPointer.root) { instanceLocation }
            expect("Incorrect type, expected integer but was string") { error }
        }
    }

}
