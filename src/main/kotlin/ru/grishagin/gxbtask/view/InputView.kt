package ru.grishagin.gxbtask.view

import javafx.geometry.Pos
import javafx.scene.control.CheckBox
import javafx.scene.control.TextField
import ru.grishagin.gxbtask.model.EvaluatorParams
import tornadofx.*

private const val INTERVAL_FIELD_WIDTH = 50.0

class InputView: View() {

    private val controller = find(ChartController::class)

    private var expressionInput: TextField by singleAssign()
    private var fromInput: TextField by singleAssign()
    private var toInput: TextField by singleAssign()
    private var useWolfram: CheckBox by singleAssign()

    override val root = vbox{
        hbox{
            alignment = Pos.CENTER_LEFT

            label("f(x) = "){
                prefHeight(100.0)
            }
            expressionInput = textfield("sqrt(x) - 0.45*x")

            region {
                minWidth = 20.0
            }

            label("Interval: [")
            fromInput = textfield("-2"){
                filterInput { it.controlNewText.isDouble() }
                prefWidth = INTERVAL_FIELD_WIDTH
            }
            label(", ")
            toInput = textfield("10"){
                filterInput { it.controlNewText.isDouble() }
                prefWidth = INTERVAL_FIELD_WIDTH
            }
            label("]")

            region{
                minWidth = 20.0
            }

            label("Use Wolfram Alpha API")
            useWolfram = checkbox()
        }

        region{
            minHeight = 5.0
        }

        button("Draw"){
            setOnAction {
                var from = fromInput.text.toDouble()
                var to = toInput.text.toDouble()
                //swap from/to
                if(from > to){
                    from = to.also { to = from }
                    fromInput.text = toInput.text.also { toInput.text = fromInput.text }
                }
                val step = (to - from)/3000
                controller.processExpression(EvaluatorParams(expressionInput.text, from, toInput.text.toDouble(), step, useWolfram.isSelected))
            }
        }
    }
}