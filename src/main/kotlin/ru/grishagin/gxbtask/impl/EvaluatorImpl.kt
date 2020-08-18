package ru.grishagin.gxbtask.impl

import com.sun.javafx.geom.Vec2d
import org.springframework.stereotype.Service
import ru.grishagin.gxbtask.api.Evaluator
import ru.grishagin.gxbtask.api.Parser
import ru.grishagin.gxbtask.model.EvaluatorParams
import java.util.*

@Service("native")
class EvaluatorImpl(val parser: Parser): Evaluator {

    override fun getValues(params: EvaluatorParams): List<Vec2d> {
        val variables = HashMap<String, Double>()
        val points = mutableListOf<Vec2d>()
        val parsedExpression = parser.parse(params.expression, variables)
        var x = params.from;
        while (x <= params.to) {
            variables["x"] = x
            val value = parsedExpression.invoke();
            if(value.isFinite()) {
                points.add(Vec2d(x, value))
            }
            x += params.step
        }
        return points;
    }

}