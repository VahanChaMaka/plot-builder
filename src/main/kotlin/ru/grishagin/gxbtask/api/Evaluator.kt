package ru.grishagin.gxbtask.api

import com.sun.javafx.geom.Vec2d
import ru.grishagin.gxbtask.model.EvaluatorParams

interface Evaluator {

    fun getValues(params: EvaluatorParams): List<Vec2d>

}