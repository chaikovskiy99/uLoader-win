package org.limongradstudio.catchy

import org.limongradstudio.catchy.data.remote.models.VideoData
import java.io.BufferedReader
import java.io.FileReader
import java.util.regex.Pattern

interface ITextProcessor<T> {
    fun processText(text: String): T
}

class ProcessText(
    private val lines: List<String>,
) : ITextProcessor<VideoData> {
    companion object {
        private const val FORMAT_REGEX = "(?<formatID>^\\w+)\\s"
        private const val EXT_REGEX = "^\\S+\\s+(\\S+)\\s"
        private const val RESOLUTION_REGEX = "^\\S+\\s+\\S+\\s+(\\d+x\\d+)\\s+"
        private const val FILE_SIZE_REGEX = "\\W?\\d+(\\.\\d+)?(GiB|MiB)"
        private const val VBR_REGEX = "(\\d+k\\s)video"

        val FORMAT_PATTERN: Pattern = Pattern.compile(FORMAT_REGEX)
        val EXT_PATTERN: Pattern = Pattern.compile(EXT_REGEX)
        val RESOLUTION_PATTERN: Pattern = Pattern.compile(RESOLUTION_REGEX)
        val SIZE_PATTERN: Pattern = Pattern.compile(FILE_SIZE_REGEX)
        val VBR_PATTERN: Pattern = Pattern.compile(VBR_REGEX)
    }

    fun getInfo(): List<VideoData> {
        val videoDataList = mutableListOf<VideoData>()
        lines.forEach { line ->
            videoDataList.add(processText(line))
        }
        return videoDataList.filterNot { it.formatID == null || it.formatID == "ID" }
    }

    override fun processText(text: String): VideoData {
        var data = VideoData()
        val matchFormat = FORMAT_PATTERN.matcher(text)
        val matchExt = EXT_PATTERN.matcher(text)
        val matchResolution = RESOLUTION_PATTERN.matcher(text)
        val size = SIZE_PATTERN.matcher(text)
        val vbr = VBR_PATTERN.matcher(text)

        if (matchFormat.find()) {
            data = data.copy(formatID = matchFormat.group(1))
        }
        if (matchExt.find()) {
            data = data.copy(ext = matchExt.group(1))
        }
        if (matchResolution.find()) {
            data = data.copy(resolution = matchResolution.group(1))
        }
        if (size.find()) {
            data = data.copy(fileSize = size.group(0))
        }
        if (vbr.find()) {
            data = data.copy(vbr = vbr.group(1))
        }
        return data
    }
}

fun main() {
    val path = System.getProperty("user.home")
    val lines = BufferedReader(FileReader("$path/Desktop/pattern.txt")).readLines()
    val processVid = ProcessText(lines)
    val result = processVid.getInfo()
    println(result)
}


class JSONTextProcessor : ITextProcessor<VideoData> {
    override fun processText(text: String): VideoData {
        TODO("Not yet implemented")
    }
}
