package org.limongradstudio.catchy.components


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.InternalComposeUiApi
import androidx.compose.ui.LocalSystemTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.window.DialogWindow
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.limongradstudio.catchy.ApiResult
import org.limongradstudio.catchy.VoidFun
import org.limongradstudio.catchy.VoidParamFun
import org.limongradstudio.catchy.data.remote.getNetworkImage
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDropdown


@OptIn(InternalComposeUiApi::class)
@Composable
fun MediaInfoDialog(
  modifier: Modifier = Modifier,
  download: VoidFun, videoInfo: VideoInfo,
  onSelect: VoidParamFun<String>,
) {
  println(videoInfo.thumbnail)
  val image = loadImage(videoInfo.thumbnail ?: "")
  val show = remember { mutableStateOf(true) }
  val theme = LocalSystemTheme.current
  println(theme)

  DialogWindow(
    create = {
      ComposeDialog()
    },
    dispose = {it.dispose()},
    content = {
      Text("hello")
      when (image.value) {
        is ApiResult.Error -> Text("Error loading thumbnail")
        ApiResult.Loading -> {
          CircularProgressIndicator()
        }

        is ApiResult.Success -> {
          Column {
            FormatPicker(videoInfo = videoInfo, onSelect = onSelect, visible = show.value)
            Image(
              bitmap = (image.value as ApiResult.Success<ImageBitmap>).data,
              contentDescription = null
            )
            Row(
              Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End,
            ) {
              AppButton(onClick = {
                show.value = false
                println("Show value: ${show.value}")

              }, btnText = "Cancel")
              AppButton(onClick = {}, btnText = "Download")
            }
          }
        }
      }

    },
  )
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


@Composable
fun FormatPicker(
  modifier: Modifier = Modifier, videoInfo: VideoInfo, onSelect: VoidParamFun<String>, visible: Boolean
) {

  val selectedIndex = remember { mutableStateOf(0) }

  SuperDropdown(title = "",
    modifier = Modifier.fillMaxWidth(),
    items = videoInfo.videoFormats?.map { it.format ?: "" } ?: emptyList(),
    selectedIndex = selectedIndex.value,
    onSelectedIndexChange = { selectedIndex.value = it })
}

