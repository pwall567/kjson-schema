package io.kjson.schema

import kotlin.test.Test
import kotlin.test.fail
import io.kjson.resource.ResourceLoader
import io.kjson.schema.loader.SchemaLoader

class PolygonTest {

    @Test fun `should give correct Basic result from Polygon test`() {
        val loader = SchemaLoader(ResourceLoader.classPathURL("/schema/") ?: fail("Can't locate /schema"))
//        val schemaDocument = loader.load("polygon.schema.json")
        // TODO can't complete this until items, minItems, $defs and $ref are implemented
    }

}
