/*
 * @(#) RefElementTest.kt
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
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlin.test.fail

import java.net.URI

import io.kjson.JSONInt
import io.kjson.pointer.JSONPointer
import io.kjson.resource.ResourceLoader
import io.kjson.schema.loader.SchemaLoader

class RefElementTest {

    @Test fun `should follow ref to target schema`() {
        val loader = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
        loader.preLoad("dummy-defs.schema.json")
        val schemaDocument = loader.load("dummy-ref.schema.json")
        val schema = schemaDocument.schema
        assertTrue(schema.validate(JSONInt(20)))
        assertFalse(schema.validate(JSONInt(30)))
    }

    @Test fun `should return basic output for fail on ref`() {
        val loader = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
        loader.preLoad("dummy-defs.schema.json")
        val schemaDocument = loader.load("dummy-ref.schema.json")
        val schema = schemaDocument.schema
        val output = schema.getBasicOutput(JSONInt(30))
        assertFalse(output.valid)
        val outputErrors = output.errors
        assertNotNull(outputErrors)
        expect(3) { outputErrors.size }
        with(outputErrors[0]) {
            expect(JSONPointer.root) { keywordLocation }
            expect(URI("http://example.com/test-ref#")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[1]) {
            expect(JSONPointer("/\$ref")) { keywordLocation }
            expect(URI("http://example.com/defs#/\$defs/def1")) { absoluteKeywordLocation }
            expect(JSONPointer.root) { instanceLocation }
            expect("A subschema had errors") { error }
        }
        with(outputErrors[2]) {
            expect(JSONPointer("/\$ref/maximum")) { keywordLocation } // TODO Check!!!!!!!
            expect(URI("http://example.com/defs#/\$defs/def1/maximum")) { absoluteKeywordLocation } // TODO check!!!!!!!
            expect(JSONPointer.root) { instanceLocation }
            expect("Number > maximum 25, was 30") { error }
        }
    }

}