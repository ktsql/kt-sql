package me.principality.ktsql.protocol.mysql

import me.principality.ktsql.utils.SelectParamParser
import org.junit.Test
import org.parboiled.Parboiled
import org.parboiled.parserunners.BasicParseRunner
import org.parboiled.parserunners.TracingParseRunner
import org.parboiled.support.ParseTreeUtils
import org.parboiled.support.ParsingResult


class ParboiledTest {
    @Test
    fun parserTest() {
        val t1 = System.nanoTime()
        val input = "/*comment*/SELECT @@a AS a, @@b AS b"
        val parser = Parboiled.createParser(SelectParamParser::class.java)
        val runner = BasicParseRunner<Any>(parser.Expression())

        val t2 = System.nanoTime()
        val result: ParsingResult<Any> = runner.run(input)
//        println(runner.getLog())
        val t3 = System.nanoTime()

        println(t2 - t1)
        println(t3 - t2)

        if (result.matched) {
            val parseTreePrintOut = ParseTreeUtils.printNodeTree(result)
            println(parseTreePrintOut)
        }
    }
}
