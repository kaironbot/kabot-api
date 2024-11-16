package org.wagham.kabotapi.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.wagham.kabotapi.entities.utils.ManualSource

object DnDSourceList {

	val manuals = this::class.java.getResource("manuals.json")?.readText()?.let {
		jacksonObjectMapper().readValue<List<ManualSource>>(it)
	} ?: throw IllegalStateException("Cannot load latin map for normalizer")

}