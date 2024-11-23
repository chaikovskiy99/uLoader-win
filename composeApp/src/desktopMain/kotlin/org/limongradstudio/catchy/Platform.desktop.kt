package org.limongradstudio.catchy

import kotlinx.coroutines.flow.Flow
import java.nio.file.Paths

actual suspend fun extractMediaInfo(url: String): String? {
    val command = CommandRunner(listOf("yt-dlp", "-j", url))
    val text = command.readText()
    return text
}

actual suspend fun downloadMedia(
    url: String,
    selectedFormatId: Int,
): Flow<String> {
    val path = Paths.get(System.getProperty("user.home"), "Desktop").toAbsolutePath()
    return CommandRunner(
        listOf(
            "yt-dlp",
            "-f",
            "$selectedFormatId+140",
            "-o",
            "$path/%(title)s.%(ext)s",
            url,
        ),
    ).start()
}