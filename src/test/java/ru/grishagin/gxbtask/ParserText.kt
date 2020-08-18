package ru.grishagin.gxbtask

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import ru.grishagin.gxbtask.api.Expression
import ru.grishagin.gxbtask.impl.ParserImpl
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.test.assertEquals

@RunWith(JUnit4::class)
class ParserText {

    private val variables = HashMap<String, Double>()
    private lateinit var parser: ParserImpl

    @Before
    fun init(){
        variables.clear()
        parser = ParserImpl()
    }

    @Test
    fun test_single_var(){
        val expression = parser.parse("x", variables)
        runAssertion({it}, expression, Pair(-1, 1), 1)
    }

    @Test
    fun test_simple_addition(){
        val expression = parser.parse("x + 1", variables)
        runAssertion({it + 1}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_expr_addition(){
        val expression = parser.parse("x + (1+2)", variables)
        runAssertion({it + 3}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_unary_minus(){
        val expression = parser.parse("-x", variables)
        runAssertion({-it}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_simple_minus(){
        val expression = parser.parse("x - 1", variables)
        runAssertion({it - 1}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_expr_minus(){
        val expression = parser.parse("1 - (x+1)", variables)
        runAssertion({1 - (it+1)}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_simple_multiplicaion(){
        val expression = parser.parse("x * 2", variables)
        runAssertion({it * 2}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_expr_multiplication(){
        val expression = parser.parse("x * (2*3)", variables)
        runAssertion({it * 6}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_simple_division(){
        val expression = parser.parse("x/2", variables)
        runAssertion({it / 2}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_division_by_variable(){
        val expression = parser.parse("1 / x", variables)
        runAssertion({1 / it}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_sqrt(){
        val expression = parser.parse("sqrt(x)", variables)
        runAssertion({Math.sqrt(it)}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_exponent(){
        val expression = parser.parse("x ^ 2", variables)
        runAssertion({it * it}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_exponent_by_variable(){
        val expression = parser.parse("2 ^ x", variables)
        runAssertion({Math.pow(2.0, it)}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_combined_expression_v1(){
        val expression = parser.parse("sqrt(x) - x/2", variables)
        runAssertion({Math.sqrt(it) - it/2}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_combined_expression_v2(){
        val expression = parser.parse("sqrt(x^3) - 1/(x+2)", variables)
        runAssertion({Math.sqrt(Math.pow(it, 3.0)) - 1/(it+2)}, expression, Pair(-10, 10), 5)
    }

    @Test
    fun test_combined_expression_v3(){
        val expression = parser.parse("x^(x^sqrt(x))", variables)
        runAssertion({it.pow(it.pow(sqrt(it))) }, expression, Pair(-10, 10), 5)
    }

    @Test(expected = IllegalArgumentException::class)
    fun test_illegal_parenthesis(){
        parser.parse("x*2)", variables)
    }

    private fun runAssertion(trueExpression: (Double) -> Double, parsedExpression: Expression,
                             range: Pair<Int, Int>, step: Int){
        for(x in range.first..range.second step step){
            variables["x"] = x.toDouble()
            assertEquals(trueExpression.invoke(x.toDouble()), parsedExpression.invoke())
        }
    }
}