package me.principality.ktsql.protocol.mysql.parser

import me.principality.ktsql.protocol.mysql.helper.SelectParamParser2
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
    private val OPERATORS = arrayOf(",", "@@", ".").toMutableList()
    private val KEYWORDS = arrayOf("select", "as").toMutableList()
    private val TERMS = Terminals.operators(OPERATORS).words(Scanners.IDENTIFIER).caseInsensitiveKeywords(KEYWORDS).build()
    private val TOKENIZER = Parsers.or(TERMS.tokenizer())
    private val IGNORED = Parsers.or(Scanners.JAVA_BLOCK_COMMENT, Scanners.WHITESPACES)

    /**
     * JParsec的API不够直观，容易带偏思维，但从性能上来看，初始化的时间JParsec比Parboiled要少很多
     */
    @Test
    fun testParser() {
        val t1 = System.nanoTime()
        val parser = expression(Terminals.Identifier.PARSER as Parser<Any>)
        val t2 = System.nanoTime()

        val result = parser
                .from(TOKENIZER, IGNORED.skipMany())
                .parse("SELECT /*this is a comment*/ @@a AS a.a, @@b as b, @@c as c")
        val t3 = System.nanoTime()

        println(t2 - t1)
        println(t3 - t2)

        println(result)
    }

    @Test
    fun testParse2() {
        val parser = SelectParamParser2()
        val list = parser.parse("/* mysql-connector-java-5.1.47 ( Revision: fe1903b1ecb4a96a917f7ed3190d80c049b1de29 ) */SELECT  @@session.auto_increment_increment AS auto_increment_increment, @@character_set_client AS character_set_client, @@character_set_connection AS character_set_connection, @@character_set_results AS character_set_results, @@character_set_server AS character_set_server, @@collation_server AS collation_server, @@collation_connection AS collation_connection, @@init_connect AS init_connect, @@interactive_timeout AS interactive_timeout, @@license AS license, @@lower_case_table_names AS lower_case_table_names, @@max_allowed_packet AS max_allowed_packet, @@net_buffer_length AS net_buffer_length, @@net_write_timeout AS net_write_timeout, @@query_cache_size AS query_cache_size, @@query_cache_type AS query_cache_type, @@sql_mode AS sql_mode, @@system_time_zone AS system_time_zone, @@time_zone AS time_zone, @@tx_isolation AS transaction_isolation, @@wait_timeout AS wait_timeout")
//        val list = parser.parse("SELECT /*this is a comment*/ @@a.a as a, @@b.b as b, @@c as c")

        if (list.isNotEmpty()) {
            println(list)
        }
    }

    fun identifier1(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier1: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun identifier11(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier11: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun identifier12(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier12: ${it}")
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

    fun identifier21(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier21: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun identifier22(expr: Parser<Any>): Parser<Any> {
        val id = expr.map {
            it -> println("identifier22: ${it}")
            it
        }

        return id as Parser<Any>
    }

    fun term(expr: Parser<Any>): Parser<Any> {
        val asTerm = Parsers.sequence(TERMS.token("@@"),
                Parsers.or(Parsers.sequence(identifier11(expr), TERMS.token("."), identifier12(expr)), identifier1(expr)),
                TERMS.token("as"),
                Parsers.or(Parsers.sequence(identifier21(expr), TERMS.token("."), identifier22(expr)), identifier2(expr))
        )

        return asTerm as Parser<Any>
    }

    fun terms(expr: Parser<Any>): Parser<Any> {
        val asTerms =
                Parsers.sequence(
                        term(expr),
                        Parsers.sequence(Parsers.sequence(TERMS.token(","), term(expr))).many()
                )

        return asTerms as Parser<Any>
    }

    fun expression(expr: Parser<Any>): Parser<Any> {
        val e = Parsers.sequence(TERMS.token("select"), terms(expr))

        return e as Parser<Any>
    }
}