package hibernate

import hibernate.interceptor.HibernateLoadBalancingInterceptor
import hibernate.session.HibernateLoadBalancingSession
import loadBalancer.LoadBalancer
import loadBalancer.loadBalancingMechanism.LoadBalancingMechanism
import loadBalancer.request.Request
import loadBalancer.session.LoadBalancingSession

import org.hibernate.Session
import java.util.logging.Logger

class HibernateLoadBalancer(
    configs: List<String>,
    loadBalancingMechanism: LoadBalancingMechanism<Session>,
    logging: Boolean = true,
    monitorDelayMs: Int = 3000
) :
    LoadBalancer<Session>(loadBalancingMechanism, logging) {
    private var primarySession: LoadBalancingSession<Session>? = null;
    private val log: Logger = Logger.getLogger(this.javaClass.name)

    init {
        val interceptor = HibernateLoadBalancingInterceptor(this)
        for ((ix, config) in configs.withIndex()) {
            sessions.add(HibernateLoadBalancingSession(
                config,
                interceptor,
                false,
                "db_$ix",
                monitorDelayMs,
                logging
            ))
        }


        if (sessions.isEmpty())
            throw Exception("No valid sessions")
        if (logging)
            log.info("[HIBERNATE LOAD BALANCER] Created load balancer with ${sessions.size} sessions")
    }

    override fun redirect(request: Request): Any? {
        if (!primarySession!!.isHealthy())
            throw IllegalStateException("The primary session is not healthy")

        var result: Any? = null
        for (session in sessions) {
            try {
                if (session.isPrimaryConnection) continue;
                result = session.execute(request)
            } catch (ignored: Exception) {
            }
        }
        return result
    }
    override fun connection(): Session {
        if (primarySession == null) {
            primarySession = loadBalancingMechanism.get(sessions)
            primarySession!!.isPrimaryConnection = true
            if (isLogging)
                log.info("[HIBERNATE LOAD BALANCER] Chosen session: '${primarySession!!.getConnectionName()}'")
            return primarySession!!.getConnection()
        }
        return primarySession!!.getConnection()
    }

}