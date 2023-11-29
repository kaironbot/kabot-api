package org.wagham.kabotapi.entities.dto

import kotlinx.serialization.Serializable
import org.wagham.db.models.dto.SessionOutcome
import org.wagham.db.models.embed.LabelStub

@Serializable
class SessionRegistrationDto(
    val masterId: String,
    val masterReward: Int,
    val title: String,
    val date: Long,
    val outcomes: List<SessionOutcome>,
    val labels: Set<LabelStub>
)