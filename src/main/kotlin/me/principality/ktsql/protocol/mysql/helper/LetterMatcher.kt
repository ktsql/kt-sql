package me.principality.ktsql.protocol.mysql.helper

class LetterMatcher : AbstractCharacterMatcher("Letter") {

    override fun acceptChar(c: Char): Boolean {
        return Character.isJavaIdentifierStart(c)
    }
}