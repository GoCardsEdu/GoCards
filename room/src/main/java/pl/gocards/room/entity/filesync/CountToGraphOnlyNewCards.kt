package pl.gocards.room.entity.filesync

import androidx.room.DatabaseView

/**
 * @author Grzegorz Ziemski
 */
@DatabaseView(
    viewName = "FileSync_View_CountToGraphOnlyNewCards",
    value = "SELECT " +
            "f.graph as fromGraph, " +
            "count(DISTINCT t.graph) as countToGraph " +
            "FROM FileSync_CardEdge e " +
            "LEFT JOIN FileSync_CardImported f ON e.fromCardImportedId = f.id " +
            "LEFT JOIN FileSync_CardImported t ON e.toCardImportedId = t.id " +
            "WHERE " +
            "f.graph != t.graph " +
            "AND e.deleted=0 " +
            "AND e.status IN (" +
            "'" + CardEdge.STATUS_DECK_FIRST_NEW + "'," +
            "'" + CardEdge.STATUS_DECK_SECOND_NEW + "'," +
            "'" + CardEdge.STATUS_IMPORTED_FIRST_NEW + "'," +
            "'" + CardEdge.STATUS_IMPORTED_SECOND_NEW + "')" +
            "AND f.contentStatus NOT IN (" +
            "'" + CardImported.STATUS_DELETE_BY_FILE + "'," +
            "'" + CardImported.STATUS_DELETE_BY_DECK + "') " +
            "AND t.contentStatus NOT IN (" +
            "'" + CardImported.STATUS_DELETE_BY_FILE + "'," +
            "'" + CardImported.STATUS_DELETE_BY_DECK + "') " +
            "GROUP BY fromGraph"
)
@SuppressWarnings("unused")
data class CountToGraphOnlyNewCards(
    var fromGraph: Int = 0,
    var countToGraph: Int = 0
)