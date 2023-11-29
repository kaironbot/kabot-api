package org.wagham.kabotapi.logic.impl

import kotlinx.coroutines.flow.Flow
import org.wagham.db.enums.LabelType
import org.wagham.db.models.embed.Label
import org.wagham.kabotapi.components.DatabaseComponent
import org.wagham.kabotapi.logic.LabelLogic

class LabelLogicImpl(
    private val database: DatabaseComponent
): LabelLogic {

    override fun getLabels(guildId: String, labelType: LabelType?): Flow<Label> =
        database.labelsScope.getLabels(guildId, labelType)

}