package me.principality.utils.config

// TODO rename?
object ConfigureManager {
    fun getLoginAuthority(): LoginAuthority {
        return LoginAuthority("user", "pass") //TODO fix it
    }
}