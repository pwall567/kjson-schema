/*
 * @(#) NumberComparisonElement.kt
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

package io.kjson.schema.elements

import java.math.BigDecimal

import io.kjson.JSONDecimal
import io.kjson.JSONInt
import io.kjson.JSONLong
import io.kjson.JSONNumber
import io.kjson.pointer.JSONRef
import io.kjson.schema.JSONSchema
import io.kjson.schema.SchemaLocation

abstract class NumberComparisonElement(location: SchemaLocation, val limit: JSONNumber) : AbstractElement(location) {

    override fun validate(parent: JSONSchema.ObjectSchema, instance: JSONRef<*>): Boolean =
        when (val value = instance.node) {
            is JSONInt -> validateInt(value)
            is JSONLong -> validateLong(value)
            is JSONDecimal -> validateDecimal(value)
            else -> true
        }

    private fun validateInt(value: JSONInt): Boolean = when (limit) {
        is JSONInt -> compareInt(value.value, limit.value)
        is JSONLong -> compareLong(value.value.toLong(), limit.value)
        is JSONDecimal -> compareDecimal(BigDecimal(value.value), limit.value)
    }

    private fun validateLong(value: JSONLong): Boolean = when (limit) {
        is JSONInt -> compareLong(value.value, limit.value.toLong())
        is JSONLong -> compareLong(value.value, limit.value)
        is JSONDecimal -> compareDecimal(BigDecimal(value.value), limit.value)
    }

    private fun validateDecimal(value: JSONDecimal): Boolean = when (limit) {
        is JSONInt -> compareDecimal(value.value, BigDecimal(limit.value))
        is JSONLong -> compareDecimal(value.value, BigDecimal(limit.value))
        is JSONDecimal -> compareDecimal(value.value, limit.value)
    }

    abstract fun compareInt(a: Int, b: Int): Boolean

    abstract fun compareLong(a: Long, b: Long): Boolean

    abstract fun compareDecimal(a: BigDecimal, b: BigDecimal): Boolean

    abstract fun getErrorString(): String

    override fun getErrorMessage(instance: JSONRef<*>): String = "${getErrorString()}, was ${instance.node}"

}
