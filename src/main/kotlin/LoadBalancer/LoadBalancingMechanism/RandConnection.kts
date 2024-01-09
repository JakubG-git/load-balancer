package Loadbalancer.loadbalancingmechanism

import Loadbalancer.session.LoadBalancingSession
import java.util.Random

class RandConnection<T> : LoadBalancingMechanism<T> {

    companion object {
        private val RANDOM = Random()
        var ATTEMPTS_BEFORE_EXCEPTION = 10
    }

    override fun get(loadBalancingSessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T> {
        var attempt = ATTEMPTS_BEFORE_EXCEPTION
        do {
            val session = loadBalancingSessions[RANDOM.nextInt(loadBalancingSessions.size)]
            if (session.status == LoadBalancingSession.Status.UP && session.isHealthy) {
                return session
            }
        } while (attempt-- > 0)
        throw IllegalStateException("There is no active session")
    }
}
