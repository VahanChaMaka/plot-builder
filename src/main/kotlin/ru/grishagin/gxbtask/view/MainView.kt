package ru.grishagin.gxbtask.view

import javafx.geometry.Insets
import javafx.scene.Parent
import tornadofx.*

class MainView: View(){
    override val root = borderpane{
        padding = Insets(10.0, 10.0, 10.0, 10.0)
        top<ChartView>()
        bottom<InputView>()
    }

}