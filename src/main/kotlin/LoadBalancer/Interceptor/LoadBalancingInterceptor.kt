package LoadBalancer.Interceptor


import LoadBalancer.LoadBalancer
import LoadBalancer.dbrequest.DbRequest


abstract class LoadBalancingInterceptor<T>(loadBalancer: LoadBalancer<T>) {
    protected val loadBalancer: LoadBalancer<T>

    init {
        this.loadBalancer = loadBalancer
    }

    @Throws(IllegalStateException::class)
    protected fun interceptOnSave(`object`: Any?) {
        loadBalancer.redirect(DbRequest(`object`, DbRequest.Type.INSERT))
    }

    @Throws(IllegalStateException::class)
    protected fun interceptOnDelete(`object`: Any?) {
        loadBalancer.redirect(DbRequest(`object`, DbRequest.Type.DELETE))
    }

    @Throws(IllegalStateException::class)
    protected fun interceptOnUpdate(`object`: Any?) {
        loadBalancer.redirect(DbRequest(`object`, DbRequest.Type.UPDATE))
    }

    @Throws(IllegalStateException::class)
    protected fun interceptOnLoad(`object`: Any?): Any {
        return loadBalancer.redirect(DbRequest(`object`, DbRequest.Type.SELECT))
    }
}