package org.limongradstudio.catchy.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequestedFormats(
    @SerialName("format_id") var formatId: String? = null,
    @SerialName("format_index") var formatIndex: String? = null,
    @SerialName("url") var url: String? = null,
    @SerialName("manifest_url") var manifestUrl: String? = null,
    @SerialName("tbr") var tbr: Double? = null,
    @SerialName("ext") var ext: String? = null,
    @SerialName("fps") var fps: Int? = null,
    @SerialName("protocol") var protocol: String? = null,
    @SerialName("preference") var preference: String? = null,
    @SerialName("quality") var quality: Int? = null,
    @SerialName("has_drm") var hasDrm: Boolean? = null,
    @SerialName("width") var width: Int? = null,
    @SerialName("height") var height: Int? = null,
    @SerialName("vcodec") var vcodec: String? = null,
    @SerialName("acodec") var acodec: String? = null,
    @SerialName("dynamic_range") var dynamicRange: String? = null,
    @SerialName("source_preference") var sourcePreference: Int? = null,
    @SerialName("format_note") var formatNote: String? = null,
    @SerialName("resolution") var resolution: String? = null,
    @SerialName("aspect_ratio") var aspectRatio: Double? = null,
    @SerialName("video_ext") var videoExt: String? = null,
    @SerialName("audio_ext") var audioExt: String? = null,
    @SerialName("abr") var abr: Int? = null,
    @SerialName("vbr") var vbr: Double? = null,
    @SerialName("format") var format: String? = null,
)
