package com.biggemot.largetextparser.domain

import kotlinx.coroutines.flow.Flow

interface ParserInteractor {

    fun parseFile(url: String, pattern: String): Flow<ParseResult>

    fun copyToClipboard(strings: List<String>)
}