package net.pelsmaeker.katerm.generator.diagnostics

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class LoggingMessageCollector(
    private val logger: Logger = LoggerFactory.getLogger(LoggingMessageCollector::class.java)
) : MessageCollector {
    override fun offer(message: Message): Boolean {
        when (message.severity) {
            MessageSeverity.FATAL -> logger.error(message.text)
            MessageSeverity.ERROR -> logger.error(message.text)
            MessageSeverity.WARNING -> logger.warn(message.text)
            MessageSeverity.INFO -> logger.info(message.text)
            MessageSeverity.HINT -> logger.debug(message.text)
        }
        return true
    }
}
