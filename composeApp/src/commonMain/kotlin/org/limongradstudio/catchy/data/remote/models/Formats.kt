package org.limongradstudio.catchy.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Formats(
    @SerialName("format_id") var formatId: String? = null,
    @SerialName("format_note") var formatNote: String? = null,
    @SerialName("ext") var ext: String? = null,
    @SerialName("url") var url: String? = null,
    @SerialName("width") var width: String? = null,
    @SerialName("height") var height: String? = null,
    @SerialName("fps") var fps: String? = null,
    @SerialName("resolution") var resolution: String? = null,
    @SerialName("aspect_ratio") var aspectRatio: Double? = null,
    @SerialName("filesize_approx") var filesizeApprox: String? = null,
    @SerialName("audio_ext") var audioExt: String? = null,
    @SerialName("video_ext") var videoExt: String? = null,
    @SerialName("vbr") var vbr: String? = null,
    @SerialName("format") var format: String? = null,
)
