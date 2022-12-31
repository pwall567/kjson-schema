package io.kjson.schema.elements

import io.kjson.pointer.JSONPointer
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation
import io.kjson.schema.output.BasicOutput
import io.kjson.schema.output.Output

abstract class AbstractElement(location: SchemaLocation) : JSONSchema.Element(location) {

    abstract fun getErrorMessage(instance: JSONRef<*>): String

    override fun getBasicOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): BasicOutput {
        if (validate(parent, instance))
            return BasicOutput(valid = true)
        return createBasicErrorOutput(
            instance = instance,
            relativeLocation = relativeLocation,
            error = getErrorMessage(instance)
        )
    }

    override fun getDetailedOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): Output {
        if (validate(parent, instance))
            return createValidOutput(instance, relativeLocation)
        return createOutput(
            valid = false,
            instance = instance,
            relativeLocation = relativeLocation,
            error = getErrorMessage(instance)
        )
    }

    override fun getVerboseOutput(
        parent: JSONSchema.ObjectSchema,
        instance: JSONRef<*>,
        relativeLocation: JSONPointer
    ): Output = if (validate(parent, instance)) {
        createOutput(
            valid = true,
            instance = instance,
            relativeLocation = relativeLocation,
            annotation = keyword,
        )
    } else {
        createOutput(
            valid = false,
            instance = instance,
            relativeLocation = relativeLocation,
            error = getErrorMessage(instance),
        )
    }

}
