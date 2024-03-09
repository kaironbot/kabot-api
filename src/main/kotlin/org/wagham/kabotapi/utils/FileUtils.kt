package org.wagham.kabotapi.utils

import org.apache.tika.Tika

object FileUtils {

	private val tika = Tika()

	fun inferMimeType(file: ByteArray): MimeType = MimeType.safeValueOf(tika.detect(file))
}