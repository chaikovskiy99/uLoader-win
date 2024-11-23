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

private val json =
  Json {
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
  private val _selectedVideoInfo: MutableState<VideoInfo?> = mutableStateOf(null)

  init {
    ClipBoardMonitor {
      updateUrl(it)
    }
  }

  val mediaInfo = Channel<ApiResult<VideoInfo>?>(1)

  val selectedVideoInfo: MutableState<VideoInfo?>
    get() = _selectedVideoInfo
  private val _videoInfo = mutableStateOf<ApiResult<VideoInfo>?>(null)
  val videoInfo: MutableState<ApiResult<VideoInfo>?>
    get() = _videoInfo

  private var _url: MutableState<String> = mutableStateOf("")
  val url: State<String> = _url


  fun updateSelection(videoInfo: VideoInfo) {
    _selectedVideoInfo.value = videoInfo
  }

  fun updateUrl(newUrl: String) {
    _url.value = newUrl
  }

  fun download() {
    viewModelScope.launch {
      downloadMedia(_url.value, 1).collect {
        println(it)
      }
    }
  }

  fun onClickGetInfo() {
    viewModelScope.launch {
      try {
        _videoInfo.value = ApiResult.Loading
        mediaInfo.send(ApiResult.Loading)
        val deferred = async(Dispatchers.IO) { extractMediaInfo(_url.value) }
        val jsonData = deferred.await()
        _videoInfo.value = ApiResult.Success(json.decodeFromString<VideoInfo>(jsonData!!))
        mediaInfo.send(ApiResult.Success(json.decodeFromString<VideoInfo>(jsonData!!)))
      } catch (e: Exception) {
        _videoInfo.value = ApiResult.Error("Something went wrong!! Please try again!")
        mediaInfo.send(ApiResult.Error("Something went wrong!! Please try again!"))
      }
    }
  }
}