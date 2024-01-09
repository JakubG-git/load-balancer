package LoadBalancer.Session

import LoggerDB.LoggerDB
import java.util.logging.Logger

class SessionMonitorThread<T>(private val session: LoadBalancingSession<T>, private val delayMs: Long) : Thread(),
    Monitor<LoadBalancingSession<T>?> {
    private var running = false
    var isLogging = false
    private val logger: Logger? = LoggerDB.getLogger(SessionMonitorThread::class.java)

    fun disable() {
        running = false
        interrupt()
    }

    override fun run() {
        try {
            running = true
            while (running) {
                watch(session)
                sleep(delayMs)
            }
        } catch (ignore: InterruptedException) {
        }
    }

    @Throws(IllegalStateException::class)
    override fun watch(`object`: LoadBalancingSession<T>?) {
        try {
            if (session.isHealthy()) {
                if (session.getStatus() === LoadBalancingSession.Status.DOWN) session.commit()
                session.setStatus(LoadBalancingSession.Status.UP)
            } else {
                if (isLogging) logger?.info("[SESSION '${session.getConnectionName()}'] Not healthy. Fix attempt")
                session.setStatus(LoadBalancingSession.Status.DOWN)
                session.fix()
            }
        } catch (ignore: Exception) {
        }
    }
}
