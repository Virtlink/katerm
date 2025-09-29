package net.pelsmaeker.katerm.regex

import java.io.StringReader

/**
 * A parser for regular expressions.
 */
class RegexParser<R : Regex<T, M>, T, M>(
    val builder: RegexBuilder<R, T, M>,
    val tokenMatcherBuilder: (String, List<String>) -> Matcher<T, M>,
    val variableMatcherBuilder: ((String) -> Matcher<T, M>)? = null,
    val listVariableMatcherBuilder: ((String) -> Matcher<T, M>)? = null,
) {
    // parse regular expressions using recursive descent
    fun parse(input: String): R {
        val reader = StringReader(input)
        val parser = Parser(reader)
        return parser.parseRegex()
    }

    private inner class Parser(
        val reader: StringReader,
    ) {
        // Examples:
        //   LEX* (MOD<$x>)? | ( LEX* (MOD<X>)? )*
        //   $a $b $c (VAR<X>)
        //   $$xs (VAR<X>)

        // Grammar:
        // parser grammar ExprParser;
        // options { tokenVocab=ExprLexer; }
        //
        // regex
        //     : term '|' regex
        //     | term
        //     ;
        //
        // term
        //     : factor+
        //     ;
        //
        // factor
        //     : base '*'
        //     | base '+'
        //     | base '?'
        //     | base '*?'
        //     | base '+?'
        //     | base '??'
        //     | base
        //     ;
        //
        // base
        //     : '(' regex ')'
        //     | value '<' valuelist '>'
        //     | value
        //     ;
        //
        // valuelist
        //     : value ',' valuelist
        //     | value
        //     ;
        //
        // value
        //     : TOKEN
        //     | '$' TOKEN
        //     | '$$' TOKEN
        //     ;
        //
        // lexer grammar ExprLexer;
        //
        // STAR : '*';
        // PLUS : '+';
        // QUES : '?';
        // STARLAZY : '*?';
        // PLUSLAZY : '+?';
        // QUESLAZY : '??';
        //
        // DOLLAR : '$' ;
        // DDOLLAR : '$$';
        // COMMA : ',';
        // OR : '|' ;
        //
        // LPAREN : '(' ;
        // RPAREN : ')' ;
        // LANGLE : '<' ;
        // RANGLE : '>' ;
        //
        // TOKEN: CHAR CHAR*;
        //
        // fragment CHAR: SINGLE_CHAR | ESCAPE_SEQ;
        // fragment SINGLE_CHAR: ~[$*+?()<>\\ \t\n\r\f];
        // fragment ESCAPE_SEQ : '\\' [$*+?()<>\\tnrf];
        //
        // WS: [ \t\n\r\f]+ -> skip ;

        fun parseRegex(): R {
            val first = parseTerm()

            if (peek() == '|') {
                expect('|')
                val second = parseRegex()
                return builder.union(first, second)
            } else {
                return first
            }
        }

        fun parseTerm(): R {
            var result = parseFactor()
            while (true) {
                when (peek()) {
                    null, '|', ')' -> return result
                    else -> {
                        val next = parseFactor()
                        result = builder.concat(result, next)
                    }
                }
            }
        }

        fun parseFactor(): R {
            val base = parseBase()
            return when (peek()) {
                '*' -> {
                    expect('*')
                    if (peek() == '?') {
                        expect('?')
                        builder.starLazy(base)
                    } else {
                        builder.star(base)
                    }
                }
                '+' -> {
                    expect('+')
                    if (peek() == '?') {
                        expect('?')
                        builder.plusLazy(base)
                    } else {
                        builder.plus(base)
                    }
                }
                '?' -> {
                    expect('?')
                    if (peek() == '?') {
                        expect('?')
                        builder.questionLazy(base)
                    } else {
                        builder.question(base)
                    }
                }
                else -> base
            }
        }

        fun parseBase(): R {
            return when (peek()) {
                '(' -> {
                    expect('(')
                    val inner = parseRegex()
                    expect(')')
                    inner
                }
                else -> {
                    val value = parseValue()
                    if (peek() == '<') {
                        expect('<')
                        val arguments = mutableListOf<String>()
                        arguments.add(value)
                        while (peek() == ',') {
                            expect(',')
                            arguments.add(parseValue())
                        }
                        expect('>')
                        val matcher = tokenMatcherBuilder(value, arguments)
                        builder.atom(matcher)
                    } else when {
                        value.startsWith("$$") -> {
                            if (listVariableMatcherBuilder == null) throw UnsupportedOperationException("List variables are not supported: $value")
                            val varName = value.substring(2)
                            assert(varName.isNotEmpty())
                            val matcher = listVariableMatcherBuilder(varName)
                            return builder.atom(matcher)
                        }
                        value.startsWith('$') -> {
                            if (variableMatcherBuilder == null) throw UnsupportedOperationException("Variables are not supported: $value")
                            val varName = value.substring(1)
                            assert(varName.isNotEmpty())
                            val matcher = variableMatcherBuilder(varName)
                            return builder.atom(matcher)
                        }
                        else -> {
                            // Just a token
                            val matcher = tokenMatcherBuilder(value, emptyList())
                            builder.atom(matcher)
                        }
                    }
                }
            }
        }

        fun parseValue(): String {
            return when (peek()) {
                '$' -> {
                    expect('$')
                    if (peek() == '$') {
                        expect('$')
                        // Double dollar: list variable
                        parseToken()
                    } else {
                        // Single dollar: variable
                        parseToken()
                    }
                }
                else -> {
                    // Identifier: token
                    parseToken()
                }
            }
        }

        fun parseToken(): String {
            val sb = StringBuilder()
            skipWhitespace()
            while (true) {
                val char = peek(significantWhitespace = true) ?: break
                if (char in setOf('$', '*', '+', '?', '(', ')', '<', '>', '|', ',', ' ', '\t', '\n', '\r')) {
                    break
                }
                if (char == '\\') {
                    // Escape sequence
                    expect('\\')
                    val nextChar = read(significantWhitespace = true) ?: throw IllegalArgumentException("Expected a character after escape, but got EOF")
                    val escapedChar = when (nextChar) {
                        't' -> '\t'
                        'n' -> '\n'
                        'r' -> '\r'
                        'f' -> '\u000C'
                        else -> nextChar // literal character
                    }
                    sb.append(escapedChar)
                } else {
                    sb.append(char)
                    skip(significantWhitespace = true)
                }
            }
            if (sb.isEmpty()) {
                throw IllegalArgumentException("Expected a token, but got none")
            }
            return sb.toString()
        }

        private fun peek(significantWhitespace: Boolean = false): Char? {
            reader.mark(1)
            var char: Int
            do {
                char = reader.read()
                if (char == -1) {
                    reader.reset()
                    return null
                }
            } while (!significantWhitespace && char.toChar().isWhitespace())
            reader.reset()
            return char.toChar()
        }

        private fun read(significantWhitespace: Boolean = false): Char? {
            var char: Int
            do {
                char = reader.read()
                if (char == -1) {
                    return null
                }
            } while (!significantWhitespace && char.toChar().isWhitespace())
            return char.toChar()
        }

        private fun skip(significantWhitespace: Boolean = false) {
            read(significantWhitespace) ?: throw IllegalArgumentException("Expected a character, but got EOF")
        }

        private fun skipWhitespace() {
            while (true) {
                val char = peek(significantWhitespace = true) ?: break
                if (!char.isWhitespace()) break
                read(significantWhitespace = true)
            }
        }

        private fun expect(char: Char, significantWhitespace: Boolean = false): Char {
            val readChar = read(significantWhitespace)
            if (readChar != char) {
                throw IllegalArgumentException("Expected '$char', but got '${readChar ?: "EOF"}'")
            }
            return readChar
        }

        private fun expectOneOf(chars: Set<Char>, significantWhitespace: Boolean = false): Char {
            val readChar = read(significantWhitespace)
            if (readChar == null || readChar !in chars) {
                throw IllegalArgumentException("Expected one of '${chars.joinToString("")}', but got '${readChar ?: "EOF"}'")
            }
            return readChar
        }

        private fun isEof(significantWhitespace: Boolean = false): Boolean {
            return peek(significantWhitespace) == null
        }
    }
}
