/*
 * @(#) MaximumElementTest.kt
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
import kotlin.test.assertNull
import kotlin.test.assertTrue

import io.kjson.JSONInt
import io.kjson.JSONObject
import io.kjson.schema.loader.SchemaLoader

class MaximumElementTest {

    @Test fun `should validate number less than or equal to maximum`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("maximum", 42)
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        assertTrue(schema.validate(JSONInt(27)))
        assertFalse(schema.validate(JSONInt(54)))
    }

    @Test fun `should return basic output for number less than maximum`() {
        val loader = SchemaLoader()
        val json = JSONObject.build {
            add("maximum", 42)
        }
        val schemaDocument = loader.loadFromJSON(json)
        val schema = schemaDocument.schema
        val basicOutput = schema.getBasicOutput(JSONInt(27))
        assertTrue(basicOutput.valid)
        assertNull(basicOutput.errors)
    }

}
