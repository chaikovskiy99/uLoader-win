package org.limongradstudio.catchy.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.limongradstudio.catchy.ApiResult
import org.limongradstudio.catchy.AppEvent
import org.limongradstudio.catchy.components.AppButton
import org.limongradstudio.catchy.components.loadImage
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun Home(
  modifier: Modifier = Modifier,
  url: String,
  event: (AppEvent) -> Unit,
  mediaInfo: Channel<ApiResult<VideoInfo>?>,
  parentWindowState: WindowState
) {

  val defaultInnerPadding = DpSize(8.dp, 10.dp)

  val videoInfo = mediaInfo.receiveAsFlow().collectAsState(null)

  Column(
    modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    TextField(
      url,
      { event(AppEvent.UpdateUrlEvent(it)) },
      modifier = Modifier.fillMaxWidth(),
      maxLines = 1,
      insideMargin = defaultInnerPadding
    )
    val show = remember { mutableStateOf(true) }
    Row(
      Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
      AppButton(onClick = {
        event(AppEvent.GetMediaInfoEvent)
        show.value = true
      }, btnText = "Extract Media")
    }

    Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
      when (videoInfo.value) {
        is ApiResult.Error -> {
          Card {
            Text("Something Wrong")
          }
        }

        ApiResult.Loading -> {
          Card(insideMargin = PaddingValues(20.dp)) {
            Row(horizontalArrangement = Arrangement.Center) { Text("Loading....") }
            Row(horizontalArrangement = Arrangement.Center) { CircularProgressIndicator() }
          }
        }

        is ApiResult.Success -> {
          val media = (videoInfo.value as ApiResult.Success<VideoInfo>).data
          if (show.value) {
            Dialog(onDismissRequest = { show.value = false }) {
              Surface(color = Color.Gray, contentColor = contentColorFor(Color.Gray)) {
                Box(modifier = Modifier.padding(16.dp)) {
                  val image = loadImage(media.thumbnail ?: "")
                  when (image.value) {
                    is ApiResult.Error -> Text("Error loading thumbnail")
                    ApiResult.Loading -> {
                      CircularProgressIndicator()
                    }

                    is ApiResult.Success -> {
                      val imageBitmap = (image.value as ApiResult.Success<ImageBitmap>).data
                      val expanded = remember { mutableStateOf(false) }
                      Column {
                        Box(modifier = Modifier.fillMaxSize().wrapContentSize(Alignment.TopStart)) {
                          androidx.compose.material3.DropdownMenu(expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }) {
                            media.videoFormats?.forEach {
                              DropdownMenuItem(
                                text = { Text(it.formatId.toString(), color = Color.Blue) },
                                onClick = {})
                            }
                          }
                        }
                        Image(imageBitmap, "")
                        Row(
                          Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End,
                        ) {
                          AppButton(onClick = {
                            show.value = false
                            println("Show value: ${show.value}")

                          }, btnText = "Cancel")
                          AppButton(
                            onClick = { event(AppEvent.DownloadEvent) },
                            btnText = "Download"
                          )
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }

        null -> {}
      }
    }
  }
}




