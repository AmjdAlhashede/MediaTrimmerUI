package io.github.trimmer.extensions

fun Long.snapToFrame(frameInterval: Long): Long {
    return (this / frameInterval) * frameInterval
}
