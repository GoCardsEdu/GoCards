package pl.gocards.ui.common

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.util.Xml
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.elevation.SurfaceColors
import org.xmlpull.v1.XmlPullParser
import pl.gocards.R

/**
 * Adds scrolling and decoration to dividers.
 * @author Grzegorz Ziemski
 */
class RecyclerViewFactory {

    fun create(
        activity: Activity,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
    ): RecyclerView {
        return RecyclerView(
            activity,
            getScrollbarAttributeSet(activity)
        ).apply {
            this.adapter = adapter
            this.layoutManager = LinearLayoutManager(activity)
            this.addItemDecoration(createDividerItemDecoration(activity))
        }
    }

    private fun getScrollbarAttributeSet(context: Context): AttributeSet? {
        return try {
            val parser: XmlPullParser = context.resources.getXml(R.xml.scrollbar)
            parser.next()
            parser.nextTag()
            Xml.asAttributeSet(parser)
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun createDividerItemDecoration(context: Context): DividerItemDecoration {
        val divider = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        val color = SurfaceColors.SURFACE_5.getColor(context)
        val drawable =
            GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, intArrayOf(color, color))
        drawable.setSize(1, 1)
        divider.setDrawable(drawable)
        return divider
    }
}