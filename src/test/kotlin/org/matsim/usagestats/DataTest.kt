package org.matsim.usagestats

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.Assert
import org.junit.Test

/**
 * @author thibautd
 */
class DataTest {
    @Test
    fun testPartialDeserialization() {
        val value = ObjectMapper().readValue("{\"machine\": {\"osName\": \"xunil\"}}", UsageStats::class.java)

        Assert.assertEquals("unexpected deserialized value", value.machine.osName, "xunil")
        Assert.assertNull("unexpected deserialized missing value", value.matsim.matsimVersion)
    }
}