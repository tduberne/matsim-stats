package org.matsim.usagestats

import com.google.inject.Binding
import org.matsim.api.core.v01.Scenario
import org.matsim.core.controler.MatsimServices
import org.matsim.core.gbl.Gbl
import java.lang.management.ManagementFactory
import java.lang.management.MemoryType

data class UsageStats(val memory: MemoryData,
                      val scenario: ScenarioData,
                      val machine: MachineData,
                      val matsim: MatsimRunData) {
    constructor(services: MatsimServices, unexpectedShutdown: Boolean) : this(
            MemoryData(),
            ScenarioData(services.scenario),
            MachineData(),
            MatsimRunData(services, unexpectedShutdown)
    )
}

data class MemoryData(val peakHeapMB: Double,
                      val peakNonHeapMB: Double) {
    constructor() : this(
            peakUseMB(MemoryType.HEAP),
            peakUseMB(MemoryType.NON_HEAP)
    )
}

fun peakUseMB(type: MemoryType) : Double =
        ManagementFactory.getMemoryPoolMXBeans()
                .filter { it.type == type }
                .map { it.peakUsage.used }
                .sum() / 1E6


data class ScenarioData(val populationSize: Int,
                        val nLinks: Int,
                        val nNodes: Int,
                        val nFacilities: Int,
                        val nTransitLines: Int,
                        val nTransitStops: Int) {
    constructor(scenario: Scenario) :
        this(scenario.population.persons.size,
                scenario.network.links.size,
                scenario.network.nodes.size,
                scenario.activityFacilities.facilities.size,
                scenario.transitSchedule.transitLines.size,
                scenario.transitSchedule.facilities.size)
}

data class MachineData(val osName: String,
                       val osArch: String,
                       val osVersion: String,
                       val jvmVendor: String,
                       val jvmVersion: String) {
    constructor() : this(
            System.getProperty("os.name"),
            System.getProperty("os.arch"),
            System.getProperty("os.version"),
            System.getProperty("java.vendor"),
            System.getProperty("java.version")
    )
}

// TODO: add stack trace if crash
// TODO: information on config parameters
data class MatsimRunData(val matsimVersion: String,
                         val guiceBindings: List<GuiceBindingData>,
                         val unexpectedShutdown: Boolean) {
    constructor(controller: MatsimServices, unexpectedShutdown: Boolean) : this(
            Gbl.getBuildInfoString(),
            // TODO: filter only MATSim classes?
            controller.injector.bindings.values.map(::GuiceBindingData),
            unexpectedShutdown
    )
}

data class GuiceBindingData(val key: String,
                            val annotation: String?,
                            val type: String,
                            val provider: String,
                            val source: String) {
    constructor(binding: Binding<*>) : this(
            binding.key.toString(),
            binding.key.annotation?.toString(),
            binding.key.typeLiteral.toString(),
            binding.provider.toString(),
            binding.source.toString()
    )
}

// TODO: information on input files
