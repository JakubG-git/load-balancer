package loadbalancer.loadbalancingmechanism

import loadbalancer.session.LoadBalancingSession

interface LoadBalancingMechanism<T> {
    @Throws(IllegalStateException::class)
    fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T>
}
