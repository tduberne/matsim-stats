package org.matsim.usagestats

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.inject.Binding
import org.matsim.api.core.v01.Scenario
import org.matsim.core.config.Config
import org.matsim.core.controler.MatsimServices
import org.matsim.core.gbl.Gbl
import java.lang.management.ManagementFactory
import java.lang.management.MemoryType
import java.util.*
import javax.persistence.*

// all classes are "embeddable", to allow including them in records with more information on server side if needed

@Embeddable
data class UsageStats(@Embedded
                      var memory: MemoryData = MemoryData(),
                      @Embedded
                      var scenario: ScenarioData = ScenarioData(),
                      @Embedded
                      var machine: MachineData = MachineData(),
                      @Embedded
                      var matsim: MatsimRunData = MatsimRunData(),
                      @Embedded
                      var files: FileVersionsData = FileVersionsData()) {
    constructor() : this(memory = MemoryData())

    companion object {
        fun create(services: MatsimServices, unexpectedShutdown: Boolean) = UsageStats(
                MemoryData.create(),
                ScenarioData.create(services.scenario),
                MachineData.create(),
                MatsimRunData.create(services, unexpectedShutdown),
                FileVersionsData.create(services.config))
    }
}

@Embeddable
data class FileVersionsData(var configFileVersion: String?,
                            var populationFileVersion: String? = null,
                            var networkFileVersion: String? = null,
                            var facilitiesFileVersion: String? = null,
                            var transitScheduleFileVersion: String? = null,
                            var transitVehiclesFileVersion: String? = null) {
    constructor() : this(null)

    companion object {
        fun create(config: Config) = FileVersionsData(
                identifyFileFormat(config.context),
                identifyFileFormat(lazy { config.plans().getInputFileURL(config.context) }),
                identifyFileFormat(lazy { config.network().getInputFileURL(config.context) }),
                identifyFileFormat(lazy { config.facilities().getInputFileURL(config.context) }),
                identifyFileFormat(lazy { config.transit().getTransitScheduleFileURL(config.context) }),
                identifyFileFormat(lazy { config.transit().getVehiclesFileURL(config.context) } ))
    }
}

@Embeddable
data class MemoryData(var peakHeapMB: Double? = null,
                      var peakNonHeapMB: Double? = null) {
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


@Embeddable
data class ScenarioData(var populationSize: Int? = null,
                        var nLinks: Int? = null,
                        var nNodes: Int? = null,
                        var nFacilities: Int? = null,
                        var nTransitLines: Int? = null,
                        var nTransitStops: Int? = null) {
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

@Embeddable
data class MachineData(var osName: String? = null,
                       var osArch: String? = null,
                       var osVersion: String? = null,
                       var jvmVendor: String? = null,
                       var jvmVersion: String? = null) {
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
@Embeddable
data class MatsimRunData(var matsimVersion: String? = null,
                         @OneToMany(cascade = [CascadeType.ALL],
                                 orphanRemoval = true,
                                 fetch = FetchType.EAGER)
                         @JoinColumn(name="usage_data_id")
                         var guiceBindings: List<GuiceBindingData> = emptyList(),
                         var unexpectedShutdown: Boolean? = null) {
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

// define column type as being "text" to allow for arbitrary length. Might be hibernate specific, but Strings are by
// default persisted in columns of type varchar(255) (up to 255 characters)
@Entity
data class GuiceBindingData(@Column(columnDefinition="text")
                            var key: String? = null,
                            @Column(columnDefinition="text")
                            var annotation: String? = null,
                            @Column(columnDefinition="text")
                            var type: String? = null,
                            @Column(columnDefinition="text")
                            var provider: String? = null,
                            @Column(columnDefinition="text")
                            var source: String? = null) {
    // not part of automatic equals method etc.
    // This is what we want.
    @Id @GeneratedValue @JsonIgnore
    var id: UUID? = null

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
