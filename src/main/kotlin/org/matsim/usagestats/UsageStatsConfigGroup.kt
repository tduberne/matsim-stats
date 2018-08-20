package org.matsim.usagestats

import org.matsim.core.config.ReflectiveConfigGroup

class UsageStatsConfigGroup : ReflectiveConfigGroup(GROUP_NAME) {
    @get:StringGetter("endpoints")
    @set:StringSetter("endpoints")
    var endpoints: List<String> = listOf("http://ivt-molik.ethz.ch/api/data")

    companion object {
        val GROUP_NAME = "usageStats"
    }
}