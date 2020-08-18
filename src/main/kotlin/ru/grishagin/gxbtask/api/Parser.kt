package ru.grishagin.gxbtask.api

interface Parser {

    fun parse(str: String, variables: Map<String, Double>): Expression

}