package org.limongradstudio.catchy.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import org.limongradstudio.catchy.ApiResult
import org.limongradstudio.catchy.components.VideoInfoBox
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import kotlin.reflect.KFunction0

@Composable
fun Home(
  modifier: Modifier = Modifier,
  url: String,
  onUpdateUrl: (String) -> Unit,
  onSearchClicked: () -> Unit,
  videoInfo: ApiResult<VideoInfo>? = null,
  download: KFunction0<Unit>
) {
  val defaultInnerPadding = DpSize(8.dp, 10.dp)
  Column(
    modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    TextField(
      url,
      { onUpdateUrl(it) },
      modifier = Modifier.fillMaxWidth(),
      maxLines = 1,
      insideMargin = defaultInnerPadding
    )
    Row(
      Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
      Button(insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        onClick = { onSearchClicked() }) {
        Text("Get Media")
      }
    }
    Box(Modifier.fillMaxWidth().weight(1f)) {
      when (videoInfo) {
        is ApiResult.Error -> {
          Text("Something Wrong")
        }

        ApiResult.Loading -> {
          CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        is ApiResult.Success -> {
          VideoInfoBox(download = download, videoInfo = videoInfo.data)
        }
        null -> {}
      }
    }
  }
}




