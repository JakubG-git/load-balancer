package LoggerDB

import java.util.*
import java.util.logging.ConsoleHandler
import java.util.logging.Logger

object LoggerDB {
    private val LOGGERS: MutableMap<Class<*>, Logger?> = HashMap()
    fun getLogger(c: Class<*>): Logger? {
        var logger = LOGGERS[c]
        if (logger != null) return logger
        val handler = ConsoleHandler()
        handler.formatter = FormatterDB()
        logger = Logger.getLogger(c.name)
        logger.useParentHandlers = false
        logger.addHandler(handler)
        LOGGERS.put(c, logger)
        return logger
    }
}