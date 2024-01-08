package LoadBalancer.Session

import LoadBalancer.dbrequest.DbRequest

interface UnitOfWork {
    @Throws(IllegalStateException::class)
    fun register(value: DbRequest?)

    @Throws(IllegalStateException::class)
    fun commit()
}