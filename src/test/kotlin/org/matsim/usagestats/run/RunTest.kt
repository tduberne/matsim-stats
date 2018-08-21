package org.matsim.usagestats.run

import org.junit.Rule
import org.junit.Test
import org.matsim.core.config.ConfigUtils
import org.matsim.core.controler.Controler
import org.matsim.testcases.MatsimTestUtils
import org.matsim.usagestats.UsageStatsModule
import java.io.File
import java.net.URL

/**
 * @author thibautd
 */
class RunTest {
    @Rule @JvmField
    val utils = MatsimTestUtils()

    @Test
    fun testRunDoesNotCrash() {
        // TODO load an actual scenario
        val config = utils.createConfig(File("./").toURI().toURL())
        config.controler().lastIteration = 0

        val controler = Controler(config)

        controler.addOverridingModule(UsageStatsModule())

        controler.run()
    }
}