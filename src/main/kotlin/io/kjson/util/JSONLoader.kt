/*
 * @(#) JSONLoader.kt
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

import java.io.File
import java.net.URL
import java.nio.file.Path

import io.kjson.JSON
import io.kjson.JSON.asArrayOrNull
import io.kjson.JSON.asObjectOrNull
import io.kjson.JSONArray
import io.kjson.JSONIncorrectTypeException
import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.resource.ResourceDescriptor
import io.kjson.resource.ResourceLoader
import io.kjson.util.Util.looksLikeYAML
import io.kjson.yaml.YAML

/**
 * Resource loader for JSON (and YAML) files.
 *
 * @author  Peter Wall
 */
class JSONLoader private constructor(
    resourcePath: Path?,
    resourceURL: URL,
) : ResourceLoader<JSONValue?, JSONLoader>(resourcePath, resourceURL) {

    constructor(resourceFile: File = currentDirectory) : this(resourceFile.toPath(), resourceFile.toURI().toURL())

    constructor(resourcePath: Path) : this(resourcePath, resourcePath.toUri().toURL())

    constructor(resourceURL: URL) : this(derivePath(resourceURL), resourceURL)

    override fun load(rd: ResourceDescriptor): JSONValue? {
        return rd.getReader().use {
            when {
                rd.looksLikeYAML() -> YAML.parse(it).rootNode
                else -> JSON.parse(it.readText())
            }
        }
    }

    override fun resolvedLoader(resourcePath: Path?, resourceURL: URL) = JSONLoader(resourcePath, resourceURL)

    fun loadObject(name: String): JSONObject = load(name).let {
        it.asObjectOrNull ?: throw JSONIncorrectTypeException("Resource", JSONObject::class, it, name)
    }

    fun loadArray(name: String): JSONArray = load(name).let {
        it.asArrayOrNull ?: throw JSONIncorrectTypeException("Resource", JSONArray::class, it, name)
    }

    fun loadObject(): JSONObject = load().let {
        it.asObjectOrNull ?: throw JSONIncorrectTypeException("Resource", JSONObject::class, it, resourceURL)
    }

    fun loadArray(): JSONArray = load().let {
        it.asArrayOrNull ?: throw JSONIncorrectTypeException("Resource", JSONArray::class, it, resourceURL)
    }

}
