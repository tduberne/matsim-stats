package org.matsim.usagestats

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.matsim.testcases.MatsimTestUtils

/**
 * @author thibautd
 */
class FileParsingTest {
    @Rule
    val utils = MatsimTestUtils()

    @Test
    fun testIdentifyDtd() {
        val type = identifyFileFormat( utils.packageInputDirectory+"/test.xml" )
        Assert.assertEquals("unexpected dtd", "population_v5.dtd", type)
    }
}