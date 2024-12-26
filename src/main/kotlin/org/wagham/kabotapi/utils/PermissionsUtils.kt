package org.wagham.kabotapi.utils

fun guard(condition: Boolean, lazyMessage: () -> String) {
	if (!condition) {
		throw IllegalAccessException(lazyMessage())
	}
}