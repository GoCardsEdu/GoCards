package pl.gocards.room.entity.filesync

import androidx.room.DatabaseView

/**
 * @author Grzegorz Ziemski
 */
@DatabaseView(
    viewName = "FileSync_View_GraphEdge",
    value = "SELECT " +
            "f.graph as fromGraph, " +
            "t.graph as toGraph, " +
            "sum(e.weight) as weight " +
            "FROM FileSync_CardEdge e " +
            "LEFT JOIN FileSync_CardImported f ON e.fromCardImportedId = f.id " +
            "LEFT JOIN FileSync_CardImported t ON e.toCardImportedId = t.id " +
            "WHERE fromGraph != toGraph " +
            "AND f.contentStatus NOT IN (" +
            "'" + CardImported.STATUS_DELETE_BY_FILE + "'," +
            "'" + CardImported.STATUS_DELETE_BY_DECK + "') " +
            "AND t.contentStatus NOT IN (" +
            "'" + CardImported.STATUS_DELETE_BY_FILE + "'," +
            "'" + CardImported.STATUS_DELETE_BY_DECK + "') " +
            "GROUP BY f.graph, t.graph " +
            "ORDER BY weight DESC"
)
@SuppressWarnings("unused")
open class GraphEdge(
    var fromGraph: Int,
    var toGraph: Int,
    /**
     * The sum of edge weights between graphs.
     */
    var weight: Int
)