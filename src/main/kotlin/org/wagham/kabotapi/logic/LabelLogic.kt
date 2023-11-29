package org.wagham.kabotapi.logic

import kotlinx.coroutines.flow.Flow
import org.wagham.db.enums.LabelType
import org.wagham.db.models.embed.Label

interface LabelLogic {

    /**
     * Retrieves all the [Label]s in a guild. It will filter by [LabelType] if specified, otherwise all the labels
     * will be returned.
     *
     * @param guildId the id of the guild.
     * @param labelType a [LabelType] or null.
     * @return a [Flow] of [Label]s.
     */
    fun getLabels(guildId: String, labelType: LabelType? = null): Flow<Label>

}