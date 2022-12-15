/*
 * @(#) JSONLoaderTest.kt
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

package io.kjson.util

import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlin.test.expect
import kotlin.test.fail

import java.io.File
import java.nio.file.FileSystems

import io.kjson.JSON.asInt
import io.kjson.JSON.asString
import io.kjson.JSON.asStringOrNull
import io.kjson.JSONIncorrectTypeException
import io.kjson.JSONObject
import io.kjson.resource.ResourceLoader

class JSONLoaderTest {

    @Test fun `should load JSON using classpath`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't find schema directory"))
        val json = loader.load("dummy-schema.schema.json")
        assertTrue(json is JSONObject)
        expect("https://io.kjson/dummy") { json["\$id"].asStringOrNull }
        expect("integer") { json["type"].asStringOrNull }
    }

    @Test fun `should load JSON using file reference`() {
        val loader = JSONLoader(File("src/test/resources/schema"))
        val json = loader.load("dummy-schema.schema.json")
        assertTrue(json is JSONObject)
        expect("https://io.kjson/dummy") { json["\$id"].asStringOrNull }
        expect("integer") { json["type"].asStringOrNull }
    }

    @Test fun `should load JSON using path reference`() {
        val loader = JSONLoader(FileSystems.getDefault().getPath("src/test/resources/schema"))
        val json = loader.load("dummy-schema.schema.json")
        assertTrue(json is JSONObject)
        expect("https://io.kjson/dummy") { json["\$id"].asStringOrNull }
        expect("integer") { json["type"].asStringOrNull }
    }

    @Test fun `should load JSON object`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't find schema directory"))
        val json = loader.loadObject("dummy-schema.schema.json")
        expect("https://io.kjson/dummy") { json["\$id"].asStringOrNull }
        expect("integer") { json["type"].asStringOrNull }
    }

    @Test fun `should fail when trying to load JSON object when wrong type`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/json/") ?: fail("Can't find JSON directory"))
        assertFailsWith<JSONIncorrectTypeException> { loader.loadObject("dummy-array.json") }.let {
            expect("Resource") { it.nodeName }
            expect("JSONObject") { it.target }
            expect("dummy-array.json") { it.key }
            expect("Resource not correct type (JSONObject), was [ ... ], at dummy-array.json") { it.message }
        }
    }

    @Test fun `should load JSON array`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/json/") ?: fail("Can't find JSON directory"))
        val json = loader.loadArray("dummy-array.json")
        expect(2) { json.size }
        with(json[0]) {
            assertTrue(this is JSONObject)
            expect("abc") { this["field1"].asString }
            expect(123) { this["field2"].asInt }
        }
        with(json[1]) {
            assertTrue(this is JSONObject)
            expect("xyz") { this["field1"].asString }
            expect(987) { this["field2"].asInt }
        }
    }

    @Test fun `should fail when trying to load JSON array when wrong type`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't find JSON directory"))
        assertFailsWith<JSONIncorrectTypeException> { loader.loadArray("dummy-schema.schema.json") }.let {
            expect("Resource") { it.nodeName }
            expect("JSONArray") { it.target }
            expect("dummy-schema.schema.json") { it.key }
            expect("Resource not correct type (JSONArray), was { ... }, at dummy-schema.schema.json") { it.message }
        }
    }

    @Test fun `should load JSON object using resolved JSONLoader`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't find schema directory"))
        val resolvedLoader = loader.resolve("dummy-schema.schema.json")
        val json = resolvedLoader.loadObject()
        expect("https://io.kjson/dummy") { json["\$id"].asStringOrNull }
        expect("integer") { json["type"].asStringOrNull }
    }

    @Test fun `should load JSON array using resolved JSONLoader`() {
        val loader = JSONLoader(ResourceLoader.classPathURL("/json/") ?: fail("Can't find JSON directory"))
        val resolvedLoader = loader.resolve("dummy-array.json")
        val json = resolvedLoader.loadArray()
        expect(2) { json.size }
        with(json[0]) {
            assertTrue(this is JSONObject)
            expect("abc") { this["field1"].asString }
            expect(123) { this["field2"].asInt }
        }
        with(json[1]) {
            assertTrue(this is JSONObject)
            expect("xyz") { this["field1"].asString }
            expect(987) { this["field2"].asInt }
        }
    }

}
