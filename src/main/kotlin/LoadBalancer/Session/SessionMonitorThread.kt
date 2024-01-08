package LoadBalancer.Session

import LoadBalancer.Session.LoadBalancingSession
import logging.DBLogger

class SessionMonitorThread<T>(private val session: LoadBalancingSession<T>, private val delayMs: Long) : Thread(),
    Monitor<LoadBalancingSession<T>?> {
    private var running = false
    var isLogging = false

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
                if (isLogging) DBLogger.getLogger(javaClass)
                    .info("[SESSION '" + session.getConnectionName() + "']" + " Not healthy. Fix attempt")
                session.setStatus(LoadBalancingSession.Status.DOWN)
                session.fix()
            }
        } catch (ignore: Exception) {
        }
    }
}