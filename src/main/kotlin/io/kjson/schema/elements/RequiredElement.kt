package io.kjson.schema.elements

import io.kjson.JSONObject
import io.kjson.JSONValue
import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.pointer.hasChild
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

class RequiredElement(location: SchemaLocation, val propertyNames: List<String>) : JSONSchema.Element(location) {

    override val keyword: String = "required"

    override fun validate(instance: JSONRef<*>): Boolean {
        if (!instance.isRef<JSONObject>())
            return true
        val objectRef = instance.asRef<JSONObject>()
        for (propertyName in propertyNames) {
            if (!objectRef.hasChild<JSONValue?>(propertyName))
                return false
        }
        return true
    }

    override fun getBasicOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): BasicOutput {
        if (!instance.isRef<JSONObject>())
            return BasicOutput(true)
        val errors = mutableListOf<BasicOutput.Entry>()
        val objectRef = instance.asRef<JSONObject>()
        for (propertyName in propertyNames) {
            if (!objectRef.hasChild<JSONValue?>(propertyName))
                errors.add(createBasicOutputUnit(instance, relativeLocation,
                        "Required property '$propertyName' not found"))
        }
        return if (errors.isEmpty())
            BasicOutput(true)
        else
            BasicOutput(false, errors)
    }

    override fun getDetailedOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
        TODO("Not yet implemented")
    }

    override fun getVerboseOutput(instance: JSONRef<*>, relativeLocation: JSONPointer): Output {
        TODO("Not yet implemented")
    }

}
