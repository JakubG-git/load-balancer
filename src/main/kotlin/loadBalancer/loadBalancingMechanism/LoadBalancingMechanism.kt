package loadBalancer.loadBalancingMechanism

import loadBalancer.session.LoadBalancingSession

interface LoadBalancingMechanism<T> {
    fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T>
}