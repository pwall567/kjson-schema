/*
 * @(#) MaximumHandlerTest.kt
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
import kotlin.test.assertFailsWith
import kotlin.test.assertIs
import kotlin.test.expect

import java.math.BigDecimal

import io.kjson.JSONDecimal
import io.kjson.JSONInt
import io.kjson.JSONObject
import io.kjson.schema.JSONSchema
import io.kjson.schema.JSONSchemaException
import io.kjson.schema.elements.MaximumElement
import io.kjson.schema.loader.SchemaLoader

class MaximumHandlerTest {

    @Test fun `should create maximum element`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("maximum", 42)
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertIs<JSONSchema.ObjectSchema>(schema)
        val element = schema.elements[0]
        assertIs<MaximumElement>(element)
        expect(JSONInt(42)) { element.limit }
    }

    @Test fun `should create maximum element for decimal`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("maximum", BigDecimal("1.5"))
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertIs<JSONSchema.ObjectSchema>(schema)
        val element = schema.elements[0]
        assertIs<MaximumElement>(element)
        expect(JSONDecimal(BigDecimal("1.5"))) { element.limit }
    }

    @Test fun `should throw exception when maximum is not number`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("maximum", "wrong")
        }
        assertFailsWith<JSONSchemaException> { loader.loadFromJSON(json) }.let {
            expect("maximum value must be number") { it.message }
        }
    }

}
