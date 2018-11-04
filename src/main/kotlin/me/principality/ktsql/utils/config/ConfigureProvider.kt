package me.principality.ktsql.utils.config

import com.typesafe.config.ConfigFactory
import java.util.*

// TODO put data to distributed configure center?
object ConfigureProvider {
    private val info = Properties()
    private val config = ConfigFactory.load()
    private val loginAuth: LoginAuthority by lazy {
        val user = config.getString("user")
        val pass = config.getString("pass")
        LoginAuthority(user, pass)
    }

    init {
        val flavor = config.getString("flavor")
        val zkquorum = config.getString("zkquorum")

        info.put("model",
                ("inline:"
                        + "{\n"
                        + "  version: '1.0',\n"
                        + "  defaultSchema: 'HBASE'," // 注释掉这行，CreateTable强制创建MutableArrayTable
                        + "   schemas: [\n"
                        + "     {\n"
                        + "       type: 'custom',\n"
                        + "       name: 'HBASE',\n"
                        + "       factory: 'me.principality.ktsql.backend.hbase.HBaseSchemaFactory',\n"
                        + "       operand: {\n"
                        + "         flavor: '${flavor}',\n"
                        + "         zkquorum: '${zkquorum}'\n"
                        + "       }\n"
                        + "     }\n"
                        + "   ]\n"
                        + "}"))
    }

    fun getLoginAuthority(): LoginAuthority {
        return loginAuth
    }

    fun getCalciteConfig(): Properties {
        return info
    }
}