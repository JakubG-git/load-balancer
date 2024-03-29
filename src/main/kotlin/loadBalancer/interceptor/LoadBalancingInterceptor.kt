package loadBalancer.interceptor

import loadBalancer.LoadBalancer
import loadBalancer.request.Request

abstract class LoadBalancingInterceptor<T>(private val loadBalancer: LoadBalancer<T>) {
    protected fun interceptOnSave(obj: Any?) {
        loadBalancer.redirect(Request(obj, Request.Type.INSERT))
    }
    protected fun interceptOnDelete(obj: Any?) {
        loadBalancer.redirect(Request(obj, Request.Type.DELETE))
    }
    protected fun interceptOnUpdate(obj: Any?) {
        loadBalancer.redirect(Request(obj, Request.Type.UPDATE))
    }
    protected fun interceptOnLoad(obj: Any?) {
        loadBalancer.redirect(Request(obj, Request.Type.SELECT))
    }
}