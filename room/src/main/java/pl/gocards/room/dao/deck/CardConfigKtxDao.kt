package pl.gocards.room.dao.deck

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import pl.gocards.room.dao.BaseKtxDao
import pl.gocards.room.entity.deck.CardConfig
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_DEFINITION_FONT_SIZE
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_FONT_SIZE_DEFAULT
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_FONT_SIZE_MAX
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_FONT_SIZE_MIN
import pl.gocards.room.entity.deck.DeckConfig.Companion.STUDY_CARD_TERM_FONT_SIZE

/**
 * Naming convention:
 * https://docs.spring.io/spring-data/jpa/docs/2.5.5/reference/html/#jpa.query-methods.query-creation
 *
 * @author Grzegorz Ziemski
 */
@Dao
abstract class CardConfigKtxDao: BaseKtxDao<CardConfig> {

    private val mutex = Mutex()

    @Query("SELECT * FROM Core_CardConfig WHERE cardId=:cardId AND `key`=:key")
    abstract suspend fun getConfigByKey(cardId: Int, key: String): CardConfig?

    @Query("DELETE FROM Core_CardConfig WHERE cardId=:cardId AND `key`=:key")
    abstract suspend fun deleteByKey(cardId: Int, key: String)

    private suspend fun getIntByKey(
        cardId: Int,
        key: String
    ): Int? {
        return try {
            val config: CardConfig? = getConfigByKey(cardId, key)
            config?.value?.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun update(
        cardId: Int,
        key: String,
        value: String,
        defaultValue: String
    ) {
        mutex.withLock {
            if (value == defaultValue) {
                deleteByKey(cardId, key)
            } else {
                val config: CardConfig? = getConfigByKey(cardId, key)
                if (config == null) {
                    insertAll(CardConfig(cardId, key, value))
                } else {
                    config.value = value
                    updateAll(config)
                }
            }
        }
    }

    suspend fun getStudyCardTermFontSize(cardId: Int): Int? {
        val fontSize = getIntByKey(
            cardId,
            STUDY_CARD_TERM_FONT_SIZE
        )
        fontSize?.let {
            if (fontSize !in STUDY_CARD_FONT_SIZE_MIN..STUDY_CARD_FONT_SIZE_MAX) {
                return null
            }
        }
        return fontSize
    }

    suspend fun updateStudyCardTermFontSize(cardId: Int, value: Int) {
        update(
            cardId,
            STUDY_CARD_TERM_FONT_SIZE,
            value.toString(),
            STUDY_CARD_FONT_SIZE_DEFAULT.toString()
        )
    }

    suspend fun getStudyCardDefinitionFontSize(cardId: Int): Int? {
        val fontSize = getIntByKey(
            cardId,
            STUDY_CARD_DEFINITION_FONT_SIZE,
        )
        fontSize?.let {
            if (fontSize !in STUDY_CARD_FONT_SIZE_MIN..STUDY_CARD_FONT_SIZE_MAX) {
                return null
            }
        }
        return fontSize
    }

    suspend fun updateStudyCardDefinitionFontSize(cardId: Int, value: Int) {
        update(
            cardId,
            STUDY_CARD_DEFINITION_FONT_SIZE,
            value.toString(),
            STUDY_CARD_FONT_SIZE_DEFAULT.toString()
        )
    }

    suspend fun getStudyCardTdDisplayRatio(cardId: Int): Float? {
        return getConfigByKey(cardId, CardConfig.STUDY_CARD_TD_DISPLAY_RATIO)?.value?.toFloat()
    }

    suspend fun updateStudyCardTdDisplayRatio(cardId: Int, value: Float) {
        update(
            cardId,
            CardConfig.STUDY_CARD_TD_DISPLAY_RATIO,
            value.toString(),
            CardConfig.STUDY_CARD_TD_DISPLAY_RATIO_DEFAULT.toString()
        )
    }
}