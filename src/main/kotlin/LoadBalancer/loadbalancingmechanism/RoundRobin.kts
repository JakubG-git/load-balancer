package loadbalancer.loadbalancingmechanism

import loadbalancer.session.LoadBalancingSession

class RoundRobin<T> : LoadBalancingMechanism<T> {

    private var index = -1

    override fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T> {
        if (++index >= sessions.size) index = 0
        for (i in sessions.indices) {
            val session = sessions[index]
            if (session.status == LoadBalancingSession.Status.UP && session.isHealthy) {
                return session
            }
            if (++index >= sessions.size) index = 0
        }
        throw IllegalStateException("There is no active session")
    }
}
