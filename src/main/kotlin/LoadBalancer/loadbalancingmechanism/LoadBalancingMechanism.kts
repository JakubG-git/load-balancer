package Loadbalancer.loadbalancingmechanism

import Loadbalancer.session.LoadBalancingSession

interface LoadBalancingMechanism<T> {
    @Throws(IllegalStateException::class)
    fun get(sessions: List<LoadBalancingSession<T>>): LoadBalancingSession<T>
}
