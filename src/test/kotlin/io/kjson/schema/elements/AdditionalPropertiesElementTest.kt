/*
 * @(#) AdditionalPropertiesElementTest.kt
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

import java.net.URI
import java.net.URL

import io.kjson.JSONBoolean
import io.kjson.JSONObject
import io.kjson.pointer.JSONPointer
import io.kjson.schema.loader.SchemaLoader

class AdditionalPropertiesElementTest {

    @Test fun `should validate additional property of object`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        val instance1 = JSONObject.build {
            add("aaa", 24)
        }
        assertTrue(schema.validate(instance1))
        val instance2 = JSONObject.build {
            add("aaa", 26)
        }
        assertFalse(schema.validate(instance2))
    }

    @Test fun `should return basic output for additional property validation`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 24)
        }
        assertTrue(schema.validate(instance))
        val output = schema.getBasicOutput(instance)
        assertTrue(output.valid)
        assertNull(output.errors)
    }

    @Test fun `should return basic output for additional property validation error`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("properties", JSONObject.build {
                add("bbb", JSONObject.build {
                    add("maximum", 30)
                })
            })
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 26)
            add("bbb", 26)
        }
        assertFalse(schema.validate(instance))
        val output = schema.getBasicOutput(instance)
        assertFalse(output.valid)
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(4) { outputErrors.size }
        with(outputErrors[0]) {
            expect(JSONPointer.root) { keywordLocation }
            expect(URI("https://example.com/schema#")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[1]) {
            expect(JSONPointer("/additionalProperties")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("Additional property 'aaa' found but was invalid") { error }
        }
        with(outputErrors[2]) {
            expect(JSONPointer("/additionalProperties")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[3]) {
            expect(JSONPointer("/additionalProperties/maximum")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties/maximum")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("Number > maximum 25, was 26") { error }
        }
    }

    @Test fun `should return basic output for additionalProperties false`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("additionalProperties", JSONBoolean.FALSE)
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 26)
        }
        assertFalse(schema.validate(instance))
        val output = schema.getBasicOutput(instance)
        assertFalse(output.valid)
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(2) { outputErrors.size }
        with(outputErrors[0]) {
            expect(JSONPointer.root) { keywordLocation }
            expect(URI("https://example.com/schema#")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[1]) {
            expect(JSONPointer("/additionalProperties")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("Additional property 'aaa' found but was invalid") { error }
        }
    }

    @Test fun `should return detailed output for additional property validation`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 24)
        }
        assertTrue(schema.validate(instance))
        val output = schema.getDetailedOutput(instance)
        assertTrue(output.valid)
        assertNull(output.errors)
    }

    @Test fun `should return detailed output for additional property validation error`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("properties", JSONObject.build {
                add("bbb", JSONObject.build {
                    add("maximum", 30)
                })
            })
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 26)
            add("bbb", 26)
        }
        assertFalse(schema.validate(instance))
        val output = schema.getDetailedOutput(instance)
        assertFalse(output.valid)
        expect(JSONPointer("/additionalProperties")) { output.keywordLocation }
        expect(URI("https://example.com/schema#/additionalProperties")) { output.absoluteKeywordLocation }
        expect(JSONPointer.root) { output.instanceLocation }
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(2) { outputErrors.size }
        with(outputErrors[0]) {
            assertFalse(valid)
            expect(JSONPointer("/additionalProperties")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("Additional property 'aaa' found but was invalid") { error }
            assertNull(errors)
            assertNull(annotation)
            assertNull(annotations)
        }
        with(outputErrors[1]) {
            assertFalse(valid)
            expect(JSONPointer("/additionalProperties/maximum")) { keywordLocation }
            expect(URI("https://example.com/schema#/additionalProperties/maximum")) { absoluteKeywordLocation }
            expect(JSONPointer("/aaa")) { instanceLocation }
            expect("Number > maximum 25, was 26") { error }
            assertNull(errors)
            assertNull(annotation)
            assertNull(annotations)
        }
    }

    @Test fun `should return detailed output for additional property false`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("properties", JSONObject.build {
                add("bbb", JSONObject.build {
                    add("maximum", 30)
                })
            })
            add("additionalProperties", JSONBoolean.FALSE)
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 26)
            add("bbb", 26)
        }
        val output = schema.getDetailedOutput(instance)
        assertFalse(output.valid)
        expect(JSONPointer("/additionalProperties")) { output.keywordLocation }
        expect(URI("https://example.com/schema#/additionalProperties")) { output.absoluteKeywordLocation }
        expect(JSONPointer("/aaa")) { output.instanceLocation }
        expect("Additional property 'aaa' found but was invalid") { output.error }
        assertNull(output.errors)
    }

    @Test fun `should return verbose output for additional property validation`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("additionalProperties", JSONObject.build {
                add("maximum", 25)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val instance = JSONObject.build {
            add("aaa", 24)
        }
        assertTrue(schema.validate(instance))
        with(schema.getVerboseOutput(instance)) {
            println(this)
            assertTrue(valid)
            expect(JSONPointer.root) { keywordLocation }
            expect(URI("https://example.com/schema#")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            assertNull(error)
            assertNull(errors)
            assertNull(annotation)
            with(annotations) {
                assertNotNull(this)
                expect(1) { size }
                with(this[0]) {
                    assertTrue(valid)
                    expect(JSONPointer("/additionalProperties")) { keywordLocation }
                    expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
                    expect(JSONPointer.root) { instanceLocation }
                    assertNull(error)
                    assertNull(errors)
                    assertNull(annotation)
                    with(annotations) {
                        assertNotNull(this)
                        expect(1) { size }
                        with(this[0]) {
                            assertTrue(valid)
                            expect(JSONPointer("/additionalProperties")) { keywordLocation }
                            expect(URI("https://example.com/schema#/additionalProperties")) { absoluteKeywordLocation }
                            expect(JSONPointer("/aaa")) { instanceLocation }
                            assertNull(error)
                            assertNull(errors)
//                            expect("additionalProperties") { annotation } // TODO check
//                            assertNull(annotations) // TODO check
                        }
                    }
                }
            }
        }
    }

}
