package org.limongradstudio.catchy.data.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

suspend fun getNetworkImage(resource: String): ByteArray? = withContext(Dispatchers.IO) {
  try {
    // Create a URL instTimber.e(e, "Error fetching image: %s", resource)Timber.e(e, "Error fetching image: %s", resource)ance directly
    val url = URL(resource)

    // Open connection and read the stream
    (url.openConnection() as HttpURLConnection).inputStream.buffered().use { inputStream ->
      inputStream.readAllBytes() // Read all bytes from the InputStream
    }
  } catch (e: Exception) {
    e.printStackTrace() // Log the exception
    return@withContext null // Return null in case of an error
  }
}
