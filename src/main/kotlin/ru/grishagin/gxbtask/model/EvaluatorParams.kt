package ru.grishagin.gxbtask.model

data class EvaluatorParams(val expression: String,
                           val from: Double, val to: Double, val step: Double,
                           val useWolfram: Boolean = false)