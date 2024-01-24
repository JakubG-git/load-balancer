package loadBalancer.loadBalancingMechanism

import loadBalancer.session.LoadBalancingSession

class WeightedResponseTime<T> : LoadBalancingMechanism<T> {
    override fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T> {
        if (sessions.isEmpty()) {
            throw IllegalStateException("No sessions available")
        }

        var minSession: LoadBalancingSession<T>? = null
        var minTime = Double.MAX_VALUE

        for (session in sessions) {
            val averageTime = session.getAverageResponseTime() ?: Double.MAX_VALUE
            if (averageTime < minTime) {
                minTime = averageTime
                minSession = session
            }
        }

        return minSession ?: throw IllegalStateException("No active session found")
    }
}
