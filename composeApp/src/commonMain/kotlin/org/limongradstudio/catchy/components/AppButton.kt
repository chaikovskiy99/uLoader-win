package org.limongradstudio.catchy.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.limongradstudio.catchy.VoidFun
import top.yukonga.miuix.kmp.basic.Button
import top.yukonga.miuix.kmp.basic.Text

@Composable
fun AppButton(modifier: Modifier = Modifier, onClick: VoidFun, btnText: String) {
  Button(
    insideMargin = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    onClick = onClick
  ) {
    Text(btnText)
  }
}


