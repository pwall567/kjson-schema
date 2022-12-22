/*
 * @(#) RefHandler.kt
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

import io.kjson.JSONIncorrectTypeException
import io.kjson.JSONString
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.KeywordHandler
import io.kjson.schema.loader.SchemaLoader
import io.kjson.schema.JSONSchemaException.Companion.fatal
import io.kjson.schema.SchemaLocation
import io.kjson.schema.elements.RefElement
import io.kjson.util.Util.resolve
import net.pwall.log.getLogger

object RefHandler : KeywordHandler {

    val log = getLogger()

    override fun process(loadContext: SchemaLoader.LoadContext): JSONSchema.Element {
        val ref = loadContext.ref
        if (!ref.isRef<JSONString>())
            throw JSONIncorrectTypeException("\$ref", "JSONString", ref.node, loadContext.schemaLocation)
        val refValue = ref.asRef<JSONString>().node.value
        val resolved = loadContext.schemaLocation.uri.resolve(refValue)
//        val uri = loadContext.schemaLoader.resolve(refValue).resourceURL.toURI()
//        val json = loadContext.schemaLoader.locate(uri) ?: log.fatal("Can't locate \$ref URI - $uri")
        val json = loadContext.schemaLoader.locate(resolved) ?: log.fatal("Can't locate \$ref URI - $resolved")
        val schema = loadContext.copy(
//            schemaLocation = SchemaLocation.fromURI(uri),
            schemaLocation = SchemaLocation.fromURI(resolved),
            ref = JSONRef(json),
        ).process()
        return RefElement(loadContext.schemaLocation, schema)
    }

}
