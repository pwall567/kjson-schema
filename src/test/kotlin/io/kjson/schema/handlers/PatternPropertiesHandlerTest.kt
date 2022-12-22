/*
 * @(#) PatternPropertiesHandlerTest.kt
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

import io.kjson.JSONInt
import io.kjson.JSONObject
import io.kjson.schema.JSONSchema
import io.kjson.schema.elements.MaximumElement
import io.kjson.schema.elements.PatternPropertiesElement
import io.kjson.schema.loader.SchemaLoader

class PatternPropertiesHandlerTest {

    @Test fun `should create properties element`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("patternProperties", JSONObject.build {
                add("^[a-z]{1,9}$", JSONObject.build {
                    add("maximum", 25)
                })
            })
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertIs<JSONSchema.ObjectSchema>(schema)
        val element = schema.elements[0]
        assertIs<PatternPropertiesElement>(element)
        val properties = element.properties
        expect(1) { properties.size }
        with(properties[0]) {
            expect("^[a-z]{1,9}$") { first.toString() }
            val nestedSchema = second
            assertIs<JSONSchema.ObjectSchema>(nestedSchema)
            val nestedElement = nestedSchema.elements[0]
            assertIs<MaximumElement>(nestedElement)
            expect(JSONInt(25)) { nestedElement.limit }
        }
    }

}
