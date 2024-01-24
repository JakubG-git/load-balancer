package hibernate.session

import hibernate.interceptor.HibernateLoadBalancingInterceptor
import loadBalancer.request.Request
import loadBalancer.session.LoadBalancingSession
import jakarta.persistence.FlushModeType
import org.hibernate.Session
import org.hibernate.cfg.Configuration
import java.util.logging.Logger

class HibernateLoadBalancingSession(
    private val config: String,
    private val interceptor: HibernateLoadBalancingInterceptor,
    isPrimarySession: Boolean,
    sessionName: String?,
    delayMs: Int,
    logging: Boolean
) :
    LoadBalancingSession<Session>(sessionName!!, isPrimarySession, delayMs.toLong(), logging) {
    private lateinit var interceptedSession: Session
    private lateinit var session: Session
    private val log: Logger = Logger.getLogger(this.javaClass.name)

    init {
        try {
            connect()
        } catch (exception: Exception) {
            if (logging)
                log.warning(
                    "[HIBERNATE SESSION '${getConnectionName()}'] Could not create connection." +
                            "Details: ${exception.message}"
                )
        }
        thread.start()
    }

    @Throws(Exception::class)
    override fun execute(request: Request): Any? {
        return try {
            if (!session.transaction.isActive)
                session.beginTransaction()
            when (request.type) {
                Request.Type.INSERT -> session.persist(request.obj)
                Request.Type.UPDATE -> session.merge(request.obj)
                Request.Type.DELETE -> session.remove(request.obj)
                else -> throw UnsupportedOperationException("Operation ${request.type} is not supported")
            }
            session.transaction.commit()
            session.clear()
            null
        } catch (exception: UnsupportedOperationException) {
            if (isLogging)
                log.warning("[HIBERNATE SESSION '${getConnectionName()}'] ${exception.message}")
            throw exception
        } catch (exception: Exception) {
            if (isLogging)
                log.warning("[HIBERNATE SESSION '${getConnectionName()}'] ${exception.message}")
            status = Status.DOWN
            register(request)
            throw exception
        }
    }

    override fun isHealthy(): Boolean {
        return try {
            if (session.isOpen)
                session.flushMode = FlushModeType.COMMIT

            val resultFromSession = session.createNativeQuery(
                HEALTH_NATIVE_QUERY,
                Int::class.java
            ).resultList

            session.flushMode = FlushModeType.AUTO

            if (interceptedSession.isOpen)
                interceptedSession.flushMode = FlushModeType.COMMIT

            val resultFromInterceptedSession = interceptedSession.createNativeQuery(
                HEALTH_NATIVE_QUERY,
                Int::class.java
            ).resultList

            interceptedSession.flushMode = FlushModeType.AUTO

            resultFromSession[0] == 1 && resultFromInterceptedSession[0] == 1
        } catch (exception: Exception) {
            false
        }
    }

    override fun getConnection(): Session {
        return interceptedSession
    }

    @Throws(Exception::class)
    override fun fix() {
        session.sessionFactory.close()
        session.close()
        interceptedSession.sessionFactory.close()
        interceptedSession.close()
        connect()
        if (isHealthy()){
            commit()
        }

        else throw IllegalStateException("Could not fix the session")
        status = Status.UP
    }

    @Throws(Exception::class)
    override fun close() {
        try {
            commit()
        } catch (ignore: IllegalStateException) {
        }
        super.close()
        session.sessionFactory?.close()
        session.close()
        interceptedSession.sessionFactory?.close()
        interceptedSession.close()
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