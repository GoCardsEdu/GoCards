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

    /**
     * Used to color code in a list:
     * the new card created in the file
     */
    var fileSyncCreatedAt: Long? = null,

    /**
     * Used to color code in a list:
     * the card updated in the file
     */
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
            if (htmlUtil.isSimpleHtml(term)) {
                card.isTermSimpleHtml = true
            }

            card.isTermFullHtml = false
            if (htmlUtil.isFullHtml(term)) {
                card.isTermFullHtml = true
            }
        }

        private fun setDefinitionHtmlFlags(card: Card) {
            val definition = card.definition

            card.isDefinitionSimpleHtml = false
            if (htmlUtil.isSimpleHtml(definition)) {
                card.isDefinitionSimpleHtml = true
            }

            card.isDefinitionFullHtml = false
            if (htmlUtil.isFullHtml(definition)) {
                card.isDefinitionFullHtml = true
            }
        }
    }
}