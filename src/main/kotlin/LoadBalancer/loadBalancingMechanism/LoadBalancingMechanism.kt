package LoadBalancer.loadBalancingMechanism

import LoadBalancer.Session.LoadBalancingSession

interface LoadBalancingMechanism<T> {
    @Throws(IllegalStateException::class)
    fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T>
}
