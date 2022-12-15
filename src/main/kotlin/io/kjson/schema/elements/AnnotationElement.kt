package io.kjson.schema.elements

import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

class AnnotationElement(override val keyword: String, val value: String, location: SchemaLocation) :
        JSONSchema.Element(location) {

    override fun validate(instance: JSONRef<*>): Boolean = true

    override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput = BasicOutput(true)

    override fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
        TODO("Not yet implemented")
    }

    override fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
        TODO("Not yet implemented")
    }

}
