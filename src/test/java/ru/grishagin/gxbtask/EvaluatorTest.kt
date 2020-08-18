package ru.grishagin.gxbtask

import com.sun.javafx.geom.Vec2d
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import ru.grishagin.gxbtask.api.Expression
import ru.grishagin.gxbtask.api.Parser
import ru.grishagin.gxbtask.impl.EvaluatorImpl
import ru.grishagin.gxbtask.model.EvaluatorParams
import kotlin.test.assertEquals

@RunWith(MockitoJUnitRunner::class)
class EvaluatorTest{

    @InjectMocks
    private lateinit var evaluator: EvaluatorImpl

    @Mock
    private lateinit var mockParser: Parser

    @Test
    fun test_value_sequence(){
        val expression = object: Expression{
            private var nextVal: Double = -2.0

            override fun invoke(): Double {
                nextVal += 2
                return nextVal
            }
        }
        val params = EvaluatorParams("x", 0.0, 10.0, 2.0)
        Mockito.`when`(mockParser.parse(params.expression, mapOf()))
                .thenReturn(expression)

        val expected = mutableListOf<Vec2d>()
        for (x in 0..10 step 2){
            expected.add(Vec2d(x.toDouble(), x.toDouble()))
        }
        assertEquals(expected, evaluator.getValues(params))
    }

    @Test
    fun test_ignore_NaN(){
        val params = EvaluatorParams("", 0.0, 10.0, 2.0)
        Mockito.`when`(mockParser.parse(params.expression, mapOf()))
                .thenReturn { Double.NaN}

        val expected = listOf<Vec2d>()
        assertEquals(expected, evaluator.getValues(params))
    }

    @Test
    fun test_ignore_infinity(){
        val params = EvaluatorParams("", 0.0, 10.0, 2.0)
        Mockito.`when`(mockParser.parse(params.expression, mapOf()))
                .thenReturn { Double.POSITIVE_INFINITY}

        val expected = listOf<Vec2d>()
        assertEquals(expected, evaluator.getValues(params))
    }
}