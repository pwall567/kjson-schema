/*
 * @(#) DefsHandlerTest.kt
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

package io.kjson.schema.handlers

import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.expect

import java.net.URI
import java.net.URL

import io.kjson.JSONInt
import io.kjson.JSONObject
import io.kjson.pointer.JSONPointer
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.elements.MaximumElement
import io.kjson.schema.loader.SchemaLoader

class DefsHandlerTest {

    @Test fun `should create defs element`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("\$defs", JSONObject.build {
                add("def1", JSONObject.build {
                    add("maximum", 25)
                })
            })
        }
        val schemaDocument = loader.loadFromJSON(json, URL("https://example.com/schema"))
        val schema = schemaDocument.schema
        assertIs<JSONSchema.ObjectSchema>(schema)
        expect(0) { schema.elements.size }
        val cachedSchema = loader.schemaCache[SchemaLocation(URI("https://example.com/schema"), JSONPointer("/\$defs/def1"))]
        assertIs<JSONSchema.ObjectSchema>(cachedSchema)
        expect(1) { cachedSchema.elements.size }
        val element = cachedSchema.elements[0]
        assertIs<MaximumElement>(element)
        expect(JSONInt(25)) { element.limit }
    }

}
