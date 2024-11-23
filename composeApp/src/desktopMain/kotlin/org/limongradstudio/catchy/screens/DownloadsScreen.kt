package org.limongradstudio.catchy.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.TextField

@Composable
fun DownloadsScreen(modifier: Modifier = Modifier) {
  Column(modifier = modifier.padding(16.dp)) {
    TextField("Downloads", {}, modifier = Modifier.fillMaxWidth())
  }
}