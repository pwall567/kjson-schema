/*
 * @(#) JSON.kt
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

import java.net.URI

import io.kjson.pointer.JSONPointer
import io.kjson.resource.ResourceDescriptor

object Util {

    fun URI.withFragment(newFragment: String?): URI = if (isOpaque)
            URI(scheme, schemeSpecificPart, newFragment)
        else
            URI(scheme, authority, path, query, newFragment)

    fun URI.withFragment(pointer: JSONPointer): URI =
            withFragment(pointer.takeIf { it != JSONPointer.root }?.toString())

    fun URI?.resolve(string: String): URI = if (this != null) resolve(string) else URI(string)

    fun ResourceDescriptor.looksLikeYAML(): Boolean {
        mimeType?.let { // there is no standard mime type for YAML
            if (it.contains("yaml", ignoreCase = true) || it.contains("yml", ignoreCase = true)) // beware - symlink!
                return true
            if (it.contains("json", ignoreCase = true))
                return false
        }
        url.path.let {
            if (it.endsWith(".yaml", ignoreCase = true) || it.endsWith(".yml", ignoreCase = true))
                return true
        }
        return false
    }

    fun plural(n: Int, name: String) = buildString {
        append(n)
        append(' ')
        append(name)
        if (n != 1)
            append('s')
    }

    fun plural(n: Int, name: String, pluralName: String) = buildString {
        append(n)
        append(' ')
        if (n == 1)
            append(name)
        else
            append(pluralName)
    }

}
