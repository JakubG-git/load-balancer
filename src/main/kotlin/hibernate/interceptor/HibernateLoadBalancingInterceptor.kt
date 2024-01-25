package hibernate.interceptor

import loadBalancer.interceptor.LoadBalancingInterceptor
import loadBalancer.LoadBalancer
import org.hibernate.CallbackException
import org.hibernate.Interceptor
import org.hibernate.Session
import org.hibernate.type.Type

class HibernateLoadBalancingInterceptor(loadBalancer: LoadBalancer<Session>) :
    LoadBalancingInterceptor<Session>(loadBalancer),
    Interceptor {
    @Throws(CallbackException::class)
    override fun onSave(
        entity: Any?,
        id: Any?,
        state: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?
    ): Boolean {
        return try {
            interceptOnSave(entity)
            false
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }

    @Throws(CallbackException::class)
    override fun onDelete(
        entity: Any?,
        id: Any?,
        state: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?) {
        try {
            interceptOnDelete(entity)
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }

    @Throws(CallbackException::class)
    override fun onFlushDirty(
        entity: Any?,
        id: Any?,
        currentState: Array<out Any>?,
        previousState: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?
    ): Boolean {
        return try {
            interceptOnUpdate(entity)
            false
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }
    @Throws(CallbackException::class)
    override fun onLoad(
        entity: Any?,
        id: Any?,
        state: Array<out Any>?,
        propertyNames: Array<out String>?,
        types: Array<out Type>?
    ): Boolean {
        return try {
            interceptOnLoad(entity)
            false
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }
}