package org.limongradstudio.catchy

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.coroutines.flow.Flow
import top.yukonga.miuix.kmp.basic.Text

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}
actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getMediaParser(): MediaParser = AndroidMediaParser()

class AndroidMediaParser : MediaParser {
    override suspend fun <T> extractInfo(resource: String): T? {
        return "" as T;
    }

    override suspend fun <T> download(resource: String, selectedFormatId: Int): T {
        TODO("Not yet implemented")
    }

}

actual suspend fun setup(force: Boolean){
    TODO()
}