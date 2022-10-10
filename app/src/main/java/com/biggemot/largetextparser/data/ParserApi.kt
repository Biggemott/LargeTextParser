package com.biggemot.largetextparser.data

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

interface ParserApi {

    @Streaming
    @GET
    suspend fun loadFile(@Url url: String): Response<ResponseBody>
}