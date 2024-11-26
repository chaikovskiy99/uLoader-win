package org.limongradstudio.catchy

typealias VoidFun = () -> Unit
typealias VoidParamFun<T> = (type: T) -> Unit
typealias ParamFunWithReturn<T, R> = (type: T) -> R
typealias DownloadProgress<T> = (total: T, downloaded: T) -> Unit
