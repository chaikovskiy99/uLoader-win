package org.limongradstudio.catchy.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


object Network {
  val client = HttpClient(OkHttp){
    engine { dispatcher = Dispatchers.IO }
  }
}

suspend fun getNetworkImage(resource: String): ByteArray? = withContext(Dispatchers.IO) {
  try {
    val response = Network.client.get(resource)
    if(response.status != HttpStatusCode.OK)
      return@withContext null
    return@withContext response.readRawBytes()
  } catch (e: Exception) {
    e.printStackTrace()
    return@withContext null
  }
}
