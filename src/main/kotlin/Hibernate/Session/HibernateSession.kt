package Hibernate.Session

import Hibernate.Interceptor.HibernateLoadBalancingInterceptor
import LoadBalancer.Session.LoadBalancingSession
import jakarta.persistence.FlushModeType
import loadbalancer.dbrequest.DbRequest
import logging.DBLogger
import org.hibernate.Session
import org.hibernate.cfg.Configuration

class HibernateLoadBalancingSession(
    private val config: String,
    private val interceptor: HibernateLoadBalancingInterceptor,
    isPrimarySession: Boolean,
    sessionName: String?,
    delayMs: Int,
    logging: Boolean
) :
    LoadBalancingSession<Session?>(sessionName!!, isPrimarySession, delayMs.toLong(), logging) {
    private var interceptedSession: Session? = null
    private var session: Session? = null

    init {
        try {
            connect()
        } catch (exception: Exception) {
            if (logging) DBLogger.getLogger(javaClass)
                .warning("[HIBERNATE SESSION '" + getConnectionName() + "'] Could not create connection. Details: " + exception.message)
        }
        thread.start()
    }

    @Throws(Exception::class)
    override fun execute(request: DbRequest): Any? {
        return try {
            if (!session!!.getTransaction().isActive()) session!!.beginTransaction()
            when (request.getType()) {
                INSERT -> session!!.save(request.getObject())
                UPDATE -> session!!.update(request.getObject())
                DELETE -> session!!.delete(request.getObject())
                else -> throw UnsupportedOperationException(("Operation '" + request.getType()).toString() + "' is not supported")
            }
            session!!.getTransaction().commit()
            session!!.clear()
            null
        } catch (exception: UnsupportedOperationException) {
            if (logging) DBLogger.getLogger(javaClass)
                .warning("[HIBERNATE SESSION '" + getConnectionName() + "'] " + exception.message)
            throw exception
        } catch (exception: Exception) {
            if (logging) DBLogger.getLogger(javaClass)
                .warning("[HIBERNATE SESSION '" + getConnectionName() + "'] " + exception.message)
            setStatus(Status.DOWN)
            register(request)
            throw exception
        }
    }

    override fun isHealthy(): Boolean {
        return try {
            if (session!!.isOpen()) session!!.setFlushMode(FlushModeType.COMMIT)

            val resultFromSession: List<Int> = session!!.createNativeQuery(
                HEALTH_NATIVE_QUERY,
                Int::class.java
            ).list()
            session!!.setFlushMode(FlushModeType.AUTO)
            if (interceptedSession!!.isOpen()) interceptedSession!!.setFlushMode(FlushModeType.COMMIT)
            val resultFromInterceptedSession: List<Int> = interceptedSession!!.createNativeQuery(
                HEALTH_NATIVE_QUERY,
                Int::class.java
            ).list()
            interceptedSession!!.setFlushMode(FlushModeType.AUTO)
            resultFromSession[0] == 1 && resultFromInterceptedSession[0] == 1
        } catch (exception: Exception) {
            false
        }
    }

    override fun getConnection(): Session? {
        return interceptedSession
    }

    @Throws(Exception::class)
    override fun fix() {
        if (session != null) {
            session!!.getSessionFactory().close()
            session!!.close()
        }
        if (interceptedSession != null) {
            interceptedSession!!.getSessionFactory().close()
            interceptedSession!!.close()
        }
        connect()
        if (isHealthy()) commit() else throw IllegalStateException("Could not fix the session")
        setStatus(Status.UP)
    }

    @Throws(Exception::class)
    override fun close() {
        try {
            commit()
        } catch (ignore: IllegalStateException) {
        }
        super.close()
        session?.getSessionFactory()?.close()
        session?.close()
        interceptedSession?.getSessionFactory()?.close()
        interceptedSession?.close()
    }

    private fun connect() {
        session = Configuration().configure(config).buildSessionFactory().openSession()
        interceptedSession = Configuration().configure(config).buildSessionFactory().withOptions().interceptor(
            interceptor
        ).openSession()
    }

    companion object {
        private const val HEALTH_NATIVE_QUERY = "select 1"
    }
}