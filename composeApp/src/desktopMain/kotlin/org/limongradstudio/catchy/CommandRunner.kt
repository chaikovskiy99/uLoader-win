package org.limongradstudio.catchy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException

class CommandRunner(
    private val commands: List<String>,
) {
    private val pb =
        ProcessBuilder(commands).apply {
            redirectErrorStream(true)
        }

    private val outputs = mutableListOf<String>()
    private var content: String? = null

    suspend fun readText() =
        withContext(Dispatchers.IO) {
            return@withContext try {
                val process = pb.start()
                content = process.inputStream.bufferedReader().readText()
                process.waitFor()
                content
            } catch (e: IOException) {
                println("error while reading")
                null
            }
        }

    fun start() =
        flow {
            try {
                val process = pb.start()
                process.inputStream.bufferedReader().useLines { it.forEach { line -> emit(line) } }
                process.waitFor()
            } catch (e: Exception) {
                println(e)
            }
        }.flowOn(Dispatchers.IO)
}
