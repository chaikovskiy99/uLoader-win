package org.limongradstudio.catchy

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.limongradstudio.catchy.data.remote.models.Formats
import org.limongradstudio.catchy.data.remote.models.VideoData
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import java.nio.file.Paths

const val YT_BINARY_URL = "https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp.exe"
const val YT_DLP = "yt-dlp.exe"
const val FFMPEG = "ffmpeg.exe"
const val TMP_DIR_KEY = "java.io.tmpdir"

class HomeViewModel : ViewModel() {
    val showDialog = mutableStateOf(false)
    private val out = mutableStateOf<List<String>>(emptyList())
    val selectedItem = mutableStateOf<Formats?>(null)
    val allVideoFormats = mutableStateListOf<VideoData>()
    val url = mutableStateOf("")
    val dropState = mutableStateOf(false)
    val downloadOut = mutableStateOf("")
    var videoInfo = mutableStateOf<VideoInfo?>(null)
    val loading = mutableStateOf(false)

    init {
        println("called init")
        ClipBoardMonitor { newText ->
            updateText(newText)
        }
    }

    private val json =
        Json {
            isLenient = true // Enables lenient parsing
            ignoreUnknownKeys = true // Ignores unknown keys in the JSON
            coerceInputValues = true // Handles unexpected nulls in non-nullable fields
        }

    fun extractVideoInfo() {
        loading.value = true
        val command = CommandRunner(listOf("yt-dlp", "-j", url.value))
        viewModelScope.launch(Dispatchers.IO) {
            val text = command.readText()
            loading.value = false
            videoInfo.value = json.decodeFromString<VideoInfo>(text!!)
            showDialog.value = true
        }
    }

    suspend fun onDownload() {
        val path = Paths.get(System.getProperty("user.home"), "Desktop").toAbsolutePath()

        CommandRunner(
            listOf(
                "yt-dlp",
                "-f",
                "${selectedItem.value?.formatId}+140",
                "-o",
                "$path/%(title)s.%(ext)s",
                url.value,
            ),
        ).start().collect {
            downloadOut.value = "saving video in $path\n$it"
        }
    }

    fun updateDropState(value: Boolean) = value.also { dropState.value = value }

    fun updateDialogState(value: Boolean) {
        showDialog.value = value
    }

    fun updateText(newValue: String) {
        url.value = newValue
    }

    fun updateSelectedItem(newVal: Formats) {
        selectedItem.value = newVal
    }
}

