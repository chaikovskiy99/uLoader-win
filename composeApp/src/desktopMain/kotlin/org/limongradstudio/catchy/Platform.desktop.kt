package org.limongradstudio.catchy

import io.ktor.client.request.get
import io.ktor.client.statement.readRawBytes
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentLength
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.limongradstudio.catchy.data.remote.Network
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Paths
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

actual fun getMediaParser(): MediaParser = DesktopMediaParser()

class DesktopMediaParser() : MediaParser {

  companion object {
    const val BEST_AUDIO = "bestaudio"
    const val BEST_VIDEO = "bestvideo"
  }

  override suspend fun <T> extractInfo(resource: String): T? {
    val command = CommandRunner(listOf("yt-dlp", "-j", resource))
    val text = command.readText()
    return text as T
  }

  override suspend fun <T> download(resource: String, selectedFormatId: Int): T {
    val path = Paths.get(System.getProperty("user.home"), "Desktop").toAbsolutePath()
    return CommandRunner(
      listOf(
        "yt-dlp",
        "-f",
        "${if (selectedFormatId > 0) selectedFormatId else BEST_VIDEO} +$BEST_AUDIO",
        "-o",
        "$path/%(title)s.%(ext)s",
        resource,
      ),
    ).start() as T
  }
}


actual suspend fun setup(force: Boolean) {
  val urls = listOf(
    FFMPEG_BIN to "ffmpeg.zip",
    YT_BINARY_URL to "yt-dlp.exe"
  )
  // Persistent folder for storing binaries
  if (!System.getProperty("os.name").contains("Windows", ignoreCase = true))
    return;
  val appDataFolder = Paths.get(System.getenv("LOCALAPPDATA"), "MyApp", "binaries").toAbsolutePath()
  appDataFolder.createDirectories()

  coroutineScope {
    for ((url, fileName) in urls) {
      launch {
        val filePath = appDataFolder.resolve(fileName)

        // Download and handle the file
        if (!filePath.exists() || force) {
          println("Downloading $fileName...")
          try {
            val response = Network.client.get(url)
            if (response.status != HttpStatusCode.OK) {
              error("Failed to download $fileName from $url")
            }

            val size = response.contentLength() ?: 0
            response.readRawBytes().inputStream().buffered().readWithDetails(
              filePath.toString(), totalSize = size
            ) { total, downloaded ->
              println("Progress: $fileName - $downloaded / $total bytes")
            }
            println("$fileName downloaded successfully.")
          } catch (e: Exception) {
            println("Error downloading $fileName: ${e.message}")
          }

          // Extract if it's a ZIP
          if (fileName.endsWith(".zip")) {
            try {
              println("Extracting $fileName...")
              extractZip(filePath.toString(), appDataFolder.toString())
              println("$fileName extracted successfully.")
            } catch (e: Exception) {
              println("Error extracting $fileName: ${e.message}")
            }
          }
        } else {
          println("$fileName already exists. Skipping download.")
        }
      }
    }

    // Add extracted folder to PATH environment variable
    println("Setting up environment variables...")
    try {
      addToEnvironmentPath(appDataFolder.toString())
      println("Environment variable PATH updated successfully.")
    } catch (e: Exception) {
      println("Error updating environment variables: ${e.message}")
    }

    // Check library versions (Placeholder)
    println("Checking versions...")
    // TODO: Add version check logic here
  }

  println("Setup complete. Ready to go!")
}

// Function to update PATH environment variable on Windows
fun addToEnvironmentPath(newPath: String) {
  if (System.getProperty("os.name").contains("Windows", ignoreCase = true)) {
    val processBuilder = ProcessBuilder(
      "cmd.exe", "/c",
      "setx PATH \"%PATH%;$newPath\""
    )
    val process = processBuilder.start()
    if (process.waitFor() != 0) {
      throw Exception("Failed to update PATH environment variable")
    }
  } else {
    println("Environment variable update is only implemented for Windows.")
  }
}


fun BufferedInputStream.readWithDetails(
  outPath: String, totalSize: Long, progressUpdate: DownloadProgress<Long>
) {
  use { input ->
    var read = 0
    val buffer = ByteArray(4096)
    println("totalBytes $totalSize")
    var readBytes: Long = 0L
    BufferedOutputStream(FileOutputStream(outPath)).use { output ->
      while ((input.read(buffer).also { bytes -> read = bytes }) != -1) {
        output.write(buffer, 0, read)
        readBytes += read
        progressUpdate(totalSize, readBytes)
      }
    }
  }
}

fun extractZip(zipInput: String, outputPath: String) {
  try {
    ZipInputStream(FileInputStream(zipInput)).use { zis ->
      var entry: ZipEntry? = zis.nextEntry
      while (entry != null) {
        val fileName = entry.name
        val file = File(outputPath, fileName)

        // Create parent directories if needed
        if (entry.isDirectory) {
          file.mkdirs()
        } else {
          file.parentFile.mkdirs() // Ensure parent directory exists
          FileOutputStream(file).use { fos ->
            zis.copyTo(fos)
          }
        }
        zis.closeEntry()
        entry = zis.nextEntry
      }
    }
  } catch (e: IOException) {
    // Handle the exception (e.g., log, rethrow, or display an error message)
    println("Error extracting ZIP: ${e.message}")
  }
}


