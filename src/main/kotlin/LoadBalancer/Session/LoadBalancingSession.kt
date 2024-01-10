package LoadBalancer.Session

import LoadBalancer.Request.Request
import java.util.logging.Logger

abstract class LoadBalancingSession<T>(
    private val connectionName: String,
    var isPrimaryConnection: Boolean,
    delayMs: Long,
    logging: Boolean
) : UnitOfWork, AutoCloseable {

    enum class Status {
        UP, DOWN
    }

    var status: Status = Status.DOWN
    private val queue: MutableList<Request> = mutableListOf()
    var isLogging: Boolean = logging
        set(value) {
            field = value
            thread.isLogging = value
        }
    protected val thread: SessionMonitorThread<T> = SessionMonitorThread(this, delayMs)
    private val log: Logger = Logger.getLogger(this.javaClass.name)

    init {
        this.thread.isLogging = logging
    }

    fun getConnectionName(): String {
        return connectionName
    }

    override fun register(value: Request) {
        queue.add(value)
    }

    override fun commit() {
        if (isLogging)
            log.info("[SESSION '$connectionName'] Commit called with '${queue.size}' DBRequests")
        while (queue.isNotEmpty()) {
            val request = queue.removeFirst()
            try {
                execute(request)
            } catch (exception: Exception) {
                if (isLogging)
                    log.warning("[SESSION '$connectionName'] ${exception.message}")
                queue.add(request)
                throw IllegalStateException("Could not execute request. Details: ${exception.message}")
            }
        }
    }

    override fun close() {
        thread.disable()
        thread.join()
    }

    abstract fun getConnection(): T

    abstract fun execute(request: Request): Any?

    abstract fun isHealthy(): Boolean

    abstract fun fix()
}
