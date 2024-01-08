package LoadBalancer.Session

interface Monitor<T> {
    @Throws(IllegalStateException::class)
    fun watch(`object`: T)
}