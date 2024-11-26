package org.limongradstudio.catchy
import java.awt.Toolkit
import java.awt.datatransfer.Clipboard
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import kotlin.concurrent.fixedRateTimer

/**
 * find if relevant commands or binaries are installed
 * if installed check it's version and then check for latest version from repo
 * if the versions don't match download the latest version from the repo
 *
 */

class ClipBoardMonitor(
    private val onTextChange: (String) -> Unit,
) {
    private val clipboard: Clipboard = Toolkit.getDefaultToolkit().systemClipboard
    private var previousContent: String? = null

    // Regex pattern to match URLs (both HTTP/HTTPS)
    private val urlPattern = Regex("https?://[\\w-]+(\\.[\\w-]+)+[/#?]?.*$")
    private val test = Regex("(?<=k)\\s")

    init {
        startMonitoring()
    }

    private fun startMonitoring() {
        fixedRateTimer("clipboard-monitor", false, 200L, 2000L) {
            val currentContent = getClipboardText()
            if (previousContent != currentContent && isUrl(currentContent)) {
                previousContent = currentContent
                if (currentContent != null) {
                    onTextChange(currentContent)
                }
            }
        }
    }

    private fun getClipboardText(): String? =
        try {
            val contents: Transferable = clipboard.getContents(null)
            if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                contents.getTransferData(DataFlavor.stringFlavor) as String
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }

    private fun isUrl(text: String?) = text?.let { urlPattern.matches(it) } == true
}
