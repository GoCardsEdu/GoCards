/*
 * Copyright (c) 2023. Grzegorz Ziemski
 */

package pl.gocards.room.entity.deck

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Grzegorz Ziemski
 */
data class CardLearningProgressAndHistory(

    @Embedded
    var progress: CardLearningProgress,

    @Relation(
        parentColumn = "cardLearningHistoryId",
        entityColumn = "id"
    )
    var history: CardLearningHistory
)