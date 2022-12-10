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
import io.kjson.JSON

import io.kjson.JSONBoolean
import io.kjson.JSONObject
import io.kjson.JSONString
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.pointer.child
import io.kjson.pointer.forEachKey
import io.kjson.pointer.get
import io.kjson.resource.Loader
import io.kjson.resource.ResourceLoader
import io.kjson.schema.JSONSchema
import io.kjson.schema.JSONSchemaException
import io.kjson.schema.JSONSchemaException.Companion.fatal
import io.kjson.schema.SchemaDialect
import io.kjson.schema.SchemaLocation
import io.kjson.util.JSONLoader
import io.kjson.util.Util.resolve
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
        return preload(schemaDocument)
    }

    private fun preload(schemaDocument: SchemaDocument): SchemaDocument {
        schemaDocument.json.let { json ->
            if (json is JSONBoolean) {
                schemaDocument.schema = JSONSchema.booleanSchema(
                    booleanValue = json.value,
                    location = SchemaLocation(schemaDocument.baseURI, JSONPointer.root),
                )
                schemaDocument.state = SchemaDocument.State.PROCESSED
                return schemaDocument
            }
            if (json !is JSONObject)
                log.fatal("Schema must be boolean or object - $resourceURL")
            PreLoadContext(
                schemaDialect = schemaDocument.schemaDialect,
                baseURI = resourceURL.toURI(),
                ref = JSONRef(json),
                idMapping = IdMapping(json).also { idMappings[resourceURL.toURI()] = it },
                idMappings = idMappings,
            ).scan()
            schemaDocument.state = SchemaDocument.State.PRESCANNED
        }
        return schemaDocument
    }

    override fun load(): SchemaDocument {
        val schemaDocument = preLoad()
        if (schemaDocument.state == SchemaDocument.State.PROCESSED)
            return schemaDocument
        val loadContext = LoadContext(
            schemaDialect = schemaDocument.schemaDialect,
            schemaLocation = SchemaLocation(schemaDocument.baseURI, JSONPointer.root),
            ref = JSONRef(schemaDocument.json),
        )
//        schemaDocument.schema = processSchema(schemaDocument, JSONRef(schemaDocument.json).asRef())
        schemaDocument.schema = loadContext.process()
        schemaDocument.state = SchemaDocument.State.PROCESSED
        return schemaDocument
    }

    fun loadFromString(string: String): SchemaDocument {
        return loadFromJSON(JSON.parse(string) ?: throw JSONSchemaException("JSON string was \"null\""))
    }

    fun loadFromJSON(json: JSONValue): SchemaDocument {
        val schemaDocument = SchemaDocument(json, null)
        preload(schemaDocument)
        val loadContext = LoadContext(
            schemaDialect = schemaDocument.schemaDialect,
            schemaLocation = SchemaLocation(schemaDocument.baseURI, JSONPointer.root),
            ref = JSONRef(json),
        )
//        schemaDocument.schema = processSchema(schemaDocument, JSONRef(schemaDocument.json).asRef())
        schemaDocument.schema = loadContext.process()
        schemaDocument.state = SchemaDocument.State.PROCESSED
        return schemaDocument
    }

//    private fun processSchema(schemaDocument: SchemaDocument, ref: JSONRef<JSONObject>): JSONSchema {
//        val elements = mutableListOf<JSONSchema.Element>()
//        ref.forEachKey<JSONValue> {
//            val handler = schemaDocument.schemaDialect.findKeywordHandler(it)
//            val schemaLocation = SchemaLocation(schemaDocument.baseURI, pointer)
//            handler.process(schemaLocation, this)?.let { element -> elements.add(element) }
//        }
//        return JSONSchema.ObjectSchema(SchemaLocation(schemaDocument.baseURI, ref.pointer), schemaDocument.baseURI, elements)
//    }

    private fun findSchemaDocument(): SchemaDocument {
        documentEntries.find { it.url == resourceURL }?.let { return it }
        return SchemaDocument(
            json = jsonLoader.load() ?: log.fatal("Schema file is \"null\" - $resourceURL"),
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

    data class LoadContext(
        val schemaDialect: SchemaDialect,
        val schemaLocation: SchemaLocation,
        val ref: JSONRef<*>,
    ) {

        fun process(): JSONSchema {
            when (val node = ref.node) {
                is JSONBoolean -> return JSONSchema.booleanSchema(node.value, schemaLocation)
                is JSONObject -> {
                    val elements = mutableListOf<JSONSchema.Element>()
                    val objectRef = ref.asRef<JSONObject>()
                    objectRef.forEachKey<JSONValue> {
                        val handler = schemaDialect.findKeywordHandler(it)
                        val loadContext = this@LoadContext.copy(
                            schemaLocation = schemaLocation.child(it),
                            ref = this,
                        )
                        handler.process(loadContext)?.let { element -> elements.add(element) }
                    }
                    return JSONSchema.ObjectSchema(schemaLocation, null, elements)
                }
                else -> log.fatal("Schema must be boolean or object - $schemaLocation")
            }
        }

    }

    data class PreLoadContext(
        val schemaDialect: SchemaDialect,
        val baseURI: URI?,
        val ref: JSONRef<*>,
        val idMapping: IdMapping,
        val idMappings: MutableMap<URI, IdMapping>,
    ) {

        fun scan() {
            val objectRef = ref.asRef<JSONObject>()
            val json = objectRef.node
            when (val idProperty = json["\$id"]) {
                null -> objectRef.forEachKey<JSONValue> {
                    val handler = schemaDialect.findKeywordHandler(it)
                    handler.preScan(copy(ref = this))
                }
                is JSONString -> {
                    val idURI = baseURI.resolve(idProperty.value).checkIdURI("\$id")
                    for (property in json.keys) {
                        val handler = schemaDialect.findKeywordHandler(property)
                        handler.preScan(copy(
                            baseURI = idURI,
                            ref = JSONRef(json).child<JSONValue>(property),
                            idMapping = IdMapping(json).also { idMappings[idURI] = it },
                        ))
                    }
                }
                else -> log.fatal("\$id must be string")
            }
        }

    }

    companion object {

        val log = getLogger()

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
