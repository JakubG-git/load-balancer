package LoadBalancer.Session

import LoadBalancer.dbrequest.DbRequest
import logging.LoggerDB
import java.util.LinkedList

abstract class LoadBalancingSession<T> : UnitOfWork, AutoCloseable {

    enum class Status {
        UP, DOWN
    }

    private val connectionName: String
    private var isPrimaryConnection: Boolean
    private var status: Status
    private val queue: LinkedList<DbRequest>
    protected val thread: SessionMonitorThread<T>
    protected var logging: Boolean

    constructor(connectionName: String, isPrimaryConnection: Boolean, delayMs: Long, logging: Boolean) {
        this.connectionName = connectionName
        this.isPrimaryConnection = isPrimaryConnection
        this.status = Status.DOWN
        this.queue = LinkedList()
        this.thread = SessionMonitorThread(this, delayMs)
        this.logging = logging
        this.thread.setLogging(logging)
    }

    fun getConnectionName(): String {
        return connectionName
    }

    fun isPrimaryConnection(): Boolean {
        return isPrimaryConnection
    }

    fun setPrimaryConnection(primaryConnection: Boolean) {
        isPrimaryConnection = primaryConnection
    }

    fun getStatus(): Status {
        return status
    }

    fun setStatus(status: Status) {
        this.status = status
    }

    fun isLogging(): Boolean {
        return logging
    }

    fun setLogging(logging: Boolean) {
        this.logging = logging
        this.thread.setLogging(logging)
    }

    override fun register(value: DbRequest) {
        queue.add(value)
    }

    override fun commit() {
        if (logging)
            LoggerDB.getLogger(javaClass).info("[SESSION '$connectionName'] Commit called with '${queue.size}' DBRequests")
        while (!queue.isEmpty()) {
            val request = queue.remove()
            try {
                execute(request)
            } catch (exception: Exception) {
                if (logging)
                    LoggerDB.getLogger(javaClass).warning("[SESSION '$connectionName'] ${exception.message}")
                queue.push(request)
                throw IllegalStateException("Could not execute request. Details: ${exception.message}")
            }
        }
    }

    override fun close() {
        thread.disable()
        thread.join()
    }

    abstract fun getConnection(): T

    abstract fun execute(request: DbRequest): Any

    abstract fun isHealthy(): Boolean

    abstract fun fix()
}
