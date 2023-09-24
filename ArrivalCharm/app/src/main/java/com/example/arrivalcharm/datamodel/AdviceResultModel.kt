package com.example.arrivalcharm.datamodel

data class AdviceResultModel(
    val query: String,
    val slips: List<Slip>,
    val total_results: String
)