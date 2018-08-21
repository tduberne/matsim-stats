package org.matsim.usagestats

import org.apache.log4j.Logger
import org.matsim.core.utils.io.IOUtils
import java.io.File
import java.net.URL
import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamReader
import javax.xml.stream.events.XMLEvent

val log = Logger.getLogger("org.matsim.usagestats")

/**
 * @author thibautd
 */
fun identifyFileFormat(path: String?): String? {
    if (path == null) return null

    return identifyFileFormat(File(path).toURI().toURL())
}

fun identifyFileFormat(path: Lazy<URL?>): String? {
    try {
        return identifyFileFormat(path.value)
    }
    catch (e: NullPointerException) {}
    return null
}

fun identifyFileFormat(path: URL?): String? {
    if(path == null) return null

    log.debug("identifying file format for file $path")

    try {
        val xmlReader = XMLInputFactory.newInstance()!!.createXMLStreamReader(IOUtils.getInputStream(path))
        val dtdRegex = Regex("[^/\b]*\\.dtd")

        do {
            if (xmlReader.eventType == XMLEvent.DTD) {
                return dtdRegex.find(xmlReader.text)?.value
            }
        } while (xmlReader.next() != XMLStreamReader.END_DOCUMENT)
    }
    catch (e: Exception) {
        log.debug("Error while identifying dtd of file $path", e)
    }

    return null
}