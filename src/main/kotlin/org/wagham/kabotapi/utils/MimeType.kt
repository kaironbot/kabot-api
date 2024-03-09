package org.wagham.kabotapi.utils

enum class MimeType(val mimeType: String) {
	IMAGE_WEBP("image/webp"),
	IMAGE_PNG("image/png"),
	IMAGE_JPEG("image/jpeg"),
	UNKNOWN("unknown");

	companion object {
		fun safeValueOf(value: String): MimeType = MimeType.entries.firstOrNull { it.mimeType == value } ?: UNKNOWN

		fun isImage(type: MimeType): Boolean =
			type == IMAGE_JPEG || type == IMAGE_PNG || type == IMAGE_WEBP
	}
}