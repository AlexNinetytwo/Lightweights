package de.alex.lightweights.ui.components

import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import de.alex.lightweights.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChartMarkerView(
    context: Context,
    private val startDate: LocalDate
) : MarkerView(context, R.layout.marker_view) {

    private val valueText: TextView = findViewById(R.id.valueText)
    private val dateText: TextView = findViewById(R.id.dateText)

    private val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        if (e == null) return

        val date = startDate.plusDays(e.x.toLong())

        dateText.text = date.format(formatter)

        valueText.text =
            if (highlight?.axis == YAxis.AxisDependency.RIGHT) {
                "Stärke: ${e.y.toInt()}"
            } else {
                "Volumen: ${e.y.toInt()} kg"
            }

        super.refreshContent(e, highlight)
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-(width / 2f), -height.toFloat())
    }
}
