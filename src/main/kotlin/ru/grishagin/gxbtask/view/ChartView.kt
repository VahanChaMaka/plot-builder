package ru.grishagin.gxbtask.view

import com.sun.javafx.geom.Vec2d
import ru.grishagin.gxbtask.model.EvaluatorParams
import tornadofx.*
import javafx.scene.paint.Color
import java.util.*
import java.util.stream.Collectors

private const val CANVAS_X_SIZE = 500.0
private const val CANVAS_Y_SIZE = 250.0
private const val CANVAS_PADDING = 40.0

private const val MARKS_DENSITY = 50
private const val LABEL_WIDTH = 20.0

private const val ARR_WIDTH = 5.0
private const val ARR_HEIGHT = 10.0

private val GRID_COLOR = Color(0.2, 0.2, 0.2, 0.1)

class ChartView: View() {

    override val root = group {
        rectangle {
            x = 0.0
            y = 0.0
            width = CANVAS_X_SIZE + CANVAS_PADDING*2
            height = CANVAS_Y_SIZE + LABEL_WIDTH*2
            fill = Color(0.0, 0.0, 0.0, 0.0)
        }
    }

    fun waitEvaluation(){
        runLater {
            root.getChildList()?.clear()
            with(root) {
                label("Please wait")
            }
        }
    }

    fun draw(params: EvaluatorParams, values: List<Vec2d>){
        root.getChildList()?.clear()
        with(root){
            val xScale = (params.to - params.from) / CANVAS_X_SIZE
            val horizontalOffset = - params.from/xScale
            val minMax = values.stream()
                    .map { v -> v.y }
                    .collect(Collectors.summarizingDouble(Double::toDouble))
            val yScale = Math.abs(minMax.max - minMax.min) / CANVAS_Y_SIZE
            val verticalOffset = minMax.max/yScale
            for(v in values){
                circle {
                    centerX = v.x / xScale + horizontalOffset
                    centerY = -v.y/yScale + verticalOffset
                    radius = 1.0
                    fill = Color.RED
                }
            }

            xAxisBuild(verticalOffset, params)

            yAxisBuild(horizontalOffset, minMax)
        }
    }

    private fun xAxisBuild(verticalOffset: Double, params: EvaluatorParams) {
        with(root) {
            //x axis
            val verticalAxisOffset: Double
            if (verticalOffset < 0) {
                verticalAxisOffset = 0.0
            } else if (verticalOffset > CANVAS_Y_SIZE) {
                verticalAxisOffset = CANVAS_Y_SIZE
            } else {
                verticalAxisOffset = verticalOffset
            }
            //axis
            line {
                startX = 1.0
                startY = verticalAxisOffset
                endX = CANVAS_X_SIZE
                endY = verticalAxisOffset
            }

            //arrow
            polygon(CANVAS_X_SIZE, verticalAxisOffset,
                    CANVAS_X_SIZE - ARR_HEIGHT, verticalAxisOffset - ARR_WIDTH/2,
                    CANVAS_X_SIZE - ARR_HEIGHT, verticalAxisOffset + ARR_WIDTH/2)

            val labelValueStep = (params.to - params.from) / (CANVAS_X_SIZE/ MARKS_DENSITY)
            var labelValue = params.from
            for (xPoint in 0..CANVAS_X_SIZE.toInt() step MARKS_DENSITY){
                //grid
                line{
                    startX = xPoint.toDouble()
                    startY = 0.0
                    endX = xPoint.toDouble()
                    endY = CANVAS_Y_SIZE
                    fill = GRID_COLOR
                    strokeWidth = 0.2
                }
                //numbers
                label("%.2f".format(labelValue)){
                    maxWidth(LABEL_WIDTH)
                    relocate(xPoint.toDouble()- LABEL_WIDTH/2, CANVAS_Y_SIZE)
                }

                labelValue += labelValueStep
            }
        }
    }

    private fun yAxisBuild(horizontalOffset: Double, minMax: DoubleSummaryStatistics){
        with(root) {
            val horizontalAxisOffset: Double
            if(horizontalOffset < 0){
                horizontalAxisOffset = 0.0
            } else if(horizontalOffset > CANVAS_X_SIZE){
                horizontalAxisOffset = CANVAS_X_SIZE
            } else {
                horizontalAxisOffset = horizontalOffset
            }

            //axis
            line {
                startX = horizontalAxisOffset
                startY = 0.0
                endX = horizontalAxisOffset
                endY = CANVAS_Y_SIZE
            }
            //arrow
            polygon(horizontalAxisOffset, 0.0,
                    horizontalAxisOffset - ARR_WIDTH/2, ARR_HEIGHT,
                    horizontalAxisOffset + ARR_WIDTH/2, ARR_HEIGHT)

            val labelValueStep = (minMax.max - minMax.min) / (CANVAS_Y_SIZE/ MARKS_DENSITY)
            var labelValue = minMax.min
            for (yPoint in 0..CANVAS_Y_SIZE.toInt() step MARKS_DENSITY){
                //grid
                line{
                    startX = 0.0
                    startY = yPoint.toDouble()
                    endX = CANVAS_X_SIZE
                    endY = yPoint.toDouble()
                    fill = GRID_COLOR
                    strokeWidth = 0.2
                }
                //numbers
                label("%.2f".format(labelValue)){
                    maxWidth(LABEL_WIDTH)
                    relocate(-CANVAS_PADDING, CANVAS_Y_SIZE - yPoint.toDouble() - 8)
                }

                labelValue += labelValueStep
            }
        }
    }
}