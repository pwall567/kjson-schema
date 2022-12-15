/*
 * @(#) SchemaDocument.kt
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

import java.net.URI
import java.net.URL

import io.kjson.JSON.asStringOrNull
import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.schema.JSONSchema
import io.kjson.schema.JSONSchemaException.Companion.fatal
import io.kjson.schema.SchemaDialect
import io.kjson.schema.SchemaLocation
import io.kjson.util.Util.withFragment
import net.pwall.log.getLogger

class SchemaDocument(
    val json: JSONValue,
    val url: URL?,
) {

    enum class State { INITIAL, PRESCANNED, PROCESSED }

    var state: State = State.INITIAL

    val baseURI: URI? = if (json is JSONObject && json.containsKey("\$id")) {
        json["\$id"].asStringOrNull?.let { URI(it).checkAbsolute() } ?: log.fatal("\$id must be string - $url")
    } else url?.toURI()

    val schemaDialect: SchemaDialect =
            if (json is JSONObject) SchemaDialect.findDialect(json) else SchemaDialect.latest()

    var schema: JSONSchema = JSONSchema.BooleanSchema(SchemaLocation(baseURI, JSONPointer.root), true)

    companion object {

        val log = getLogger()

        fun URI.checkAbsolute(): URI =
            if (scheme != null) this.withFragment(null) else log.fatal("\$id must be absolute URI - $this")

    }

}
