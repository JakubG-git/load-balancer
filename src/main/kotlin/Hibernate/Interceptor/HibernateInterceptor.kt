package Hibernate.Interceptor

import LoadBalancer.Interceptor.LoadBalancingInterceptor
import LoadBalancer.LoadBalancer
import org.hibernate.CallbackException
import org.hibernate.Interceptor
import org.hibernate.Session
import org.hibernate.type.Type

class HibernateLoadBalancingInterceptor(loadBalancer: LoadBalancer<Session?>?) :
    LoadBalancingInterceptor<Session?>(loadBalancer),
    Interceptor {
    @Throws(CallbackException::class)
    fun onSave(
        entity: Any?,
        id: Any?,
        state: Array<Any?>?,
        propertyNames: Array<String?>?,
        types: Array<Type?>?
    ): Boolean {
        return try {
            interceptOnSave(entity)
            false
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }

    @Throws(CallbackException::class)
    fun onDelete(entity: Any?, id: Any?, state: Array<Any?>?, propertyNames: Array<String?>?, types: Array<Type?>?) {
        try {
            interceptOnDelete(entity)
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }

    @Throws(CallbackException::class)
    fun onFlushDirty(
        entity: Any?,
        id: Any?,
        currentState: Array<Any?>?,
        previousState: Array<Any?>?,
        propertyNames: Array<String?>?,
        types: Array<Type?>?
    ): Boolean {
        return try {
            interceptOnUpdate(entity)
            false
        } catch (exception: IllegalStateException) {
            throw CallbackException(exception.message)
        }
    }
}