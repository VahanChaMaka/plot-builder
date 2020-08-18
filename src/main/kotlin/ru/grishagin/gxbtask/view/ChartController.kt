package ru.grishagin.gxbtask.view

import javafx.scene.control.Alert
import javafx.stage.StageStyle
import ru.grishagin.gxbtask.api.Evaluator
import ru.grishagin.gxbtask.model.EvaluatorParams
import tornadofx.Controller
import tornadofx.fail

class ChartController: Controller()  {

    private val chartView = find(ChartView::class)

    private val nativeEvaluator: Evaluator by di("native")
    private val wolframEvaluator: Evaluator by di("wolfram")

    fun processExpression(params: EvaluatorParams){
        runAsync {
            chartView.waitEvaluation()
            resolveEvaluator(params).getValues(params)
        } ui { values -> chartView.draw(params, values) } fail {
            val message = if(params.useWolfram){
                "Something went wrong, please try again"
            } else {
                "Expression parse error"
            }
            Alert(Alert.AlertType.ERROR, message)
                    .apply {
                        headerText = ""
                        initStyle(StageStyle.UTILITY)
                        showAndWait()
                    }
        }
    }

    private fun resolveEvaluator(params: EvaluatorParams): Evaluator{
        return if(params.useWolfram){
            wolframEvaluator
        } else {
            nativeEvaluator
        }
    }
}
