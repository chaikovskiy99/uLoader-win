package org.limongradstudio.catchy

interface Platform {
  val name: String
}

expect fun getPlatform(): Platform

expect fun getMediaParser(): MediaParser

interface MediaParser {
  suspend fun <T> extractInfo(resource: String): T?
  suspend fun <T> download(resource: String, selectedFormatId: Int): T
}

expect suspend fun setup(force: Boolean = false)