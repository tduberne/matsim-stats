package org.matsim.usagestats

import com.google.inject.Binding
import org.matsim.api.core.v01.Scenario
import org.matsim.core.controler.MatsimServices
import org.matsim.core.gbl.Gbl
import java.lang.management.ManagementFactory
import java.lang.management.MemoryType
import javax.persistence.*

@Entity
data class UsageStats(var memory: MemoryData = MemoryData(),
                      var scenario: ScenarioData = ScenarioData(),
                      var machine: MachineData = MachineData(),
                      var matsim: MatsimRunData = MatsimRunData()) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(memory = MemoryData())

    companion object {
        fun create(services: MatsimServices, unexpectedShutdown: Boolean) = UsageStats(
                MemoryData.create(),
                ScenarioData.create(services.scenario),
                MachineData.create(),
                MatsimRunData.create(services, unexpectedShutdown))
    }
}

@Entity
data class MemoryData(var peakHeapMB: Double? = null,
                      var peakNonHeapMB: Double? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(peakHeapMB = null)

    companion object {
        fun create() = MemoryData(
                peakUseMB(MemoryType.HEAP),
                peakUseMB(MemoryType.NON_HEAP))
    }
}

fun peakUseMB(type: MemoryType) : Double =
        ManagementFactory.getMemoryPoolMXBeans()
                .filter { it.type == type }
                .map { it.peakUsage.used }
                .sum() / 1E6


@Entity
data class ScenarioData(var populationSize: Int? = null,
                        var nLinks: Int? = null,
                        var nNodes: Int? = null,
                        var nFacilities: Int? = null,
                        var nTransitLines: Int? = null,
                        var nTransitStops: Int? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(populationSize = null)

    companion object {
        fun create(scenario: Scenario) =
                ScenarioData(scenario.population.persons.size,
                        scenario.network.links.size,
                        scenario.network.nodes.size,
                        scenario.activityFacilities.facilities.size,
                        scenario.transitSchedule.transitLines.size,
                        scenario.transitSchedule.facilities.size)
    }
}

@Entity
data class MachineData(var osName: String? = null,
                       var osArch: String? = null,
                       var osVersion: String? = null,
                       var jvmVendor: String? = null,
                       var jvmVersion: String? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(osName = null)

    companion object {
        fun create() = MachineData(
                System.getProperty("os.name"),
                System.getProperty("os.arch"),
                System.getProperty("os.version"),
                System.getProperty("java.vendor"),
                System.getProperty("java.version")
        )
    }
}

// TODO: add stack trace if crash
// TODO: information on config parameters
@Entity
data class MatsimRunData(var matsimVersion: String? = null,
                         @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true)
                         var guiceBindings: List<GuiceBindingData>? = null,
                         var unexpectedShutdown: Boolean? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(matsimVersion = null)

    companion object {
        fun create(controller: MatsimServices, unexpectedShutdown: Boolean) = MatsimRunData(
                Gbl.getBuildInfoString(),
                // TODO: filter only MATSim classes?
                controller.injector.bindings.values.map { GuiceBindingData.create(it) },
                unexpectedShutdown
        )
    }
}

@Entity
data class GuiceBindingData(var key: String? = null,
                            var annotation: String? = null,
                            var type: String? = null,
                            var provider: String? = null,
                            var source: String? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    lateinit var id: String

    constructor() : this(key = null)

    companion object {
        fun create(binding: Binding<*>) = GuiceBindingData(
                binding.key.toString(),
                binding.key.annotation?.toString(),
                binding.key.typeLiteral.toString(),
                binding.provider.toString(),
                binding.source.toString()
        )
    }
}

// TODO: information on input files
