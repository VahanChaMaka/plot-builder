package ru.grishagin.gxbtask.impl

import org.springframework.stereotype.Service
import ru.grishagin.gxbtask.api.Expression
import ru.grishagin.gxbtask.api.Parser
import java.lang.IllegalArgumentException
import java.util.HashMap

@Service
class ParserImpl: Parser {

    private val functions = HashMap<String, (Double) -> Double>()

    init {
        //other function can be added as well
        functions["sqrt"] = Math::sqrt
    }

    override fun parse(str: String, variables: Map<String, Double>): Expression {
        return object : Any() {
            var pos: Int = -1
            var ch: Int = 0

            //if check pos+1 is smaller than string length ch is char at new pos
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            //skips 'spaces' and if current char is what was searched, if true move to next char return true
            //else return false
            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }


            fun parse(): Expression {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw IllegalArgumentException("Unexpected: " + ch.toChar())
                return x
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor | factor `^` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor

            fun parseExpression(): Expression {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) { // addition
                        val a = x
                        val b = parseTerm()
                        x = { a.invoke() + b.invoke() }
                    } else if (eat('-'.toInt())) { // subtraction
                        val a = x
                        val b = parseTerm()
                        x = { a.invoke() - b.invoke() }
                    } else {
                        return x
                    }
                }
            }

            fun parseTerm(): Expression {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) {
                        val a = x
                        val b = parseFactor() // multiplication
                        x = { a.invoke() * b.invoke() }
                    } else if (eat('/'.toInt())) {
                        val a = x
                        val b = parseFactor() // division
                        x = { a.invoke() / b.invoke() }
                    } else if (eat('^'.toInt())){
                        val a = x
                        val b = parseFactor() // multiplication
                        x = { Math.pow(a.invoke(), b.invoke()) }
                    }
                    else
                        return x
                }
            }

            fun parseFactor(): Expression {
                if (eat('+'.toInt())) return parseFactor() // unary plus
                if (eat('-'.toInt())) {
                    val b = parseFactor() // unary minus
                    return { -1 * b.invoke() }
                }

                var x: Expression
                val startPos = this.pos
                if (eat('('.toInt())) { // parentheses
                    x = parseExpression()
                    eat(')'.toInt())
                } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numbers
                    while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) {
                        nextChar()
                    }
                    val xx = java.lang.Double.parseDouble(str.substring(startPos, this.pos))
                    x = { xx }
                } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions and variables
                    while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
                    val name = str.substring(startPos, this.pos)
                    if (functions.containsKey(name)) {
                        val func = functions[name]
                        val arg = parseFactor()
                        x = { func!!.invoke(arg.invoke()) }
                    } else {
                        x = { variables[name] ?: 0.0 }
                    }
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }

                return x
            }
        }.parse()
    }
}