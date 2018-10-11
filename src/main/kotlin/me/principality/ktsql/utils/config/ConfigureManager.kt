package me.principality.ktsql.utils.config

import java.util.*

// TODO rename? put data to zookeeper?
object ConfigureManager {
    val info = Properties()

    init {
        info.put("model",
                ("inline:"
                        + "{\n"
                        + "  version: '1.0',\n"
                        + "   schemas: [\n"
                        + "     {\n"
                        + "       type: 'custom',\n"
                        + "       name: 'hbase',\n"
                        + "       factory: 'me.principality.ktsql.backend.hbase.HBaseSchemaFactory',\n"
                        + "       operand: {\n"
                        + "         flavor: 'SCANNABLE',\n"
                        + "         zkquorum: '127.0.0.1:2222'\n"
                        + "       }\n"
                        + "     }\n"
                        + "   ]\n"
                        + "}"))
    }

    fun getLoginAuthority(): LoginAuthority {
        return LoginAuthority("user", "pass") //TODO add authority implement
    }

    fun getCalciteConfig(): Properties {
        return info
    }
}