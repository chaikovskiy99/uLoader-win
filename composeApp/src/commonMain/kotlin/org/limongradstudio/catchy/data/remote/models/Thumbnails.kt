package org.limongradstudio.catchy.data.remote.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Thumbnails(
    @SerialName("url") var url: String? = null,
    @SerialName("preference") var preference: Int? = null,
    @SerialName("id") var id: String? = null,
)
