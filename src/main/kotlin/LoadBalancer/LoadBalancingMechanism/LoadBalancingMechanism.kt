package LoadBalancer.LoadBalancingMechanism

import LoadBalancer.Session.LoadBalancingSession

interface LoadBalancingMechanism<T> {
    fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T>
}