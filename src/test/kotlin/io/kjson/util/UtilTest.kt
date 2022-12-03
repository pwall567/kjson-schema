package io.kjson.util

import kotlin.test.Test
import kotlin.test.assertNull
import kotlin.test.expect
import java.net.URI
import io.kjson.pointer.JSONPointer
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

}
