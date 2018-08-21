package org.matsim.usagestats

import org.matsim.core.config.ReflectiveConfigGroup
import org.matsim.core.utils.collections.CollectionUtils

class UsageStatsConfigGroup : ReflectiveConfigGroup(GROUP_NAME) {
    val endpoints: MutableSet<String> = mutableSetOf("http://ivt-mokil.ethz.ch/api/data")

    fun addEndpoint(url: String) {
        endpoints.add(url)
    }

    @StringGetter("endpoints")
    private fun getEndpointsString(): String = CollectionUtils.setToString(endpoints)

    @StringSetter("endpoints")
    private fun setEndpointsString(v: String) {
        endpoints.clear()
        endpoints.addAll(CollectionUtils.stringToSet(v))
    }

    companion object {
        val GROUP_NAME = "usageStats"
    }
}