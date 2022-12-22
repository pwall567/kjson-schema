/*
 * @(#) UtilTest.kt
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
import kotlin.test.assertNull
import kotlin.test.expect

import java.net.URI

import io.kjson.pointer.JSONPointer
import io.kjson.util.Util.plural
import io.kjson.util.Util.withFragment

class UtilTest {

    @Test fun `should add correct fragment to URI`() {
        val baseURI = URI("https://example.com")
        baseURI.withFragment(JSONPointer("/test1")).let {
            expect("https://example.com#/test1") { it.toString() }
            expect("/test1") { it.fragment }
        }
    }

    @Test fun `should add correct fragment to opaque URI`() {
        val baseURI = URI("urn:uuid:b779e2d4-a529-11ec-8c4d-4f14ec374d69")
        baseURI.withFragment(JSONPointer("/test1")).let {
            expect("urn:uuid:b779e2d4-a529-11ec-8c4d-4f14ec374d69#/test1") { it.toString() }
            expect("/test1") { it.fragment }
        }
    }

    @Test fun `should replace existing fragment`() {
        val baseURI = URI("https://example.com#/previous")
        baseURI.withFragment(JSONPointer("/test1")).let {
            expect("https://example.com#/test1") { it.toString() }
            expect("/test1") { it.fragment }
        }
    }

    @Test fun `should add empty fragment to URI`() {
        val baseURI = URI("https://example.com#/previous")
        baseURI.withFragment(JSONPointer.root).let {
            expect("https://example.com") { it.toString() }
            assertNull(it.fragment)
        }
    }

    @Test fun `should add empty fragment to opaque URI`() {
        val baseURI = URI("urn:uuid:b779e2d4-a529-11ec-8c4d-4f14ec374d69#/previous")
        baseURI.withFragment(JSONPointer.root).let {
            expect("urn:uuid:b779e2d4-a529-11ec-8c4d-4f14ec374d69") { it.toString() }
            assertNull(it.fragment)
        }
    }

    @Test fun `should give correct plural for regular nouns`() {
        expect("1 item") { plural(1, "item") }
        expect("2 items") { plural(2, "item") }
        expect("3 items") { plural(3, "item") }
        expect("999 items") { plural(999, "item") }
        expect("0 items") { plural(0, "item") }
    }

    @Test fun `should give correct plural for irregular nouns`() {
        expect("1 property") { plural(1, "property", "properties") }
        expect("2 properties") { plural(2, "property", "properties") }
        expect("3 properties") { plural(3, "property", "properties") }
        expect("999 properties") { plural(999, "property", "properties") }
        expect("0 properties") { plural(0, "property", "properties") }
    }

}
