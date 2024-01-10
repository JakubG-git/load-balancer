package loadBalancer.session

import loadBalancer.request.Request

interface UnitOfWork {
    @Throws(IllegalStateException::class)
    fun register(value: Request)

    @Throws(IllegalStateException::class)
    fun commit()

}