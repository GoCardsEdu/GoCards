package pl.gocards.room.entity.deck

import androidx.room.Entity
import androidx.room.PrimaryKey
import pl.gocards.room.util.HtmlUtil
import pl.gocards.room.util.TimeUtil

/**
 * The card in the deck.
 *
 * @author Grzegorz Ziemski
 */
@Entity(tableName = "Core_Card")
data class Card(

    @PrimaryKey
    var id: Int? = null,
    var ordinal: Int = 0,

    /**
     * Synonyms: Front
     *
     * To simplify SQL queries, NULL values are not used.
     * It would have to be used "IS NULL" instead of "= NULL".
     */
    var term: String = "",

    /**
     * Synonyms: Back
     *
     * To simplify SQL queries, NULL values are not used.
     * It would have to be used "IS NULL" instead of "= NULL".
     */
    var definition: String = "",

    var createdAt: Long = TimeUtil.getNowEpochSec(),
    var updatedAt: Long = TimeUtil.getNowEpochSec(),
    var deletedAt: Long? = null,
    var fileSyncCreatedAt: Long? = null,
    var fileSyncModifiedAt: Long? = null,
    var isTermSimpleHtml: Boolean = false,
    var isTermFullHtml: Boolean = false,
    var isDefinitionSimpleHtml: Boolean = false,
    var isDefinitionFullHtml: Boolean = false,
    var disabled: Boolean = false
) {

    override fun equals(other: Any?): Boolean {
        return if (other is Card) {
            id == other.id
        } else super.equals(other)
    }

    override fun hashCode(): Int {
        return id!!
    }

    companion object {

        private val htmlUtil: HtmlUtil = HtmlUtil.getInstance()

        fun setTerm(card: Card, term: String?) {
            card.term = term ?: ""
        }

        fun setDefinition(card: Card, definition: String?) {
            card.definition = definition ?: ""
        }

        fun setHtmlFlags(card: Card) {
            setTermHtmlFlags(card)
            setDefinitionHtmlFlags(card)
        }

        private fun setTermHtmlFlags(card: Card) {
            val term = card.term
            card.isTermSimpleHtml = false
            card.isTermFullHtml = false
            if (htmlUtil.isSimpleHtml(term)) {
                card.isTermSimpleHtml = true
                if (htmlUtil.isFullHtml(term)) {
                    card.isTermFullHtml = true
                }
            }
        }

        private fun setDefinitionHtmlFlags(card: Card) {
            card.isDefinitionSimpleHtml = false
            card.isDefinitionFullHtml = false
            val definition = card.definition
            if (htmlUtil.isSimpleHtml(definition)) {
                card.isDefinitionSimpleHtml = true
                if (htmlUtil.isFullHtml(definition)) {
                    card.isDefinitionFullHtml = true
                }
            }
        }
    }
}