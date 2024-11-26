package org.limongradstudio.catchy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.limongradstudio.catchy.components.AppBottomBar
import org.limongradstudio.catchy.screens.DownloadsScreen
import org.limongradstudio.catchy.screens.Home
import org.limongradstudio.catchy.screens.SettingsScreen
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.darkColorScheme
import top.yukonga.miuix.kmp.theme.lightColorScheme

@Composable
fun App() {
  val currentRoute = remember { mutableStateOf(0) }
  val updateCurrentRoute = { newRoute: Int -> currentRoute.value = newRoute }
  LaunchedEffect(Unit) {
    launch(Dispatchers.IO) { setup() }
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
          0 -> {
            Home(
              modifier = Modifier.fillMaxSize(),
              url = vm.url.value,
              event = vm::onEvent,
              mediaInfo = vm.mediaInfo,
            )
          }

          1 -> DownloadsScreen()
          2 -> SettingsScreen()
        }
      }
    }
  }
}

