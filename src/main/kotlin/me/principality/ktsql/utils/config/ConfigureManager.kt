package me.principality.ktsql.utils.config

import java.util.*

// TODO rename?
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
                        + "       factory: 'me.principality.backend.hbase.HBaseSchemaFactory',\n"
                        + "       operand: {\n"
                        + "         directory: '/does/not/exist'\n"
                        + "       }\n"
                        + "     }\n"
                        + "   ]\n"
                        + "}"))
    }

    fun getLoginAuthority(): LoginAuthority {
        return LoginAuthority("user", "pass") //TODO fix it
    }

    fun getCalciteConfig(): Properties {
        return info
    }
}