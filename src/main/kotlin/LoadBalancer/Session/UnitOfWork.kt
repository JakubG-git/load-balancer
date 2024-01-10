package LoadBalancer.Session

import LoadBalancer.Request.Request

interface UnitOfWork {
    @Throws(IllegalStateException::class)
    fun register(value: Request)

    @Throws(IllegalStateException::class)
    fun commit()
}