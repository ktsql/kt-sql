package me.principality.ktsql.protocol.mysql

import me.principality.ktsql.utils.SelectParamParser
import org.junit.Test
import org.parboiled.Parboiled
import org.parboiled.parserunners.TracingParseRunner
import org.parboiled.support.ParseTreeUtils
import org.parboiled.support.ParsingResult


class ParboiledTest {
    @Test
    fun parserTest() {
        val input = "/*comment*/SELECT @@a AS a, @@b AS b"
        val parser = Parboiled.createParser(SelectParamParser::class.java)
        val runner = TracingParseRunner<Any>(parser.Expression())
        val result: ParsingResult<Any> = runner.run(input)
        println(runner.getLog())

        if (result.matched) {
            val parseTreePrintOut = ParseTreeUtils.printNodeTree(result)
            println(parseTreePrintOut)
        }
    }
}
