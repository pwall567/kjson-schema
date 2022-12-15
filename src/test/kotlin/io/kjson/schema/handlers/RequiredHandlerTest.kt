/*
 * @(#) RequiredHandlerTest.kt
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
import io.kjson.JSONArray

import io.kjson.JSONObject
import io.kjson.schema.JSONSchema
import io.kjson.schema.elements.RequiredElement
import io.kjson.schema.loader.SchemaLoader

class RequiredHandlerTest {

    @Test fun `should create required element`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("required", JSONArray.build {
                add("aaa")
                add("bbb")
                add("ccc")
            })
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertIs<JSONSchema.ObjectSchema>(schema)
        val element = schema.elements[0]
        assertIs<RequiredElement>(element)
        val propertyNames = element.propertyNames
        expect(3) { propertyNames.size }
        expect("aaa") { propertyNames[0] }
        expect("bbb") { propertyNames[1] }
        expect("ccc") { propertyNames[2] }
    }

}
