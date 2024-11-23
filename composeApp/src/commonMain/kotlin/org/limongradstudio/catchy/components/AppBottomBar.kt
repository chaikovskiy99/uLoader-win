package org.limongradstudio.catchy.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.contentColorFor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import catchy.composeapp.generated.resources.Res
import catchy.composeapp.generated.resources.downloads
import org.jetbrains.compose.resources.imageResource
import org.limongradstudio.catchy.BottomNavRoute
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun AppBottomBar(
  modifier: Modifier = Modifier, currentRoute: String, routeUpdater: (route: String) -> Unit
) {
  val tintColor = if (isSystemInDarkTheme()) Color.White else Color.Black
  BottomNavigation(
    contentColor = contentColorFor(MiuixTheme.colorScheme.background),
    backgroundColor = MiuixTheme.colorScheme.background
  ) {

    BottomNavigationItem(selected = currentRoute === BottomNavRoute.Home.route, onClick = {
      routeUpdater(BottomNavRoute.Home.route)
    }, label = { Text(BottomNavRoute.Home.route) }, icon = {
      Icon(
        imageVector = Icons.Default.Home, contentDescription = null, tint = tintColor
      )
    })
    val bitmap = imageResource(Res.drawable.downloads)
    BottomNavigationItem(selected = currentRoute === BottomNavRoute.Downloads.route, onClick = {
      routeUpdater(BottomNavRoute.Downloads.route)
    }, label = { Text(BottomNavRoute.Downloads.route) }, icon = {
      Icon(
        bitmap = bitmap,
        modifier = Modifier.size(24.dp),
        contentDescription = null,
        tint = tintColor
      )

    })
//                    val bitmap = imageResource(Res.drawable.downloads)
    BottomNavigationItem(selected = currentRoute === BottomNavRoute.Settings.route,
      label = { Text(BottomNavRoute.Settings.route) },
      onClick = {
        routeUpdater(BottomNavRoute.Settings.route)
      },
      icon = {
        Icon(
          imageVector = Icons.Default.Settings, contentDescription = null, tint = tintColor
        )
      })
  }
}