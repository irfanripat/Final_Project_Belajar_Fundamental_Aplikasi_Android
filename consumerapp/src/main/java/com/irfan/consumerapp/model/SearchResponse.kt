package com.irfan.consumerapp.model

data class SearchResponse(
    val total_count: Int?,
    val incomplete_results: Boolean?,
    val items: ArrayList<DetailUser>?
)