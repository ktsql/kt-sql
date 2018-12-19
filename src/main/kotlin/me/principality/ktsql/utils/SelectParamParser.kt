package me.principality.ktsql.utils

import org.parboiled.BaseParser
import org.parboiled.Rule
import org.parboiled.annotations.BuildParseTree

/**
 * 缺失comment的处理，Parboiled对space的处理也有点绕
 *
 * Expr <- SELECT Terms
 * AsTerms <- AsTerm (, AsTerm)*
 * AsTerm <- a AS b
 * a <- @@identifier
 * b <- identifier
 *
 */
@BuildParseTree
internal open class SelectParamParser : BaseParser<Any>() {

    open fun Expression(): Rule {
        return Sequence( Spacing(), Select(), Terms()
        )
    }

    open fun Select(): Rule {
        return Sequence(IgnoreCase("select"), Spacing())
    }

    open fun Terms(): Rule {
        return Sequence(
                Term(),
                ZeroOrMore(Sequence(Comma(), Term()))
        )
    }

    open fun Comma(): Rule {
        return Sequence(Ch(','), Spacing())
    }

    open fun Term(): Rule {
        return Sequence(
                SysIdentifier(),
                As(),
                Identifier()
        )
    }

    open fun As(): Rule {
        return Sequence(IgnoreCase("as"), Spacing())
    }

    open fun SysIdentifier(): Rule {
        return Sequence(
                SysPrefix(),
                Sequence(LetterMatcher(), ZeroOrMore(LetterMatcher())),
                Spacing()
        )
    }

    open fun SysPrefix(): Rule {
        return String("@@")
    }

    open fun Identifier(): Rule {
        return Sequence(
                Sequence(LetterMatcher(), ZeroOrMore(LetterMatcher())),
                Spacing()
        )
    }

    open fun Spacing(): Rule {
        return ZeroOrMore(FirstOf(

                // whitespace
                OneOrMore(AnyOf(" \t\r\n").label("Whitespace")),

                // traditional comment
                Sequence("/*", ZeroOrMore(TestNot("*/"), BaseParser.ANY), "*/").label("TraditionalComment"),

                // end of line comment
                Sequence(
                        "//",
                        ZeroOrMore(TestNot(AnyOf("\r\n")), BaseParser.ANY),
                        FirstOf("\r\n", '\r', '\n', BaseParser.EOI).label("LineComment")
                )
        ))
    }

    override fun fromStringLiteral(string: String): Rule {
        return if (string.endsWith(" "))
            Sequence(String(string.substring(0, string.length - 1)), Spacing())
        else
            String(string)
    }
}

