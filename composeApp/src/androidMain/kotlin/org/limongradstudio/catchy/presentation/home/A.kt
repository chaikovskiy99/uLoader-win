package org.limongradstudio.catchy.presentation.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.SuperDialog
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Preview
@Composable
fun ADialog(modifier: Modifier = Modifier) {
  val state = remember { mutableStateOf(false) }
  MiuixTheme {
    SuperDialog(
      show = state,
      title = "Hello",
      onDismissRequest = { state.value = false },
      outsideMargin = DpSize(16.dp, 16.dp)


    ) {
      Text("hello")
    }
  }
}

@Preview
@Composable
fun APP(modifier: Modifier = Modifier) {
  MaterialTheme {
    Column {
      Text("hello")
    }
  }
}