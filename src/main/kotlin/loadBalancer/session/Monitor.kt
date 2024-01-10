package loadBalancer.session

interface Monitor<T> {
    @Throws(IllegalStateException::class)
    fun watch(`object`: T)
}