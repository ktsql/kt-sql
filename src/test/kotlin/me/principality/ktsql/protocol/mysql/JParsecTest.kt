package me.principality.ktsql.protocol.mysql

import org.jparsec.Parser
import org.jparsec.Terminals
import org.jparsec.Scanners
import org.jparsec.Parsers
import org.junit.Test


/**
 * /* comments */ SELECT @@arg AS arg, @@arg AS arg, ...
 *
 * Expr <- SELECT Terms
 * AsTerms <- AsTerm (, AsTerm)*
 * AsTerm <- a AS b
 * a <- @@identifier
 * b <- identifier
 *
 * SQL parser
 * https://github.com/jparsec/jparsec/blob/master/jparsec-examples/src/main/java/org/jparsec/examples/sql/parser/TerminalParser.java
 */
class JParsecTest {
    private val OPERATORS = arrayOf(",", "@@").toMutableList()
    private val KEYWORDS = arrayOf("select", "as").toMutableList()
    private val TERMS = Terminals.operators(OPERATORS).words(Scanners.IDENTIFIER).caseInsensitiveKeywords(KEYWORDS).build()
    private val TOKENIZER = Parsers.or(TERMS.tokenizer())
    private val IGNORED = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES)

    /**
     * JParsec的API不够直观，容易带偏思维
     */
    @Test
    fun testParser() {
        val parser = expression(Terminals.Identifier.PARSER as Parser<Any>)
        val result = parser
                .from(TOKENIZER, IGNORED.skipMany())
                .parse("SELECT /*this is a comment*/ @@a AS a, @@b as b")

        println(result)
    }

    fun identifier1(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier1: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun identifier2(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier2: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun term(expr: Parser<Any>): Parser<Any> {
        val asTerm = Parsers.sequence(TERMS.token("@@"),
                identifier1(expr),
                TERMS.token("as"),
                identifier2(expr))

        return asTerm as Parser<Any>
    }

    fun terms(expr: Parser<Any>): Parser<Any> {
        val asTerms =
                Parsers.sequence(term(expr), Parsers.array(Parsers.sequence(TERMS.token(","), term(expr))))

        return asTerms as Parser<Any>
    }

    fun expression(expr: Parser<Any>): Parser<Any> {
        val e = Parsers.sequence(TERMS.token("select"), terms(expr))

        return e as Parser<Any>
    }
}