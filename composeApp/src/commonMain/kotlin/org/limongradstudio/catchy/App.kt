package org.limongradstudio.catchy

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import catchy.composeapp.generated.resources.Res
import catchy.composeapp.generated.resources.img
import org.jetbrains.compose.resources.imageResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.limongradstudio.catchy.components.AppBottomBar

import org.limongradstudio.catchy.screens.Home
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

enum class BottomNavRoute(val route: String) {
  Home("Home"), Downloads("Downloads"), Settings("Settings")
}

@Composable
@Preview
fun App() {
  val currentRoute = remember { mutableStateOf("Home") }

  val updateCurrentRoute = { route: String ->
    currentRoute.value = route
  }

  MiuixTheme(
    colors = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
  ) {
    Scaffold(
      bottomBar = {
        AppBottomBar(currentRoute = currentRoute.value, routeUpdater = updateCurrentRoute)
      },
    ) {
      val vm: AppViewModel = viewModel()
      Box(Modifier.fillMaxSize().padding(it)) {
        when (currentRoute.value) {
          "Home" -> {
            Home(
              modifier = Modifier.fillMaxSize(),
              url = vm.url.value,
              onUpdateUrl = vm::updateUrl,
              onSearchClicked = vm::onClickGetInfo,
              videoInfo = vm.videoInfo.value,
              download = vm::download
            )
          }

          "Downloads" -> {
//            DownloadsScreen()
          }

          "Settings" -> {
//            Settings()
          }
        }

        val bitmap = imageResource(Res.drawable.img)
        Image(
          modifier = Modifier.blur(
            radius = 16.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded
          ).alpha(0.3f), contentScale = ContentScale.FillBounds,

          bitmap = bitmap, contentDescription = null
        )
      }
    }
  }
}

