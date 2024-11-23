package org.limongradstudio.catchy.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.SystemTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.limongradstudio.catchy.ApiResult
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.net.HttpURLConnection
import java.net.URL


@OptIn(InternalComposeUiApi::class)
@Composable
fun VideoInfoBox(
  modifier: Modifier = Modifier, download: () -> Unit, videoInfo: VideoInfo
) {
  println(videoInfo.thumbnail)
  val image = loadImage(videoInfo.thumbnail ?: "")
  val show = remember { mutableStateOf(true) }
  Column(Modifier.fillMaxSize()) {
    val theme = LocalSystemTheme.current
    println(theme)
    if (show.value) {
      Dialog(onDismissRequest = {
        show.value = false
      }) {
        Surface(
          modifier = Modifier.padding(8.dp),
          shape = RoundedCornerShape(8.dp),
          shadowElevation = .6f,
          color = MiuixTheme.colorScheme.background,
        ) {
          val textColor = if (theme == SystemTheme.Dark) Color.White else Color.Blue
          Column(Modifier.padding(16.dp)) {
            when (image.value) {
              is ApiResult.Error -> Text("Error loading thumbnail")
              ApiResult.Loading -> {
                CircularProgressIndicator()
              }

              is ApiResult.Success -> {
                Image(
                  bitmap = (image.value as ApiResult.Success<ImageBitmap>).data,
                  contentDescription = null
                )
                Text(videoInfo.title ?: "", color = textColor)
                Text(videoInfo.averageRating ?: "", color = textColor)
              }
            }
          }
        }
      }
    }
  }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun loadImage(url: String): State<ApiResult<ImageBitmap>> {
  val image = produceState<ApiResult<ImageBitmap>>(ApiResult.Loading, key1 = url) {
    if (url.isEmpty()) {
      ApiResult.Error("No URL")
    }
    val x: ByteArray? = getNetworkImage(url)
    value = if (x != null) {
      ApiResult.Success(x.decodeToImageBitmap())
    } else {
      ApiResult.Error("Error loading image")
    }
  }
  return image
}


suspend fun getNetworkImage(resource: String): ByteArray? = withContext(Dispatchers.IO) {
  try {
    // Create a URL instance directly
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

@Preview
@Composable
fun FormatPicker(modifier: Modifier = Modifier) {
}

