package io.kjson.schema.handlers

import io.kjson.JSONString
import io.kjson.schema.JSONSchema
import io.kjson.schema.KeywordHandler
import io.kjson.schema.elements.AnnotationElement
import io.kjson.schema.loader.SchemaLoader

class AnnotationHandler(val keyword: String) : KeywordHandler {

    override fun process(loadContext: SchemaLoader.LoadContext): JSONSchema.Element {
        val value = loadContext.ref.asRef<JSONString>().node.value
        return AnnotationElement(keyword, value, loadContext.schemaLocation)
    }

}
