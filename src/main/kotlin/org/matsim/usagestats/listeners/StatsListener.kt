package org.matsim.usagestats.listeners

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.log4j.Logger
import org.matsim.core.controler.events.ShutdownEvent
import org.matsim.core.controler.listener.ShutdownListener
import org.matsim.usagestats.UsageStats
import org.matsim.usagestats.UsageStatsConfigGroup

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

            val json = jsonWriter.writeValueAsString(stats)

            val configGroup = event.services.config.getModule(UsageStatsConfigGroup.GROUP_NAME) as UsageStatsConfigGroup

            HttpClientBuilder.create().build().use {
                for (endpoint in configGroup.endpoints) {
                    val request = HttpPost(endpoint)
                    request.entity = StringEntity(json)
                    request.addHeader("content-type", "application/json")

                    val response = it.execute(request)
                    // be smarter with response?
                    log.debug("Transmitting usage data to $endpoint resulted in status ${response.statusLine?.statusCode}")
                }
            }
        }
        catch (t: Throwable) {
            log.debug("problem sending usage statistics.", t)
        }
    }

    companion object {
        private val log = Logger.getLogger(StatsListener::class.java)
    }
}