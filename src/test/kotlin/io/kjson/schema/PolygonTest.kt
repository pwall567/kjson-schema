/*
 * @(#) PolygonTest.kt
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
import kotlin.test.assertNotNull
import kotlin.test.expect
import kotlin.test.fail

import java.net.URI

import io.kjson.pointer.JSONPointer
import io.kjson.resource.ResourceLoader
import io.kjson.schema.loader.SchemaLoader
import io.kjson.util.JSONLoader

class PolygonTest {

    @Test fun `should give correct Basic result from Polygon test`() {
        val loader = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
        val schemaDocument = loader.load("polygon.schema.json")
        val jsonLoader = JSONLoader(ResourceLoader.classPathURL("/json/") ?: fail("Can't locate /json"))
        val json = jsonLoader.load("polygon.json")
        val output = schemaDocument.schema.getBasicOutput(json)
        output.errors?.forEach {
            println(it)
        }
        assertFalse(output.valid)
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(6) { outputErrors.size }
        with(outputErrors[0]) {
            expect(JSONPointer.root) { keywordLocation }
            expect(URI("https://example.com/polygon#")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[1]) {
            expect(JSONPointer("/items")) { keywordLocation }
            expect(URI("https://example.com/polygon#/items")) { absoluteKeywordLocation }
            expect(JSONPointer("/1")) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[2]) {
            expect(JSONPointer("/items/\$ref")) { keywordLocation }
            expect(URI("https://example.com/polygon#/\$defs/point")) { absoluteKeywordLocation }
            expect(JSONPointer("/1")) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[3]) {
            expect(JSONPointer("/items/\$ref/additionalProperties")) { keywordLocation }
            expect(URI("https://example.com/polygon#/\$defs/point/additionalProperties")) { absoluteKeywordLocation }
            expect(JSONPointer("/1/z")) { instanceLocation }
            expect("Additional property 'z' found but was invalid") { error }
        }
        with(outputErrors[4]) {
            expect(JSONPointer("/items/\$ref/required")) { keywordLocation }
            expect(URI("https://example.com/polygon#/\$defs/point/required")) { absoluteKeywordLocation }
            expect(JSONPointer("/1")) { instanceLocation }
            expect("Required property 'y' not found") { error }
        }
        with(outputErrors[5]) {
            expect(JSONPointer("/minItems")) { keywordLocation }
            expect(URI("https://example.com/polygon#/minItems")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("Expected at least 3 items but found 2") { error }
        }
    }

}
