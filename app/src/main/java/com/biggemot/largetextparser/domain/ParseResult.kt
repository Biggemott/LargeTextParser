package com.biggemot.largetextparser.domain

sealed class ParseResult {
    data class Data(val value: String) : ParseResult()
    object Finish : ParseResult()
    object Error : ParseResult()
}
