package LoadBalancer

import LoadBalancer.LoadBalancingMechanism.LoadBalancingMechanism
import LoadBalancer.Request.Request
import LoadBalancer.Session.LoadBalancingSession
import java.util.logging.Logger

abstract class LoadBalancer<T>(
    protected var loadBalancingMechanism: LoadBalancingMechanism<T>,
    logging: Boolean) : AutoCloseable {
    protected val sessions: MutableList<LoadBalancingSession<T>> = mutableListOf()
    private val log: Logger = Logger.getLogger(this.javaClass.name)
    var isLogging: Boolean = logging
        set(value) {
            field = value
            for (session in sessions) {
                session.isLogging = value
            }
        }
    open fun redirect(request: Request): Any? {
        if (request.type == Request.Type.SELECT) {
            while (true) {
                try {
                    val session = loadBalancingMechanism.get(sessions)
                    return session.execute(request)
                } catch (e: Exception) {
                    if (isLogging)
                        log.warning("[LOAD BALANCER] ${e.message}")
                }
            }
        }

        var result: Any? = null

        for (session in sessions) {
            try {
                result = session.execute(request)
            } catch (e: Exception) {
                if (isLogging)
                    log.warning("[LOAD BALANCER] ${e.message}")
            }
        }

        return result
    }

    override fun close() {
        for (session in sessions)
            session.close()
    }

    abstract fun connection(): T
}