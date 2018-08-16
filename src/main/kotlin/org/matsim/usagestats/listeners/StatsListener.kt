package org.matsim.usagestats.listeners

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.log4j.Logger
import org.matsim.core.controler.events.ShutdownEvent
import org.matsim.core.controler.listener.ShutdownListener
import org.matsim.usagestats.UsageStats

/**
 * @author thibautd
 */
class StatsListener: ShutdownListener {
    override fun notifyShutdown(event: ShutdownEvent?) {
        try {
            val stats = UsageStats.create(event!!.services, event.isUnexpected)

            // TODO: POST and write to file instead of to screen
            val jsonWriter = jacksonObjectMapper()
                    .setDefaultPropertyInclusion(JsonInclude.Include.NON_EMPTY)
                    .writerWithDefaultPrettyPrinter()

            println(jsonWriter.writeValueAsString(stats))
        }
        catch (t: Throwable) {
            log.debug("problem sending usage statistics.", t)
        }
    }

    companion object {
        private val log = Logger.getLogger(StatsListener::class.java)
    }
}