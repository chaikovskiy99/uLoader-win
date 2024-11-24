package org.limongradstudio.catchy

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.limongradstudio.catchy.data.remote.models.VideoInfo

private val json = Json {
  isLenient = true // Enables lenient parsing
  ignoreUnknownKeys = true // Ignores unknown keys in the JSON
  coerceInputValues = true // Handles unexpected nulls in non-nullable fields
}

sealed class ApiResult<out T> {
  data class Success<R>(val data: R) : ApiResult<R>()
  data class Error(val message: String) : ApiResult<Nothing>()
  data object Loading : ApiResult<Nothing>()
}

class AppViewModel : ViewModel() {
  private val _selectedVideoInfo: MutableState<String> = mutableStateOf("")

  init {
    ClipBoardMonitor {
      updateUrl(it)
    }
  }

  val mediaInfo = Channel<ApiResult<VideoInfo>?>(1)
  val selectedVideoInfo: MutableState<String>
    get() = _selectedVideoInfo

  private var _url: MutableState<String> = mutableStateOf("")
  val url: State<String> = _url

  fun onEvent(event: AppEvent){
    when(event){
      is AppEvent.DownloadEvent ->  download()
      AppEvent.GetMediaInfoEvent -> onClickGetInfo()
      is AppEvent.UpdateFormatEvent -> updateSelection(event.formatId)
      is AppEvent.UpdateUrlEvent -> updateUrl(event.url)
    }
  }

  private fun updateSelection(videoInfo: String) {
    _selectedVideoInfo.value = videoInfo
  }

  private fun updateUrl(newUrl: String) {
    _url.value = newUrl
  }

  private fun download() {
    viewModelScope.launch {
      downloadMedia(_url.value, 1).collect {
        println(it)
      }
    }
  }

  private fun onClickGetInfo() {
    viewModelScope.launch {
      try {
        mediaInfo.send(ApiResult.Loading)
        val deferred = async(Dispatchers.IO) { extractMediaInfo(_url.value) }
        val jsonData = deferred.await()

        mediaInfo.send(ApiResult.Success(json.decodeFromString<VideoInfo>(jsonData!!)))
      } catch (e: Exception) {

        mediaInfo.send(ApiResult.Error("Something went wrong!! Please try again!"))
      }
    }
  }
}

sealed class AppEvent {
  data object DownloadEvent : AppEvent()
  data object  GetMediaInfoEvent : AppEvent()
  data class UpdateFormatEvent(val formatId: String) : AppEvent()
  data class UpdateUrlEvent(val url: String) : AppEvent()
}



