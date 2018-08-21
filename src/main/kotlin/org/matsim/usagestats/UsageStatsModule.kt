package org.matsim.usagestats

import org.matsim.core.config.Config
import org.matsim.core.controler.AbstractModule
import org.matsim.usagestats.listeners.StatsListener


/**
 * @author thibautd
 */
class UsageStatsModule : AbstractModule() {
    override fun install() {
        addControlerListenerBinding().to(StatsListener::class.java)
    }
}