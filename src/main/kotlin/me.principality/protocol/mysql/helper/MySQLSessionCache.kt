package me.principality.protocol.mysql.helper

import com.google.common.cache.CacheBuilder

object MySQLSessionCache {

    private val connectionCache = CacheBuilder.newBuilder().build<String, Int>()

    /**
     * Put connection id by channel id.
     *
     * @param channelId    NetSocket channel id
     * @param connectionId MySQL connection id
     */
    fun putConnection(channelId: String, connectionId: Int) {
        connectionCache.put(channelId, connectionId)
    }

    /**
     * Get connection id by channel id.
     *
     * @param channelId NetSocket channel id
     * @return connectionId MySQL connection id
     */
    fun getConnection(channelId: String): Int {
        return connectionCache.getIfPresent(channelId)!!
    }
}