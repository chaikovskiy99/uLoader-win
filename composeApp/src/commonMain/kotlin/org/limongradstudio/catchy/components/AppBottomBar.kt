package org.limongradstudio.catchy.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem

@Composable
fun AppBottomBar(
  modifier: Modifier = Modifier,
  currentRoute: Int,
  routeUpdater: (route: Int) -> Unit
) {

  NavigationBar(listOf(
    NavigationItem(label = "Home", Icons.Default.Home),
    NavigationItem(label = "Downloads", Icons.Default.ArrowDropDown),
    NavigationItem(label = "Settings", Icons.Default.Settings),
  ),
    showDivider = true,
    selected = currentRoute,
    onClick = { routeUpdater(it) }
  )
}