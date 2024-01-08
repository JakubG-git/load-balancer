package Hibernate

import Hibernate.Interceptor.HibernateLoadBalancingInterceptor
import Hibernate.Session.HibernateLoadBalancingSession
import LoadBalancer.LoadBalancer
import LoadBalancer.Session.LoadBalancingSession
import loadbalancer.dbrequest.DbRequest
import loadbalancer.loadbalancingmechanism.LoadBalancingMechanism
import loadbalancer.loadbalancingmechanism.RoundRobin
import logging.DBLogger
import org.hibernate.Session

class HibernateLoadBalancer @JvmOverloads constructor(
    configs: List<String?>,
    loadBalancingMechanism: LoadBalancingMechanism<Session?>? = RoundRobin(),
    logging: Boolean = true,
    monitorDelayMs: Int = 3000
) :
    LoadBalancer<Session?>(loadBalancingMechanism, logging) {
    private var primarySession: LoadBalancingSession<Session>?

    init {
        // set up load balancing mechanism
        this.sessions = ArrayList<Any>()
        primarySession = null

        // create sessions
        var index = 1
        val interceptor = HibernateLoadBalancingInterceptor(this)
        for (config in configs) {
            sessions.add(
                HibernateLoadBalancingSession(
                    config!!,
                    interceptor,
                    false,
                    "db_$index",
                    monitorDelayMs,
                    logging
                )
            )
            ++index
        }

        // verify load balancer state
        if (sessions.isEmpty()) throw Exception("No valid session detected")
        if (logging) DBLogger.getLogger(javaClass)
            .info(("[HIBERNATE LOAD BALANCER ROOT] Created load balancer with '" + sessions.size()).toString() + "' sessions.")
    }

    @Throws(IllegalStateException::class)
    fun redirect(request: DbRequest?): Any? {
        check(primarySession!!.isHealthy()) { "The session marked as the primary session is not healthy. Cannot execute any requests to not loose any data" }
        var result: Any? = null
        for (session in sessions) {
            try {
                if (session.isPrimaryConnection()) continue
                result = session.execute(request)
            } catch (ignore: Exception) {
            }
        }
        return result
    }

    fun connection(): Session {
        if (primarySession != null) primarySession!!.setPrimaryConnection(false)
        primarySession = loadBalancingMechanism.get(sessions)
        primarySession!!.setPrimaryConnection(true)
        primarySession!!.getConnection().clear()
        if (logging) DBLogger.getLogger(javaClass)
            .info("[HIBERNATE LOAD BALANCER ROOT] Chosen session: '" + primarySession!!.getConnectionName() + "'")
        return primarySession!!.getConnection()
    }
}