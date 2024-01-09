package loadbalancer.loadbalancingmechanism

import loadbalancer.session.LoadBalancingSession

class WeightedResponseTime<T> : LoadBalancingMechanism<T> {

    override fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T> {
        if (sessions.isEmpty()) throw IllegalStateException("No sessions available")

        return sessions.minByOrNull { it.getAverageResponseTime() ?: Double.MAX_VALUE }
            ?: throw IllegalStateException("No active session found")
    }
}
