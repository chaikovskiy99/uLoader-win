package org.limongradstudio.catchy.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.res.loadXmlImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import org.limongradstudio.catchy.data.remote.models.Formats
import org.limongradstudio.catchy.data.remote.models.VideoInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.limongradstudio.catchy.HomeViewModel
import org.xml.sax.InputSource
import top.yukonga.miuix.kmp.theme.MiuixTheme
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Paths
import javax.swing.JFileChooser
import javax.swing.filechooser.FileSystemView


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel,
) {
    val tabs = listOf("Home", "Downloads", "Settings")
    val scope = rememberCoroutineScope()
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextField(viewModel.url.value, { viewModel.updateText(it) }, modifier = Modifier.weight(1f, true))
            OutlinedButton(onClick = {
                viewModel.extractVideoInfo()
            }) {
                Text("Get Info")
            }
        }

        if (viewModel.showDialog.value) {
            FormatDialog(
                scope = scope,
                updateDialog = viewModel::updateDialogState,
                dropState = viewModel.dropState.value,
                updateDropState = viewModel::updateDropState,
                onDownload = viewModel::onDownload,
                data = viewModel::videoInfo.get().value,
                showSettingsDialog = viewModel.showDialog.value,
                selectedVideoData = viewModel.selectedItem.value,
                setSelectedVideoData = viewModel::updateSelectedItem,
            )
        }

        val selectedTabIndex = remember { mutableStateOf(0) }

        TabRow(
            selectedTabIndex = selectedTabIndex.value,
            backgroundColor = androidx.compose.ui.graphics.Color.LightGray,
            modifier = Modifier.fillMaxWidth(),
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex.value]),
                )
            },
            divider = {
                TabRowDefaults.Divider()
            },
        ) {
            tabs.forEachIndexed { index, tab ->
                Tab(
                    selected = index == selectedTabIndex.value,
                    onClick = {
                        selectedTabIndex.value = index
                    },
                    text = { Text(tab) },
                )
            }
        }

        when (selectedTabIndex.value) {
            0 -> {
                if (viewModel.loading.value) {
                    Column(
                        Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(
                                rememberScrollState(),
                            ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        if (viewModel.downloadOut.value.isNotEmpty()) {
                            Text(viewModel.downloadOut.value)
                        }
                    }
                }
            }

            1 ->
                Column {
                }

            2 -> {
                SettingsTab()
            }
        }
    }
}



@Composable
private fun PreviewHomeScreen() {
//    HomeScreen()
}


@Composable
fun FormatDialog(
    modifier: Modifier = Modifier,
    dropState: Boolean = false,
    updateDropState: (boolean: Boolean) -> Unit = {},
    showSettingsDialog: Boolean = false,
    onDownload: suspend () -> Unit = {},
    updateDialog: (Boolean) -> Unit = {},
    selectedVideoData: Formats? = null,
    setSelectedVideoData: (Formats) -> Unit = {},
    data: VideoInfo? = null,
    scope: CoroutineScope,
) {
    Dialog(
        onDismissRequest = { updateDialog(false) },
        content = {
            Surface(elevation = 16.dp, shape = RoundedCornerShape(5)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (data != null) {
                        Text("${data.fileName}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("Duration: ${data.durationString ?: ""}", maxLines = 1, overflow = TextOverflow.Ellipsis)
                        AsyncImage(
                            load = {
                                loadImageBitmap(data.thumbnail!!)
                            },
                            painterFor = { remember { BitmapPainter(it) } },
                            contentDescription = "thumbnail",
                            modifier = Modifier.fillMaxWidth().height(250.dp),
                        )
                    }
                    if (selectedVideoData != null) {
                        Text(
                            "${selectedVideoData.resolution} - ${selectedVideoData.ext}  - ${selectedVideoData.filesizeApprox?.toHumanSize() ?: "Unknown size"} ",
                            modifier =
                                Modifier.clickable {
                                    updateDropState(true)
                                },
                        )
                    } else {
                        Text("Click to select video format. ", modifier = Modifier.clickable { updateDropState(true) })
                    }

                    FormatSelector(
                        dropState = dropState,
                        videoData = data,
                        updateDropState = {
                            updateDropState(false)
                        },
                        setSelectedVideoData = { setSelectedVideoData(it) },
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Button(onClick = {
                            updateDialog(false)
                        }) {
                            Text("Cancel")
                        }
                        Spacer(Modifier.width(16.dp))
                        Button(onClick = {
                            scope.launch {
                                onDownload()
                                updateDialog(false)
                            }
                        }) {
                            Text("Download")
                        }
                    }
                }
            }
        },
    )
}


@Composable
fun SettingsTab(modifier: Modifier = Modifier) {
    val selectedDirectory =
        remember {
            mutableStateOf<File?>(null)
        }
    Column(modifier = modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Change Downloads Folder")
            val userHome = System.getProperty("user.home")
            Text(
                selectedDirectory.value?.absolutePath ?: Paths
                    .get(userHome, "Downloads")
                    .toAbsolutePath()
                    .toString(),
                modifier =
                    Modifier.clickable {
                        selectedDirectory.value = filePicker()
                    },
            )
        }
    }
}


@Composable
fun FormatSelector(
    modifier: Modifier = Modifier,
    videoData: VideoInfo? = null,
    dropState: Boolean = false,
    updateDropState: () -> Unit = {},
    setSelectedVideoData: (Formats) -> Unit,
) {
    println("called $videoData")

    Box(modifier = modifier.fillMaxWidth().padding(8.dp)) {
        DropdownMenu(expanded = dropState, onDismissRequest = { updateDropState() }) {
            videoData?.videoFormats?.filter { fmt -> fmt.videoExt != "none" }?.forEachIndexed { _, fmt ->

                DropdownMenuItem(onClick = {
                    setSelectedVideoData(fmt)
                    updateDropState()
                }) {
                    Text(fmt.resolution ?: "")
                    Spacer(Modifier.width(8.dp))
                    Text(fmt.ext ?: "")
                    Spacer(Modifier.width(8.dp))
                    Text(fmt.filesizeApprox?.toHumanSize() ?: "Unknown size")
                    Spacer(Modifier.width(8.dp))
                }
            }
        }
    }
}



@Composable
private fun PreviewFormatSelector() {
}

fun filePicker(): File? {
    val chooser = JFileChooser(FileSystemView.getFileSystemView().homeDirectory)

    chooser.dialogTitle = "Select Directory"
    chooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
    val result = chooser.showOpenDialog(null)
    return if (result == JFileChooser.APPROVE_OPTION) chooser.selectedFile else null
}

val LocalVideoData = compositionLocalOf { VideoInfo() }


@Composable
fun <T> AsyncImage(
    load: suspend () -> T,
    painterFor: @Composable (T) -> Painter,
    contentDescription: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val image: T? by produceState<T?>(null) {
        value =
            withContext(Dispatchers.IO) {
                try {
                    load()
                } catch (e: IOException) {
                    // instead of printing to console, you can also write this to log,
                    // or show some error placeholder
                    e.printStackTrace()
                    null
                }
            }
    }

    if (image != null) {
        Image(
            painter = painterFor(image!!),
            contentDescription = contentDescription,
            contentScale = contentScale,
            modifier = modifier,
        )
    }
}

fun loadXmlImageVector(
    file: File,
    density: Density,
): ImageVector = file.inputStream().buffered().use { loadXmlImageVector(InputSource(it), density) }

fun loadXmlImageVector(
    url: String,
    density: Density,
): ImageVector = URL(url).openStream().buffered().use { loadXmlImageVector(InputSource(it), density) }

fun loadImageBitmap(url: String): ImageBitmap = URL(url).openStream().buffered().use(::loadImageBitmap)

fun loadImageBitmap(file: File): ImageBitmap = file.inputStream().buffered().use(::loadImageBitmap)

fun loadSvgPainter(
    file: File,
    density: Density,
): Unit = file.inputStream().buffered().use { loadSvgPainter(it, density) }

fun loadSvgPainter(
    url: String,
    density: Density,
): Unit = URL(url).openStream().buffered().use { loadSvgPainter(it, density) }

fun String.toHumanSize() =
    this.toFloatOrNull()?.let {
        val currentVal = it / (1024f * 1024f) // convert to mb
        when {
            currentVal <= 999.99 -> {
                return@let "$currentVal MB"
            }

            else -> {
                return@let "${currentVal / 1024f} GB"
            }
        }
    }
