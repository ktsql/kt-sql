package me.principality.ktsql.utils

import mu.KotlinLogging
import org.jparsec.Parser
import org.jparsec.Parsers
import org.jparsec.Scanners
import org.jparsec.Terminals

/**
 * JParsec的API不够直观，容易带偏思维，但从性能上来看，初始化的时间JParsec比Parboiled要少很多
 *
 * Expr <- SELECT Terms
 * AsTerms <- AsTerm (, AsTerm)*
 * AsTerm <- a AS b
 * a <- @@identifier
 * b <- identifier
 */
class SelectParamParser2 {
    private val logger = KotlinLogging.logger {}
    private val OPERATORS = arrayOf(",", "@@").toMutableList()
    private val KEYWORDS = arrayOf("select", "as").toMutableList()
    private val TERMS = Terminals.operators(OPERATORS).words(Scanners.IDENTIFIER).caseInsensitiveKeywords(KEYWORDS).build()
    private val TOKENIZER = Parsers.or(TERMS.tokenizer())
    private val IGNORED = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES)
    private val resultList = mutableListOf<Pair<String, String>>()
    private var sysParam: String = ""
    private var aliasName: String = ""
    private val parser = expression(Terminals.Identifier.PARSER as Parser<Any>).from(TOKENIZER, IGNORED.skipMany())

    fun parse(source: String): List<Pair<String, String>> {
        try {
            parser.parse(source)
        } catch (ex: Exception) {
            logger.debug { ex }
            return mutableListOf<Pair<String, String>>() // 返回一个空值
        }

        return resultList
    }

    private fun identifier1(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            sysParam = it as String
            it
        }

        return id as Parser<Any>
    }

    private fun identifier2(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            aliasName = it as String

            val pair = Pair<String, String>(sysParam, aliasName)
            resultList.add(pair)
            it
        }

        return id as Parser<Any>
    }

    private fun term(expr: Parser<Any>): Parser<Any> {
        val asTerm = Parsers.sequence(TERMS.token("@@"),
                identifier1(expr),
                TERMS.token("as"),
                identifier2(expr))

        return asTerm as Parser<Any>
    }

    private fun terms(expr: Parser<Any>): Parser<Any> {
        val asTerms =
                Parsers.sequence(term(expr), Parsers.array(Parsers.sequence(TERMS.token(","), term(expr))))

        return asTerms as Parser<Any>
    }

    private fun expression(expr: Parser<Any>): Parser<Any> {
        val e = Parsers.sequence(TERMS.token("select"), terms(expr))

        return e as Parser<Any>
    }
}