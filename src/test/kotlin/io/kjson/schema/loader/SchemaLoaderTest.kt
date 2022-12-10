/*
 * @(#) SchemaLoaderTest.kt
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

package io.kjson.schema.loader

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlin.test.fail

import java.net.URI

import io.kjson.JSONInt
import io.kjson.JSONString
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.resource.ResourceLoader
import io.kjson.schema.JSONSchema

class SchemaLoaderTest {

    @Test fun `should load trivial schema`() {
        val loader = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
        val schemaDocument = loader.load("dummy-schema.schema.json")
        assertIs<JSONSchema.ObjectSchema>(schemaDocument.schema)
        assertTrue { schemaDocument.schema.validate(JSONRef(JSONInt(123))) }
        assertFalse { schemaDocument.schema.validate(JSONRef(JSONString("abc"))) }
    }

    @Test fun `should pre-scan example from JSON Schema documentation`() {
        val sl = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
        val sd = sl.preLoad("id-example")
        expect(SchemaDocument.State.PRESCANNED) { sd.state }
        sl.idMappings.keys.forEach {
            println(it)
        }
        val json = sd.json
        expect("") { ptr(json, sl.locate(URI("https://example.com/root.json"))) }
        expect("") { ptr(json, sl.locate(URI("https://example.com/root.json#"))) }
        expect("/\$defs/A") { ptr(json, sl.locate(URI("https://example.com/root.json#foo"))) }
        expect("/\$defs/A") { ptr(json, sl.locate(URI("https://example.com/root.json#/\$defs/A"))) }
        expect("/\$defs/B") { ptr(json, sl.locate(URI("https://example.com/other.json"))) }
        expect("/\$defs/B") { ptr(json, sl.locate(URI("https://example.com/other.json#"))) }
        expect("/\$defs/B") { ptr(json, sl.locate(URI("https://example.com/root.json#/\$defs/B"))) }
        expect("/\$defs/B/\$defs/X") { ptr(json, sl.locate(URI("https://example.com/other.json#bar"))) }
        expect("/\$defs/B/\$defs/X") { ptr(json, sl.locate(URI("https://example.com/other.json#/\$defs/X"))) }
        expect("/\$defs/B/\$defs/X") { ptr(json, sl.locate(URI("https://example.com/root.json#/\$defs/B/\$defs/X"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("https://example.com/t/inner.json"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("https://example.com/t/inner.json#"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("https://example.com/t/inner.json#bar"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("https://example.com/other.json#/\$defs/Y"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("https://example.com/root.json#/\$defs/B/\$defs/Y"))) }
        expect("/\$defs/C") { ptr(json, sl.locate(URI("urn:uuid:ee564b8a-7a87-4125-8c96-e9f123d6766f"))) }
        expect("/\$defs/C") { ptr(json, sl.locate(URI("urn:uuid:ee564b8a-7a87-4125-8c96-e9f123d6766f#"))) }
        expect("/\$defs/C") { ptr(json, sl.locate(URI("https://example.com/root.json#/\$defs/C"))) }
        expect("") { ptr(json, sl.locate(URI("${sd.url}"))) }
        expect("") { ptr(json, sl.locate(URI("${sd.url}#"))) }
        expect("/\$defs/A") { ptr(json, sl.locate(URI("${sd.url}#/\$defs/A"))) }
        expect("/\$defs/B") { ptr(json, sl.locate(URI("${sd.url}#/\$defs/B"))) }
        expect("/\$defs/B/\$defs/X") { ptr(json, sl.locate(URI("${sd.url}#/\$defs/B/\$defs/X"))) }
        expect("/\$defs/B/\$defs/Y") { ptr(json, sl.locate(URI("${sd.url}#/\$defs/B/\$defs/Y"))) }
        expect("/\$defs/C") { ptr(json, sl.locate(URI("${sd.url}#/\$defs/C"))) }
    }

    private fun ptr(root: JSONValue, json: JSONValue?): String = JSONPointer.root.locateChild(root, json).toString()

}
