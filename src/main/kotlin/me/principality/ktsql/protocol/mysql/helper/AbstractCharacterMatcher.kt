package me.principality.ktsql.protocol.mysql.helper

import org.parboiled.MatcherContext
import org.parboiled.matchers.CustomMatcher

abstract class AbstractCharacterMatcher protected constructor(label: String) : CustomMatcher(label) {

    override fun isSingleCharMatcher(): Boolean {
        return true
    }

    override fun canMatchEmpty(): Boolean {
        return false
    }

    override fun isStarterChar(c: Char): Boolean {
        return acceptChar(c)
    }

    override fun getStarterChar(): Char {
        return 'a'
    }

    override fun <V> match(context: MatcherContext<V>): Boolean {
        if (!acceptChar(context.currentChar)) {
            return false
        }
        context.advanceIndex(1)
        context.createNode()
        return true
    }

    protected abstract fun acceptChar(c: Char): Boolean
}
