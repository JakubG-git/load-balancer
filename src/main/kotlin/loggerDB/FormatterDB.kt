package loggerDB

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.Formatter
import java.util.logging.LogRecord

class FormatterDB : Formatter() {
    override fun format(record: LogRecord): String {
        val format = StringBuilder()
        format.append(
            when (record.level.toString()) {
                "INFO" -> "\u001B[32m"
                "WARNING", "SEVERE" -> "\u001B[31m"
                else -> "\u001B[37m"
            }
        )
        format.append("[").append(record.level.toString()).append(' ').append(df.format(Date(record.millis)))
            .append("] ")
        format.append("[").append(record.sourceClassName).append('.').append(record.sourceMethodName).append("]\n")
        format.append(record.message).append("\n\n").append("\u001B[0m")
        return format.toString()
    }

    companion object {
        private val df: DateFormat = SimpleDateFormat("hh:mm:ss")
    }
}