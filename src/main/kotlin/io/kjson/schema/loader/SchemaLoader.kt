/*
 * @(#) SchemaLoader.kt
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

import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path

import io.kjson.JSONBoolean
import io.kjson.JSONObject
import io.kjson.JSONString
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.pointer.forEachKey
import io.kjson.pointer.get
import io.kjson.resource.Loader
import io.kjson.resource.ResourceLoader
import io.kjson.schema.JSONSchemaException.Companion.fatal
import io.kjson.schema.SchemaDialect
import io.kjson.util.JSONLoader
import io.kjson.util.Util.withFragment
import net.pwall.log.getLogger

class SchemaLoader private constructor(
    private val jsonLoader: JSONLoader,
    private val documentEntries: MutableList<SchemaDocument> = mutableListOf(),
    val idMappings: MutableMap<URI, IdMapping> = mutableMapOf(),
) : Loader<SchemaDocument, SchemaLoader> {

    constructor(resourceFile: File = ResourceLoader.currentDirectory) : this(JSONLoader(resourceFile))

    constructor(resourcePath: Path) : this(JSONLoader(resourcePath))

    constructor(resourceURL: URL) : this(JSONLoader(resourceURL))

    override val resourceURL: URL
        get() = jsonLoader.resourceURL

    override fun resolve(name: String): SchemaLoader {
        return SchemaLoader(jsonLoader.resolve(name), documentEntries, idMappings)
    }

    fun preLoad(name: String): SchemaDocument {
        return resolve(name).preLoad()
    }

    fun preLoad(): SchemaDocument {
        val schemaDocument = findSchemaDocument()
        if (schemaDocument.state == SchemaDocument.State.PRESCANNED ||
                schemaDocument.state == SchemaDocument.State.PROCESSED)
            return schemaDocument
        schemaDocument.state = SchemaDocument.State.PRESCANNED
        schemaDocument.json.let {
            if (it is JSONBoolean)
                return schemaDocument
            if (it !is JSONObject)
                log.fatal("Schema must be boolean or object - $resourceURL")
            val idMapping = IdMapping(it)
            idMappings[resourceURL.toURI()] = idMapping
            scanForId(schemaDocument.schemaDialect, resourceURL.toURI(), JSONRef(it), idMapping, idMappings)
        }
        return schemaDocument
    }

    override fun load(): SchemaDocument {
        TODO("Not yet implemented")
    }

    fun findSchemaDocument(): SchemaDocument {
        documentEntries.find { it.url == resourceURL }?.let { return it }
        return SchemaDocument(
            json = jsonLoader.load() ?: log.fatal("Schema file is \"null\""),
            url = resourceURL
        ).also { documentEntries.add(it) }
    }

    fun locate(uri: URI): JSONObject? {
        log.info { "locate $uri" }
        val absoluteURI = uri.withFragment(null)
        val idMapping = idMappings[absoluteURI] ?: return null
        uri.fragment?.takeIf { it.isNotEmpty() }?.let {
            if (it.startsWith("/")) {
                val ptr = JSONPointer(it)
                return idMapping.json[ptr] as? JSONObject
            }
            val ptr = idMapping.anchors[it] ?: return null
            return idMapping.json[ptr] as? JSONObject
        }
        return idMapping.json
    }

    class IdMapping(val json: JSONObject) {

        val anchors = mutableMapOf<String, JSONPointer>()

        fun addAnchor(anchor: String, pointer: JSONPointer) {
            if (anchors.containsKey(anchor))
                log.fatal("Duplicate anchor - $anchor")
            anchors[anchor] = pointer
        }

    }

    data class PreLoadContext(
        val schemaDialect: SchemaDialect,
        val baseURI: URI,
        val ref: JSONRef<*>,
        val idMapping: IdMapping,
        val idMappings: MutableMap<URI, IdMapping>,
    ) // TODO pass this as parameter instead of long parameter list (?)

    companion object {

        val log = getLogger()

        fun scanForId(
            schemaDialect: SchemaDialect,
            baseURI: URI,
            ref: JSONRef<*>,
            idMapping: IdMapping,
            idMappings: MutableMap<URI, IdMapping>,
        ) {
            val json = ref.asRef<JSONObject>().node
            when (val idProperty = json["\$id"]) {
                null -> {
                    for (property in json.keys) {
                        val handler = schemaDialect.findKeywordHandler(property)
                        handler.preScan(schemaDialect, baseURI, ref.child(property), idMapping, idMappings)
                    }
                }
                is JSONString -> {
                    val idURI = baseURI.resolve(idProperty.value).checkIdURI("\$id")
                    val newIdMapping = IdMapping(json)
                    idMappings[idURI] = newIdMapping
                    for (property in json.keys) {
                        val handler = schemaDialect.findKeywordHandler(property)
                        handler.preScan(schemaDialect, idURI, JSONRef(json).child(property), newIdMapping, idMappings)
                    }
                }
                else -> log.fatal("\$id must be string")
            }
        }

        fun scanCompoundForId(
            schemaDialect: SchemaDialect,
            baseURI: URI,
            ref: JSONRef<JSONObject>,
            idMapping: IdMapping,
            idMappings: MutableMap<URI, IdMapping>,
        ) {
            ref.forEachKey<JSONValue> {
                scanForId(schemaDialect, baseURI, this, idMapping, idMappings)
            }
        }

        private fun URI.checkIdURI(type: String): URI = when {
            scheme == null -> log.fatal("$type URI must be absolute")
            fragment == null -> this
            fragment.isEmpty() -> {
                log.warn { "$type URI should not include empty fragment - $this" }
                withFragment(null)
            }
            else -> log.fatal("$type URI must not include fragment - $this")
        }

    }

}
