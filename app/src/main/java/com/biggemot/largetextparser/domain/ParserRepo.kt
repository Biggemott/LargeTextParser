package com.biggemot.largetextparser.domain

import kotlinx.coroutines.flow.Flow

interface ParserRepo {

    fun getFileStream(url: String): Flow<ParseResult>
    fun copyToClipboard(strings: List<String>)
    fun appendLogLine(line: String)
    fun clearLog()
}