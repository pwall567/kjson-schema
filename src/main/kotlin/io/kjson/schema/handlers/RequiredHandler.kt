package io.kjson.schema.handlers

import io.kjson.JSONArray
import io.kjson.JSONString
import io.kjson.pointer.child
import io.kjson.schema.JSONSchema
import io.kjson.schema.KeywordHandler
import io.kjson.schema.elements.RequiredElement
import io.kjson.schema.loader.SchemaLoader

object RequiredHandler : KeywordHandler {

    override fun process(loadContext: SchemaLoader.LoadContext): JSONSchema.Element? {
        val arrayRef = loadContext.ref.asRef<JSONArray>()
        val propertyNames = List<String>(arrayRef.node.size) { arrayRef.child<JSONString>(it).node.value }
        return RequiredElement(loadContext.schemaLocation, propertyNames)
    }

}
