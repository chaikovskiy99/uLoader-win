package org.limongradstudio.catchy.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import org.jetbrains.skia.Bitmap
import org.jetbrains.skia.Image
import org.limongradstudio.catchy.ApiResult
import org.limongradstudio.catchy.AppEvent
import org.limongradstudio.catchy.components.AppButton
import org.limongradstudio.catchy.data.remote.getNetworkImage
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.extra.SuperDropdown
import top.yukonga.miuix.kmp.extra.SuperSwitch
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissDialog

@Composable
fun Home(
  modifier: Modifier = Modifier,
  url: String,
  event: (AppEvent) -> Unit,
  mediaInfo: Channel<ApiResult<VideoInfo>?>,
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
          val dialogState = mutableStateOf(true)
          if (show.value) {
            dialog2(dialogState, media)
          }
        }

        null -> {}
      }
    }
  }
}


@Composable
fun dialog2(showDialog: MutableState<Boolean>, videoInfo: VideoInfo) {
  val dropdownOptions = videoInfo.videoFormats?.map { it.formatId ?: "" } ?: emptyList()
  val dropdownSelectedOption = remember { mutableStateOf(0) }
  var miuixSuperSwitchState by remember { mutableStateOf(false) }
  val imageState = remember { mutableStateOf<ImageBitmap?>(null) }
  if(videoInfo.thumbnail != null){
    LaunchedEffect(Unit){
      val bytes=  getNetworkImage(videoInfo.thumbnail!!)
      if(bytes != null){
        println(bytes)
        imageState.value = Image.makeFromEncoded(bytes).toComposeImageBitmap()
      }
    }
  }


  SuperDialog(
    title = "Dialog 2",
    show = showDialog,
    onDismissRequest = {
      dismissDialog(showDialog)
    }
  ) {
    Card(
      color = MiuixTheme.colorScheme.secondaryContainer,
    ) {
      if(imageState.value != null){
        println(imageState.value)
        Image(
          bitmap = imageState.value!!,
          contentDescription = "Thumbnail",
        )
      }
      SuperDropdown(
        title = "Dropdown",
        items = dropdownOptions,
        selectedIndex = dropdownSelectedOption.value,
        onSelectedIndexChange = { newOption -> dropdownSelectedOption.value = newOption },
        defaultWindowInsetsPadding = false
      )
      SuperSwitch(
        title = "Switch",
        checked = miuixSuperSwitchState,
        onCheckedChange = {
          miuixSuperSwitchState = it
        }
      )
    }
    Spacer(Modifier.height(12.dp))
    Row(
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      TextButton(
        text = "Cancel",
        onClick = {
          dismissDialog(showDialog)
        },
        modifier = Modifier.weight(1f)
      )
      Spacer(Modifier.width(20.dp))
      TextButton(
        text = "Confirm",
        onClick = {
          dismissDialog(showDialog)
        },
        modifier = Modifier.weight(1f),
        colors = ButtonDefaults.textButtonColorsPrimary()
      )
    }
  }
}
