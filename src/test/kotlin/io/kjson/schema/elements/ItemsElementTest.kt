/*
 * @(#) ItemsElementTest.kt
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

import io.kjson.JSONArray
import io.kjson.JSONObject
import io.kjson.pointer.JSONPointer
import io.kjson.schema.loader.SchemaLoader

class ItemsElementTest {

    @Test fun `should validate array items`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("items", JSONObject.build {
                add("maximum", 42)
            })
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        JSONArray.build {
            add(24)
            add(30)
        }.let { assertTrue(schema.validate(it)) }
        JSONArray.build {
            add(24)
            add(50)
        }.let { assertFalse(schema.validate(it)) }
    }

    @Test fun `should return Basic output for array validation`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("items", JSONObject.build {
                add("maximum", 42)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val goodArray = JSONArray.build {
            add(24)
            add(30)
        }
        schema.getBasicOutput(goodArray).let {
            assertTrue(it.valid)
            assertNull(it.errors)
        }
        val badArray = JSONArray.build {
            add(24)
            add(50)
        }
        schema.getBasicOutput(badArray).let {
            assertFalse(it.valid)
            val outputErrors = it.errors
            assertNotNull(outputErrors)
            expect(3) { outputErrors.size }
            with(outputErrors[0]) {
                expect(JSONPointer.root) { keywordLocation }
                expect(URI("https://example.com/schema#")) { absoluteKeywordLocation }
                expect(JSONPointer.root) { instanceLocation }
                expect("A subschema had errors") { error }
            }
            with(outputErrors[1]) {
                expect(JSONPointer("/items")) { keywordLocation }
                expect(URI("https://example.com/schema#/items")) { absoluteKeywordLocation }
                expect(JSONPointer("/1")) { instanceLocation }
                expect("A subschema had errors") { error }
            }
            with(outputErrors[2]) {
                expect(JSONPointer("/items/maximum")) { keywordLocation }
                expect(URI("https://example.com/schema#/items/maximum")) { absoluteKeywordLocation }
                expect(JSONPointer("/1")) { instanceLocation }
                expect("Number > maximum 42, was 50") { error }
            }
        }
    }

    @Test fun `should return detailed output for array validation`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("items", JSONObject.build {
                add("maximum", 42)
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        val goodArray = JSONArray.build {
            add(24)
            add(30)
        }
        schema.getDetailedOutput(goodArray).let {
            assertTrue(it.valid)
            assertNull(it.error)
            assertNull(it.errors)
        }
        val badArray = JSONArray.build {
            add(24)
            add(50)
        }
        schema.getDetailedOutput(badArray).let {
            // this is an example of "Nodes that have a single child are replaced by the child."
            // https://json-schema.org/draft/2020-12/json-schema-core.html#section-12.4.3
            assertFalse(it.valid)
            expect(JSONPointer("/items/maximum")) { it.keywordLocation }
            expect(URI("https://example.com/schema#/items/maximum")) { it.absoluteKeywordLocation }
            expect(JSONPointer("/1")) { it.instanceLocation }
            expect("Number > maximum 42, was 50") { it.error }
        }
    }

}
