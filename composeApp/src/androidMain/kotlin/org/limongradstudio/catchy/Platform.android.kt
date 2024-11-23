package org.limongradstudio.catchy

import android.os.Build
import kotlinx.coroutines.flow.Flow

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()


actual suspend fun extractMediaInfo(url: String): String? {
    TODO("Not yet implemented")
}

actual suspend fun downloadMedia(
    url: String,
    selectedFormatId: Int
): Flow<String> {
    TODO("Not yet implemented")
}