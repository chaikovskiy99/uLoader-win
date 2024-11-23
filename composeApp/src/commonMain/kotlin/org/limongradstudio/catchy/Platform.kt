package org.limongradstudio.catchy

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect suspend fun extractMediaInfo(url: String) : String?

expect suspend fun downloadMedia(url: String, selectedFormatId: Int) : Flow<String>
