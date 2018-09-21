package me.principality.ktsql.protocol.mysql.helper

object ConnectionIdGenerator {
    private var currentId: Int = 0

    /**
     * Get next connection id.
     *
     * @return next connection id
     */
    @Synchronized
    fun nextId(): Int {
        if (currentId >= Integer.MAX_VALUE) {
            currentId = 0
        }
        return ++currentId
    }
}